package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE)
            return;

        Location l = e.getTo();
        Player p = e.getPlayer();
        ArenaManager am = ArenaManager.getManager();
        Arena a = am.getSpectatingGame(p);
        if (a == null)
            return;

        if (am.isInsideArenaBounds(a, l))
            return;

        p.sendMessage(ChatColor.RED + "You cannot teleport outside the arena!");
        e.setCancelled(true);
    }

}