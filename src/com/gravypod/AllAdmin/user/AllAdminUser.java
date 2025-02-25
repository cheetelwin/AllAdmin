/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.gravypod.AllAdmin.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.gravypod.AllAdmin.AllAdmin;
import com.gravypod.AllAdmin.permissions.AdminPerms;
import com.gravypod.AllAdmin.permissions.Group;
import com.gravypod.AllAdmin.permissions.PermissionData;
import com.gravypod.AllAdmin.utils.PermissionsTesting;
import com.gravypod.AllAdmin.utils.TeleportUtils;

public class AllAdminUser implements IUser {
	
	private final Player bukkitPlayer;
	
	private Location lastLocation;
	
	private final File userDataFile;
	
	private final FileConfiguration userData = new YamlConfiguration();
	
	private boolean canColourChat;
	
	private final HashMap<String, Location> userHome = new HashMap<String, Location>();
	
	public AllAdminUser(final Player _bukkitPlayer) {
	
		bukkitPlayer = _bukkitPlayer;
		
		userDataFile = new File(AllAdmin.getInstance().getDataFolder(), "userdata/" + bukkitPlayer.getName() + ".yml");
		
		final AllAdmin plugin = AllAdmin.getInstance();
		
		final BukkitScheduler schedular = plugin.getServer().getScheduler();
		
		schedular.scheduleAsyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
			
				try {
					
					if (!userDataFile.exists()) {
						userDataFile.createNewFile();
					}
					
					userData.load(userDataFile);
					
					userData.save(userDataFile);
					
					synchronized(userHome) {
						
						userHome.put("home", TeleportUtils.getLocation(userData, "homes", "home"));
						
					}
					
				} catch (final Exception e) {
				}
				
			}
			
		});
		
		schedular.scheduleAsyncRepeatingTask(plugin, new UpdateInfo(this), 10L, 1000L);
		
		updateLastLocation();
		
		canColourChat = bukkitPlayer.hasPermission("AllAdmin.chat.colour");
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
			
				AdminPerms.assignPermissions(bukkitPlayer);
				
			}
			
		}, 1);
		
	}
	
	public final String getUserName() {
	
		return bukkitPlayer.getName();
		
	}
	
	public final String getDisplayName() {
	
		return bukkitPlayer.getDisplayName();
		
	}
	
	public final Inventory getInventory() {
	
		return bukkitPlayer.getInventory();
		
	}
	
	public final boolean doesHaveItem(final Material m) {
	
		return getInventory().contains(m);
		
	}
	
	public final boolean doesHaveItem(final ItemStack m) {
	
		return getInventory().contains(m);
		
	}
	
	public final boolean doesHaveItem(final int m) {
	
		return getInventory().contains(m);
	}
	
	@Override
	public void sendCommandFaliure(final String command, final String why) {
	
		bukkitPlayer.sendMessage(AllAdmin.getMessages(why));
		
	}
	
	@Override
	public boolean canUseCommand(final String command) {
	
		return PermissionsTesting.canUseCommand(bukkitPlayer, command);
		
	}
	
	@Override
	public final boolean hasPermission(final String permission) {
	
		return PermissionsTesting.internalHasPermissions(this, permission);
		
	}
	
	/**
	 * 
	 * To be removed and replaced with internal methods
	 * 
	 * @return
	 * 
	 */
	public final Player getBukkitPlayer() {
	
		return bukkitPlayer;
	}
	
	public final Location getLastLocation() {
	
		return lastLocation;
	}
	
	/**
	 * 
	 * Set data into the users data file
	 * 
	 * @param path
	 *            - Yaml path
	 * @param data
	 *            - Yaml data
	 * 
	 */
	public void setData(final String path, final Object data) {
	
		AllAdmin.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(AllAdmin.getInstance(), new Runnable() {
			
			@Override
			public void run() {
			
				synchronized(userData) {
					userData.set(path, data);
				}
				
			}
			
		});
		
	}
	
	/**
	 * 
	 * Set a home location.
	 * 
	 * @param loc
	 *            - Location of the home.
	 * @param homeName
	 *            - Name to save under [ Or null for default ]
	 * 
	 */
	public void setHome(final Location loc, final String homeName) {
	
		final String name = homeName != null ? homeName : "home";
		
		TeleportUtils.setLocation(userData, "homes", name, bukkitPlayer.getLocation());
		
		try {
			
			userData.save(userDataFile);
			
		} catch (final IOException e) {
		}
		
	}
	
	/**
	 * 
	 * Get the users home location
	 * 
	 * @param name
	 *            - null or the name of the home. [Null is default home]
	 * @return
	 * 
	 */
	public final Location getHome(final String name) {
	
		return TeleportUtils.getLocation(userData, "homes", name != null ? name : "home");
		
	}
	
	/**
	 * 
	 * Update their last location.
	 * 
	 */
	public void updateLastLocation() {
	
		synchronized(bukkitPlayer) {
			
			lastLocation = bukkitPlayer.getLocation();
			
		}
		
	}
	
	@Override
	public void sendMessage(final String message) {
	
		bukkitPlayer.sendMessage(message);
		
	}
	
	@Override
	public void saveData() {
	
		synchronized(userData) {
			
			try {
				
				userData.set("lastLocation.x", lastLocation.getBlockX());
				userData.set("lastLocation.y", lastLocation.getBlockY());
				userData.set("lastLocation.z", lastLocation.getBlockZ());
				
				userData.save(userDataFile);
				
			} catch (final Exception e) {
			}
			
		}
		
	}
	
	@Override
	public boolean isPlayer() {
	
		return true;
		
	}
	
	@Override
	public boolean canColourChat() {
	
		synchronized(bukkitPlayer) {
			
			canColourChat = bukkitPlayer.hasPermission("AllAdmin.chat.colour");
			
			return canColourChat;
			
		}
		
	}
	
	/**
	 * 
	 * Set the group the player is in.
	 * 
	 * @param groupName
	 * 
	 */
	public void setGroup(final String groupName) {
	
		userData.set("permissions.group", groupName);
		try {
			userData.save(userDataFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Get the group this player is in.
	 * 
	 * @return
	 * 
	 */
	public Group getGroup() {
	
		if (!userData.contains("permissions.group")) {
			userData.set("permissions.group", PermissionData.getDefaultGroup().getName());
		}
		
		String group = userData.getString("permissioins.group");
		
		if (!PermissionData.getGroups().containsKey(group)) {
			group = PermissionData.getDefaultGroup().getName();
		}
		
		return PermissionData.getGroups().get(group);
		
	}
	
}
