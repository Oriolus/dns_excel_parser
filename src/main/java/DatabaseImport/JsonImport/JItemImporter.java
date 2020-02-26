package DatabaseImport.JsonImport;

import DatabaseImport.PgJdbcFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class JItemImporter {

    public void do_import(BufferedReader reader, LocalDate ofDay, PgJdbcFactory jdbcFactory, int batchSize) throws SQLException, IOException {

        try (Connection conn = jdbcFactory.getSimpleConnection()) {

            conn.setAutoCommit(false);

            PreparedStatement statement = conn.prepareStatement("insert into stg.product_item (item, load_date) values (?::json, ?)");

            int currentBatchSize = 0;
            String read = null;

            while ((read = reader.readLine()) != null) {
                statement.setString(1, read);
                statement.setDate(2, Date.valueOf(ofDay));
                statement.addBatch();
                currentBatchSize += 1;

                if (currentBatchSize >= batchSize) {
                    statement.executeBatch();
                    conn.commit();
                    statement.clearBatch();
                    currentBatchSize = 0;
                }
            }
            if (currentBatchSize > 0) {
                statement.executeBatch();
                conn.commit();
                statement.clearBatch();
                currentBatchSize = 0;
            }
            statement.close();

        } finally {
            reader.close();
        }
    }

    public void clearStg(PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            String clearQuery = "truncate table stg.product_item";
            PreparedStatement statement = conn.prepareStatement(clearQuery);
            boolean execResult = statement.execute();
            statement.close();
        }
    }

}
