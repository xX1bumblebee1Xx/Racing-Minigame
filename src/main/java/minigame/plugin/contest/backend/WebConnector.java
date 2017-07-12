package minigame.plugin.contest.backend;

import minigame.plugin.contest.backend.database.MySQL;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public class WebConnector
{
    private final MySQL database;

    public WebConnector(final MySQL database)
    {
        this.database = database;
        this.database.openConnection();
        this.database.update("CREATE TABLE IF NOT EXISTS game (id INT NOT NULL AUTO_INCREMENT, name TINYTEXT, uuid MEDIUMTEXT, kit TINYTEXT, coins INT, PRIMARY KEY(id));");
    }

    //todo a bit later...
}