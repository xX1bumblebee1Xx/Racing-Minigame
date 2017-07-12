package minigame.plugin.contest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import minigame.plugin.contest.backend.GamePlayer;
import minigame.plugin.contest.commands.MainCommand;
import minigame.plugin.contest.engine.listeners.PlayerInteract;
import minigame.plugin.contest.engine.listeners.PlayerMove;
import minigame.plugin.contest.engine.listeners.PlayerQuit;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

    private @Getter static Plugin instance;

    private static final LoadingCache<UUID, GamePlayer> playerCache;

    static
    {
        playerCache = CacheBuilder.newBuilder()
                .maximumSize(100L)
                .expireAfterAccess(10L, TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, GamePlayer>() {
                    @Override
                    @ParametersAreNonnullByDefault
                    public GamePlayer load(UUID uuid) throws Exception
                    {
                        return new GamePlayer(uuid);
                    }
                });
    }

    public GamePlayer getPlayer(final UUID obj)
    {
        GamePlayer player = null;
        try
        {
            player = playerCache.get(obj);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return player;
    }

    public void clear(final UUID obj)
    {
        playerCache.invalidate(obj);
    }

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

    @Override
    public void onDisable()
    {
        if (!playerCache.asMap().isEmpty())
            playerCache.asMap().clear();
    }

    private void registerEvents() {
        Stream.of(
                new PlayerInteract(),
                new PlayerMove(),
                new PlayerQuit()
        ).forEach(e -> getServer().getPluginManager().registerEvents(e, this));
    }
}
