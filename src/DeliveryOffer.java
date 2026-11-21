public class DeliveryOffer {
    private final String carrierName;
    private final int price;
    private final String refusalReason;

    private DeliveryOffer(String carrierName, int price, String refusalReason) {
        this.carrierName = carrierName;
        this.price = price;
        this.refusalReason = refusalReason;
    }

    public static DeliveryOffer accepted(String carrierName, int price) {
        return new DeliveryOffer(carrierName, price, null);
    }

    public static DeliveryOffer refused(String carrierName, String refusalReason) {
        return new DeliveryOffer(carrierName, 0, refusalReason);
    }

    public boolean isAccepted() {
        return refusalReason == null;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public int getPrice() {
        return price;
    }

    public String getRefusalReason() {
        return refusalReason;
    }

    @Override
    public String toString() {
        if (isAccepted()) {
            return carrierName + " — " + price + " руб.";
        }
        return carrierName + " — отказ: " + refusalReason;
    }
}
