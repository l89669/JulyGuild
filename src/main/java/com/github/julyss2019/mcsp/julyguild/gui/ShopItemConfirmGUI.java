package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julyguild.shop.reward.Reward;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopItemConfirmGUI extends BaseConfirmGUI {
    private GuildMember guildMember;
    private Guild guild;
    private Reward reward;
    private String priceFormula;
    private ConfigurationSection section;

    public ShopItemConfirmGUI(@NotNull GUI lastGUI, @NotNull GuildMember guildMember, @NotNull ConfigurationSection sellSection, @NotNull Reward reward) {
        this(lastGUI, guildMember, sellSection, reward, null);
    }

    protected ShopItemConfirmGUI(@NotNull GUI lastGUI, @NotNull GuildMember guildMember, @NotNull ConfigurationSection sellSection, @NotNull Reward reward, @Nullable PlaceholderContainer placeholderContainer) {
        super(lastGUI, guildMember.getGuildPlayer(), sellSection.getConfigurationSection("ConfirmGUI"), placeholderContainer);

        this.section = sellSection;
        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();
        this.reward = reward;
        this.priceFormula = sellSection.getString("price");

    }

    @Override
    public boolean canUse() {
        double price = Util.calculate(PlaceholderText.replacePlaceholders(priceFormula, new PlaceholderContainer().addGuildPlaceholders(guild)));

        return lastGUI.canUse() && price == (double) getPlaceholderContainer().getPlaceholder("price").getValue() && guild.getGuildBank().has(GuildBank.BalanceType.GMONEY, price);
    }

    @Override
    public void onConfirm() {
        double cost = Util.calculate(PlaceholderText.replacePlaceholders(priceFormula, new PlaceholderContainer().addGuildPlaceholders(guild)));

        guild.getGuildBank().withdraw(GuildBank.BalanceType.GMONEY, cost);
        reward.execute(guildMember);
        Util.sendMsg(getBukkitPlayer(), PlaceholderText.replacePlaceholders(section.getString("success_message"), new PlaceholderContainer().add("price", Util.SIMPLE_DECIMAL_FORMAT.format(cost))));
        back(40L);
    }

    @Override
    public void onCancel() {
        back();
    }

    @Override
    public GUIType getType() {
        return GUIType.SHOP_CONFIRM;
    }
}
