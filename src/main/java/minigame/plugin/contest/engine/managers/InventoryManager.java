package minigame.plugin.contest.engine.managers;

import minigame.plugin.contest.engine.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryManager {

    public void openHeroMenu(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "Select a hero");

        ItemStack filler = createItem(Material.STAINED_GLASS_PANE, (short)14, " ", null);
        ItemStack close = createItem(Material.NETHER_STAR, (short)0, "&cClose", null);

        for (int i = 0; i<27; i++)
            inv.setItem(i, filler);
        inv.setItem(22, close);

        int slot = 9;
        for (Kit k : Kit.values()) {
            ItemStack kitItem = createItem(k.getMaterial(), (short)0, k.getColor() + k.getName(), null);
            inv.setItem(slot, kitItem);
            slot++;
        }

        p.openInventory(inv);
    }

    public ItemStack createItem(Material mat, short metadata, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1, metadata);
        List<String> coloredLore = new ArrayList<>();

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        if (lore != null) {
            for (String loreItem : coloredLore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreItem));
            }
            im.setLore(coloredLore);
        }
        item.setItemMeta(im);
        return item;
    }


}
