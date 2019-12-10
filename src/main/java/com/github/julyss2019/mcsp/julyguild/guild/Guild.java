package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.ConfigGuildIcon;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.gui.player.MainGUI;
import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.request.guild.BaseGuildRequest;
import com.github.julyss2019.mcsp.julyguild.request.guild.GuildRequest;
import com.github.julyss2019.mcsp.julyguild.request.guild.GuildRequestType;
import com.github.julyss2019.mcsp.julyguild.request.guild.JoinGuildRequest;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Guild {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();

    private final File file;
    private YamlConfiguration yml;
    private boolean deleted;
    private UUID uuid;
    private String name;
    private GuildOwner owner;
    private Map<UUID, GuildMember> memberMap = new HashMap<>();
    private Map<UUID, List<GuildRequest>> playerRequestMap = new HashMap<>();
    private Map<String, OwnedIcon> iconMap = new HashMap<>();
    private OwnedIcon currentIcon;
    private GuildBank guildBank;
    private List<String> announcements;
    private long createTime;
    private int additionMemberCount;

    Guild(File file) {
        this.file = file;

        if (!file.exists()) {
            throw new RuntimeException("公会不存在");
        }

        load();
    }

    /**
     * 载入
     * @return
     */
    private void load() {
        this.yml = YamlConfiguration.loadConfiguration(file);
        this.deleted = yml.getBoolean("deleted");
        this.name = yml.getString("name");
        this.uuid = UUID.fromString(yml.getString("uuid"));
        this.guildBank = new GuildBank(this);
        this.announcements = yml.getStringList("announcements");
        this.createTime = yml.getLong("create_time");
        this.additionMemberCount = yml.getInt("addition_member_count");

        loadMembers();
        loadRequests();
        loadIcons();
    }

/*    private void verify() {
        *//*
        应该使用 UUID 代替实体，否则将会受到类构造时间的影响
         *//*
        for (GuildMember guildMember : getMembers()) {
            UUID memberUuid = guildMember.getUuid();
            UUID memberGuildUuid = guildPlayerManager.getGuildPlayer(memberUuid).getGuildUuid();

            // 没指向本公会
            if (!memberGuildUuid.equals(getUuid())) {
                throw new RuntimeException("公会成员 " + memberUuid + " 指向的公会 " + memberGuildUuid +  " 不是本公会(" + getUuid() + ")");
            }
        }

    }*/

    private void loadMembers() {
        memberMap.clear();

        if (yml.contains("members")) {
            for (String memberUuidStr : yml.getConfigurationSection("members").getKeys(false)) {
                Position position = Position.valueOf(yml
                        .getConfigurationSection("members")
                        .getConfigurationSection(memberUuidStr)
                        .getString("position"));
                UUID memberUuid = UUID.fromString(memberUuidStr);
                GuildMember member = position == Position.MEMBER
                        ? new GuildMember(this, memberUuid)
                        : new GuildOwner(this, memberUuid);

                memberMap.put(memberUuid, member);

                if (member instanceof GuildOwner) {
                    this.owner = (GuildOwner) member;
                }
            }
        }
    }

    private void loadRequests() {
        playerRequestMap.clear();

        if (yml.contains("requests")) {
            // 载入玩家请求
            for (String uuid : yml.getConfigurationSection("requests").getKeys(false)) {
                ConfigurationSection requestSection = yml.getConfigurationSection("requests").getConfigurationSection(uuid);
                GuildRequestType guildRequestType = GuildRequestType.valueOf(requestSection.getString("type"));
                BaseGuildRequest request;


                switch (guildRequestType) {
                    case JOIN:
                        request = new JoinGuildRequest();
                        break;
                    default:
                        throw new RuntimeException("不支持的请求类型");
                }

                UUID requesterUUID = UUID.fromString(requestSection.getString("requester"));

                request.setRequester(guildPlayerManager.getGuildPlayer(requesterUUID));
                request.setTime(requestSection.getLong("time"));
                request.setUuid(UUID.fromString(uuid));

                if (!playerRequestMap.containsKey(requesterUUID)) {
                    playerRequestMap.put(requesterUUID, new ArrayList<>());
                }

                playerRequestMap.get(requesterUUID).add(request);
            }
        }
    }

    private void loadIcons() {
        iconMap.clear();

/*        if (yml.contains("icons")) {
            for (String uuid : yml.getConfigurationSection("icons").getKeys(false)) {
                ConfigurationSection iconSection = yml.getConfigurationSection("icons").getConfigurationSection(uuid);
                ConfigGuildIcon configGuildIcon = IconShopConfig.getIcon(iconSection.getName());

                if (configGuildIcon == null) {
                    JulyGuild.getInstance().warning("公会图标 " + iconSection.getName() + " 在");
                    continue;
                }

                OwnedIcon icon = new OwnedIcon(this, Optional.ofNullable().orElseThrow());

                iconMap.put(icon.getName(), icon);
            }
        }*/
    }

    /**
     * 得到公会唯一标识符
     * @return
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * 是否已被解散
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 得到公会银行
     * @return
     */
    public GuildBank getGuildBank() {
        return guildBank;
    }

    /**
     * 设置主人
     * 旧主人将变成普通成员
     * @param newOwner
     */
    public void setOwner(GuildMember newOwner) {
        GuildMember oldOwner = owner;
        UUID newOwnerUuid = newOwner.getUuid();

        if (newOwner.equals(owner)) {
            throw new IllegalArgumentException("成员已是会长");
        }

        if (!memberMap.containsKey(newOwnerUuid)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + newOwnerUuid + ".position", Position.OWNER.name());
        yml.set("members." + oldOwner.getUuid() + ".position", Position.MEMBER.name());
        save();
        loadMembers();

        if (newOwner.isOnline()) {
            newOwner.getGuildPlayer().updateGUI(GUIType.values());
        }
    }

    /**
     * 是否为成员
     * @param guildPlayer
     * @return
     */
    public boolean isMember(GuildPlayer guildPlayer) {
        return memberMap.containsKey(guildPlayer.getUuid());
    }

    public boolean isOwnedIcon(ConfigGuildIcon configGuildIcon) {
        return iconMap.containsKey(configGuildIcon.getName());
    }

    public OwnedIcon getCurrentIcon() {
        return currentIcon;
    }

    public void setCurrentIcon(OwnedIcon ownedIcon) {
        String iconName = ownedIcon.getName();

        if (!iconMap.containsKey(iconName)) {
            throw new IllegalArgumentException("图标不存在");
        }

        yml.set("current_icon", iconName);
        save();
        this.currentIcon = iconMap.get(iconName);
    }

    public void giveIcon(ConfigGuildIcon configGuildIcon) {
        for (OwnedIcon ownedIcon : getIcons()) {
            if (ownedIcon.getConfigGuildIcon().equals(configGuildIcon)) {
                throw new IllegalArgumentException("图标已拥有");
            }
        }

        yml.set("icons." + configGuildIcon.getName() + ".create_time", System.currentTimeMillis());
        save();
        loadIcons();
    }

    public Collection<OwnedIcon> getIcons() {
        return new ArrayList<>(iconMap.values());
    }

    /**
     * 是否为宗主
     * @param guildPlayer
     * @return
     */
    public boolean isOwner(GuildPlayer guildPlayer) {
        return getOwner().getGuildPlayer().equals(guildPlayer);
    }

    /**
     * 得到成员
     * @param guildPlayer
     * @return
     */
    public GuildMember getMember(GuildPlayer guildPlayer) {
        return memberMap.get(guildPlayer.getUuid());
    }

    /**
     * 公会文件
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * 公会名
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 是否有效
     * @return
     */
    public boolean isValid() {
        return !deleted;
    }

    /**
     * 成员数量
     * @return
     */
    public int getMemberCount() {
        return memberMap.size();
    }

    /**
     * 得到公会主人
     * @return
     */
    public GuildOwner getOwner() {
        return owner;
    }

    /**
     * 得到成员（包含主人）
     * @return
     */
    public List<GuildMember> getMembers() {
        return new ArrayList<>(memberMap.values());
    }

    /**
     * 添加成员
     * @param guildPlayer
     */
    public void addMember(GuildPlayer guildPlayer) {
        String playerName = guildPlayer.getName();

        if (isMember(guildPlayer)) {
            throw new IllegalArgumentException("成员已存在");
        }

        yml.set("members." + playerName + ".permission", Position.MEMBER.name());
        yml.set("members." + playerName + ".join_time", System.currentTimeMillis());
        YamlUtil.saveYaml(yml, file);
        loadMembers();
        guildPlayer.pointGuild(this);
        updateMembersGUI(GUIType.MEMBER);
    }

    /**
     * 删除成员
     * @param guildMember
     */
    public void removeMember(GuildMember guildMember) {
        if (guildMember instanceof GuildOwner) {
            throw new IllegalArgumentException("不能删除会长成员");
        }

        if (!isMember(guildMember.getGuildPlayer())) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + guildMember.getUuid().toString(), null);
        save();
        loadMembers();
        guildMember.getGuildPlayer().pointGuild((UUID) null);
        updateMembersGUI(GUIType.MEMBER);
    }

    /**
     * 删除公会
     * @return
     */
    public void delete() {
        yml.set("deleted", true);
        YamlUtil.saveYaml(yml, file);

        for (GuildMember guildMember : getMembers()) {
            GuildPlayer guildPlayer = guildMember.getGuildPlayer();

            guildPlayer.pointGuild((Guild) null);

            if (guildPlayer.isUsingGUI()) {
                GUI usingGUI = guildPlayer.getUsingGUI();

                if (usingGUI instanceof MainGUI) {
                    usingGUI.reopen();
                } else {
                    guildPlayer.closeGUI();
                }
            }
        }

        JulyGuild.getInstance().getGuildManager().unload(this);
        this.deleted = true;
    }

    /**
     * 得到最大成员数
     * @return
     */
    public int getAdditionMemberCount() {
        return this.additionMemberCount;
    }

    /**
     * 设置最大成员数
     * @param additionMemberCount
     * @return
     */
    public void setAdditionMemberCount(int additionMemberCount) {
        yml.set("max_member_count", additionMemberCount);
        YamlUtil.saveYaml(yml, file);
        this.additionMemberCount = additionMemberCount;
    }

    /**
     * 得到创建时间
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * 设置公告
     * @param announcements
     */
    public void setAnnouncements(List<String> announcements) {
        yml.set("announcements", announcements);
        YamlUtil.saveYaml(yml, file);
        this.announcements = announcements;
    }

    /**
     * 更新所有成员的GUI
     */
    public void updateMembersGUI(GUIType... guiTypes) {
        for (GuildMember guildMember : getMembers()) {
            GuildPlayer guildPlayer = guildMember.getGuildPlayer();

            if (guildPlayer.isOnline()) {
                guildPlayer.updateGUI(guiTypes);
            }
        }
    }

    /**
     * 是否有请求
     * @param guildPlayer
     * @param guildRequestType
     * @return
     */
    public boolean hasRequest(GuildPlayer guildPlayer, GuildRequestType guildRequestType) {
        for (GuildRequest guildRequest : getPlayerRequests(guildPlayer)) {
            if (guildRequest.getRequester().equals(guildPlayer) && guildRequestType == guildRequest.getType()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 删除玩家的请求
     * @param guildRequest
     */
    public void removeRequest(GuildRequest guildRequest) {
        String uuid = guildRequest.getUUID().toString();

        yml.set("requests." + uuid, null);
        save();
        loadRequests();

        if (guildRequest instanceof JoinGuildRequest) {
            updateMembersGUI(GUIType.PLAYER_JOIN_REQUEST);
        }
    }

    /**
     * 添加玩家的请求
     * @param guildRequest
     * @return
     */
    public UUID addRequest(GuildRequest guildRequest) {
        if (guildRequest.isOnlyOne() && hasRequest(guildRequest.getRequester(), guildRequest.getType())) {
            throw new IllegalArgumentException("请求类型只允许存在一个!");
        }

        String uuid = guildRequest.getUUID().toString();

        yml.set("requests." + uuid + ".player", guildRequest.getRequester().getName());
        yml.set("requests." + uuid + ".type", guildRequest.getType().name());
        yml.set("requests." + uuid + ".time", guildRequest.getCreationTime());

        save();
        loadRequests();

        if (guildRequest instanceof JoinGuildRequest) {
            updateMembersGUI(GUIType.PLAYER_JOIN_REQUEST);
        }

        return guildRequest.getUUID();
    }

    /**
     * 得到玩家请求
     * @param guildPlayer
     * @return
     */
    public Collection<GuildRequest> getPlayerRequests(GuildPlayer guildPlayer) {
        return getPlayerRequests(guildPlayer.getUuid());
    }

    /**
     * 得到玩家请求
     * @param uuid
     * @return
     */
    public Collection<GuildRequest> getPlayerRequests(UUID uuid) {
        return playerRequestMap.get(uuid);
    }


    /**
     * 得到公告
     * @return
     */
    public List<String> getAnnouncements() {
        return announcements;
    }

    /**
     * 得到yaml
     * @return
     */
    public YamlConfiguration getYaml() {
        return yml;
    }

    /**
     * 保存文件
     */
    public void save() {
        YamlUtil.saveYaml(yml, file);
    }

    /**
     * 得到公会等级权重值
     * @return
     */
    public int getRank() {
        String formula = PlaceholderText.replacePlaceholders(MainSettings.getRankingListFormula(), new Placeholder.Builder().addGuildPlaceholders(this).build());

        try {
            return (int) Parser.parse(formula).evaluate();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("公会等级计算公式不合法: " + formula);
        }
    }

    /**
     * 广播消息（在线成员）
     * @param message
     */
    public void broadcastMessage(String message) {
        for (GuildMember member : getMembers()) {
            if (member.isOnline()) {
                Util.sendColoredMessage(member.getGuildPlayer().getBukkitPlayer(), message);
            }
        }
    }

    /**
     * 得到在线的成员
     * @return
     */
    public List<GuildMember> getOnlineMembers() {
        return getMembers().stream().filter(GuildMember::isOnline).collect(Collectors.toList());
    }

    /**
     * 得到在线的成员数量
     * @return
     */
    public int getOnlineMemberCount() {
        return getOnlineMembers().size();
    }
}
