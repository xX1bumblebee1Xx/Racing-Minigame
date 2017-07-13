package minigame.plugin.contest;

import lombok.Getter;
import minigame.plugin.contest.backend.GameCache;
import minigame.plugin.contest.backend.WebConnector;
import minigame.plugin.contest.backend.database.MySQL;
import minigame.plugin.contest.commands.MainCommand;
import minigame.plugin.contest.engine.listeners.*;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.stream.Stream;

public class Main extends JavaPlugin {

    /**
     *
     *      - Mavenized the project
     *      - Use J8's features
     *
     *      We're using the cache for handling
     *      player data, and connecting it to
     *      the database.
     *
     */


    //TODO
    //spectator walls
    //spectator leave

    private @Getter static Plugin instance;
    private @Getter static final GameCache cache = new GameCache();
    private @Getter static final WebConnector api = new WebConnector(new MySQL("localhost", 3306, "root", "", "koenzime"));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        Bukkit.getServer().getPluginCommand("race").setExecutor(new MainCommand());
        registerEvents();
        ArenaManager.getManager().loadArenas();
    }

    @Override
    public void onDisable()
    {
        if (!GameCache.getPlayerCache().asMap().isEmpty())
            GameCache.getPlayerCache().asMap().clear();
    }

    private void registerEvents() {
        Stream.of(
                new PlayerInteract(),
                new PlayerMove(),
                new PlayerQuit(),
                new EntityDamageByEntity(),
                new PlayerCommandPreProccess(),
                new PlayerTeleport(),
                new SignChange(),
                new ConnectionListener()
        ).forEach(e -> getServer().getPluginManager().registerEvents(e, this));
    }
}
