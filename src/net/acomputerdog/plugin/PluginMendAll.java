package net.acomputerdog.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMendAll extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mend")) {
            if (sender.hasPermission("mendall.mend")) {
                if (sender instanceof Player) {
                    mendPlayer((Player)sender);
                    sendMessage(sender, "Items mended successfully.");
                } else {
                    sendError(sender, "This command can only be run by a player!");
                }
            } else {
                sendError(sender, "You do not have permission!");
            }
        } else {
            sendError(sender, "Unknown command passed to SendAll!");
        }
        return true;
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.AQUA + message);
    }

    private void mendPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();
        ExperienceManager exp = new ExperienceManager(player); //use ExperienceManager to make sure EXP is updated correctly
        for (ItemStack item : inventory.getContents()) { //loop through each item in inventory
            if (item != null) {
                if (exp.getTotalExperience() == 0) { //stop when player runs out of XP
                    break;
                }

                int durabilityNeeded = item.getDurability();
                if (durabilityNeeded > 0) { //make sure item is damaged
                    if (item.containsEnchantment(Enchantment.MENDING)) { //make sure item has mending
                        int xpNeeded = durabilityNeeded / 2;
                        if (exp.getTotalExperience() < xpNeeded) { //if player does not have enough xp adjust amount of given durability and taken XP to match
                            xpNeeded = exp.getTotalExperience();
                            durabilityNeeded = xpNeeded * 2;
                        }

                        //repair item and take XP
                        item.setDurability((short) (item.getDurability() - durabilityNeeded));
                        exp.setTotalExperience(exp.getTotalExperience() - xpNeeded);
                    }
                }
            }
        }
    }
}
