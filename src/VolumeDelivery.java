public class VolumeDelivery implements DeliveryStrategy{

    @Override
    public String getRefusalReason(double weight, double volume, String zone) {
        if (volume>5){
            return "Обьем превышает 5 м**3";
        }
        else{
        return null;}
    }

    @Override
    public int calculate(double weight, double volume, String zone) {
        if (getRefusalReason(weight,volume,zone)==null){
            Math.max(250, (int) Math.round(volume * 9000));
        }
        return 0;
    }

    @Override
    public String getName() {
        return "Доставка по обьему";
    }
}
