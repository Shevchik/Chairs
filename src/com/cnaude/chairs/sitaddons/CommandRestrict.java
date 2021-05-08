package com.cnaude.chairs.sitaddons;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.cnaude.chairs.core.Chairs;
import com.cnaude.chairs.core.ChairsConfig;
import com.cnaude.chairs.core.ChairsConfig.ListMode;
import com.cnaude.chairs.core.PlayerSitData;

public class CommandRestrict implements Listener {

	protected final Chairs plugin;
	protected final ChairsConfig config;
	protected final PlayerSitData sitdata;

	public CommandRestrict(Chairs plugin) {
		this.plugin = plugin;
		this.config = plugin.getChairsConfig();
		this.sitdata = plugin.getPlayerSitData();
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String playercommand = event.getMessage().toLowerCase();
		if (plugin.getPlayerSitData().isSitting(player)) {
			for (String command : config.restrictionsCommandsList) {
				if (playercommand.startsWith(command) != config.restrictionsCommandsMode.equals(ListMode.allow)) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.msgSitCommandRestricted));
				}
			}
		}
	}

}
