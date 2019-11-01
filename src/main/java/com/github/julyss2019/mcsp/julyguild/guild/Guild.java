package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.MainConfig;
import com.github.julyss2019.mcsp.julyguild.gui.GUIType;
import com.github.julyss2019.mcsp.julyguild.guild.exception.GuildLoadException;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildAdmin;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.player.GuildOwner;
import com.github.julyss2019.mcsp.julyguild.guild.player.Permission;
import com.github.julyss2019.mcsp.julyguild.guild.request.BaseGuildRequest;
import com.github.julyss2019.mcsp.julyguild.guild.request.GuildRequest;
import com.github.julyss2019.mcsp.julyguild.guild.request.GuildRequestType;
import com.github.julyss2019.mcsp.julyguild.guild.request.JoinGuildRequest;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Guild {
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final YamlConfiguration guiYaml = plugin.getGuiYamlConfig();
    private final GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private final GuildManager guildManager = plugin.getGuildManager();

    private final File file;
    private YamlConfiguration yml;

    private boolean valid;
    private boolean deleted;
    private UUID uuid;
    private GuildOwner owner;
    private String name;
    private Map<String, GuildMember> memberMap = new HashMap<>();
    private int maxMemberCount;
    private long creationTime;
    private List<String> announcements;
    private Map<String, GuildRequest> uuidRequestMap = new HashMap<>();
    private Map<String, List<GuildRequest>> playerRequestMap = new HashMap<>();
    private Map<String, OwnedIcon> iconMap = new HashMap<>();
    private OwnedIcon currentIcon;
    private GuildBank guildBank;

    private final OwnedIcon DEFAULT_ICON;

    protected Guild(File file) {
        this.file = file;
        this.DEFAULT_ICON = OwnedIcon.createNew(Material.valueOf(MainConfig.getDefaultIconMaterial())
                , MainConfig.getDefaultIconData(), MainConfig.getDefaultIconFirstLore());
    }

    public void init() {
        if (!file.exists()) {
            throw new GuildLoadException("宗门不存在!");
        }

        this.yml = YamlConfiguration.loadConfiguration(file);
        this.deleted = yml.getBoolean("deleted");
        this.valid = !deleted;
        this.uuid = UUID.fromString(yml.getString("uuid"));
    }

    /**
     * 载入（或初始化）
     * @return
     */
    public Guild load() {
        if (isDeleted()) {
            throw new GuildLoadException("宗门已被删除");
        }

        memberMap.clear();

        this.owner = new GuildOwner(this, guildPlayerManager.getGuildPlayer(yml.getString("owner.name")));
        this.name = yml.getString("name");
        this.maxMemberCount = yml.getInt("max_member_count", MainConfig.getDefaultMaxMemberCount());
        this.announcements = yml.getStringList("announcements");
        this.creationTime = yml.getLong("creation_time");
        this.guildBank = new GuildBank(this).load();

        if (this.announcements == null) {
            this.announcements = new ArrayList<>();
        }

        if (announcements.size() == 0) {
            announcements.addAll(MainConfig.getAnnouncementDefault());
        }

        if (yml.contains("members")) {
            for (String memberName : yml.getConfigurationSection("members").getKeys(false)) {
                loadMember(memberName);
            }
        }

        memberMap.put(owner.getName(), owner);

        if (yml.contains("requests")) {
            // 载入玩家请求
            for (String uuid : yml.getConfigurationSection("requests").getKeys(false)) {
                ConfigurationSection requestSection = yml.getConfigurationSection("requests").getConfigurationSection(uuid);
                GuildRequestType guildRequestType = GuildRequestType.valueOf(requestSection.getString("type"));
                BaseGuildRequest request = null;
                String playerName = requestSection.getString("player");

                switch (guildRequestType) {
                    case JOIN:
                        request = new JoinGuildRequest();
                        break;
                }

                request.setRequester(guildPlayerManager.getGuildPlayer(playerName));
                request.setTime(requestSection.getLong("time"));
                request.setUuid(UUID.fromString(uuid));

                uuidRequestMap.put(uuid, request);

                if (!playerRequestMap.containsKey(playerName)) {
                    playerRequestMap.put(playerName, new ArrayList<>());
                }

                playerRequestMap.get(playerName).add(request);
            }
        }

        if (yml.contains("icons")) {
            for (String uuid : yml.getConfigurationSection("icons").getKeys(false)) {
                ConfigurationSection iconSection = yml.getConfigurationSection("icons").getConfigurationSection(uuid);
                OwnedIcon icon = new OwnedIcon(Material.valueOf(iconSection.getString("material")), (short) iconSection.getInt("durability"), UUID.fromString(uuid));

                iconMap.put(uuid, icon);
            }
        }

        iconMap.put(DEFAULT_ICON.getUuid().toString(), DEFAULT_ICON);

        if (currentIcon == null) {
            this.currentIcon = DEFAULT_ICON;
        }

        return this;
    }

    public GuildBank getGuildBank() {
        return guildBank;
    }

    public void setOwner(GuildMember newOwner) {
        GuildMember oldOwner = owner;
        String newOwnerName = newOwner.getName();

        if (newOwner.equals(owner)) {
            throw new IllegalArgumentException("成员已是宗主");
        }

        if (!memberMap.containsKey(newOwnerName)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("owner.name", newOwner.getName());
        yml.set("owner.join_time", System.currentTimeMillis());
        save();
        memberMap.remove(oldOwner.getName()); // 删除旧宗主
        this.owner = new GuildOwner(this, guildPlayerManager.getGuildPlayer(newOwnerName));
        memberMap.put(newOwnerName, owner);
        addMember(oldOwner.getGuildPlayer());

        if (newOwner.isOnline()) {
            newOwner.getGuildPlayer().getGuildPlayer().updateGUI(GUIType.values());
        }
    }

    public boolean isMember(String name) {
        return memberMap.containsKey(name);
    }

    public boolean isOwnedIcon(Material material, short durability) {
        for (OwnedIcon ownedIcon : getIcons()) {
            if (ownedIcon.getMaterial() == material && ownedIcon.getData() == durability) {
                return true;
            }
        }

        return false;
    }

    public OwnedIcon getCurrentIcon() {
        return currentIcon;
    }

    public void setCurrentIcon(UUID uuid) {
        // 默认图标不存配置
        if (uuid.equals(DEFAULT_ICON.getUuid())) {
            yml.set("current_icon", null);
            save();
            this.currentIcon = iconMap.get(uuid.toString());
            return;
        }

        String uuidStr = uuid.toString();

        if (!iconMap.containsKey(uuidStr)) {
            throw new IllegalArgumentException("图标不存在");
        }

        yml.set("current_icon", uuidStr);
        save();
        this.currentIcon = iconMap.get(uuidStr);
    }

    public void setCurrentIcon(OwnedIcon ownedIcon) {
        setCurrentIcon(ownedIcon.getUuid());
    }

    public UUID giveIcon(Material material, short durability) {
        for (OwnedIcon icon : getIcons()) {
            if (icon.getMaterial() == material && icon.getData() == durability) {
                throw new IllegalArgumentException("图标已拥有");
            }
        }

        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();

        yml.set("icons." + uuidStr + ".material", material.name());
        yml.set("icons." + uuidStr + ".durability", durability);
        save();
        iconMap.put(uuidStr, new OwnedIcon(material, durability, uuid));
        return uuid;
    }

    public List<OwnedIcon> getIcons() {
        return new ArrayList<>(iconMap.values());
    }


    public void loadMember(String memberName) {
        Permission permission = Permission.valueOf(yml.getString("members." + memberName + ".permission"));
        GuildPlayer guildPlayer = guildPlayerManager.getGuildPlayer(memberName);

        switch (permission) {
            case MEMBER:
                memberMap.put(memberName, new GuildMember(this, guildPlayer));
                break;
            case ADMIN:
                memberMap.put(memberName, new GuildAdmin(this, guildPlayer));
                break;
        }
    }

    public void setMemberPermission(GuildMember guildMember, Permission permission) {
        String memberName = guildMember.getName();

        if (!memberMap.containsKey(memberName)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + memberName + ".permission", permission.name());
        save();
        memberMap.remove(memberName);
        loadMember(memberName);
    }

    /**
     * 是否为宗主
     * @param name
     * @return
     */
    public boolean isOwner(String name) {
        return getOwner().getName().equalsIgnoreCase(name);
    }

    public GuildMember getMember(GuildPlayer guildPlayer) {
        return getMember(guildPlayer.getName());
    }

    /**
     * 得到成员
     * @param name
     * @return
     */
    public GuildMember getMember(String name) {
        if (!memberMap.containsKey(name)) {
            throw new IllegalArgumentException("成员不存在.");
        }

        return memberMap.get(name);
    }

    /**
     * 宗门文件
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
     * 是否已被解散
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 是否有效
     * @return
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 成员数量
     * @return
     */
    public int getMemberCount() {
        return memberMap.size();
    }

    /**
     * 得到宗门主任
     * @return
     */
    public GuildOwner getOwner() {
        return owner;
    }

    public boolean hasMember(String playerName) {
        return memberMap.containsKey(playerName);
    }

    /**
     * 得到成员：包含主人
     * @return
     */
    public List<GuildMember> getMembers() {
        return new ArrayList<>(memberMap.values());
    }

    public List<GuildMember> getSortedMembers() {
        return getMembers().stream().sorted((o1, o2) -> o1.getPermission().getLevel() > o2.getPermission().getLevel() ? -1 : 0).collect(Collectors.toList());
    }

    /**
     * 添加成员
     * @param guildPlayer
     */
    public void addMember(GuildPlayer guildPlayer) {
        String playerName = guildPlayer.getName();

        if (memberMap.containsKey(playerName) || guildPlayer.equals(owner.getGuildPlayer())) {
            throw new IllegalArgumentException("成员已存在");
        }

        yml.set("members." + playerName + ".permission", Permission.MEMBER.name());
        yml.set("members." + playerName + ".join_time", System.currentTimeMillis());
        YamlUtil.saveYaml(yml, file);
        guildPlayer.setGuild(this);
        memberMap.put(playerName, new GuildMember(this, guildPlayer));
        updateMembersGUI(GUIType.MEMBER, GUIType.MAIN);
    }

    /**
     * 删除成员
     * @param guildMember
     */
    public void removeMember(GuildMember guildMember) {
        String memberName = guildMember.getName();

        if (guildMember instanceof GuildOwner) {
            throw new IllegalArgumentException("不能删除宗主成员");
        }

        if (!memberMap.containsKey(memberName)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + memberName, null);
        save();
        guildMember.getGuildPlayer().setGuild(null);
        memberMap.remove(memberName);
        updateMembersGUI(GUIType.MEMBER);
    }

    /**
     * 删除宗门
     * @return
     */
    public void delete() {
        yml.set("deleted", true);
        YamlUtil.saveYaml(yml, file);

        for (GuildMember guildMember : getMembers()) {
            guildMember.getGuildPlayer().setGuild(null);

            if (guildMember.getGuildPlayer().isOnline()) {
                guildMember.getGuildPlayer().getGuildPlayer().updateGUI(GUIType.values());
            }
        }

        guildManager.unload(this);
        this.valid = false;
    }

    /**
     * 宗门唯一区别符
     * @return
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * 得到宗门图标
     * @return
     */
    public OwnedIcon getIcon() {
        return currentIcon;
    }

    /**
     * 得到最大成员数
     * @return
     */
    public int getMaxMemberCount() {
        return maxMemberCount;
    }

    /**
     * 设置最大成员数
     * @param maxMemberCount
     * @return
     */
    public void setMaxMemberCount(int maxMemberCount) {
        yml.set("max_member_count", maxMemberCount);
        YamlUtil.saveYaml(yml, file);
        this.maxMemberCount = maxMemberCount;
    }

    /**
     * 得到创建时间
     * @return
     */
    public long getCreationTime() {
        return creationTime;
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
     * 是否有请求
     * @param guildPlayer
     * @param guildRequestType
     * @return
     */
    public boolean hasRequest(GuildPlayer guildPlayer, GuildRequestType guildRequestType) {
        for (GuildRequest guildRequest : getRequests()) {
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
        uuidRequestMap.remove(uuid);
        updateMembersGUI(GUIType.PLAYER_JOIN_REQUEST);
    }

    /**
     * 更新所有成员的GUI
     */
    public void updateMembersGUI(GUIType... guiTypes) {
        for (GuildMember guildMember : getMembers()) {
            GuildPlayer guildPlayer = guildMember.getGuildPlayer();

            if (guildPlayer.isOnline()) {
                guildPlayer.getGuildPlayer().updateGUI(guiTypes);
            }
        }
    }

    /**
     * 添加玩家的请求
     * @param guildRequest
     * @return
     */
    public String addRequest(GuildRequest guildRequest) {
        if (guildRequest.isOnlyOne() && hasRequest(guildRequest.getRequester(), guildRequest.getType())) {
            throw new IllegalArgumentException("&e请求类型只允许存在一个!");
        }

        String uuid = guildRequest.getUUID().toString();

        yml.set("requests." + uuid + ".player", guildRequest.getRequester().getName());
        yml.set("requests." + uuid + ".type", guildRequest.getType().name());
        yml.set("requests." + uuid + ".time", guildRequest.getCreationTime());

        if (guildRequest instanceof JoinGuildRequest) {
            updateMembersGUI(GUIType.PLAYER_JOIN_REQUEST);
        }

        save();
        uuidRequestMap.put(uuid, guildRequest);
        return uuid;
    }

    /**
     * 得到请求（不排序）
     * @return
     */
    public List<GuildRequest> getRequests() {
        return new ArrayList<>(uuidRequestMap.values());
    }

    public List<GuildRequest> getPlayerRequests(String playerName) {
        return playerRequestMap.get(playerName);
    }

    /**
     * 得到请求数量
     * @return
     */
    public int getRequestCount() {
        return uuidRequestMap.size();
    }

    /**
     * 得到公告
     * @return
     */
    public List<String> getAnnouncements() {
        return announcements;
    }

    public YamlConfiguration getYml() {
        return yml;
    }

    public void save() {
        YamlUtil.saveYaml(yml, file);
    }

    public int getRank() {
        try {
            return (int) Parser.parse(MainConfig.getRankingListFormula()
                    .replace("%GUILD_MONEY%", String.valueOf(getGuildBank().getBalance(GuildBank.BalanceType.MONEY)))
                    .replace("%GUILD_POINTS%", String.valueOf(getGuildBank().getBalance(GuildBank.BalanceType.POINTS)))
                    .replace("%GUILD_MEMBER_COUNT%", String.valueOf(getMemberCount()))
                    .replace("%GUILD_MAX_MEMBER_COUNT%", String.valueOf(getMaxMemberCount())))
                    .evaluate();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("公会等级计算公式不合法");
        }
    }

    public void broadcastMessage(String message) {
        for (GuildMember member : getMembers()) {
            if (member.isOnline()) {
                Util.sendColoredMessage(member.getBukkitPlayer(), message);
            }
        }
    }

    public List<GuildMember> getOnlineMembers() {
        return getMembers().stream().filter(GuildMember::isOnline).collect(Collectors.toList());
    }

    public int getOnlineMemberCount() {
        return getOnlineMembers().size();
    }
}
