package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
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