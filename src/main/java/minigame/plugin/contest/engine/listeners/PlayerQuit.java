package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (PlayerInteract.Aleft.containsKey(p))
            PlayerInteract.Aleft.remove(p);
        if (PlayerInteract.Aright.containsKey(p))
            PlayerInteract.Aright.remove(p);

        ArenaManager am = ArenaManager.getManager();
        if (am.isInGame(p)) {
            am.removePlayer(p);
        }
    }

}