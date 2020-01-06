package DnsPriceParser.service;

import DnsPriceParser.data.Prices;
import DnsPriceParser.data.Shop;
import DnsPriceParser.data.Item;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.omg.CORBA.UShortSeqHelper;

import java.time.ZonedDateTime;
import java.util.*;

public class WorkbookParser {

    class ParsedSheet {
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

    public Prices parse(String city, ZonedDateTime excelDate, Workbook excel) {

        Map<String, Shop> shops = this.getShops(city, excel.getSheetAt(1));
        Prices prices = new Prices(city, excelDate, shops.size());

        for (int list = 1; list < excel.getNumberOfSheets(); list++) {
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
            int row = 1;
            for
            (
                    Row curRow = curPage.getRow(row);
                    curRow != null && !"Код".equals(curRow.getCell(0).getStringCellValue());
                    row += 1, curRow = curPage.getRow(row)
            )
            {
                Shop shop = parseShop(curRow.getCell(0).getStringCellValue(), city);
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

        // extract addresss
        String[] addressSplit = unparsedStr.split("Адрес:");
        if (addressSplit.length < 2) {
            throw new IllegalArgumentException("No argument Address in shop string");
        }
        shop.setAddress(addressSplit[1].trim());

        return shop;
    }

    private Item parseRow(Row row, String category, int shopCount) {
        Item item = new Item();

        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
            item.setCode(String.valueOf((long)row.getCell(0).getNumericCellValue()));
        } else {
            item.setCode(row.getCell(0).getStringCellValue());
        }
        item.setTitle(row.getCell(1).getStringCellValue());

        item.setShops(new LinkedList<String>());
        for (int colIndex = 2; colIndex < 2 + shopCount; colIndex++) {
            String shopCode = row.getCell(colIndex).getStringCellValue();
            if (!"".equals(shopCode)) {
                item.getShops().add(shopCode);
            }
        }

        item.setPrice((int)row.getCell((2 + shopCount)).getNumericCellValue());
        item.setBonus((int)row.getCell((2 + shopCount + 1)).getNumericCellValue());

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
                parsed.getItems().add(parseRow(curRow, currentCategory, shops.size()));
            }
        }

        return parsed;
    }

}
