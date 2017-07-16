package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.Main;
import minigame.plugin.contest.backend.GamePlayer;
import minigame.plugin.contest.backend.scoreboard.GameBoard;
import minigame.plugin.contest.backend.util.packet.TitlePacket;
import minigame.plugin.contest.engine.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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
            if (System.currentTimeMillis() - start > 200)            {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(ChatColor.RED + "Loop time result: > 200 ms.\nCheck the databases.");
                Main.getCache().clear(e.getUniqueId()); //Remove from cache
                return;
            }
            id = Main.getApi().getId(e.getUniqueId());
        } while (id < 0);
    }
}