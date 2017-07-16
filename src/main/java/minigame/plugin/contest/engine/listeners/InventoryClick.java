package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.Util;
import minigame.plugin.contest.engine.Kit;
import minigame.plugin.contest.engine.managers.InventoryManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    Util u = new Util();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equalsIgnoreCase("Select a hero"))
            return;
        if (e.getCurrentItem() == null)
            return;
        if (!e.getCurrentItem().hasItemMeta())
            return;
        if (!e.getCurrentItem().getItemMeta().hasDisplayName())
            return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        String dis = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        if (dis.equalsIgnoreCase("close")) {
            p.closeInventory();
        } else {
            Kit kit = u.getKitFromString(dis);
            if (kit == null) {
                p.sendMessage(ChatColor.RED + "Failed to find a kit called " + dis);
                return;
            }

            if (InventoryManager.getSelected().containsValue(kit)) {
                p.sendMessage(ChatColor.RED + "That hero is already selected!");
                return;
            }

            InventoryManager.getSelected().put(p.getUniqueId(), kit);
            p.closeInventory();
            p.sendMessage(ChatColor.GREEN + "Selected " + dis);
        }
    }

}