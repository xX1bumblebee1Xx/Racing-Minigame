package minigame.plugin.contest.engine;

import minigame.plugin.contest.Main;
import minigame.plugin.contest.Util;
import minigame.plugin.contest.backend.GameCache;
import minigame.plugin.contest.backend.GamePlayer;
import minigame.plugin.contest.engine.managers.ArenaManager;
import minigame.plugin.contest.engine.managers.InventoryManager;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

    Util u = new Util();

    private ArenaManager manager;

    private String name = null;
    private List<UUID> finished = new ArrayList<>();
    private List<UUID> players = new ArrayList<>();
    private List<UUID> spectators = new ArrayList<>();
    private Location lobby = null;
    private Location spec = null;
    private Location endP1 = null;
    private Location endP2 = null;
    private boolean inProgress = false;
    private boolean frozen = true;


    public Arena(String name) {
        this.manager = ArenaManager.getManager();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getFinished() {
        return finished;
    }

    public List<UUID> getSpecatators() {
        return spectators;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpecSpawn() {
        return spec;
    }

    public boolean isInProgress() { return inProgress; }

    public void addSpectator(Player p) {
        getSpecatators().add(p.getUniqueId());
        ArenaManager.locs.put(p.getUniqueId(), p.getLocation());
        p.teleport(getSpecSpawn());
        p.setGameMode(GameMode.SPECTATOR);
    }

    public void setFinished(Player p, int position) {
        finished.add(position, p.getUniqueId());
        String suffix = position == 0 ? "st" : position == 1 ? "nd" : position == 2 ? "rd" : "th";
        u.sendTitle(p, "You finished " + (position+1) + suffix, "green");
        p.sendMessage(ChatColor.GREEN + "You finished " + (position+1) + suffix);
    }

    public void setEndZone(Location p1, Location p2) {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        c.set("arenas." + name + ".end.world", p1.getWorld().getName());
        c.set("arenas." + name + ".end.x1", p1.getBlockX());
        c.set("arenas." + name + ".end.y1", p1.getBlockY());
        c.set("arenas." + name + ".end.z1", p1.getBlockZ());
        c.set("arenas." + name + ".end.x2", p2.getBlockX());
        c.set("arenas." + name + ".end.y2", p2.getBlockY());
        c.set("arenas." + name + ".end.z2", p2.getBlockZ());
        try {
            c.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLobby(Location l) {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        c.set("arenas." + name + ".lobby.world", l.getWorld().getName());
        c.set("arenas." + name + ".lobby.x", l.getBlockX());
        c.set("arenas." + name + ".lobby.y", l.getBlockY());
        c.set("arenas." + name + ".lobby.z", l.getBlockZ());
        c.set("arenas." + name + ".lobby.pitch", l.getPitch());
        c.set("arenas." + name + ".lobby.yaw", l.getYaw());
        try {
            c.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lobby = l;
    }

    public void setSpecSpawn(Location l) {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        c.set("arenas." + name + ".specSpawn.world", l.getWorld().getName());
        c.set("arenas." + name + ".specSpawn.x", l.getBlockX());
        c.set("arenas." + name + ".specSpawn.y", l.getBlockY());
        c.set("arenas." + name + ".specSpawn.z", l.getBlockZ());
        c.set("arenas." + name + ".specSpawn.pitch", l.getPitch());
        c.set("arenas." + name + ".specSpawn.yaw", l.getYaw());
        try {
            c.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        spec = l;
    }

    public void addSpawn(Location l) {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        int spawnAmounts = c.getInt("arenas." + name + ".spawnAmounts");
        spawnAmounts++;

        World w = l.getWorld();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        double pitch = l.getPitch();
        double yaw = l.getYaw();

        c.set("arenas." + name + ".spawnAmounts", spawnAmounts);
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".world", w.getName());
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".x", x);
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".y", y);
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".z", z);
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".pitch", pitch);
        c.set("arenas." + name + ".spawns." + spawnAmounts + ".yaw", yaw);

        try {
            c.save(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getEndZoneP1() {
        if (endP1 != null)
            return endP1;

        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        String worldName = c.getString("arenas." + name + ".end.world");
        if (worldName == null)
            return null;

        World w = Bukkit.getServer().getWorld(worldName);
        int x = c.getInt("arenas." + name + ".end.x1");
        int y = c.getInt("arenas." + name + ".end.y1");
        int z = c.getInt("arenas." + name + ".end.z1");
        return new Location(w, x, y, z);
    }

    public Location getEndZoneP2() {
        if (endP2 != null)
            return endP2;

        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        World w = Bukkit.getServer().getWorld(c.getString("arenas." + name + ".end.world"));
        int x = c.getInt("arenas." + name + ".end.x2");
        int y = c.getInt("arenas." + name + ".end.y2");
        int z = c.getInt("arenas." + name + ".end.z2");
        return new Location(w, x, y, z);
    }

    public List<Location> getSpawns() {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);

        if (c.getConfigurationSection("arenas." + name + ".spawns") == null)
            return Collections.emptyList();

        List<Location> spawns = new ArrayList<>();
        Set<String> spawnIds = c.getConfigurationSection("arenas." + name + ".spawns").getKeys(false);
        for (String id : spawnIds) {
            World w = Bukkit.getServer().getWorld(c.getString("arenas." + name + ".spawns." + id + ".world"));
            int x = c.getInt("arenas." + name + ".spawns." + id + ".x");
            int y = c.getInt("arenas." + name + ".spawns." + id + ".y");
            int z = c.getInt("arenas." + name + ".spawns." + id + ".z");
            float pitch = c.getInt("arenas." + name + ".spawns." + id + ".pitch");
            float yaw = c.getInt("arenas." + name + ".spawns." + id + ".yaw");
            spawns.add(new Location(w, x, y, z, yaw, pitch));
        }

        return spawns;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public Location getP1() {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        World w = Bukkit.getServer().getWorld(c.getString("arenas." + name + ".bounds.world"));
        int x1 = c.getInt("arenas." + name + ".bounds.x1");
        int y1 = c.getInt("arenas." + name + ".bounds.y1");
        int z1 = c.getInt("arenas." + name + ".bounds.z1");
        Location l1 = new Location(w, x1, y1, z1);
        return l1;
    }

    public Location getP2() {
        File locations = new File(Main.getInstance().getDataFolder() + File.separator + "locations.yml");
        YamlConfiguration c = YamlConfiguration.loadConfiguration(locations);
        World w = Bukkit.getServer().getWorld(c.getString("arenas." + name + ".bounds.world"));
        int x2 = c.getInt("arenas." + name + ".bounds.x2");
        int y2 = c.getInt("arenas." + name + ".bounds.y2");
        int z2 = c.getInt("arenas." + name + ".bounds.z2");
        Location l2 = new Location(w, x2, y2, z2);
        return l2;
    }

    public void init() {
        final int min = Main.getInstance().getConfig().getInt("min-players");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (inProgress) cancel();
                if (getPlayers().size() >= min) {
                    start();
                    cancel();
                } else {
                    for (UUID uuid : getPlayers()) {
                        Player p = Bukkit.getServer().getPlayer(uuid);
                        if (p != null) {
                            p.sendMessage(ChatColor.RED + "Not enough players to begin!");
                        }
                    }
                }
            }
                                        //TODO time
        }.runTaskTimer(Main.getInstance(), 20*60, 20*60);
    }

    public void start() {
        if (inProgress)
            return;

        inProgress = true;
        Arena a = manager.getArena(name);
        final int length = Main.getInstance().getConfig().getInt("match-length");

        for (int i = 0; i < getPlayers().size(); i++) {
            Player p = Bukkit.getServer().getPlayer(getPlayers().get(i));
            p.teleport(getSpawns().get(i));
        }

        new BukkitRunnable() {
            int time = Main.getInstance().getConfig().getInt("match-countdown");
            @Override
            public void run() {
                if (time <= 0) {
                    cancel();
                    u.sendTitle(a, "GO!", "green");
                    u.sendSubtitle(a, "", "white");
                    frozen = false;

                    PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3);
                    for (UUID uuid : getPlayers()) {
                        Player p = Bukkit.getServer().getPlayer(uuid);
                        if (p == null)
                            continue;

                        p.addPotionEffect(speed);

                        if (!InventoryManager.getSelected().containsKey(uuid)) {
                            Kit kit = u.getRandomKit();

                            while (InventoryManager.getSelected().containsValue(kit))
                                kit = u.getRandomKit();

                            p.sendMessage(ChatColor.GREEN + "Selected " + kit.getName());
                            InventoryManager.getSelected().put(p.getUniqueId(), kit);
                        }
                    }

                    cancel();
                } else {
                    u.sendTitle(a, String.valueOf(time), "red");
                    u.sendSubtitle(a, "seconds until the game starts!", "yellow");
                    time--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                end();
            }
        }.runTaskLater(Main.getInstance(), length*1200);
    }

    public void end() {
        for (Iterator<UUID> i = getPlayers().iterator(); i.hasNext();) {
            UUID uuid = i.next();
            final Player p = Bukkit.getServer().getPlayer(uuid);
            u.sendTitle(this, "Game over!", "red");
            Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (p == null)
                    return;
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.getInventory().setContents(ArenaManager.inv.get(p.getUniqueId()));
                ArenaManager.inv.remove(p.getUniqueId());
                p.teleport(ArenaManager.locs.get(p.getUniqueId()));
                ArenaManager.locs.remove(p.getUniqueId());
                p.setFireTicks(0);
                p.removePotionEffect(PotionEffectType.SPEED);
            }, 100);
        }

        for (Iterator<UUID> i = getSpecatators().iterator(); i.hasNext();) {
            UUID uuid = i.next();
            final Player p = Bukkit.getServer().getPlayer(uuid);
            u.sendTitle(this, "Game over!", "red");
            p.sendMessage(ChatColor.GOLD + "Game over!");
            Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () ->
                    p.teleport(ArenaManager.locs.get(p.getUniqueId())), 100);
        }

        for (UUID uuid : getPlayers()) {
            Player p = Bukkit.getServer().getPlayer(uuid);
            if (p == null)
                continue;

            String rewardType = "";
            p.sendMessage(ChatColor.GOLD + "--- Race Results ---");
            for (int i = 0; i < getFinished().size(); i++) {
                Player t = Bukkit.getServer().getPlayer(getFinished().get(i));
                if (t == null)
                    continue;

                rewardType = i == 0 ? "first" : i == 1 ? "second" : i == 2 ? "third" : "default";
                String suffix = i == 0 ? "st" : i == 1 ? "nd" : i == 2 ? "rd" : "th";
                p.sendMessage(ChatColor.GOLD + t.getName() + " finished " + (i+1) + suffix);
            }

            int reward = Main.getInstance().getConfig().getInt("rewards." + rewardType);
            GameCache gc = new GameCache();
            GamePlayer gp = gc.getPlayer(uuid);
            gp.incrementCoins(reward);
        }

        inProgress = false;
        frozen = true;
        getPlayers().clear();
        getSpecatators().clear();
        init();
    }
}
