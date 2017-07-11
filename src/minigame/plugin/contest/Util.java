package minigame.plugin.contest;

import minigame.plugin.contest.managers.ArenaManager;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class Util {

    public void giveCharacterSelector(Player p)  {
        ItemStack i = new ItemStack(Material.WOOL);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Select a hero");
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

    public boolean isInsideArena(Player p, Location to) {
        Arena a = ArenaManager.getManager().getArena(p.getUniqueId());
        Location p1 = a.getP1();
        Location p2 = a.getP2();
        if(to.getX() < p1.getBlockX() || to.getX() > p2.getBlockX()) {
            return false;
        } else if(to.getZ() > p1.getBlockZ() || to.getZ() < p2.getBlockZ()) {
            return false;
        } else if(to.getY() > p1.getBlockY() || to.getY() < p2.getBlockY()) {
            return false;
        }
        return true;
    }

    public void sendSubtitle(Arena a, String text, String color) {
        List<UUID> players = a.getPlayers();
        if (!(players.isEmpty())) {
            PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\", \"color\":\"" + color +  "\"}"), 35, 40, 35);
            for (UUID uuid : players) {
                Player p = Bukkit.getServer().getPlayer(uuid);
                if (p == null) return;
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
            }
        }
    }
    public void sendSubtitle(Player p, String text, String color) {
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\", \"color\":\"" + color +  "\"}"), 35, 40, 35);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
    }

    public void sendTitle(Arena a, String text, String color) {
        List<UUID> players = a.getPlayers();
        if (!(players.isEmpty())) {
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\", \"color\":\"" + color +  "\"}"), 35, 40, 35);
            for (UUID uuid : players) {
                Player p = Bukkit.getServer().getPlayer(uuid);
                if (p == null) return;
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
            }
        }
    }
    public void sendTitle(Player p, String text, String color) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\", \"color\":\"" + color +  "\"}"), 35, 40, 35);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
    }

}
