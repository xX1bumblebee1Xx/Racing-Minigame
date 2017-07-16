package minigame.plugin.contest;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.Kit;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Util {

    public void giveCharacterSelector(Player p)  {
        ItemStack i = new ItemStack(Material.WOOL);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Select a hero");
        im.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&',"&aClick to use ability")));
        i.setItemMeta(im);
        p.getInventory().setItem(0, i);
    }

    public void giveLeaveItem(Player p)  {
        ItemStack i = new ItemStack(Material.BARRIER);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.RED + "Leave");
        i.setItemMeta(im);
        p.getInventory().setItem(8, i);
    }

    public boolean isHeroSelected(Kit hero) {
        for (Kit k : Kit.values()) {
            if (k == hero)
                return true;
        }
        return false;
    }

    public Kit getKitFromString(String name) {
        for (Kit k : Kit.values()) {
            if (k.getName().equals(name))
                return k;
        }
        return null;
    }

    public Location getCenter(Arena a) {
        World w = a.getP1().getWorld();
        Location top = new Location(w, 0, 0, 0);
        top.setX(a.getP1().getBlockX());
        top.setY(a.getP1().getBlockY());
        top.setZ(a.getP1().getBlockZ());

        Location bottom = new Location(w, 0, 0, 0);
        bottom.setX(a.getP2().getX());
        bottom.setY(a.getP2().getY());
        bottom.setZ(a.getP2().getZ());

        double X =  ((top.getX() - bottom.getX())/2) + bottom.getX();
        double Y =  ((top.getY() - bottom.getY())/2) + bottom.getY();
        double Z =  ((top.getZ() - bottom.getZ())/2) + bottom.getZ();

        return new Location(w, X, Y, Z);

    }

    public Kit getRandomKit() {
        Random r = new Random();
        int c = r.nextInt(7);
        int i = 0;
        for (Kit k : Kit.values()) {
            if (i == c)
                return k;
            i++;
        }
        return null;
    }

    public String parseSignLine(String line, Arena a, String action) {
        line = line.replace("%arena_name%", a.getName());
        line = line.replace("%action%", action);

        return ChatColor.translateAlternateColorCodes('&', line);
    }
}
