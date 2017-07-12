package minigame.plugin.contest.engine;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public enum Kit
{
    FLASH("Flash", ChatColor.RED),
    PROF_ZOOM("Prof.Zoom", ChatColor.YELLOW),
    SONIC("Sonic", ChatColor.BLUE),
    ROADRUNNER("Roadrunner", ChatColor.DARK_PURPLE),
    QUICKSILVER("Quicksilver", ChatColor.AQUA),
    GODSPEED("Godspeed", ChatColor.GOLD),
    DASH("Dash", ChatColor.RED),
    SPEEDY_GONZALES("Speedy Gonzales", ChatColor.YELLOW);

    private @Getter final String name;
    private @Getter final ChatColor color;

    Kit(final String name, final ChatColor color)
    {
        this.name = name;
        this.color = color;
    }

    public String getTag(boolean space)
    {
        return color + name + (space ? " " : "");
    }
}