package minigame.plugin.contest.engine.listeners;

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
        if (ArenaManager.getManager().isInGame(p)) {
            //TODO remove from game and restore pos/inv
        }
    }

}