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

    @Config(path = "guild.request.join.timeout")
    private static int requestJoinTimeout;

    @Config(path = "guild.default_max_member_count")
    private static int defaultMaxMemberCount;

    @Config(path = "guild.announcement.default")
    private static List<String> announcementDefault;

    @Config(path = "guild.ranking_list.formula")
    private static String rankingListFormula;

    @Config(path = "guild.icon.default.material")
    private static String iconDefaultMaterial;

    @Config(path = "guild.icon.default.durability")
    private static short iconDefaultDurability;

    @Config(path = "guild.icon.default.first_lore")
    private static String iconDefaultFirstLore;

    @Config(path = "guild.dismiss.wait")
    private static int dismissWait;

    @Config(path = "guild.dismiss.confirm_str")
    private static String dismissConfirmStr;

    @Config(path = "guild.exit.wait")
    private static int exitWait;

    @Config(path = "guild.exit.confirm_str")
    private static String exitConfirmStr;

    @Config(path = "guild.create.input.cancel_str")
    private static String createInputCancelStr;

    @Config(path = "guild.create.input.wait_sec")
    private static int createInputWaitSec;

    @Config(path = "guild.papi.non_str")
    private static String papiNonStr;

    @Config(path = "guild.create.no_duplication_name")
    private static boolean createNoDuplicationName;

    @Config(path = "guild.member_damage.disabled_notice_interval")
    private static int memberDamageDisableNoticeInterval;

    @Config(path = "guild.gui.default.colored")
    private static boolean guiDefaultColored;

    @Config(path = "guild.gui.default.use_papi")
    private static boolean guiDefaultUsePapi;

    @Config(path = "guild.gui.default.hide_all_flags")
    private static boolean guiDefaultHideAllFlags;

    @Config(path = "guild.shop.launcher")
    private static String shopLauncher;

    public static boolean isGuiDefaultHideAllFlags() {
        return guiDefaultHideAllFlags;
    }

    public static String getShopLauncher() {
        return shopLauncher;
    }

    public static boolean isGuiDefaultUsePapi() {
        return guiDefaultUsePapi;
    }

    public static boolean isGuiDefaultColored() {
        return guiDefaultColored;
    }

    public static int getMemberDamageDisableNoticeInterval() {
        return memberDamageDisableNoticeInterval;
    }

    public static boolean isCreateNoDuplicationName() {
        return createNoDuplicationName;
    }

    public static String getPapiNonStr() {
        return papiNonStr;
    }

    public static String getCreateInputCancelStr() {
        return createInputCancelStr;
    }

    public static int getCreateInputWaitSec() {
        return createInputWaitSec;
    }

    public static int getDismissWait() {
        return dismissWait;
    }

    public static String getDismissConfirmStr() {
        return dismissConfirmStr;
    }

    public static String getIconDefaultMaterial() {
        return iconDefaultMaterial;
    }

    public static short getIconDefaultDurability() {
        return iconDefaultDurability;
    }

    public static String getIconDefaultFirstLore() {
        return iconDefaultFirstLore;
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

    public static List<String> getAnnouncementDefault() {
        return announcementDefault;
    }

    public static String getRankingListFormula() {
        return rankingListFormula;
    }

    public static int getExitWait() {
        return exitWait;
    }

    public static String getExitConfirmStr() {
        return exitConfirmStr;
    }
}
