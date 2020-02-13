package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.Receiver;
import com.github.julyss2019.mcsp.julyguild.request.Request;
import com.github.julyss2019.mcsp.julyguild.request.Sender;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Guild implements Sender, Receiver {
    private final File file;
    private YamlConfiguration yml;
    private boolean deleted;
    private UUID uuid;
    private String name;
    private GuildOwner owner;
    private Map<UUID, GuildMember> memberMap = new HashMap<>();
    private Map<UUID, GuildIcon> iconMap = new HashMap<>();
    private GuildIcon currentIcon;
    private GuildBank guildBank;
    private GuildMessageBox guildMessageBox;
    private List<String> announcements;
    private long createTime;
    private int additionMemberCount;
    private GuildSpawn spawn;
    private boolean memberDamageEnabled;
    private boolean valid = true;

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

        if (isDeleted()) {
            return;
        }

        this.name = yml.getString("name");
        this.uuid = UUID.fromString(yml.getString("uuid"));
        this.guildBank = new GuildBank(this);
        this.guildMessageBox = new GuildMessageBox(this);
        this.announcements = yml.getStringList("announcements");
        this.createTime = yml.getLong("creation_time");
        this.additionMemberCount = yml.getInt("addition_member_count");
        this.memberDamageEnabled = yml.getBoolean("member_pvp_enabled", true);

        if (yml.contains("spawn")) {
            this.spawn = new GuildSpawn(this);
        }

        loadMembers();
        loadIcons();
    }

    public GuildSpawn getSpawn() {
        return spawn;
    }

    public void setSpawn(@NotNull Location location) {
        yml.set("spawn", location);
        save();
        this.spawn = new GuildSpawn(this);
    }

    public boolean isMemberDamageEnabled() {
        return memberDamageEnabled;
    }

    public void setMemberDamageEnabled(boolean b) {
        yml.set("member_damage_enabled", b);
        save();
        this.memberDamageEnabled = b;
    }

    public GuildMessageBox getGuildMessageBox() {
        return guildMessageBox;
    }

    private void loadMembers() {
        memberMap.clear();

        if (yml.contains("members")) {
            for (String memberUuidStr : yml.getConfigurationSection("members").getKeys(false)) {
                GuildPosition guildPosition = GuildPosition.valueOf(yml
                        .getConfigurationSection("members")
                        .getConfigurationSection(memberUuidStr)
                        .getString("position"));
                UUID memberUuid = UUID.fromString(memberUuidStr);
                GuildPlayer guildPlayer = JulyGuild.getInstance().getGuildPlayerManager().getGuildPlayer(memberUuid);
                GuildMember member = guildPosition == GuildPosition.MEMBER
                        ? new GuildMember(this, guildPlayer)
                        : new GuildOwner(this, guildPlayer);

                memberMap.put(memberUuid, member);
                member.getGuildPlayer().pointGuild(this);

                if (member instanceof GuildOwner) {
                    this.owner = (GuildOwner) member;
                }
            }
        }
    }

    private void loadIcons() {
        iconMap.clear();
    }

    public  boolean hasSpawn() {
        return spawn != null;
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
    public void setOwner(@NotNull GuildMember newOwner) {
        GuildMember oldOwner = owner;
        UUID newOwnerUuid = newOwner.getUuid();

        if (newOwner.equals(owner)) {
            throw new IllegalArgumentException("成员已是会长");
        }

        if (!memberMap.containsKey(newOwnerUuid)) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + newOwnerUuid + ".position", GuildPosition.OWNER.name());
        yml.set("members." + oldOwner.getUuid() + ".position", GuildPosition.MEMBER.name());
        save();
        loadMembers();
    }

    /**
     * 是否为成员
     * @param guildPlayer
     * @return
     */
    public boolean isMember(@NotNull GuildPlayer guildPlayer) {
        return memberMap.containsKey(guildPlayer.getUuid());
    }

    public boolean isMember(@NotNull UUID uuid) {
        return memberMap.containsKey(uuid);
    }

    public GuildIcon getCurrentIcon() {
        return currentIcon;
    }

    public Collection<GuildIcon> getIcons() {
        return new ArrayList<>(iconMap.values());
    }

    public boolean isOwner(@NotNull GuildMember guildMember) {
        return owner.equals(guildMember);
    }

    /**
     * 是否为宗主
     * @param guildPlayer
     * @return
     */
    public boolean isOwner(@NotNull GuildPlayer guildPlayer) {
        return owner.getGuildPlayer().equals(guildPlayer);
    }

    /**
     * 得到成员
     * @param guildPlayer
     * @return
     */
    public GuildMember getMember(@NotNull GuildPlayer guildPlayer) {
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
    public void addMember(@NotNull GuildPlayer guildPlayer) {
        UUID uuid = guildPlayer.getUuid();

        if (isMember(guildPlayer)) {
            throw new IllegalArgumentException("成员已存在");
        }

        yml.set("members." + uuid + ".position", GuildPosition.MEMBER.name());
        yml.set("members." + uuid + ".join_time", System.currentTimeMillis());
        save();

        memberMap.put(guildPlayer.getUuid(), new GuildMember(this, guildPlayer));
        guildPlayer.pointGuild(this);
    }

    /**
     * 删除成员
     * @param guildMember
     */
    public void removeMember(@NotNull GuildMember guildMember) {
        if (guildMember instanceof GuildOwner) {
            throw new IllegalArgumentException("不能删除会长成员");
        }

        if (!isMember(guildMember.getGuildPlayer())) {
            throw new IllegalArgumentException("成员不存在");
        }

        yml.set("members." + guildMember.getUuid(), null);
        save();
        memberMap.remove(guildMember.getUuid());
        guildMember.getGuildPlayer().pointGuild(null);
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid && !deleted;
    }

    /**
     * 删除公会
     * @return
     */
    public void delete() {
        yml.set("deleted", true);
        YamlUtil.saveYaml(yml, file, StandardCharsets.UTF_8);
        this.deleted = true;
        getMembers().forEach(guildMember -> guildMember.getGuildPlayer().pointGuild(null));
        JulyGuild.getInstance().getRequestManager().getSentRequests(this).forEach(Request::delete);
        JulyGuild.getInstance().getRequestManager().getReceivedRequests(this).forEach(Request::delete);
        JulyGuild.getInstance().getGuildManager().unloadGuild(this);
    }

    public int getMaxMemberCount() {
        return MainSettings.getDefaultMaxMemberCount() + getAdditionMemberCount();
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
        yml.set("addition_member_count", additionMemberCount);
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
    public void setAnnouncements(@NotNull List<String> announcements) {
        yml.set("announcements", announcements);
        YamlUtil.saveYaml(yml, file);
        this.announcements = announcements;
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
        YamlUtil.saveYaml(yml, file, StandardCharsets.UTF_8);
    }

    /**
     * 得到公会等级权重值
     * @return
     */
    public int getRank() {
        String formula = PlaceholderText.replacePlaceholders(MainSettings.getRankingListFormula(), new PlaceholderContainer().addGuildPlaceholders(this));

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
                Util.sendMsg(member.getGuildPlayer().getBukkitPlayer(), message);
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

    @Override
    public Receiver.Type getReceiverType() {
        return Receiver.Type.GUILD;
    }

    @Override
    public List<Request> getReceivedRequests() {
        return JulyGuild.getInstance().getRequestManager().getReceivedRequests(this);
    }

    @Override
    public Sender.Type getSenderType() {
        return Sender.Type.GUILD;
    }
}
