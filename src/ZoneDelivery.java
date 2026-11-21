import java.util.Locale;

public class ZoneDelivery implements DeliveryStrategy {
    private static final int CITY_PRICE = 300;
    private static final int REGION_PRICE = 600;
    private static final int OTHER_PRICE = 1200;

    @Override
    public String getName() {
        return "По зонам";
    }

    @Override
    public String getRefusalReason(Parcel parcel) {
        if (normalize(parcel.getZone()).equals("заграница")) {
            return "доставка за границу не выполняется";
        }
        return null;
    }

    @Override
    public int calculate(Parcel parcel) {
        String refusal = getRefusalReason(parcel);
        if (refusal != null) {
            throw new IllegalStateException("Нельзя считать цену при отказе: " + refusal);
        }

        String zone = normalize(parcel.getZone());
        if (zone.equals("город")) {
            return CITY_PRICE;
        }
        if (zone.equals("область")) {
            return REGION_PRICE;
        }
        return OTHER_PRICE;
    }

    private String normalize(String zone) {
        return zone.trim().toLowerCase(Locale.ROOT);
    }
}
