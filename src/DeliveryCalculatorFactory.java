/**
 * Простая фабрика: собирает готовый калькулятор со стандартным набором
 * перевозчиков в одном месте.
 *
 * Это не обязательная часть задания, а небольшой бонус: если завтра
 * появится пятый перевозчик, достаточно добавить одну строку здесь,
 * и все точки запуска (Main, Demo, SelfTest) получат его автоматически.
 */
public final class DeliveryCalculatorFactory {

    private DeliveryCalculatorFactory() {
        // Утилитарный класс: создавать его экземпляры не нужно.
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
