package minigame.plugin.contest.backend.util.packet;

import minigame.plugin.contest.backend.util.Reflection;
import minigame.plugin.contest.engine.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vengeancey on 16.7.2017..
 */
public class TitlePacket extends Reflection
{
    private final String title, subtitle;
    private transient Object titlePacket, subtitlePacket;
    private transient Object enumTitle, enumSubtitle;
    private transient Object titleComponent, subtitleComponent;
    private Constructor<?> titleConstructor, subtitleConstructor;

    public TitlePacket(String title, String subtitle)
    {
        this.title = title.length() > 20 ? title.substring(0, 19) : title;
        this.subtitle = subtitle;

        try
        {
            this.enumTitle = getNMS("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            this.enumSubtitle = getNMS("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);

            this.titleComponent = getNMS("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json(title));
            this.subtitleComponent = getNMS("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json(subtitle));

            this.titleConstructor = getNMS("PacketPlayOutTitle").getConstructor(
                    getNMS("PacketPlayOutTitle").getDeclaredClasses()[0],
                    getNMS("IChatBaseComponent"),
                    int.class, int.class, int.class
            );
            this.subtitleConstructor = getNMS("PacketPlayOutTitle").getConstructor(
                    getNMS("PacketPlayOutTitle").getDeclaredClasses()[0],
                    getNMS("IChatBaseComponent"),
                    int.class, int.class, int.class
            );
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendToArena(Arena a, int duration)
    {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : a.getPlayers()) {
            Player p = Bukkit.getServer().getPlayer(uuid);
            if (p == null)
                continue;
            players.add(p);
        }
        send(players, duration);
    }

    public void sendToPlayer(Player p, int duration)
    {
        List<Player> players = new ArrayList<>();
        players.add(p);
        send(players, duration);
    }

    public void send(Collection<? extends Player> players, int duration)
    {
        try
        {
            this.titlePacket = this.titleConstructor.newInstance(enumTitle, titleComponent, 20, 20*duration, 20);
            this.subtitlePacket = this.subtitleConstructor.newInstance(enumSubtitle, subtitleComponent, 20, 20*duration, 20);

            players.forEach(p -> sendPacket(p, titlePacket, subtitlePacket));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}