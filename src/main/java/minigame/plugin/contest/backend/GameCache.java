package minigame.plugin.contest.backend;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public class GameCache
{
    private @Getter static final LoadingCache<UUID, GamePlayer> playerCache;
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
}