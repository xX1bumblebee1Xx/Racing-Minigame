package minigame.plugin.contest.commands;

import minigame.plugin.contest.Main;
import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.listeners.PlayerInteract;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("race")) {
            if (!(args.length > 0)) {
                sender.sendMessage(ChatColor.RED + "Invalid arguments! Type /race help for help.");
                return false;
            }

            ArenaManager am = ArenaManager.getManager();
            if (args[0].equalsIgnoreCase("help")) {
                //TODO
                return true;
            } else if (args[0].equalsIgnoreCase("arenawand")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                ItemStack wand = new ItemStack(Material.STICK);
                ItemMeta wm = wand.getItemMeta();
                wm.setDisplayName(ChatColor.YELLOW + "Arena wand");
                wand.setItemMeta(wm);
                p.getInventory().addItem(wand);
                p.sendMessage(ChatColor.GREEN + "You have been given the arena creation wand.");
                p.sendMessage(ChatColor.GREEN + "Right-click to select position 1, Left-click to select position 2");
                return true;
            } else if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race create <name>");
                    return false;
                }

                if (!PlayerInteract.Aleft.containsKey(p)) {
                    p.sendMessage(ChatColor.RED + "Please make sure both positions are set!");
                    return false;
                } else if (!PlayerInteract.Aright.containsKey(p)) {
                    p.sendMessage(ChatColor.RED + "Please make sure both positions are set!");
                    return false;
                }

                Location l = PlayerInteract.Aleft.get(p);
                Location r = PlayerInteract.Aright.get(p);
                if (!l.getWorld().getName().equals(r.getWorld().getName())) {
                    p.sendMessage(ChatColor.RED + "Both positions must be in the same world");
                    return false;
                }

                String name = args[1];
                Arena check = am.getArena(name);
                if (check != null) {
                    p.sendMessage(ChatColor.GRAY + "There is already an arena called " + name);
                    return false;
                }

                am.createArena(name, l, r);
                p.sendMessage(ChatColor.GRAY + "Successfully created new arena called " + name);
                Arena a = am.getArena(name);
                a.init();
                return true;
            } else if (args[0].equalsIgnoreCase("setend")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race setend <name>");
                    return false;
                }

                if (!PlayerInteract.Aleft.containsKey(p)) {
                    p.sendMessage(ChatColor.RED + "Please make sure both positions are set!");
                    return false;
                } else if (!PlayerInteract.Aright.containsKey(p)) {
                    p.sendMessage(ChatColor.RED + "Please make sure both positions are set!");
                    return false;
                }

                Location l = PlayerInteract.Aleft.get(p);
                Location r = PlayerInteract.Aright.get(p);
                if (!l.getWorld().getName().equals(r.getWorld().getName())) {
                    p.sendMessage(ChatColor.RED + "Both positions must be in the same world");
                    return false;
                }

                String name = args[1];
                Arena a = am.getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.GRAY + "Could not find an arena called " + name);
                    return false;
                }

                a.setEndZone(l, r);
                p.sendMessage(ChatColor.GRAY + "Successfully set end zone for " + name);
                return true;
            } else if (args[0].equalsIgnoreCase("setlobby")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race setlobby <name>");
                    return false;
                }

                String name = args[1];
                Arena a = ArenaManager.getManager().getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Could not find an arena called " + name);
                    return false;
                }

                a.setLobby(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Successfully set lobby for " + name);
                return true;
            } else if (args[0].equalsIgnoreCase("setspectator") || args[0].equalsIgnoreCase("setspec")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race setspec <name>");
                    return false;
                }

                String name = args[1];
                Arena a = am.getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Could not find an arena called " + name);
                    return false;
                }
                a.setSpecSpawn(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Successfully set spectator spawn for " + name);
                return true;
            } else if (args[0].equalsIgnoreCase("join")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race join <name>");
                    return false;
                }

                String name = args[1];
                Arena a = am.getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Could not find an arena called " + name);
                    return false;
                }
                int max = a.getSpawns().size();
                if (am.isInGame(p)) {
                    p.sendMessage(ChatColor.RED + "You cannot join more than one game!");
                    return false;
                }
                if (a.isInProgress()) {
                    p.sendMessage(ChatColor.RED + "That game is currently in progress.");
                    return false;
                }
                if (a.getPlayers().size() >= max) {
                    p.sendMessage(ChatColor.RED + "That game is already full!");
                    return false;
                }
                Location lobby = a.getLobby();
                if (lobby == null) {
                    p.sendMessage(ChatColor.RED + "A lobby has not been set!");
                    return false;
                }
                am.addPlayer(p, name);
                return true;
            } else if (args[0].equalsIgnoreCase("addspawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race addspawn <name>");
                    return false;
                }

                String name = args[1];
                Arena a = ArenaManager.getManager().getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Could not find an arena called " + name);
                    return false;
                }

                a.addSpawn(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Successfully added spawn for " + name);
                return true;
            } else if (args[0].equalsIgnoreCase("spectate") || args[0].equalsIgnoreCase("spec")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                if (!sender.hasPermission("races.spectate")) {
                    sender.sendMessage(ChatColor.RED + "You do not have the required permissions!");
                    return false;
                }

                Player p = (Player) sender;
                if (!(args.length > 1)) {
                    p.sendMessage(ChatColor.RED + "Invalid arguments! Correct usage is /race spectate <name>");
                    return false;
                }

                String name = args[1];
                Arena a = ArenaManager.getManager().getArena(name);
                if (a == null) {
                    p.sendMessage(ChatColor.RED + "Could not find an arena called " + name);
                    return false;
                }

                a.addSpectator(p);
                p.sendMessage(ChatColor.GREEN + "You are now spectating " + name);
                return true;
            } else if (args[0].equalsIgnoreCase("leave")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                    return false;
                }
                if (!sender.hasPermission("races.spectate")) {
                    sender.sendMessage(ChatColor.RED + "You do not have the required permissions!");
                    return false;
                }

                Player p = (Player) sender;

                if (!am.isInGame(p) || am.getSpectatingGame(p) == null) {
                    p.sendMessage(ChatColor.RED + "You are not in a game!");
                    return false;
                }

                am.removePlayer(p);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("races.reload")) {
                    sender.sendMessage(ChatColor.RED + "You do not have the required permissions!");
                    return false;
                }

                Main.getInstance().reloadConfig();;
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid arguments! Type /race help for help.");
                return false;
            }
        }
        return false;
    }
}
