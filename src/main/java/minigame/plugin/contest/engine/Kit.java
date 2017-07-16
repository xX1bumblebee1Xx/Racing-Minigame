package minigame.plugin.contest.engine;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public enum Kit
{
    FLASH("Flash", ChatColor.RED, Material.RECORD_3),
    PROF_ZOOM("Prof.Zoom", ChatColor.YELLOW, Material.RECORD_4),
    SONIC("Sonic", ChatColor.BLUE, Material.RECORD_5),
    ROADRUNNER("Roadrunner", ChatColor.DARK_PURPLE, Material.RECORD_6),
    QUICKSILVER("Quicksilver", ChatColor.AQUA, Material.RECORD_7),
    GODSPEED("Godspeed", ChatColor.GOLD, Material.RECORD_8),
    DASH("Dash", ChatColor.RED, Material.RECORD_9),
    SPEEDY_GONZALES("Speedy Gonzales", ChatColor.YELLOW, Material.RECORD_10);

    private @Getter final String name;
    private @Getter final ChatColor color;
    private @Getter final Material material;

    Kit(final String name, final ChatColor color, final Material material)
    {
        this.name = name;
        this.color = color;
        this.material = material;
    }

    public String getTag(boolean space)
    {
        return color + name + (space ? " " : "");
    }
}