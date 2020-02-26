package DatabaseImport.JsonImport;

import DatabaseImport.PgJdbcFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JCityImporter {

    public void do_import(List<String> cities, LocalDate ofDay, PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            PreparedStatement statement = conn.prepareStatement("insert into stg.city (title, load_date) values (?, ?)");
            for (String city : cities) {
                statement.setString(1, city);
                statement.setDate(2, Date.valueOf(ofDay));
                statement.addBatch();
            }
            statement.executeBatch();
            statement.close();
        }
    }

    public void merge(PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            String mergeQuery =  "call dwh.merge_cities();";
            PreparedStatement statement = conn.prepareStatement(mergeQuery);
            boolean execResult = statement.execute();
            statement.close();
        }
    }

    public void clearStg(PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()){
            String clearQuery = "truncate table stg.city";
            PreparedStatement statement = conn.prepareStatement(clearQuery);
            boolean execResult = statement.execute();
            statement.close();
        }
    }

}
