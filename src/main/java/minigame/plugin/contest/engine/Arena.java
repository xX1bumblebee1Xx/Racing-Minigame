package minigame.plugin.contest.engine;

import lombok.Getter;
import minigame.plugin.contest.Main;
import minigame.plugin.contest.Util;
import minigame.plugin.contest.backend.GameCache;
import minigame.plugin.contest.backend.GamePlayer;
import minigame.plugin.contest.backend.scoreboard.GameBoard;
import minigame.plugin.contest.backend.util.packet.TitlePacket;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.*;
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

    private @Getter String name = null;
    private @Getter List<UUID> finished = new ArrayList<>();
    private @Getter List<UUID> players = new ArrayList<>();
    private @Getter List<UUID> spectators = new ArrayList<>();
    private @Getter List<UUID> abilityUsed = new ArrayList<>();
    private @Getter Map<UUID, Kit> selected = new HashMap<>();
    private @Getter Location lobby = null;
    private @Getter Location specSpawn = null;
    private @Getter Location endP1 = null;
    private @Getter Location endP2 = null;
    private @Getter boolean inProgress = false;
    private @Getter boolean frozen = true;

    public Arena(String name) {
        this.manager = ArenaManager.getManager();
        this.name = name;
    }

    public void addSpectator(Player p) {
        getSpectators().add(p.getUniqueId());
        ArenaManager.locs.put(p.getUniqueId(), p.getLocation());
        p.teleport(getSpecSpawn());
        p.setGameMode(GameMode.SPECTATOR);
    }

    public void setFinished(Player p, int position) {
        finished.add(position, p.getUniqueId());
        String suffix = position == 0 ? "st" : position == 1 ? "nd" : position == 2 ? "rd" : "th";
        new TitlePacket("&aYou finished " + (position+1) + suffix, "").sendToPlayer(p, 5);
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
        specSpawn = l;
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

    public void updateBoards() {
        int min = Main.getInstance().getConfig().getInt("min-players");
        for (UUID uuid : getPlayers()) {
            GamePlayer gp = Main.getCache().getPlayer(uuid);

            GameBoard board = new GameBoard(getPlayers().size() >= min ?
                    "&a&lIn-game" : "&e&lWaiting");
            board.add(" ");
            board.add("&6Coins: &e" + gp.getCoins());

            board.register();
            board.send(Bukkit.getServer().getPlayer(uuid));
        }
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
                    new TitlePacket("&aGO!", "").sendToArena(a, 2);
                    updateBoards();
                    frozen = false;

                    PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
                    for (UUID uuid : getPlayers()) {
                        Player p = Bukkit.getServer().getPlayer(uuid);
                        if (p == null)
                            continue;

                        p.addPotionEffect(speed);

                        if (!getSelected().containsKey(uuid)) {
                            Kit kit = u.getRandomKit();

                            while (getSelected().containsValue(kit))
                                kit = u.getRandomKit();

                            p.sendMessage(ChatColor.GREEN + "Selected " + kit.getName());
                            getSelected().put(p.getUniqueId(), kit);
                        }
                    }

                    cancel();
                } else {
                    new TitlePacket("&c"+String.valueOf(time),"&eseconds until the game starts!").sendToArena(a, 2);
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
            new TitlePacket("&cGame Over", "").sendToArena(this, 5);

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
                p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
            }, 150);
        }

        for (Iterator<UUID> i = getSpectators().iterator(); i.hasNext();) {
            UUID uuid = i.next();
            final Player p = Bukkit.getServer().getPlayer(uuid);
            new TitlePacket("&cGame Over", "").sendToArena(this, 5);
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

            GameBoard board = new GameBoard("&c&lGame Over");
            board.add(" ");
            board.add("&6Winners:");
            for (int id = 0; id < getFinished().size(); id++) {
                Player t = Bukkit.getServer().getPlayer(getFinished().get(id));
                if (t == null)
                    continue;

                board.add("&6" + (id+1) + ": " + p.getName());
            }
            board.register();
            board.send(p);
        }

        inProgress = false;
        frozen = true;
        getPlayers().clear();
        getSpectators().clear();
        init();
    }
}
