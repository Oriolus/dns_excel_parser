package DatabaseImport;

import DatabaseImport.JsonImport.JCategoryImporter;
import DatabaseImport.JsonImport.JCityImporter;
import DatabaseImport.JsonImport.JItemImporter;
import JsonedDns.Exporter.FileHelper;
import JsonedDns.Exporter.JData.JCategory;
import JsonedDns.Reader.CategoryReader;
import JsonedDns.Reader.CityReader;
import JsonedDns.Reader.ItemReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class SimpleEtl {

    private static final Logger logger = LogManager.getLogger(SimpleEtl.class);

    public void mergeCities(PgJdbcFactory jdbcFactory) throws SQLException {
        String mergeQuery =
"with non_existing as\n" +
        "(\n" +
        "  select\n" +
        "    stg_city.title as title\n" +
        "  from\n" +
        "    stg.city stg_city\n" +
        "\n" +
        "    left join dwh.city dwh_city on\n" +
        "      dwh_city.title = stg_city.title\n" +
        "  where\n" +
        "    dwh_city.id is null\n" +
        ")\n" +
        "insert into\n" +
        "  dwh.city (title)\n" +
        "select\n" +
        "  title\n" +
        "from\n" +
        "  non_existing\n" +
        ";\n";

        Connection conn = jdbcFactory.getSimpleConnection();

        try {
            Statement statement = conn.createStatement();
            statement.execute(mergeQuery);
            statement.execute("truncate table stg.city");
            statement.close();

        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }

    }

    public void mergeCategories(PgJdbcFactory jdbcFactory) throws SQLException{
        
        String mergeQuery =  "call dwh.merge_categories();";
        
        Connection conn = jdbcFactory.getSimpleConnection();

        try {
            Statement statement = conn.createStatement();
            statement.execute(mergeQuery);
            statement.close();
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        
    }


    public void processDay(String sourceFolder, LocalDate ofDay) throws IOException, SQLException {

        validateExisting(sourceFolder, ofDay);

        String categoriesFile = FileHelper.getCategoriesFilename(ofDay);
        String shopsFile = FileHelper.getShopsFileName(ofDay);
        String citiesFile = FileHelper.getCitiesFilename(ofDay);
        String itemsFile = FileHelper.getItemsFileName(ofDay);

        PgJdbcFactory jdbcFactory = new PgJdbcFactory();

        loadCategories(Paths.get(sourceFolder, categoriesFile), ofDay, jdbcFactory);

        loadCities(Paths.get(sourceFolder, citiesFile), ofDay, jdbcFactory);

        loadItems(Paths.get(sourceFolder, itemsFile), ofDay, jdbcFactory);
    }

    private void validateExisting(String sourceFolder, LocalDate ofDay) throws IOException {
        String categoriesFile = FileHelper.getCategoriesFilename(ofDay);
        String shopsFile = FileHelper.getShopsFileName(ofDay);
        String citiesFile = FileHelper.getCitiesFilename(ofDay);
        String itemsFile = FileHelper.getItemsFileName(ofDay);

        if (!Files.exists(Paths.get(sourceFolder, categoriesFile))) {
            throw new IOException(Paths.get(sourceFolder, categoriesFile).toString());
        }

        if (!Files.exists(Paths.get(sourceFolder, shopsFile))) {
            throw new IOException(Paths.get(sourceFolder, shopsFile).toString());
        }

        if (!Files.exists(Paths.get(sourceFolder, citiesFile))) {
            throw new IOException(Paths.get(sourceFolder, citiesFile).toString());
        }

        if (!Files.exists(Paths.get(sourceFolder, itemsFile))) {
            throw new IOException(Paths.get(sourceFolder, itemsFile).toString());
        }
    }
    
    private void loadCategories(Path filename, LocalDate ofDay, PgJdbcFactory jdbcFactory) throws IOException, SQLException {
        List<JCategory> categories = (new CategoryReader().do_import(filename.toString()));
        JCategoryImporter categoryImporter = new JCategoryImporter();
        categoryImporter.clearStg(jdbcFactory);
        categoryImporter.do_import(categories, ofDay, jdbcFactory);
        categoryImporter.merge(jdbcFactory);
    }

    private void loadCities(Path filename, LocalDate ofDay, PgJdbcFactory jdbcFactory) throws IOException, SQLException {
        List<String> cities = (new CityReader()).getCities(filename.toString());
        JCityImporter cityImporter = new JCityImporter();
        cityImporter.clearStg(jdbcFactory);
        cityImporter.do_import(cities, ofDay, jdbcFactory);
        cityImporter.merge(jdbcFactory);
    }

    private void loadItems(Path filename, LocalDate ofDay, PgJdbcFactory jdbcFactory) throws IOException, SQLException {
        try (BufferedReader reader = (new ItemReader()).getReader(filename.toString())) {
            JItemImporter itemImporter = new JItemImporter();
            itemImporter.clearStg(jdbcFactory);
            logger.info("before do_import");
            itemImporter.do_import(reader, ofDay, jdbcFactory, 5000);
            logger.info("before after");
        }
    }

    /*
create local temp table if not exists product_stg_info (
	"date" date
	, city varchar(256)
	, category varchar(1024)
	, title varchar(256)
	, code varchar(32)
	, price int
)
tablespace upg_dns;
    * */


}
