package DatabaseImport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PgJdbcFactory {

    public Connection getConnection(Config dbConfig) throws SQLException {
        String url = dbConfig.getUrl();
        Properties props = new Properties();
        props.setProperty("user", dbConfig.getUser());
        props.setProperty("password", dbConfig.getPassword());
        Connection conn = DriverManager.getConnection(url, props);

        return conn;
    }

    public Connection getSimpleConnection() throws SQLException {
        return SimpleDbConfig.getSimpleConnection();
    }

}
