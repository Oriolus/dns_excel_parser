package Exporter.JsonExporter;

import DnsPriceParser.data.Item;
import DnsPriceParser.data.Shop;
import com.sun.istack.internal.NotNull;

import java.time.ZonedDateTime;
import java.util.Date;

public class JConverter {

    private JConverter() { }

    public static JItem toJItem(@NotNull Item from, @NotNull String city, @NotNull Date date) {
        return new JItem(
                date,
                city,
                from.getCategory(),
                from.getCode(),
                from.getTitle(),
                from.getPrice(),
                from.getBonus(),
                from.getShops()
        );
    }

    public static JShop toJShop(@NotNull Shop from, @NotNull Date date) {
        return new JShop(
                date,
                from.getCity(),
                from.getAddress(),
                from.getSchedule(),
                from.getTitle(),
                from.getCode()
        );
    }

}
