package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.Main;
import minigame.plugin.contest.backend.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public class ConnectionListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(final AsyncPlayerPreLoginEvent e)
    {
        long start = System.currentTimeMillis();
        int id;
        Main.getApi().addUser(e.getUniqueId());
        do
        {
            //max loop time of 50ms
            if (System.currentTimeMillis() - start > 50)
            {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(ChatColor.RED + "Loop time result: > 50 ms.\nCheck the databases.");
                Main.getCache().clear(e.getUniqueId()); //Remove from cache
                return;
            }
            id = Main.getApi().getId(e.getUniqueId());
        } while (id < 0);
        /**
         * TODO
         * Set state
         * Wait for sufficient players to join
         * Start countdown
         * Initiate game
         * Teleport players to arena
         */
    }
}