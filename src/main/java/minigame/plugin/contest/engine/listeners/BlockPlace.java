package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ArenaManager am = ArenaManager.getManager();
        Player p = e.getPlayer();

        if (!am.isInGame(p))
            return;

        Arena a = am.getArena(p.getUniqueId());
        if (a == null)
            return;

        e.setCancelled(true);
    }

}