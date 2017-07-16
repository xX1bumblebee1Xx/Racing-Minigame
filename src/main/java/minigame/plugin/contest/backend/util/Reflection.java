package minigame.plugin.contest.backend.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Created by Vengeancey on 14.7.2017..
 */
public class Reflection implements IMethod
{
    private @Getter static Reflection reflection;

    protected Reflection()
    {
        reflection = reflection == null ? new Reflection() : reflection;
    }

    @Override
    public Class<?> getNMS(final String name)
    {
        Class<?> clazz = null;
        try
        {
            final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            clazz = Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return clazz;
    }

    @Override
    public void sendPacket(final Player player, Object... packets)
    {
        try
        {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            Stream.of(packets).forEach(packet -> {
                try
                {
                    playerConnection.getClass().getMethod("sendPacket", getNMS("Packet")).invoke(playerConnection, packet);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void broadcastPacket(Object... packets)
    {
        Bukkit.getOnlinePlayers().forEach(p -> sendPacket(p, packets));
    }
}