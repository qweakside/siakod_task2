public final class DeliveryCalculatorFactory {

    private DeliveryCalculatorFactory() {
    }

    public static DeliveryCalculator createStandard() {
        DeliveryCalculator calculator = new DeliveryCalculator();
        calculator.addStrategy(new WeightStepDelivery());
        calculator.addStrategy(new VolumeDelivery());
        calculator.addStrategy(new ZoneDelivery());
        calculator.addStrategy(new ExpressDelivery());
        return calculator;
    }
}
