package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.Main;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class PlayerCommandPreProccess implements Listener {

    @EventHandler
    public void onPlayerCommandPreProccess(PlayerCommandPreprocessEvent e) {
        ArenaManager am = ArenaManager.getManager();
        Player p = e.getPlayer();

        if (!am.isInGame(p))
            return;

        Arena a = am.getArena(p.getUniqueId());
        if (a == null)
            return;

        List<String> blocked = Main.getInstance().getConfig().getStringList("blocked-commands");
        if (blocked.contains(e.getMessage().split(" ")[0])) {
            p.sendMessage(ChatColor.RED + "You cannot use that command while in a game!");
            e.setCancelled(true);
        }
    }

}