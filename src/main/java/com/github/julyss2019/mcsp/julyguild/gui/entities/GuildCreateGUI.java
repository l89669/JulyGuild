package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePayGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import com.github.julyss2019.mcsp.julylibrary.message.Title;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildCreateGUI extends BasePayGUI {
    private final String guildName;
    private final Player bukkitPlayer = getBukkitPlayer();
    private final JulyGuild plugin = JulyGuild.getInstance();
    private final GuildManager guildManager = plugin.getGuildManager();
    private final ConfigurationSection thisLangSection = plugin.getLangYaml().getConfigurationSection("GuildCreateGUI");

    protected GuildCreateGUI(@Nullable GUI lastGUI, @NotNull GuildPlayer guildPlayer, @NotNull String guildName) {
        super(lastGUI, guildPlayer, JulyGuild.getInstance().getGUIYaml("GuildCreateGUI"), new PlaceholderContainer()
                .add("name", guildName)
                .add("points_cost", MainSettings.getCreateCostPointsAmount())
                .add("money_cost", MainSettings.getCreateCostMoneyAmount()));

        this.guildName = guildName;
    }

    /**
     * 不在公会就允许使用
     * @return
     */
    @Override
    public boolean canUse() {
        return !guildPlayer.isInGuild();
    }

    @Override
    public void onMoneyPay() {
        new BaseConfirmGUI(this, guildPlayer, plugin.getGUIYaml("GuildCreateGUI").getConfigurationSection("items.money.ConfirmGUI"), super.getPlaceholderContainer()) {
            @Override
            public boolean canUse() {
                return GuildCreateGUI.this.canUse();
            }

            @Override
            public void onConfirm() {
                close();

                if (!vaultEconomy.has(bukkitPlayer, MainSettings.getCreateCostMoneyAmount())) {
                    Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("money.not_enough"), new PlaceholderContainer()
                            .add("need", MainSettings.getCreateCostMoneyAmount() - vaultEconomy.getBalance(bukkitPlayer))));
                    return;
                }

                vaultEconomy.withdraw(bukkitPlayer, MainSettings.getCreateCostMoneyAmount());
                createGuild(guildPlayer, guildName);
            }

            @Override
            public void onCancel() {
                back();
            }
        }.open();
    }

    @Override
    public void onPointsPay() {
        new BaseConfirmGUI(this, guildPlayer, plugin.getGUIYaml("GuildCreateGUI").getConfigurationSection("items.points.ConfirmGUI"), super.getPlaceholderContainer()) {
            @Override
            public boolean canUse() {
                return GuildCreateGUI.this.canUse();
            }

            @Override
            public void onConfirm() {
                close();

                if (!playerPointsEconomy.has(bukkitPlayer, MainSettings.getCreateCostPointsAmount())) {
                    Util.sendColoredMessage(bukkitPlayer, PlaceholderText.replacePlaceholders(thisLangSection.getString("points.not_enough"), new PlaceholderContainer()
                            .add("need", String.valueOf(MainSettings.getCreateCostPointsAmount() - playerPointsEconomy.getBalance(bukkitPlayer)))));
                    return;
                }

                playerPointsEconomy.withdraw(bukkitPlayer, MainSettings.getCreateCostPointsAmount());
                createGuild(guildPlayer, guildName);
            }

            @Override
            public void onCancel() {
                back();
            }
        }.open();
    }

    private void createGuild(GuildPlayer guildPlayer, String guildName) {
        Player bukkitPlayer = guildPlayer.getBukkitPlayer();

        guildManager.createGuild(guildPlayer, guildName);
        JulyMessage.broadcastColoredMessage(PlaceholderText.replacePlaceholders(thisLangSection.getString("success.broadcast"), new PlaceholderContainer()
                .add("player", bukkitPlayer.getName())
                .add("guild", guildName)));

        if (JulyMessage.canUseTitle()) {
            JulyMessage.sendTitle(bukkitPlayer, new Title.Builder().text(thisLangSection.getString("success.self_title")).colored().build());
        } else {
            JulyMessage.sendColoredMessage(bukkitPlayer, thisLangSection.getString("success.self_title"));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                new MainGUI(guildPlayer).open();
            }
        }.runTaskLater(plugin, 20L * 3L);
    }
}
