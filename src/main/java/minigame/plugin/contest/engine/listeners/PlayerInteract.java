package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class PlayerInteract implements Listener {

    public static HashMap<Player, Location> Aright = new HashMap<Player, Location>();
    public static HashMap<Player, Location> Aleft = new HashMap<Player, Location>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        Player p = e.getPlayer();
        if (!e.getItem().hasItemMeta()) return;
        if (!e.getItem().getItemMeta().hasDisplayName()) return;
        String dis = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());
        ArenaManager am = ArenaManager.getManager();

        if (dis.equals("Arena wand")) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
                e.setCancelled(true);
                Block b = e.getClickedBlock();
                p.sendMessage(ChatColor.GREEN + "Set position 1 to " +
                        b.getLocation().getBlockX() + " " + b.getLocation().getBlockY()
                        + " " +  b.getLocation().getBlockZ());

                Aleft.put(p, b.getLocation());
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
                e.setCancelled(true);
                Block b = e.getClickedBlock();
                p.sendMessage(ChatColor.GREEN + "Set position 2 to " +
                        b.getLocation().getBlockX() + " " + b.getLocation().getBlockY()
                        + " " +  b.getLocation().getBlockZ());

                Aright.put(p, b.getLocation());
            }
        } else if (dis.equalsIgnoreCase("Leave")) {
            am.removePlayer(p);
            p.sendMessage(ChatColor.GREEN + "You have left the game.");
        } else if (dis.equalsIgnoreCase(ChatColor.DARK_PURPLE + "Select a hero")) {
            //TODO
        }
    }

}