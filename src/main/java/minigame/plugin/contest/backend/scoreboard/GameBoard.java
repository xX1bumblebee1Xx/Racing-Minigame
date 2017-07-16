package minigame.plugin.contest.backend.scoreboard;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Vengeancey on 16.7.2017..
 */
public class GameBoard
{
    private transient String name;
    private Scoreboard scoreboard;
    private Objective objective;
    private Map<Integer, String> scores = Maps.newConcurrentMap();
    private int score = 15;

    public GameBoard(final String name)
    {
        this.name = name;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void register()
    {
        if (this.objective == null)
        {
            this.objective = scoreboard.registerNewObjective("sidebar", "dummy");
            this.objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        for (int i : scores.keySet())
            objective.getScore(scores.get(i)).setScore(i);
    }

    public void add(final String text)
    {
        this.scores.put(score--, ChatColor.translateAlternateColorCodes('&', text));
    }

    public void clear()
    {
        scores.values().forEach(s -> scoreboard.resetScores(s));
        scores.clear();
        score = 15;
    }

    public void send(Player... players)
    {
        Stream.of(players).forEach(p -> p.setScoreboard(scoreboard));
    }
}