public class ZoneDelivery implements DeliveryStrategy {

    @Override
    public String getRefusalReason(double weight, double volume, String zone) {
        if (zone.equals("Заграница")) {
            return "Доставка в заграницу недоступна";
        } else {
            return null;
        }

    }

    @Override
    public int calculate(double weight, double volume, String zone) {
        if (getRefusalReason(weight, volume, zone) == null) {
            if ("Город".equalsIgnoreCase(zone)) {
                return 300;
            }
            else if ("Область".equalsIgnoreCase(zone)) {
                return 600;
            } else return 1200;
        }
        return 0;
    }

    @Override
    public String getName() {
        return "Доставка по зонам";
    }
}
