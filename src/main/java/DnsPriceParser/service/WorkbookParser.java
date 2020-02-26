package DnsPriceParser.service;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Shop;
import DnsPriceParser.data.Item;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.time.LocalDate;
import java.util.*;

public class WorkbookParser {

    /* Data example
    Сеть магазинов «DNS»
    М1 — ТЦ «Домино», тел. . Режим работы: Пн-Вс с 10:00 до 20:00.  Адрес: г. Брянск, пр-т Станке Димитрова, д. 75, корп. 2 этаж
    М2 — ТД «Весна», тел. . Режим работы: Пн-Вс с 10:00 до 21:00.  Адрес: г. Брянск, 3 Интернационала, д. 17А
    М3 — ТД «Стройлон», тел. . Режим работы: Пн-Вс с 09:00 до 20:00.  Адрес: г. Брянск, ул. Бурова, д. 12А
    М4 — на «Электронике», тел. . Режим работы: Пн-Вс с 10:00 до 21:00.  Адрес: г. Брянск, ул. Красноармейская, 170
    * */

    static class ParsedSheet {
        private String title;
        private List<String> categories;
        private List<Shop> shops;
        private List<Item> items;

        public ParsedSheet() {
            this.categories = new LinkedList<String>();
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Shop> getShops() {
            return shops;
        }

        public void setShops(List<Shop> shops) {
            this.shops = shops;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public List<String> getCategories() {
            return categories;
        }

    }

    private static final int SHOPS_BEGIN_ROW = 1;
    private static final int SHOP_DATA_CELL = 0;
    private static final int FIRST_DATA_SHEET = 1;

    private static final int PRICE_CODE_CELL = 0;
    private static final int PRICE_TITLE_CELL = 1;
    private static final int PRICE_SHOPS_FIRST_SHELL = 2;
    private static final int PRICE_PRICE_OFFSET = 0;
    private static final int PRICE_BONUS_OFFSET = 1;

    public Prices parse(String city, LocalDate excelDate, Workbook excel) {

        Map<String, Shop> shops = this.getShops(city, excel.getSheetAt(1));
        Prices prices = new Prices(city, excelDate, shops.size());

        for (int list = FIRST_DATA_SHEET; list < excel.getNumberOfSheets(); list++) {
            Sheet currentSheet = excel.getSheetAt(list);
            ParsedSheet sheet = parseSheet(city, currentSheet);

            prices.extendShops(sheet.shops);
            prices.getItems().addAll(sheet.items);

        }

        return prices;
    }

    private Map<String, Shop> getShops(String city, Sheet curPage) {
        Map<String, Shop> shops = new HashMap<String, Shop>();
        if (curPage != null) {
            int row = SHOPS_BEGIN_ROW;
            for
            (
                    Row curRow = curPage.getRow(row);
                    curRow != null && !"Код".equals(curRow.getCell(SHOP_DATA_CELL).getStringCellValue());
                    row += 1, curRow = curPage.getRow(row)
            )
            {
                Shop shop = parseShop(curRow.getCell(SHOP_DATA_CELL).getStringCellValue(), city);
                shops.put(shop.getCode(), shop);
            }

        }
        return shops;
    }

    private Shop parseShop(String unparsedStr, String city) throws IllegalArgumentException {
        if (unparsedStr == null) return null;
        Shop shop = new Shop(city);

        // extract code

        String[] codeSplit = unparsedStr.split("—");

        if (codeSplit.length < 2) {
            throw new IllegalArgumentException("No argument Code in shop string");
        }
        shop.setCode(codeSplit[0].trim());

        // extract title
        String[] titleSplit = codeSplit[1].split(",");
        if (titleSplit.length < 2) {
            throw new IllegalArgumentException("No argument Title in shop string");
        }
        shop.setTitle(titleSplit[0].trim());

        // extract schedule
        String[] scheduleSplit = unparsedStr.split("Режим работы:");
        if (scheduleSplit.length < 2) {
            throw new IllegalArgumentException("No argument Schedule in shop string");
        }
        scheduleSplit = scheduleSplit[1].split("\\.", 2);
        if (scheduleSplit.length < 2) {
            throw new IllegalArgumentException("Wrong format of Schedule in shop string");
        }
        shop.setSchedule(scheduleSplit[0].trim());

        // extract address
        String[] addressSplit = unparsedStr.split("Адрес:");
        if (addressSplit.length < 2) {
            throw new IllegalArgumentException("No argument Address in shop string");
        }
        shop.setAddress(addressSplit[1].trim());

        return shop;
    }

    private Item parseRow(Row row, int shopCount) {
        Item item = new Item();

        if (row.getCell(PRICE_CODE_CELL).getCellType() == Cell.CELL_TYPE_NUMERIC) {
            item.setCode(String.valueOf((long)row.getCell(PRICE_CODE_CELL).getNumericCellValue()));
        } else {
            item.setCode(row.getCell(PRICE_CODE_CELL).getStringCellValue());
        }
        item.setTitle(row.getCell(PRICE_TITLE_CELL).getStringCellValue());

        item.setShops(new LinkedList<String>());
        for (int colIndex = PRICE_SHOPS_FIRST_SHELL; colIndex < PRICE_SHOPS_FIRST_SHELL + shopCount; colIndex++) {
            String shopCode = row.getCell(colIndex).getStringCellValue();
            if (!"".equals(shopCode)) {
                item.getShops().add(shopCode);
            }
        }

        item.setPrice((int)row.getCell((PRICE_SHOPS_FIRST_SHELL + shopCount + PRICE_PRICE_OFFSET)).getNumericCellValue());
        item.setBonus((int)row.getCell((PRICE_SHOPS_FIRST_SHELL + shopCount + PRICE_BONUS_OFFSET)).getNumericCellValue());

        return item;
    }

    private String getCategory(Row row) {
        return row.getCell(1).getStringCellValue();
    }

    private ParsedSheet parseSheet(String city, Sheet sheet) {
        Map<String, Shop> shops = getShops(city, sheet);
        ParsedSheet parsed = new ParsedSheet();
        parsed.setItems(new LinkedList<Item>());
        parsed.setTitle(sheet.getSheetName());

        parsed.setShops(new ArrayList<Shop>(shops.values()));

        int rowIndex = shops.size() + 1;
        String currentCategory = "";

        for (
                Row curRow = sheet.getRow(rowIndex) ;
                rowIndex < sheet.getLastRowNum();
                rowIndex++, curRow = sheet.getRow(rowIndex)
        ) {
            if (curRow.getCell(0).getCellType() == Cell.CELL_TYPE_STRING
                && "Код".equals(curRow.getCell(0).getStringCellValue())) {
                currentCategory = getCategory(curRow);
                parsed.getCategories().add(currentCategory);
            } else {
                Item item = parseRow(curRow, shops.size());
                item.setCategory(currentCategory);
                parsed.getItems().add(item);
            }
        }

        return parsed;
    }

}
