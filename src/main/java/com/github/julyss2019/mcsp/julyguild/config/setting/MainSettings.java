package com.github.julyss2019.mcsp.julyguild.config.setting;

import com.github.julyss2019.mcsp.julylibrary.config.Config;
import org.bukkit.Sound;

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

    @Config(path = "guild.donate.money.min")
    private static int donateMoneyMin;

    @Config(path = "guild.donate.points.min")
    private static int donatePointsMin;

    @Config(path = "guild.donate.input.cancel_string")
    private static String donateInputCancelString;

    @Config(path = "guild.donate.input.wait_second")
    private static int donateInputWaitSecond;

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

    @Config(path = "guild.exit.wait")
    private static int exitWait;

    @Config(path = "guild.exit.confirm_str")
    private static String exitConfirmStr;

    @Config(path = "guild.donate.points.fee")
    private static double donatePointsFee;

    @Config(path = "guild.donate.money.fee")
    private static double donateMoneyFee;

    @Config(path = "guild.create.input.cancel_str")
    private static String createInputCancelStr;

    @Config(path = "guild.create.input.wait_sec")
    private static int createInputWaitSec;

    @Config(path = "papi.non_str")
    private static String papiNonStr;

    @Config(path = "gui.default.use_gp")
    private static boolean guiDefaultUseGp;

    @Config(path = "gui.default.use_papi")
    private static boolean guiDefaultUsePapi;

    @Config(path = "gui.default.colored")
    private static boolean guiDefaultColored;

    @Config(path = "guild.create.no_duplication_name")
    private static boolean createNoDuplicationName;

    public static boolean isGuiDefaultColored() {
        return guiDefaultColored;
    }

    public static boolean isCreateNoDuplicationName() {
        return createNoDuplicationName;
    }

    public static String getPapiNonStr() {
        return papiNonStr;
    }

    public static boolean isGuiDefaultUseGp() {
        return guiDefaultUseGp;
    }

    public static boolean isGuiDefaultUsePapi() {
        return guiDefaultUsePapi;
    }

    public static String getCreateInputCancelStr() {
        return createInputCancelStr;
    }

    public static int getCreateInputWaitSec() {
        return createInputWaitSec;
    }

    public static double getDonatePointsFee() {
        return donatePointsFee;
    }

    public static double getDonateMoneyFee() {
        return donateMoneyFee;
    }

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

    public static String getDonateInputCancelString() {
        return donateInputCancelString;
    }

    public static int getDonateInputWaitSecond() {
        return donateInputWaitSecond;
    }

    public static int getDonateMoneyMin() {
        return donateMoneyMin;
    }

    public static int getDonatePointsMin() {
        return donatePointsMin;
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

    public static int getExitWait() {
        return exitWait;
    }

    public static String getExitConfirmStr() {
        return exitConfirmStr;
    }
}
