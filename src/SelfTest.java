import java.util.List;

/**
 * Ручные тесты без внешних библиотек.
 *
 * Запуск: java SelfTest
 *
 * Проверяются именно границы: 1 кг, 3 кг, 5 кг, 6 кг, предельные 30 кг,
 * предельные 5 м³, а также выбор победителя для разных посылок.
 * Если все проверки прошли, программа завершится с кодом 0,
 * иначе — с кодом 1.
 */
public class SelfTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Проверка тарифа по весу:");
        testWeightSteps();

        System.out.println("Проверка тарифа по объёму:");
        testVolume();

        System.out.println("Проверка тарифа по зонам:");
        testZones();

        System.out.println("Проверка экспресса:");
        testExpress();

        System.out.println("Проверка выбора победителя:");
        testCalculator();

        System.out.println("Проверка параметров посылки:");
        testParcelValidation();

        System.out.println();
        System.out.println("Итого: пройдено " + passed + ", провалено " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testWeightSteps() {
        DeliveryStrategy strategy = new WeightStepDelivery();

        checkPrice("1 кг — верхняя граница первой ступени", strategy, 1.0, 300);
        checkPrice("1.01 кг — уже вторая ступень", strategy, 1.01, 550);
        checkPrice("3 кг — верхняя граница второй ступени", strategy, 3.0, 550);
        checkPrice("3.01 кг — уже третья ступень", strategy, 3.01, 750);
        checkPrice("5 кг — верхняя граница третьей ступени", strategy, 5.0, 750);
        checkPrice("5.01 кг — один начатый кг сверх пяти", strategy, 5.01, 900);
        checkPrice("6 кг — ровно один начатый кг сверх пяти", strategy, 6.0, 900);
        checkPrice("6.01 кг — два начатых кг сверх пяти", strategy, 6.01, 1050);
        checkPrice("30 кг — предел, ещё берёт", strategy, 30.0, 4500);
        checkRefusal("30.01 кг — отказ по весу", strategy, 30.01, "больше предельных");
    }

    private static void testVolume() {
        DeliveryStrategy strategy = new VolumeDelivery();

        checkVolumePrice("0.1 м³ — срабатывает минимум 250", strategy, 0.1, 250);
        checkVolumePrice("0.5 м³ — 450", strategy, 0.5, 450);
        checkVolumePrice("5 м³ — предел, 4500", strategy, 5.0, 4500);
        checkVolumeRefusal("5.01 м³ — отказ по объёму", strategy, 5.01, "больше предельных");
    }

    private static void testZones() {
        DeliveryStrategy strategy = new ZoneDelivery();

        checkPriceForZone("Город — 300", strategy, "Город", 300);
        checkPriceForZone("город в нижнем регистре — 300", strategy, "город", 300);
        checkPriceForZone("Область — 600", strategy, "Область", 600);
        checkPriceForZone("Деревня — 1200", strategy, "Деревня", 1200);
        checkRefusalForZone("Заграница — отказ", strategy, "Заграница", "за границу");
    }

    private static void testExpress() {
        DeliveryStrategy strategy = new ExpressDelivery();

        checkPrice("1 кг — 1000", strategy, 1.0, 1000);
        checkPrice("1.01 кг — 1100", strategy, 1.01, 1100);
        checkPrice("3 кг — 1200", strategy, 3.0, 1200);
        checkPrice("3.01 кг — 1300", strategy, 3.01, 1300);
        checkPrice("6 кг — 1500", strategy, 6.0, 1500);
    }

    private static void testCalculator() {
        DeliveryCalculator calculator = DeliveryCalculatorFactory.createStandard();

        checkWinner("0.5 кг, 0.1 м³, Город — побеждает объём",
                calculator, 0.5, 0.1, "Город", "По объёму");
        checkWinner("2 кг, 2 м³, Область — побеждает вес",
                calculator, 2.0, 2.0, "Область", "По весу (ступени)");
        checkWinner("6 кг, 1 м³, Город — побеждает зона",
                calculator, 6.0, 1.0, "Город", "По зонам");
        checkWinner("40 кг, 6 м³, Заграница — побеждает экспресс",
                calculator, 40.0, 6.0, "Заграница", "Экспресс");

        List<DeliveryOffer> offers = calculator.calculateAll(new Parcel(2.0, 2.0, "Область"));
        checkEquals("калькулятор вернул 4 предложения", 4, offers.size());
        checkEquals("вес 2 кг стоит 550", 550, findPrice(offers, "По весу (ступени)"));
        checkEquals("объём 2 м³ стоит 1800", 1800, findPrice(offers, "По объёму"));
        checkEquals("зона Область стоит 600", 600, findPrice(offers, "По зонам"));
        checkEquals("экспресс 2 кг стоит 1100", 1100, findPrice(offers, "Экспресс"));

        DeliveryCalculator onlyWeight = new DeliveryCalculator();
        onlyWeight.addStrategy(new WeightStepDelivery());
        DeliveryOffer noWinner = onlyWeight.findBest(new Parcel(40.0, 1.0, "Город"));
        checkTrue("если все отказались, победителя нет", noWinner == null);
    }

    private static void testParcelValidation() {
        checkThrows("отрицательный вес запрещён", () -> new Parcel(-1.0, 1.0, "Город"));
        checkThrows("нулевой объём запрещён", () -> new Parcel(1.0, 0.0, "Город"));
        checkThrows("пустая зона запрещена", () -> new Parcel(1.0, 1.0, "   "));
    }

    private static void checkPrice(String name, DeliveryStrategy strategy,
                                   double weight, int expected) {
        Parcel parcel = new Parcel(weight, 1.0, "Город");
        checkEquals(name, expected, strategy.calculate(parcel));
    }

    private static void checkPriceForZone(String name, DeliveryStrategy strategy,
                                          String zone, int expected) {
        Parcel parcel = new Parcel(1.0, 1.0, zone);
        checkEquals(name, expected, strategy.calculate(parcel));
    }

    private static void checkVolumePrice(String name, DeliveryStrategy strategy,
                                         double volume, int expected) {
        Parcel parcel = new Parcel(1.0, volume, "Город");
        checkEquals(name, expected, strategy.calculate(parcel));
    }

    private static void checkVolumeRefusal(String name, DeliveryStrategy strategy,
                                           double volume, String expectedPart) {
        Parcel parcel = new Parcel(1.0, volume, "Город");
        checkRefusalReason(name, strategy, parcel, expectedPart);
    }

    private static void checkRefusal(String name, DeliveryStrategy strategy,
                                     double weight, String expectedPart) {
        Parcel parcel = new Parcel(weight, 1.0, "Город");
        checkRefusalReason(name, strategy, parcel, expectedPart);
    }

    private static void checkRefusalForZone(String name, DeliveryStrategy strategy,
                                            String zone, String expectedPart) {
        Parcel parcel = new Parcel(1.0, 1.0, zone);
        checkRefusalReason(name, strategy, parcel, expectedPart);
    }

    private static void checkRefusalReason(String name, DeliveryStrategy strategy,
                                           Parcel parcel, String expectedPart) {
        String reason = strategy.getRefusalReason(parcel);
        if (reason == null) {
            fail(name, "ожидался отказ, но его нет");
        } else if (!reason.contains(expectedPart)) {
            fail(name, "в причине нет \"" + expectedPart + "\": " + reason);
        } else {
            pass(name);
        }
    }

    private static void checkWinner(String name, DeliveryCalculator calculator,
                                    double weight, double volume, String zone,
                                    String expectedCarrier) {
        DeliveryOffer best = calculator.findBest(new Parcel(weight, volume, zone));
        if (best == null) {
            fail(name, "победитель не найден");
        } else {
            checkEquals(name, expectedCarrier, best.getCarrierName());
        }
    }

    private static int findPrice(List<DeliveryOffer> offers, String carrierName) {
        for (DeliveryOffer offer : offers) {
            if (offer.getCarrierName().equals(carrierName)) {
                return offer.getPrice();
            }
        }
        throw new IllegalArgumentException("Нет перевозчика " + carrierName);
    }

    private static void checkEquals(String name, int expected, int actual) {
        if (expected == actual) {
            pass(name);
        } else {
            fail(name, "ожидалось " + expected + ", получено " + actual);
        }
    }

    private static void checkEquals(String name, String expected, String actual) {
        if (expected.equals(actual)) {
            pass(name);
        } else {
            fail(name, "ожидалось \"" + expected + "\", получено \"" + actual + "\"");
        }
    }

    private static void checkTrue(String name, boolean condition) {
        if (condition) {
            pass(name);
        } else {
            fail(name, "условие ложно");
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try {
            action.run();
            fail(name, "ожидалось исключение, но его не было");
        } catch (IllegalArgumentException e) {
            pass(name);
        }
    }

    private static void pass(String name) {
        passed++;
        System.out.println("  OK   " + name);
    }

    private static void fail(String name, String details) {
        failed++;
        System.out.println("  FAIL " + name + " — " + details);
    }
}
