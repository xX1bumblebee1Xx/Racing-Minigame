package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.Main;
import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import minigame.plugin.contest.engine.managers.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerInteract implements Listener {

    InventoryManager inv = new InventoryManager();

    public static HashMap<Player, Location> Aright = new HashMap<>();
    public static HashMap<Player, Location> Aleft = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ArenaManager am = ArenaManager.getManager();

        if (e.getClickedBlock() != null) {
            if (e.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) e.getClickedBlock().getState();
                List<String> configLines = Main.getInstance().getConfig().getStringList("sign-format");
                List<String> lines = new ArrayList<>();

                configLines.stream().forEachOrdered((line) ->
                        lines.add(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', line))));

                if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase(lines.get(0)))
                    return;

                String name = ChatColor.stripColor(s.getLine(1));
                Arena a = am.getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Failed to find an arena called " + name);
                    return;
                }

                int max = a.getSpawns().size();
                if (am.isInGame(p)) {
                    p.sendMessage(ChatColor.RED + "You cannot join more than one game!");
                    return;
                }

                if (ChatColor.stripColor(s.getLine(2)).equalsIgnoreCase("Join")) {
                    if (a.isInProgress()) {
                        p.sendMessage(ChatColor.RED + "That game is currently in progress.");
                        return;
                    }
                    if (a.getPlayers().size() >= max) {
                        p.sendMessage(ChatColor.RED + "That game is already full!");
                        return;
                    }
                    Location lobby = a.getLobby();
                    if (lobby == null) {
                        p.sendMessage(ChatColor.RED + "A lobby has not been set!");
                        return;
                    }
                    p.getInventory().setHeldItemSlot(1);
                    am.addPlayer(p, name);
                    return;
                } else if (ChatColor.stripColor(s.getLine(2)).equalsIgnoreCase("Spectate")) {
                    a.addSpectator(p);
                    p.sendMessage(ChatColor.GREEN + "You are now spectating " + name);
                    return;
                }
            }
        }

        if (e.getItem() == null) return;
        if (!e.getItem().hasItemMeta()) return;
        if (!e.getItem().getItemMeta().hasDisplayName()) return;
        String dis = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());

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
            e.setCancelled(true);
        } else if (dis.equalsIgnoreCase("Select a hero")) {
            if (am.isInGame(p) && am.getArena(p.getUniqueId()).isInProgress()) {
                Arena a = am.getArena(p.getUniqueId());
                if (a != null && a.isInProgress()) {
                    if (a.getAbilityUsed().contains(p.getUniqueId())) {
                        p.sendMessage(ChatColor.RED + "You have already used your ability!");
                        return;
                    }

                    PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4);
                    PotionEffect normalSpeed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);

                    a.getAbilityUsed().add(p.getUniqueId());
                    p.removePotionEffect(PotionEffectType.SPEED);
                    p.addPotionEffect(speed);
                    p.sendMessage(ChatColor.GREEN + "You have used your ability");
                    Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(normalSpeed);
                        p.sendMessage(ChatColor.RED + "Your ability has ran out");
                    }, 100);
                    e.setCancelled(true);
                    return;
                }
            }

            inv.openHeroMenu(p);
        }

    }
}