package cl.dsoto.trading.factories;

/**
 * Created by des01c7 on 22-03-19.
 */

import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.jdbc2.optional.ConnectionPool;
import org.postgresql.jdbc2.optional.PoolingDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by root on 12-04-17.
 */
public class DataSourceFactory {

    static private final Logger logger = Logger.getLogger(DataSourceFactory.class.getName());

    private static final DataSourceFactory instance = new DataSourceFactory();

    PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();

    Connection connection = null;

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String PREFIX = "jdbc:postgresql://";
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB = "trading_dev";

    private static final String USER = "trader";
    private static final String PASS = "1q2w3e";

    /**
     * Constructor privado para el Singleton del Factory.
     */
    private DataSourceFactory() {
        try {
            //Class.forName(DRIVER);

            dataSource.setServerName(HOST);
            dataSource.setPortNumber(Integer.parseInt(PORT));
            dataSource.setDatabaseName(DB);
            dataSource.setUser(USER);
            dataSource.setPassword(PASS);
            dataSource.setLoginTimeout(20);
            dataSource.setSocketTimeout(20);

            //connection = DriverManager.getConnection(PREFIX + HOST + ":" + PORT + "/" + DB, USER, PASS);
            //connection = dataSource.getConnection();
            //connection.setAutoCommit(false);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener una conexión", e);
        }
    }

    public static DataSourceFactory getInstance() {
        return instance;
    }

    public synchronized Connection getConnection() {

        try {

            /*
            if (connection.isClosed()) {
                connection = DriverManager.getConnection(PREFIX + HOST + ":" + PORT + "/" + DB, USER, PASS);
            }
            */
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al conectarse a BD", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener una conexión", e);
        }

        return connection;
    }
}
