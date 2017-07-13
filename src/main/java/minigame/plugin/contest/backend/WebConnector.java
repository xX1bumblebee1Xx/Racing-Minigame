package minigame.plugin.contest.backend;

import minigame.plugin.contest.backend.database.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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
        this.database.update("CREATE TABLE IF NOT EXISTS game (id INT NOT NULL AUTO_INCREMENT, uuid MEDIUMTEXT, coins INT, PRIMARY KEY(id));");
    }

    public int getId(final UUID uuid)
    {
        int id = -1;
        try
        {
            final ResultSet resultSet = this.database.query(String.format("SELECT id FROM game WHERE uuid='%s';", uuid.toString()));
            id = resultSet.next() ? resultSet.getInt("id") : 0;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }

    private boolean exists(final UUID uuid)
    {
        try
        {
            return this.database.query(String.format("SELECT * FROM game WHERE uuid='%s';", uuid.toString())).next();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(final UUID uuid)
    {
        if (!exists(uuid))
            this.database.update(String.format("INSERT INTO game (uuid, coins) VALUES ('%s', '0');", uuid.toString()));
    }

    public int getCoins(final UUID uuid)
    {
        int coins = -1;
        try
        {
            ResultSet resultSet = this.database.query(String.format("SELECT coins FROM game WHERE uuid='%s';", uuid.toString()));
            coins = resultSet.next() ? resultSet.getInt("coins") : 0;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return coins;
    }

    public void incrementCoins(final UUID uuid, int amount)
    {
        setCoins(uuid, amount, true);
    }

    public void setCoins(final UUID uuid, int amount)
    {
        setCoins(uuid, amount, false);
    }

    public void setCoins(final UUID uuid, int amount, boolean increment)
    {
        this.database.update(String.format("UPDATE game SET coins=" + (increment ? getCoins(uuid) + amount : amount) + " WHERE uuid='%s';", uuid.toString()));
    }
}