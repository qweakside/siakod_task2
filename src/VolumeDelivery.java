import java.util.Locale;

/**
 * Перевозчик №2: тариф по объёму.
 *
 * 900 руб. за кубометр, но не меньше 250 руб. за отправление.
 * Предельный объём — 5 м³: больше перевозчик не берёт.
 */
public class VolumeDelivery implements DeliveryStrategy {
    private static final double MAX_VOLUME_M3 = 5.0;
    private static final int PRICE_PER_M3 = 900;
    private static final int MIN_PRICE = 250;

    @Override
    public String getName() {
        return "По объёму";
    }

    @Override
    public String getRefusalReason(Parcel parcel) {
        if (parcel.getVolume() > MAX_VOLUME_M3) {
            return String.format(Locale.ROOT,
                    "объём %.2f м³ больше предельных %.0f м³",
                    parcel.getVolume(), MAX_VOLUME_M3);
        }
        return null;
    }

    @Override
    public int calculate(Parcel parcel) {
        String refusal = getRefusalReason(parcel);
        if (refusal != null) {
            throw new IllegalStateException("Нельзя считать цену при отказе: " + refusal);
        }

        int byVolume = (int) Math.round(parcel.getVolume() * PRICE_PER_M3);
        return Math.max(MIN_PRICE, byVolume);
    }
}
