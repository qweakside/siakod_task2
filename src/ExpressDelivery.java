/**
 * Перевозчик №4: экспресс.
 *
 * 900 руб. плюс 100 руб. за каждый начатый килограмм.
 * Своих пределов у него нет: он берёт любую посылку.
 */
public class ExpressDelivery implements DeliveryStrategy {
    private static final int BASE_PRICE = 900;
    private static final int PRICE_PER_STARTED_KG = 100;

    @Override
    public String getName() {
        return "Экспресс";
    }

    @Override
    public String getRefusalReason(Parcel parcel) {
        // Экспресс не отказывается никогда.
        return null;
    }

    @Override
    public int calculate(Parcel parcel) {
        // ceil, а не (int): 0.1 кг — это уже один начатый килограмм.
        int startedKg = (int) Math.ceil(parcel.getWeight());
        return BASE_PRICE + PRICE_PER_STARTED_KG * startedKg;
    }
}
