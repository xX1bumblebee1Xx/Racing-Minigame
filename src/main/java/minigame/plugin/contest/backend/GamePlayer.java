package minigame.plugin.contest.backend;

import lombok.Getter;
import minigame.plugin.contest.engine.GameState;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public class GamePlayer
{
    private @Getter final UUID uuid;
    private @Getter String name;
    private @Getter GameState state;
    private @Getter int coins;

    public GamePlayer(final UUID uuid)
    {
        this.uuid = uuid;
        this.name = Bukkit.getPlayer(uuid).getName();
        this.state = GameState.WAITING;
        this.coins = 0;
    }

    public void updateState(final GameState state)
    {
        this.state = state;
    }

    public void setCoins(int amount)
    {
        setCoins(amount, false);
    }

    public void incrementCoins(int amount)
    {
        setCoins(amount, true);
    }

    private void setCoins(int amount, boolean increment)
    {
        this.coins = increment ? coins += amount : amount;
    }
}