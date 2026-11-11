import java.util.Locale;

/**
 * Перевозчик №1: тариф по ступеням веса.
 *
 * До 1 кг      — 300 руб.
 * До 3 кг      — 550 руб.
 * До 5 кг      — 750 руб.
 * Свыше 5 кг   — 750 руб. + 150 руб. за каждый начатый килограмм сверх пяти.
 *
 * Границы выбраны так: ровно 1 кг — это ещё первая ступень,
 * ровно 3 кг — вторая, ровно 5 кг — третья. Это осознанное решение,
 * и оно проверяется в SelfTest.
 */
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

        // Math.ceil, а не (int) weight: 5.01 кг — это уже один начатый
        // килограмм сверх пяти, а не ноль.
        int extraKg = (int) Math.ceil(weight - 5.0);
        return PRICE_UP_TO_5_KG + PRICE_PER_EXTRA_KG * extraKg;
    }
}
