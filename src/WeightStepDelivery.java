import java.util.Locale;

public class WeightStepDelivery implements DeliveryStrategy {
    private static final double MAX_WEIGHT_KG = 30.0;
    private static final int PRICE_UP_TO_1_KG = 300;
    private static final int PRICE_UP_TO_3_KG = 550;
    private static final int PRICE_UP_TO_5_KG = 750;
    private static final int PRICE_PER_EXTRA_KG = 150;

    @Override
    public String getName() {
        return "По весу (ступени)";
    }

    @Override
    public String getRefusalReason(Parcel parcel) {
        if (parcel.getWeight() > MAX_WEIGHT_KG) {
            return String.format(Locale.ROOT,
                    "вес %.2f кг больше предельных %.0f кг",
                    parcel.getWeight(), MAX_WEIGHT_KG);
        }
        return null;
    }

    @Override
    public int calculate(Parcel parcel) {
        String refusal = getRefusalReason(parcel);
        if (refusal != null) {
            throw new IllegalStateException("Нельзя считать цену при отказе: " + refusal);
        }

        double weight = parcel.getWeight();
        if (weight <= 1.0) {
            return PRICE_UP_TO_1_KG;
        }
        if (weight <= 3.0) {
            return PRICE_UP_TO_3_KG;
        }
        if (weight <= 5.0) {
            return PRICE_UP_TO_5_KG;
        }

        int extraKg = (int) Math.ceil(weight - 5.0);
        return PRICE_UP_TO_5_KG + PRICE_PER_EXTRA_KG * extraKg;
    }
}
