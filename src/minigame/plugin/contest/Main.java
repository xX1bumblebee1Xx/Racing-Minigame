package minigame.plugin.contest;

import minigame.plugin.contest.commands.MainCommand;
import minigame.plugin.contest.listeners.PlayerInteract;
import minigame.plugin.contest.listeners.PlayerMove;
import minigame.plugin.contest.listeners.PlayerQuit;
import minigame.plugin.contest.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Plugin instance;

    //TODO
    //spectate
    //death?
    //signs

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        Bukkit.getServer().getPluginCommand("race").setExecutor(new MainCommand());
        registerEvents();
        ArenaManager.getManager().loadArenas();
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMove(), this);
    }

    public static Plugin getInstance() {
        return instance;
    }
}
