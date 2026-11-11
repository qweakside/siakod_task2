/**
 * Демонстрация: несколько разных посылок, чтобы было видно,
 * что при разных весе, объёме и зоне побеждают разные перевозчики.
 *
 * Запуск: java Demo
 */
public class Demo {

    public static void main(String[] args) {
        DeliveryCalculator calculator = DeliveryCalculatorFactory.createStandard();

        show(calculator, new Parcel(0.5, 0.1, "Город"));
        show(calculator, new Parcel(2.0, 2.0, "Область"));
        show(calculator, new Parcel(6.0, 1.0, "Город"));
        show(calculator, new Parcel(40.0, 6.0, "Заграница"));
    }

    private static void show(DeliveryCalculator calculator, Parcel parcel) {
        System.out.println(parcel);
        for (DeliveryOffer offer : calculator.calculateAll(parcel)) {
            System.out.println("  " + offer);
        }

        DeliveryOffer best = calculator.findBest(parcel);
        if (best == null) {
            System.out.println("  Победитель: никто не берётся");
        } else {
            System.out.println("  Победитель: " + best);
        }
        System.out.println();
    }
}
