package DatabaseImport.JsonImport;

import DatabaseImport.PgJdbcFactory;
import JsonedDns.Exporter.JData.JCategory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JCategoryImporter {

    private static final int batchSize = 1000;

    public void do_import(List<JCategory> categories, LocalDate ofDate, PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            PreparedStatement statement = conn.prepareStatement("insert into stg.category (title, hierarchy, load_date) values (?, ?, ?)");

            int currentBatchSize = 0;
            for (JCategory category : categories) {
                statement.setString(1, category.getTitle());
                statement.setString(2, category.getHierarchy());
                statement.setDate(3, Date.valueOf(ofDate));
                statement.addBatch();
                currentBatchSize += 1;

                if (currentBatchSize >= JCategoryImporter.batchSize) {
                    statement.executeBatch();
                    statement.clearBatch();
                    currentBatchSize = 0;
                }
            }

            if (currentBatchSize != 0) {
                statement.executeBatch();
                statement.clearBatch();
                currentBatchSize = 0;
            }
            statement.close();
        }
    }

    public void merge(PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            String mergeQuery =  "call dwh.merge_categories();";
            PreparedStatement statement = conn.prepareStatement(mergeQuery);
            boolean execResult = statement.execute();
            statement.close();
        }
    }

    public void clearStg(PgJdbcFactory jdbcFactory) throws SQLException {
        try (Connection conn = jdbcFactory.getSimpleConnection()) {
            String clearQuery = "truncate table stg.category";
            PreparedStatement statement = conn.prepareStatement(clearQuery);
            boolean execResult = statement.execute();
            statement.close();
        }
    }

}
