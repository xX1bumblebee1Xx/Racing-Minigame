package minigame.plugin.contest.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public class MySQL extends Database
{

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private final String driver = "com.mysql.jdbc.Driver";

    public MySQL(String host, int port, String username, String password, String database)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public Connection openConnection()
    {
        try
        {
            if (checkConnection())
                return connection;
            final String URL = "jdbc:mysql://%s:%d/%s"; //host, port, database
            Class.forName(this.driver);
            connection = DriverManager.getConnection(String.format(URL, this.host, this.port, this.database), this.username, this.password);

            return connection;
        } catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}