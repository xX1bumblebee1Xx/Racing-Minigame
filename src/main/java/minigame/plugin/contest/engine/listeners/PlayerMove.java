package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.Util;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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

        if (!am.isInsideArenaBounds(a, e.getTo(), a.getP1(), a.getP2())) {
            Location center = u.getCenter(a);
            Vector direction = p.getLocation().toVector().subtract(center.toVector()).normalize();
            p.setVelocity(direction.setY(0).multiply(-2));
        }

        if (am.isInsideArenaBounds(a, p.getLocation(), a.getEndZoneP1(), a.getEndZoneP2())) {
            a.setFinished(p, a.getFinished().size());
            if (a.getFinished().size() == a.getPlayers().size()) {
                a.end();
            }
        }
    }

}