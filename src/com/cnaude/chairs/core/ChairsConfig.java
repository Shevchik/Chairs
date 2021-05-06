package com.cnaude.chairs.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChairsConfig {

	protected final Chairs plugin;

	protected ChairsConfig(Chairs plugin) {
		this.plugin = plugin;
	}

	protected static final String sitConfigSectionPath = "sit-config";
	protected static final String sitConfigDisabledWorldsPath = "disabled-worlds";
	protected static final String sitConfigMaxDistancePath = "max-distance";
	protected static final String sitConfigRequireEmptyHandPath = "require-empty-hand";
	protected static final String sitConfigChairEntityType = "chair-entity-type";

	protected static final String sitConfigStairsSectionPath = "stairs";
	protected static final String sitConfigStairsEnabledPath = "enabled";
	protected static final String sitConfigStairsRotatePath = "rotate";
	protected static final String sitConfigStairsMaxWidthPath = "max-width";
	protected static final String sitConfigStairsSpecialEndPath = "special-end";
	protected static final String sitConfigStairsSpecialEndSignPath = "sign";
	protected static final String sitConfigStairsSpecialEndCornerStairsPath = "corner-stairs";
	protected static final String sitConfigStairsHeight = "height";

	protected static final String sitConfigAdditionalChairsPath = "additional-blocks";

	protected static final String sitEffectsSectionPath = "sit-effects";

	protected static final String sitEffectsHealingSectionPath = "healing";
	protected static final String sitEffectsHealingEnabledPath = "enabled";
	protected static final String sitEffectsHealingMaxPercentPath = "max-percent";
	protected static final String sitEffectsHealingIntervalPath = "interval";
	protected static final String sitEffectsHealingAmountPath = "amount";

	protected static final String sitEffectsItempickupPath = "itempickup";
	protected static final String sitEffectsItempickupEnabledPath = "enabled";

	protected static final String sitRestrictionsSectionPath = "sit-restrictions";
	protected static final String sitRestricitonsCommandsSectionPath = "commands";
	protected static final String sitRestrictionsCommandsBlockAllPath = "all";
	protected static final String sitRestrictionsCommandsBlockListPath = "list";
	protected static final String sitRestrictionsCommandsAllowListPath = "allowed";

	protected static final String msgSectionPath = "messages";
	protected static final String msgEnabledPath = "enabled";
	protected static final String msgSitSectionPath = "sit";
	protected static final String msgSitEnterPath = "enter";
	protected static final String msgSitLeavePath = "leave";
	protected static final String msgSitEnabledPath = "enabled";
	protected static final String msgSitDisabledPath = "disabled";
	protected static final String msgSitCommandRestrictedPath = "commandrestricted";


	public final Set<String> sitDisabledWorlds = new HashSet<>();
	public boolean sitRequireEmptyHand = false;
	public double sitMaxDistance = 2;
	public ChairEntityType sitChairEntityType = ChairEntityType.ARROW;

	public boolean stairsEnabled = true;
	public boolean stairsAutoRotate = true;
	public int stairsMaxWidth = 16;
	public boolean stairsSpecialEndEnabled = false;
	public boolean stairsSpecialEndSign = true;
	public boolean stairsSpecialEndCornerStairs = true;
	public double stairsHeight = 0.5D;

	public final Map<Material, Double> additionalChairs = new EnumMap<>(Material.class);

	public boolean effectsHealEnabled = false;
	public int effectsHealMaxHealth = 100;
	public int effectsHealInterval = 20;
	public int effectsHealHealthPerInterval = 1;
	public boolean effectsItemPickupEnabled = false;

	public boolean restrictionsDisableAllCommands = false;
	public final Set<String> restrictionsDisabledCommands = new HashSet<>();
	public final Set<String> restrictionsAllowedCommands = new HashSet<>();

	public boolean msgEnabled = true;
	public String msgSitEnter = "&7You are now sitting.";
	public String msgSitLeave = "&7You are no longer sitting.";
	public String msgSitDisabled = "&7You have disabled chairs for yourself!";
	public String msgSitEnabled = "&7You have enabled chairs for yourself!";
	public String msgSitCommandRestricted = "&7You can't issue this command while sitting";

	public void reloadConfig() {
		File file = new File(plugin.getDataFolder(), "config.yml");

		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			ConfigurationSection sitConfigSection = config.getConfigurationSection(sitConfigSectionPath);
			if (sitConfigSection != null) {
				sitDisabledWorlds.clear();
				sitDisabledWorlds.addAll(sitConfigSection.getStringList(sitConfigDisabledWorldsPath));
				sitRequireEmptyHand = sitConfigSection.getBoolean(sitConfigRequireEmptyHandPath, sitRequireEmptyHand);
				sitMaxDistance = sitConfigSection.getDouble(sitConfigMaxDistancePath, sitMaxDistance);
				sitChairEntityType = ChairEntityType.fromString(sitConfigSection.getString(sitConfigChairEntityType, sitChairEntityType.name()));

				ConfigurationSection sitConfigStairsSection = sitConfigSection.getConfigurationSection(sitConfigStairsSectionPath);
				if (sitConfigStairsSection != null) {
					stairsEnabled = sitConfigStairsSection.getBoolean(sitConfigStairsEnabledPath, stairsEnabled);
					stairsAutoRotate = sitConfigStairsSection.getBoolean(sitConfigStairsRotatePath, stairsAutoRotate);
					stairsMaxWidth = sitConfigStairsSection.getInt(sitConfigStairsMaxWidthPath, stairsMaxWidth);
					ConfigurationSection sitConfigStairsSpecialEndSection = sitConfigStairsSection.getConfigurationSection(sitConfigStairsSpecialEndPath);
					if (sitConfigStairsSpecialEndSection != null) {
						stairsSpecialEndSign = sitConfigStairsSpecialEndSection.getBoolean(sitConfigStairsSpecialEndSignPath, stairsSpecialEndSign);
						stairsSpecialEndCornerStairs = sitConfigStairsSpecialEndSection.getBoolean(sitConfigStairsSpecialEndCornerStairsPath, stairsSpecialEndCornerStairs);
						stairsSpecialEndEnabled = stairsSpecialEndSign || stairsSpecialEndCornerStairs;
					}
					stairsHeight = sitConfigStairsSection.getDouble(sitConfigStairsHeight, stairsHeight);
				}

				ConfigurationSection sitConfigAdditionalBlocksSection = sitConfigSection.getConfigurationSection(sitConfigAdditionalChairsPath);
				if (sitConfigAdditionalBlocksSection != null) {
					for (String materialName : sitConfigAdditionalBlocksSection.getKeys(false)) {
						Material material = Material.getMaterial(materialName);
						if (material != null) {
							additionalChairs.put(material, sitConfigAdditionalBlocksSection.getDouble(materialName));
						}
					}
				}
			}

			ConfigurationSection sitEffectsSection = config.getConfigurationSection(sitEffectsSectionPath);
			if (sitEffectsSection != null) {
				ConfigurationSection sitEffectsHealSection = sitEffectsSection.getConfigurationSection(sitEffectsHealingSectionPath);
				if (sitEffectsHealSection != null) {
					effectsHealEnabled = sitEffectsHealSection.getBoolean(sitEffectsHealingEnabledPath, effectsHealEnabled);
					effectsHealMaxHealth = sitEffectsHealSection.getInt(sitEffectsHealingMaxPercentPath, effectsHealMaxHealth);
					effectsHealInterval = sitEffectsHealSection.getInt(sitEffectsHealingIntervalPath, effectsHealInterval);
					effectsHealHealthPerInterval = sitEffectsHealSection.getInt(sitEffectsHealingAmountPath, effectsHealHealthPerInterval);
				}

				ConfigurationSection sitEffectsItempickupSection = sitEffectsSection.getConfigurationSection(sitEffectsItempickupPath);
				if (sitEffectsItempickupSection != null) {
					effectsItemPickupEnabled = sitEffectsItempickupSection.getBoolean(sitEffectsItempickupEnabledPath, effectsItemPickupEnabled);
				}
			}

			ConfigurationSection sitRestirctionsSection = config.getConfigurationSection(sitRestrictionsSectionPath);
			if (sitRestirctionsSection != null) {
				ConfigurationSection sitRestrictionsCommandsSection = sitRestirctionsSection.getConfigurationSection(sitRestricitonsCommandsSectionPath);
				if (sitRestrictionsCommandsSection != null) {
					restrictionsDisableAllCommands = sitRestrictionsCommandsSection.getBoolean(sitRestrictionsCommandsBlockAllPath, restrictionsDisableAllCommands);
					restrictionsDisabledCommands.clear();
					restrictionsDisabledCommands.addAll(sitRestrictionsCommandsSection.getStringList(sitRestrictionsCommandsBlockListPath));
					restrictionsAllowedCommands.clear();
					restrictionsAllowedCommands.addAll(sitRestrictionsCommandsSection.getStringList(sitRestrictionsCommandsAllowListPath));
				}
			}

			ConfigurationSection msgSection = config.getConfigurationSection(msgSectionPath);
			if (msgSection != null) {
				msgEnabled = msgSection.getBoolean(msgEnabledPath, msgEnabled);
				ConfigurationSection msgSitSection = msgSection.getConfigurationSection(msgSitSectionPath);
				if (msgSitSection != null) {
					msgSitEnter = msgSitSection.getString(msgSitEnterPath, msgSitEnter);
					msgSitLeave = msgSitSection.getString(msgSitLeavePath, msgSitLeave);
					msgSitEnabled = msgSitSection.getString(msgSitEnabledPath, msgSitEnabled);
					msgSitDisabled = msgSitSection.getString(msgSitDisabledPath, msgSitDisabled);
					msgSitCommandRestricted = msgSitSection.getString(msgSitCommandRestrictedPath, msgSitCommandRestricted);
				}
			}
		}

		{
			FileConfiguration config = new YamlConfiguration();

			ConfigurationSection sitConfigSection = config.createSection(sitConfigSectionPath);
			{
				sitConfigSection.set(sitConfigDisabledWorldsPath, new ArrayList<>(sitDisabledWorlds));
				sitConfigSection.set(sitConfigRequireEmptyHandPath, sitRequireEmptyHand);
				sitConfigSection.set(sitConfigMaxDistancePath, sitMaxDistance);
				sitConfigSection.set(sitConfigChairEntityType, sitChairEntityType.name());

				ConfigurationSection sitConfigStairsSection = sitConfigSection.createSection(sitConfigStairsSectionPath);
				{
					sitConfigStairsSection.set(sitConfigStairsEnabledPath, stairsEnabled);
					sitConfigStairsSection.set(sitConfigStairsRotatePath, stairsAutoRotate);
					sitConfigStairsSection.set(sitConfigStairsMaxWidthPath, stairsMaxWidth);
					ConfigurationSection sitConfigStairsSpecialEndSection = sitConfigStairsSection.createSection(sitConfigStairsSpecialEndPath);
					{
						sitConfigStairsSpecialEndSection.set(sitConfigStairsSpecialEndSignPath, stairsSpecialEndSign);
						sitConfigStairsSpecialEndSection.set(sitConfigStairsSpecialEndCornerStairsPath, stairsSpecialEndCornerStairs);
					}
					sitConfigStairsSection.set(sitConfigStairsHeight, stairsHeight);
				}

				ConfigurationSection sitConfigAdditionalBlocksSection = sitConfigSection.createSection(sitConfigAdditionalChairsPath);
				{
					for (Entry<Material, Double> entry : additionalChairs.entrySet()) {
						sitConfigAdditionalBlocksSection.set(entry.getKey().toString(), entry.getValue());
					}
				}
			}

			ConfigurationSection sitEffectsSection = config.createSection(sitEffectsSectionPath);
			{
				ConfigurationSection sitEffectsHealSection = sitEffectsSection.createSection(sitEffectsHealingSectionPath);
				{
					sitEffectsHealSection.set(sitEffectsHealingEnabledPath, effectsHealEnabled);
					sitEffectsHealSection.set(sitEffectsHealingMaxPercentPath, effectsHealMaxHealth);
					sitEffectsHealSection.set(sitEffectsHealingIntervalPath, effectsHealInterval);
					sitEffectsHealSection.set(sitEffectsHealingAmountPath, effectsHealHealthPerInterval);
				}

				ConfigurationSection sitEffectsItempickupSection = sitEffectsSection.createSection(sitEffectsItempickupPath);
				{
					sitEffectsItempickupSection.set(sitEffectsItempickupEnabledPath, effectsItemPickupEnabled);
				}
			}

			ConfigurationSection sitRestirctionsSection = config.createSection(sitRestrictionsSectionPath);
			{
				ConfigurationSection sitRestrictionsCommandsSection = sitRestirctionsSection.createSection(sitRestricitonsCommandsSectionPath);
				{
					sitRestrictionsCommandsSection.set(sitRestrictionsCommandsBlockAllPath, restrictionsDisableAllCommands);
					sitRestrictionsCommandsSection.set(sitRestrictionsCommandsBlockListPath, new ArrayList<>(restrictionsDisabledCommands));
					sitRestrictionsCommandsSection.set(sitRestrictionsCommandsAllowListPath, new ArrayList<>(restrictionsAllowedCommands));
				}
			}

			ConfigurationSection msgSection = config.createSection(msgSectionPath);
			{
				msgSection.set(msgEnabledPath, msgEnabled);
				ConfigurationSection msgSitSection = msgSection.createSection(msgSitSectionPath);
				{
					msgSitSection.set(msgSitEnterPath, msgSitEnter);
					msgSitSection.set(msgSitLeavePath, msgSitLeave);
					msgSitSection.set(msgSitEnabledPath, msgSitEnabled);
					msgSitSection.set(msgSitDisabledPath, msgSitDisabled);
					msgSitSection.set(msgSitCommandRestrictedPath, msgSitCommandRestricted);
				}
			}

			try {config.save(file);} catch (IOException e) {}
		}
	}

	public static enum ChairEntityType {
		ARROW, ARMOR_STAND;

		public static ChairEntityType fromString(String string) {
			try {
				return ChairEntityType.valueOf(string);
			} catch (IllegalArgumentException e) {
				return ChairEntityType.ARMOR_STAND;
			}
		}
	}

}
