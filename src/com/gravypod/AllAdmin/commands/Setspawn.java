package com.gravypod.AllAdmin.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gravypod.AllAdmin.AllAdmin;
import com.gravypod.AllAdmin.CommandHandling.CommandHandler;
import com.gravypod.AllAdmin.CommandHandling.CommandUtil;
import com.gravypod.AllAdmin.user.AllAdminUser;

public class Setspawn extends CommandUtil {
	
	@Override
	public void registerSelf(final AllAdmin plugin, final CommandHandler commandHandler) {
	
		plugin.getCommand("setspawn").setExecutor(commandHandler);
		
	}
	
	@Override
	public final boolean doCommand(final CommandSender sender, final Command command, final String cmd, final String[] args) {
	
		if (!canUseCommand(sender, cmd, true, true)) {
			return true;
		}
		
		final AllAdminUser user = (AllAdminUser) AllAdmin.getUser(sender.getName());
		
		final Player player = user.getBukkitPlayer();
		final Location playerLocation = player.getLocation();
		
		player.getWorld().setSpawnLocation(playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ());
		
		user.sendMessage(AllAdmin.getMessages("spawnSet"));
		
		return true;
		
	}
	
	@Override
	public final String commandHelp() {
	
		return "/setspawn sets the spawn to where you are standing";
	}
	
}
