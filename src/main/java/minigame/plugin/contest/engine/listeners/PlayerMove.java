package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.Util;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    Util u = new Util();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getFrom().getX() == e.getTo().getX() || e.getFrom().getZ() == e.getTo().getZ())
            return;

        ArenaManager am = ArenaManager.getManager();
        Player p = e.getPlayer();

        if (!am.isInGame(p))
            return;

        Arena a = am.getArena(p.getUniqueId());
        if (a == null)
            return;

        if (!a.isInProgress())
            return;

        if (a.isFrozen())
            e.setCancelled(true);
    }

}