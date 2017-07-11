package minigame.plugin.contest;

import minigame.plugin.contest.managers.ArenaManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

    Util u = new Util();

    private ArenaManager manager;

    private String name = null;
    private ArrayList<UUID> players = new ArrayList<UUID>();
    private Location lobby = null;
    private Location spec = null;
    private boolean inProgress = false;
    private boolean frozen = true;


    public Arena(String name) {
        this.manager = ArenaManager.getManager();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public Location getLobby() {
        return lobby;
    }

    public Location getSpecSpawn() {
        return spec;
    }

    public boolean isInProgress() { return inProgress; }

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
            spawns.add(new Location(w, x, y, z, pitch, yaw));
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
        }.runTaskTimer(Main.getInstance(), 20*30, 20*30);
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
            System.out.println("Ran");
            u.sendTitle(this, "Game over!", "red");
            p.sendMessage(ChatColor.GOLD + "Game over!");
            i.remove();
            Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    p.getInventory().clear();
                    p.getInventory().setArmorContents(null);
                    p.getInventory().setContents(ArenaManager.inv.get(p.getUniqueId()));
                    ArenaManager.inv.remove(p.getUniqueId());
                    p.teleport(ArenaManager.locs.get(p.getUniqueId()));
                    ArenaManager.locs.remove(p.getUniqueId());
                    p.setFireTicks(0);
                }
            }, 100);
        }
        inProgress = false;
        frozen = true;
        getPlayers().clear();
        init();
    }
}
