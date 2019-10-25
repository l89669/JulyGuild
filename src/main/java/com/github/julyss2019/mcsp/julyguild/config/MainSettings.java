package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julylibrary.config.Config;

import java.util.List;

public class MainSettings {
    private static JulyGuild plugin = JulyGuild.getInstance();

    @Config(path = "prefix")
    private String prefix;

    @Config(path = "metrics_enabled")
    private boolean metricsEnabled;

    @Config(path = "guild.create.name_regex")
    private String guildCreateNameRegex;

    @Config(path = "guild.create.name_not_valid_msg")
    private String guildCreateNameNotValidMsg;

    @Config(path = "guild.create.cost.money.amount")
    private int guildCreateCostMoneyAmount;

    @Config(path = "guild.create.cost.money.enabled")
    private boolean guildCreateCostMoneyEnabled;

    @Config(path = "guild.create.cost.points.amount")
    private int guildCreateCostPointsAmount;

    @Config(path = "guild.create.cost.points.enabled")
    private boolean guildCreateCostPointsEnabled;

    @Config(path = "guild.create.cost.item.key_lore")
    private String guildCreateCostItemKeyLore;

    @Config(path = "guild.create.cost.item.enabled")
    private boolean guildCreateCostItemEnabled;

    @Config(path = "guild.create.colored_no_per_msg")
    private String guildCreateColoredNoPerMsg;

    @Config(path = "guild.create.cost.item.amount")
    private int guildCreateCostItemAmount;

    @Config(path = "guild.announcement.split_char")
    private String guildAnnouncementSplitChar;

    @Config(path = "guild.announcement.max_count")
    private int guildAnnouncementMaxCount;

    @Config(path = "guild.def_max_member_count")
    private int guildDefMaxMemberCount;

    @Config(path = "guild.request.join.timeout")
    private int guildRequestJoinTimeout;

    @Config(path = "guild.def_max_admin_count")
    private int guildDefMaxAdminCount;

    @Config(path = "guild.donate.money.enabled")
    private boolean donateMoneyEnabled;

    @Config(path = "guild.donate.points.enabled")
    private boolean donatePointsEnabled;

    @Config(path = "guild.donate.money.min")
    private int donateMinMoney;

    @Config(path = "guild.donate.points.min")
    private int donateMinPoints;

    @Config(path = "guild.promote.money.enabled")
    private boolean promoteMoneyEnabled;

    @Config(path = "guild.promote.money.formula")
    private String promoteMoneyFormula;

    @Config(path = "guild.promote.money.max_member_count")
    private int promoteMoneyMaxMemberCount;

    @Config(path = "guild.promote.points.enabled")
    private boolean promotePointsEnabled;

    @Config(path = "guild.promote.points.formula")
    private String promotePointFormula;

    @Config(path = "guild.promote.points.max_member_count")
    private int promotePointMaxMemberCount;

    @Config(path = "guild.tp_all.interval")
    private int tpAllInterval;

    @Config(path = "guild.tp_all.shift_count_interval")
    private int tpAllShiftCountInterval;

    @Config(path = "guild.tp_all.shift_count")
    private int tpAllShiftCount;

    @Config(path = "guild.tp_all.timeout")
    private int tpAllShiftTimeout;

    @Config(path = "guild.tp_all.cost.money")
    private int tpAllCostMoney;

    @Config(path = "guild.announcement.def")
    private List<String> announcementDef;

    @Config(path = "guild.ranking_list.formula")
    private String rankingListFormula;

    @Config(path = "guild.tp_all.allowed_send_worlds")
    private List<String> tpAllAllowedSendWorlds;

    @Config(path = "guild.tp_all.allowed_receive_worlds")
    private List<String> tpAllAllowedReceiveWorlds;

    public MainSettings() {}


    public void reset() {

    }

    public boolean isDonateMoneyEnabled() {
        return donateMoneyEnabled;
    }

    public boolean isDonatePointsEnabled() {
        return donatePointsEnabled && plugin.isPlayerPointsHooked();
    }

    public boolean isPromoteMoneyEnabled() {
        return promoteMoneyEnabled;
    }

    public boolean isPromotePointsEnabled() {
        return promotePointsEnabled && plugin.isPlayerPointsHooked();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRankingListFormula() {
        return rankingListFormula;
    }

    public int getDonateMinPoints() {
        return donateMinPoints;
    }

    public int getTpAllCostMoney() {
        return tpAllCostMoney;
    }

    public List<String> getTpAllAllowedSendWorlds() {
        return tpAllAllowedSendWorlds;
    }

    public List<String> getTpAllAllowedReceiveWorlds() {
        return tpAllAllowedReceiveWorlds;
    }

    public List<String> getAnnouncementDef() {
        return announcementDef;
    }

    public int getTpAllShiftTimeout() {
        return tpAllShiftTimeout;
    }

    public int getTpAllInterval() {
        return tpAllInterval;
    }

    public int getTpAllShiftCountInterval() {
        return tpAllShiftCountInterval;
    }

    public int getTpAllShiftCount() {
        return tpAllShiftCount;
    }

    public String getPromoteMoneyFormula() {
        return promoteMoneyFormula;
    }

    public int getPromoteMoneyMaxMemberCount() {
        return promoteMoneyMaxMemberCount;
    }

    public String getPromotePointFormula() {
        return promotePointFormula;
    }

    public int getPromotePointMaxMemberCount() {
        return promotePointMaxMemberCount;
    }

    public int getDonateMinMoney() {
        return donateMinMoney;
    }

    public String getGuildCreateNameRegex() {
        return guildCreateNameRegex;
    }

    public String getGuildCreateNameNotValidMsg() {
        return guildCreateNameNotValidMsg;
    }

    public int getGuildCreateCostMoneyAmount() {
        return guildCreateCostMoneyAmount;
    }

    public boolean isGuildCreateCostMoneyEnabled() {
        return guildCreateCostMoneyEnabled;
    }

    public int getGuildCreateCostPointsAmount() {
        return guildCreateCostPointsAmount;
    }

    public boolean isGuildCreateCostPointsEnabled() {
        return guildCreateCostPointsEnabled && plugin.isPlayerPointsHooked();
    }

    public String getGuildCreateCostItemKeyLore() {
        return guildCreateCostItemKeyLore;
    }

    public boolean isGuildCreateCostItemEnabled() {
        return guildCreateCostItemEnabled;
    }

    public String getGuildCreateColoredNoPerMsg() {
        return guildCreateColoredNoPerMsg;
    }

    public int getGuildCreateCostItemAmount() {
        return guildCreateCostItemAmount;
    }

    public String getGuildAnnouncementSplitChar() {
        return guildAnnouncementSplitChar;
    }

    public int getGuildAnnouncementMaxCount() {
        return guildAnnouncementMaxCount;
    }

    public int getGuildDefMaxMemberCount() {
        return guildDefMaxMemberCount;
    }

    public int getGuildRequestJoinTimeout() {
        return guildRequestJoinTimeout;
    }

    public int getGuildDefMaxAdminCount() {
        return guildDefMaxAdminCount;
    }

    @Config(path = "guild.gui.global.guild_info.display_name")
    private String globalGuildInfoDisplayName;
    @Config(path = "guild.gui.global.guild_info.lores")
    private List<String> globalGuildInfoLores;
    @Config(path = "guild.gui.main_gui.ranking_list.display_name")
    private String mainGUIRankingListDisplayName;
    @Config(path = "guild.gui.main_gui.ranking_list.lores")
    private List<String> mainGUIRankingListLores;
    @Config(path = "guild.gui.mine_gui.player_info.display_name")
    private String mineGUIPlayerInfoDisplayName;
    @Config(path = "guild.gui.mine_gui.player_info.lores")
    private List<String> mineGUIPlayerInfoLores;


    public String getGlobalGuildInfoDisplayName() {
        return globalGuildInfoDisplayName;
    }

    public List<String> getGlobalGuildInfoLores() {
        return globalGuildInfoLores;
    }

    public String getMainGUIRankingListDisplayName() {
        return mainGUIRankingListDisplayName;
    }

    public List<String> getMainGUIRankingListLores() {
        return mainGUIRankingListLores;
    }

    public String getMineGUIPlayerInfoDisplayName() {
        return mineGUIPlayerInfoDisplayName;
    }

    public List<String> getMineGUIPlayerInfoLores() {
        return mineGUIPlayerInfoLores;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }
}
