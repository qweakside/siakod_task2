public class ExpressDelivery implements DeliveryStrategy{
    @Override
    public String getRefusalReason(double weight, double volume, String zone) {
        return null;
    }
    @Override
    public int calculate(double weight, double volume, String zone) {
        if(getRefusalReason(weight,volume,zone)==null) {
            int result = (int) Math.ceil(weight);
            return 900 + result * 100;
        }
        else{
            return 0;
        }
    }

    @Override
    public String getName() {
        return "Экспресс доставка";
    }
}
