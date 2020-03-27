package com.github.julyss2019.mcsp.julyguild.config.checker;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class BaseGUIChecker implements GUIChecker {
	private ConfigurationSection section;

	public BaseGUIChecker(@NotNull ConfigurationSection section) {
		this.section = section;
	}

	public Set<Violation> check() {
		Set<Violation> violations = new HashSet<>();

		ConfigurationSection itemsSection = section.getConfigurationSection("items");

		if (itemsSection == null) {
			violations.add(new BaseViolation(section, "items 项不存在"));
		} else {
			List<String> indexItems = Arrays.asList(
					"create_guild"
					, "my_guild"
					, "page_items.precious_page.not_gave"
					, "page_items.precious_page.have");

			for (String p : indexItems) {
				if (!itemsSection.contains(p)) {
					violations.add(new BaseViolation(itemsSection, p + " 项不存在"));
				} else {

				}
			}
		}

/*		if (rowValid) {
			int row = section.getInt("row");
			ConfigurationSection otherItemSection = section.getConfigurationSection("other_items");

			if (otherItemSection != null) {
				for (String key : Optional.ofNullable(otherItemSection.getKeys(false)).orElse(new HashSet<>())) {
					ConfigurationSection itemSection = otherItemSection.getConfigurationSection(key);

					if (itemSection.contains("index")) {
						violations.addAll(checkIndexItem(itemSection, rowToIndexes(row)));
					}
				}
			}


		}*/

		return violations;
	}

	public static Set<Violation> checkGUI(@NotNull ConfigurationSection section) {
		Set<Violation> violations = new HashSet<>();
		boolean rowValid = false;

		if (!section.contains("row")) {
			violations.add(new BaseViolation(section, "缺少 row 项"));
		} else {
			int row = section.getInt("row");

			if (row < 1 || row > 6) {
				violations.add(new BaseViolation(section, "row 区间为 [1-6]"));
			} else {
				rowValid = true;
			}
		}

		if (!section.contains("title")) {
			violations.add(new BaseViolation(section, "title 不存在"));
		}

		return violations;
	}



	public static Set<Integer> rowToIndexes(int row) {
		if (row < 1 || row > 6) {
			throw new IllegalArgumentException("row 不合法");
		}

		Set<Integer> result = new HashSet<>();

		for (int i = 0; i < row * 9; i++) {
			result.add(i);
		}

		return result;
	}

	public static Set<Violation> checkIcon(@NotNull ConfigurationSection section) {
		Set<Violation> violations = new HashSet<>();

		if (!section.contains("material")) {
			violations.add(new BaseViolation(section, "缺少 material 项"));
		}

		String materialName = section.getString("material");

		try {
			Material.getMaterial(materialName);
		} catch (Exception e) {
			violations.add(new MaterialViolation(section, "material 不合法: " + materialName));
		}

		return violations;
	}

	public static Set<Violation> checkIndexItem(@NotNull ConfigurationSection section, @NotNull Set<Integer> availableIndexes) {
		Set<Violation> violations = new HashSet<>();

		if (!section.contains("index")) {
			violations.add(new BaseViolation(section, "缺少 index 项"));
		} else {
			int index = section.getInt("index") - 1;

			if (!availableIndexes.contains(index)) {
				violations.add(new BaseViolation(section, "无效的 index: " + (index + 1) + ", 有效的 index 有: " + availableIndexes
						.stream()
						.map(integer -> integer + 1)
						.collect(Collectors.toSet()).toString()));
			}
		}

		if (!section.contains("icon")) {
			violations.add(new BaseViolation(section, "缺少 icon 项."));
		} else {
			violations.addAll(checkIcon(section.getConfigurationSection("icon")));
		}

		return violations;
	}


}
