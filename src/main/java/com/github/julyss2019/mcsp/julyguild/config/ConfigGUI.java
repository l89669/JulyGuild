package com.github.julyss2019.mcsp.julyguild.config;

import com.github.julyss2019.mcsp.julyguild.placeholder.Placeholder;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryBuilder;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigGUI {
    public static class Builder extends InventoryBuilder {
        public Builder fromConfig(ConfigurationSection section, @org.jetbrains.annotations.Nullable Placeholder placeholder, boolean colored) {
            row(section.getInt( "row"));
            title(placeholder == null ? section.getString("title") : PlaceholderText.replacePlaceholders(section.getString("title"), placeholder));
            colored(colored);
            return this;
        }

        public Builder fromConfig(ConfigurationSection section) {
            return fromConfig(section, null, true);
        }

        public Builder item(ConfigGUIItem item, ItemListener itemListener) {
            item(item.getIndex(), item.getItemBuilder().build(), itemListener);
            return this;
        }

        public Builder item(ConfigGUIItem item) {
            item(item, null);
            return this;
        }
    }
}
