package DatabaseImport;

import DatabaseImport.Config;

import java.sql.Connection;
import java.sql.SQLException;

public class SimpleDbConfig {

    private SimpleDbConfig() { }

    public static Config getSimpleConfig() {
        return new Config("jdbc:postgresql://localhost/dns_dwh", "dns_admin", "123qwe");
    }

    public static Connection getSimpleConnection() throws SQLException {
        return (new PgJdbcFactory()).getConnection(getSimpleConfig());
    }


}
