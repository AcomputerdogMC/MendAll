package net.acomputerdog.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin main class
 */
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

    /**
     * Sends a chat message formatted as an error
     */
    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    /**
     * Sends a chat message formatted as a normal message
     */
    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.AQUA + message);
    }

    /**
     * Mends all of a player's items using their available EXP
     */
    private void mendPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();
        PlayerExp exp = new PlayerExp(player); //use PlayerExp to make sure EXP is updated correctly

        for (ItemStack item : inventory.getContents()) { //loop through each item in inventory
            if (item != null) {
                if (exp.getTotalExperience() == 0) { //stop when player runs out of XP
                    break;
                }

                int maxDamage = item.getType().getMaxDurability();
                int itemDamage = getDamageFor(item);

                //make sure item can be damaged, is damaged, and has mending
                if (maxDamage > 0 && itemDamage > 0 && item.containsEnchantment(Enchantment.MENDING)) {
                    int damageToHeal = itemDamage;
                    int xpNeeded = itemDamage / 2;

                    int totalExp = exp.getTotalExperience();
                    if (totalExp < xpNeeded) { //if player does not have enough xp adjust amount of given durability and taken XP to match
                        xpNeeded = totalExp;
                        damageToHeal = xpNeeded * 2;
                    }

                    //repair item and take XP
                    setDamageFor(item, maxDamage - damageToHeal);
                    exp.setTotalExperience(exp.getTotalExperience() - xpNeeded);
                }
            }
        }
    }

    private static int getDamageFor(ItemStack item) {
        if (item != null && item.getItemMeta() instanceof Damageable) {
            return ((Damageable)item.getItemMeta()).getDamage();
        } else {
            return 0;
        }
    }

    private static void setDamageFor(ItemStack item, int damage) {
        if (item != null && item.getItemMeta() instanceof Damageable) {
            ((Damageable)item.getItemMeta()).setDamage(damage);
        }
    }
}
