package minigame.plugin.contest.backend.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Vengeancey on 12.7.2017..
 */
public abstract class Database
{

    /**
     *      Our getter for the connection object.
     */

    @Getter
    protected Connection connection = null;

    /**
     *
     * @return The connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */

    public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

    /**
     *
     *      A condition which has to be met
     *      upon executing an update or query.
     *
     */

    public boolean checkConnection()
    {
        try
        {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     *      Check whether the connection is closed.
     *
     */

    public boolean closeConnection()
    {
        try
        {
            if (this.connection == null)
                return false;
            this.connection.close();
            return true;
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     *      try/catch w/ resources will automatically
     *      close our statement for us.
     *
     */

    public final ResultSet query(final String path)
    {
        ResultSet result = null;
        try (final Statement statement = this.connection.createStatement())
        {
            result = statement.executeQuery(path);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     *      try/catch w/ resources will automatically
     *      close our statement for us.
     *
     */

    public final int update(final String path)
    {
        int result = -1;
        try (final Statement statement = this.connection.createStatement())
        {
            result = statement.executeUpdate(path);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}