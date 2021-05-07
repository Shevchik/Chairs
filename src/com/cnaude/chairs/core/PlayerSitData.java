package com.cnaude.chairs.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.cnaude.chairs.api.PlayerChairSitEvent;
import com.cnaude.chairs.api.PlayerChairUnsitEvent;

public class PlayerSitData {

	protected final Chairs plugin;

	protected final NamespacedKey sitDisabledKey;

	protected final Map<Player, SitData> sittingPlayers = new HashMap<>();
	protected final Map<Block, Player> occupiedBlocks = new HashMap<>();

	public PlayerSitData(Chairs plugin) {
		this.plugin = plugin;
		this.sitDisabledKey = new NamespacedKey(plugin, "SitDisabled");
	}

	public void disableSitting(Player player) {
		player.getPersistentDataContainer().set(sitDisabledKey, PersistentDataType.BYTE, Byte.valueOf((byte) 1));
	}

	public void enableSitting(Player player) {
		player.getPersistentDataContainer().remove(sitDisabledKey);
	}

	public boolean isSittingDisabled(Player player) {
		return player.getPersistentDataContainer().getOrDefault(sitDisabledKey, PersistentDataType.BYTE, Byte.valueOf((byte) 0)).byteValue() != 0;
	}

	public boolean isSitting(Player player) {
		SitData sitdata = sittingPlayers.get(player);
		return (sitdata != null) && sitdata.sitting;
	}

	public boolean isBlockOccupied(Block block) {
		return occupiedBlocks.containsKey(block);
	}

	public Player getPlayerOnChair(Block chair) {
		return occupiedBlocks.get(chair);
	}

	public boolean sitPlayer(final Player player,  Block blocktooccupy, Location sitlocation) {
		PlayerChairSitEvent playersitevent = new PlayerChairSitEvent(player, sitlocation.clone());
		Bukkit.getPluginManager().callEvent(playersitevent);
		if (playersitevent.isCancelled()) {
			return false;
		}
		sitlocation = playersitevent.getSitLocation().clone();
		if (plugin.getChairsConfig().msgEnabled) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getChairsConfig().msgSitEnter));
		}
		Entity chairentity = plugin.getSitUtils().spawnChairEntity(sitlocation);
		SitData sitdata = null;
		switch (plugin.getChairsConfig().sitChairEntityType) {
			case ARROW: {
				sitdata = new SitData(
					chairentity, player.getLocation(), blocktooccupy,
					Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> resitPlayer(player), 1000, 1000)
				);
				break;
			}
			case ARMOR_STAND: {
				sitdata = new SitData(chairentity, player.getLocation(), blocktooccupy, -1);
				break;
			}
		}
		player.teleport(sitlocation);
		chairentity.addPassenger(player);
		sittingPlayers.put(player, sitdata);
		occupiedBlocks.put(blocktooccupy, player);
		sitdata.sitting = true;
		return true;
	}

	public void resitPlayer(final Player player) {
		SitData sitdata = sittingPlayers.get(player);
		sitdata.sitting = false;
		Entity oldentity = sitdata.entity;
		Entity chairentity = plugin.getSitUtils().spawnChairEntity(oldentity.getLocation());
		chairentity.addPassenger(player);
		sitdata.entity = chairentity;
		oldentity.remove();
		sitdata.sitting = true;
	}

	public boolean unsitPlayer(Player player) {
		return unsitPlayer(player, true, true);
	}

	public void unsitPlayerForce(Player player, boolean teleport) {
		unsitPlayer(player, false, teleport);
	}

	private boolean unsitPlayer(final Player player, boolean canCancel, boolean teleport) {
		SitData sitdata = sittingPlayers.get(player);
		final PlayerChairUnsitEvent playerunsitevent = new PlayerChairUnsitEvent(player, sitdata.teleportBackLocation.clone(), canCancel);
		Bukkit.getPluginManager().callEvent(playerunsitevent);
		if (playerunsitevent.isCancelled() && playerunsitevent.canBeCancelled()) {
			return false;
		}
		sitdata.sitting = false;
		player.leaveVehicle();
		sitdata.entity.remove();
		player.setSneaking(false);
		occupiedBlocks.remove(sitdata.occupiedBlock);
		if (sitdata.resitTaskId != -1) {
			Bukkit.getScheduler().cancelTask(sitdata.resitTaskId);
		}
		sittingPlayers.remove(player);
		if (teleport) {
			player.teleport(playerunsitevent.getTeleportLocation().clone());
		}
		if (plugin.getChairsConfig().msgEnabled) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getChairsConfig().msgSitLeave));
		}
		return true;
	}

	protected static class SitData {

		protected final Location teleportBackLocation;
		protected final Block occupiedBlock;
		protected final int resitTaskId;

		protected boolean sitting;
		protected Entity entity;

		public SitData(Entity arrow, Location teleportLocation, Block block, int resitTaskId) {
			this.entity = arrow;
			this.teleportBackLocation = teleportLocation;
			this.occupiedBlock = block;
			this.resitTaskId = resitTaskId;
		}

	}

}
