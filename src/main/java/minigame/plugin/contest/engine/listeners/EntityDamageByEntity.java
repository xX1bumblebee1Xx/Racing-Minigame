package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player))
            return;

        ArenaManager am = ArenaManager.getManager();
        Player p = (Player) e.getDamager();

        if (!am.isInGame(p))
            return;

        Arena a = am.getArena(p.getUniqueId());
        if (a == null)
            return;

        e.setCancelled(true);
    }

}