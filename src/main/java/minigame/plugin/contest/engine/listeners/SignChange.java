package minigame.plugin.contest.engine.listeners;

import minigame.plugin.contest.engine.Arena;
import minigame.plugin.contest.Main;
import minigame.plugin.contest.Util;
import minigame.plugin.contest.engine.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class SignChange implements Listener {

    Util u = new Util();

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String[] lines = e.getLines();
        List<String> configLines = Main.getInstance().getConfig().getStringList("sign-format");

        //[Races]
        //arenaName
        //action
        if (!(lines.length > 2))
            return;

        for (int i = 0; i < 3; i++)
            lines[i] = ChatColor.stripColor(lines[i]);

        if (!lines[0].equalsIgnoreCase("[Races]"))
            return;

        Arena a = ArenaManager.getManager().getArena(lines[1]);
        if (a == null) {
            e.setLine(0, "[Races]");
            e.setLine(1, ChatColor.RED + "Error!");
            e.setLine(2, "");
            e.setLine(3, "");
            return;
        }

        for (int i = 0; i <= 2; i++) {
            e.setLine(i, u.parseSignLine(configLines.get(i), a, lines[2]));
        }
    }
}