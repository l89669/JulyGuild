package com.github.julyss2019.mcsp.julyguild.config.setting;

import com.github.julyss2019.mcsp.julylibrary.config.Config;

import java.util.List;

public class MainSettings {
    @Config(path = "metrics_enabled")
    private static boolean metricsEnabled;

    @Config(path = "guild.create.name_regex")
    private static String createNameRegex;

    @Config(path = "guild.create.cost.money.amount")
    private static int createCostMoneyAmount;

    @Config(path = "guild.create.cost.points.amount")
    private static int createCostPointsAmount;

    @Config(path = "guild.create.cost.item.key_lore")
    private static String createCostItemKeyLore;

    @Config(path = "guild.create.cost.item.amount")
    private static int createCostItemAmount;

    @Config(path = "guild.announcement.split_char")
    private static String announcementSplitChar;

    @Config(path = "guild.announcement.max_count")
    private static int announcementMaxCount;

    @Config(path = "guild.default_max_member_count")
    private static int defaultMaxMemberCount;

    @Config(path = "guild.request.join.timeout")
    private static int requestJoinTimeout;

    @Config(path = "guild.default_max_admin_count")
    private static int defaultMaxAdminCount;

    @Config(path = "guild.donate.money.enabled")
    private static boolean donateMoneyEnabled;

    @Config(path = "guild.donate.points.enabled")
    private static boolean donatePointsEnabled;

    @Config(path = "guild.donate.money.min")
    private static int donateMinMoney;

    @Config(path = "guild.donate.points.min")
    private static int donateMinPoints;


    @Config(path = "guild.upgrade.money.formula")
    private static String upgradeMoneyFormula;

    @Config(path = "guild.upgrade.money.max_member_count")
    private static int upgradeMoneyMaxMemberCount;


    @Config(path = "guild.upgrade.points.formula")
    private static String upgradePointFormula;

    @Config(path = "guild.upgrade.points.max_member_count")
    private static int upgradePointsMaxMemberCount;

    @Config(path = "guild.tp_all.interval")
    private static int tpAllInterval;

    @Config(path = "guild.tp_all.shift_count_interval")
    private static int tpAllShiftCountInterval;

    @Config(path = "guild.tp_all.shift_count")
    private static int tpAllShiftCount;

    @Config(path = "guild.tp_all.timeout")
    private static int tpAllShiftTimeout;

    @Config(path = "guild.tp_all.cost.money")
    private static int tpAllCostMoney;

    @Config(path = "guild.announcement.default")
    private static List<String> announcementDefault;

    @Config(path = "guild.ranking_list.formula")
    private static String rankingListFormula;

    @Config(path = "guild.tp_all.allowed_send_worlds")
    private static List<String> tpAllAllowedSendWorlds;

    @Config(path = "guild.tp_all.allowed_receive_worlds")
    private static List<String> tpAllAllowedReceiveWorlds;

    @Config(path = "guild.default_icon.material")
    private static String defaultIconMaterial;

    @Config(path = "guild.default_icon.data")
    private static short defaultIconData;

    @Config(path = "guild.default_icon.first_lore")
    private static String defaultIconFirstLore;

    @Config(path = "guild.dismiss.wait")
    private static int dismissWait;

    @Config(path = "guild.dismiss.confirm_str")
    private static String dismissConfirmStr;

    public static int getDismissWait() {
        return dismissWait;
    }

    public static String getDismissConfirmStr() {
        return dismissConfirmStr;
    }

    public static String getDefaultIconMaterial() {
        return defaultIconMaterial;
    }

    public static short getDefaultIconData() {
        return defaultIconData;
    }

    public static String getDefaultIconFirstLore() {
        return defaultIconFirstLore;
    }

    public static boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public static String getCreateNameRegex() {
        return createNameRegex;
    }

    public static int getCreateCostMoneyAmount() {
        return createCostMoneyAmount;
    }

    public static int getCreateCostPointsAmount() {
        return createCostPointsAmount;
    }

    public static String getCreateCostItemKeyLore() {
        return createCostItemKeyLore;
    }

    public static int getCreateCostItemAmount() {
        return createCostItemAmount;
    }

    public static String getAnnouncementSplitChar() {
        return announcementSplitChar;
    }

    public static int getAnnouncementMaxCount() {
        return announcementMaxCount;
    }

    public static int getDefaultMaxMemberCount() {
        return defaultMaxMemberCount;
    }

    public static int getRequestJoinTimeout() {
        return requestJoinTimeout;
    }

    public static int getDefaultMaxAdminCount() {
        return defaultMaxAdminCount;
    }

    public static boolean isDonateMoneyEnabled() {
        return donateMoneyEnabled;
    }

    public static boolean isDonatePointsEnabled() {
        return donatePointsEnabled;
    }

    public static int getDonateMinMoney() {
        return donateMinMoney;
    }

    public static int getDonateMinPoints() {
        return donateMinPoints;
    }

    public static String getUpgradeMoneyFormula() {
        return upgradeMoneyFormula;
    }

    public static int getUpgradeMoneyMaxMemberCount() {
        return upgradeMoneyMaxMemberCount;
    }

    public static String getUpgradePointFormula() {
        return upgradePointFormula;
    }

    public static int getUpgradePointsMaxMemberCount() {
        return upgradePointsMaxMemberCount;
    }

    public static int getTpAllInterval() {
        return tpAllInterval;
    }

    public static int getTpAllShiftCountInterval() {
        return tpAllShiftCountInterval;
    }

    public static int getTpAllShiftCount() {
        return tpAllShiftCount;
    }

    public static int getTpAllShiftTimeout() {
        return tpAllShiftTimeout;
    }

    public static int getTpAllCostMoney() {
        return tpAllCostMoney;
    }

    public static List<String> getAnnouncementDefault() {
        return announcementDefault;
    }

    public static String getRankingListFormula() {
        return rankingListFormula;
    }

    public static List<String> getTpAllAllowedSendWorlds() {
        return tpAllAllowedSendWorlds;
    }

    public static List<String> getTpAllAllowedReceiveWorlds() {
        return tpAllAllowedReceiveWorlds;
    }
}
