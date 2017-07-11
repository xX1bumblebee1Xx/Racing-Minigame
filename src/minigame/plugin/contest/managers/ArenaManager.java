package minigame.plugin.contest.managers;

import minigame.plugin.contest.Arena;
import minigame.plugin.contest.Main;
import minigame.plugin.contest.Util;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaManager {

    Util u = new Util();

    private static ArenaManager manager;
    private static List<Arena> arenas = new ArrayList<>();

    public static HashMap<UUID, Location> locs = new HashMap<>();
    public static HashMap<UUID, ItemStack[]> inv = new HashMap<>();


    public static ArenaManager getManager() {
        if (manager == null)
            manager = new ArenaManager();
        return manager;
    }

    public Arena getArena(String name) {
        for (Arena a : arenas) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public Arena getArena(UUID uuid) {
        for (Arena a : getArenas()) {
            if (a.getPlayers().contains(uuid)) {
                return a;
            }
        }
        return null;
    }

    public Arena createArena(String name, Location p1, Location p2) {
        Arena a = new Arena(name);
        arenas.add(a);
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        c.set("arenas." + name + ".name", name);
        c.set("arenas." + name + ".bounds.world", p1.getWorld().getName());
        c.set("arenas." + name + ".bounds.x1", p1.getBlockX());
        c.set("arenas." + name + ".bounds.y1", p1.getBlockY());
        c.set("arenas." + name + ".bounds.z1", p1.getBlockZ());
        c.set("arenas." + name + ".bounds.x2", p2.getBlockX());
        c.set("arenas." + name + ".bounds.y2", p2.getBlockY());
        c.set("arenas." + name + ".bounds.z2", p2.getBlockZ());
        try {
            c.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }

    public List<Arena> getArenas() { return arenas; }

    public void addPlayer(Player p, String name) {
        final Arena a = getArena(name);
        if (a == null) {
            p.sendMessage(ChatColor.RED + "Could not find that arena!");
            return;
        }
        if (a.getSpawns() == null || a.getSpawns().isEmpty() || a.getLobby() == null) {
            p.sendMessage(ChatColor.RED + "Error: Not all spawns are set!");
            return;
        }
        if (isInGame(p)) {
            p.sendMessage(ChatColor.RED + "You cannot join more than one game!");
            return;
        }

        a.getPlayers().add(p.getUniqueId());
        inv.put(p.getUniqueId(), p.getInventory().getContents());
        locs.put(p.getUniqueId(), p.getLocation());
        p.getInventory().clear();
        u.giveCharacterSelector(p);
        u.giveLeaveItem(p);
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(a.getLobby());

        for (UUID uuid : a.getPlayers()) {
            Player t = Bukkit.getServer().getPlayer(uuid);
            if (t != null) {
                t.sendMessage(ChatColor.GREEN + p.getName() + " has joined the game.");
            }
        }
    }

    public void removePlayer(Player p) {
        Arena a = null;
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(p.getUniqueId())) {
                a = arena;
            }
        }

        if (a == null) {
            p.sendMessage(ChatColor.RED + "Couldn't find a game you are in!");
            return;
        }

        a.getPlayers().remove(p.getUniqueId());
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        p.getInventory().setContents(inv.get(p.getUniqueId()));
        inv.remove(p.getUniqueId());
        p.teleport(locs.get(p.getUniqueId()));
        locs.remove(p.getUniqueId());
        p.setFireTicks(0);

        for (UUID uuid : a.getPlayers()) {
            Player t = Bukkit.getServer().getPlayer(uuid);
            if (t != null) {
                t.sendMessage(ChatColor.GREEN + p.getName() + " has left the game.");
            }
        }
        if (a.getPlayers().size() <= 1) {
            if (a.isInProgress()) {
                a.end();
            }
        }
    }

    public void loadArenas() {
        File f = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        if (c.getConfigurationSection("arenas") == null)
            return;

        for (String arena : c.getConfigurationSection("arenas").getKeys(false)) {
            Arena a = new Arena(c.getString("arenas." + arena + ".name"));

            World lw = Bukkit.getServer().getWorld(c.getString("arenas." + arena + ".lobby.world"));
            int lx = c.getInt("arenas." + arena + ".lobby.x");
            int ly = c.getInt("arenas." + arena + ".lobby.y");
            int lz = c.getInt("arenas." + arena + ".lobby.z");
            int lp = c.getInt("arenas." + arena + ".lobby.pitch");
            int lyaw = c.getInt("arenas." + arena + ".lobby.yaw");
            Location lobby = new Location(lw, lx, ly, lz, lp, lyaw);

            a.setLobby(lobby);
            arenas.add(a);
            a.init();
        }
    }

    public boolean isInGame(Player p) {
        for (Arena a : arenas) {
            if (a.getPlayers().contains(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

}
