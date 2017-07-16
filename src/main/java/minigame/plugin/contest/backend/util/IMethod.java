package minigame.plugin.contest.backend.util;

import org.bukkit.entity.Player;

/**
 * Created by Zvijer on 14.7.2017..
 */
public interface IMethod
{
    Class<?> getNMS(final String name);
    void sendPacket(final Player player, Object... packets);
}