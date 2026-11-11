public class WeightStepDelivery implements DeliveryStrategy{
    @Override
    public String getRefusalReason(double weight, double volume, String zone) {
        if (weight>30){
            return "вес больше 30кг";
        }else{
        return null;}
    }

    @Override
    public int calculate(double weight, double volume, String zone) {
        if (getRefusalReason(weight,volume,zone)==null) {
            if (weight<=1)return 300;
            else if (weight<=3) {
                return 550;
            } else if (weight<=5) {
                return 750;
            }
            else return 750+150*((int) Math.ceil(weight)-5);
        }
        return 0;
    }

    @Override
    public String getName() {
        return "Доставка по весу";
    }
}
