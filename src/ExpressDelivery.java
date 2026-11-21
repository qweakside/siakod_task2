public class ExpressDelivery implements DeliveryStrategy {
    private static final int BASE_PRICE = 900;
    private static final int PRICE_PER_STARTED_KG = 100;

    @Override
    public String getName() {
        return "Экспресс";
    }

    @Override
    public String getRefusalReason(Parcel parcel) {
        return null;
    }

    @Override
    public int calculate(Parcel parcel) {
        int startedKg = (int) Math.ceil(parcel.getWeight());
        return BASE_PRICE + PRICE_PER_STARTED_KG * startedKg;
    }
}
