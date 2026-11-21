import java.util.List;

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

        System.out.println("Проверка правила ничьей:");
        testTieBreak();

        System.out.println("Проверка контракта калькулятора:");
        testCalculatorValidation();

        System.out.println("Проверка контракта стратегий:");
        testRefusedCalculateContract();

        System.out.println("Проверка параметров посылки:");
        testParcelValidation();

        System.out.println("Проверка инвариантов предложения:");
        testOfferInvariants();

        System.out.println();
        System.out.println("Итого: пройдено " + passed + ", провалено " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testWeightSteps() {
        DeliveryStrategy strategy = new WeightStepDelivery();

        checkPrice("0.5 кг — первая ступень", strategy, 0.5, 300);
        checkPrice("2 кг — вторая ступень", strategy, 2.0, 550);
        checkPrice("4 кг — третья ступень", strategy, 4.0, 750);
        checkPrice("7 кг — 750 + 2 * 150", strategy, 7.0, 1050);

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

        checkVolumePrice("0.01 м³ — срабатывает минимум 250", strategy, 0.01, 250);
        checkVolumePrice("0.1 м³ — 900", strategy, 0.1, 900);
        checkVolumePrice("0.5 м³ — 4500", strategy, 0.5, 4500);
        checkVolumePrice("1 м³ — 9000", strategy, 1.0, 9000);
        checkVolumePrice("5 м³ — предел, 45000", strategy, 5.0, 45000);

        checkTrue("минимум по объёму не нарушается на 0.02 м³",
                strategy.calculate(new Parcel(1.0, 0.02, "Город")) == 250);

        checkVolumeRefusal("5.01 м³ — отказ по объёму", strategy, 5.01, "больше предельных");
    }

    private static void testZones() {
        DeliveryStrategy strategy = new ZoneDelivery();

        checkPriceForZone("Город — 300", strategy, "Город", 300);
        checkPriceForZone("Область — 600", strategy, "Область", 600);
        checkPriceForZone("Деревня — 1200", strategy, "Деревня", 1200);

        checkPriceForZone("город в нижнем регистре — 300", strategy, "город", 300);
        checkPriceForZone(" ГОРОД с пробелами — 300", strategy, " ГОРОД ", 300);
        checkPriceForZone("область в верхнем регистре — 600", strategy, "ОБЛАСТЬ", 600);

        checkRefusalForZone("Заграница — отказ", strategy, "Заграница", "за границу");
        checkRefusalForZone("заграница в верхнем регистре — тоже отказ",
                strategy, "ЗАГРАНИЦА", "за границу");
    }

    private static void testExpress() {
        DeliveryStrategy strategy = new ExpressDelivery();

        checkPrice("0.1 кг — один начатый кг, 1000", strategy, 0.1, 1000);
        checkPrice("1 кг — 1000", strategy, 1.0, 1000);
        checkPrice("1.01 кг — 1100", strategy, 1.01, 1100);
        checkPrice("3 кг — 1200", strategy, 3.0, 1200);
        checkPrice("3.01 кг — 1300", strategy, 3.01, 1300);
        checkPrice("6 кг — 1500", strategy, 6.0, 1500);

        checkTrue("экспресс не отказывается от 100 кг / 50 м³ / Заграница",
                strategy.getRefusalReason(new Parcel(100.0, 50.0, "Заграница")) == null);
    }

    private static void testCalculator() {
        DeliveryCalculator calculator = DeliveryCalculatorFactory.createStandard();

        checkWinner("0.5 кг, 0.02 м³, Город — побеждает объём",
                calculator, 0.5, 0.02, "Город", "По объёму");
        checkWinner("2 кг, 2 м³, Область — побеждает вес",
                calculator, 2.0, 2.0, "Область", "По весу (ступени)");
        checkWinner("6 кг, 1 м³, Город — побеждает зона",
                calculator, 6.0, 1.0, "Город", "По зонам");
        checkWinner("40 кг, 6 м³, Заграница — побеждает экспресс",
                calculator, 40.0, 6.0, "Заграница", "Экспресс");

        List<DeliveryOffer> offers = calculator.calculateAll(new Parcel(2.0, 2.0, "Область"));
        checkEquals("калькулятор вернул 4 предложения", 4, offers.size());
        checkEquals("вес 2 кг стоит 550", 550, findPrice(offers, "По весу (ступени)"));
        checkEquals("объём 2 м³ стоит 18000", 18000, findPrice(offers, "По объёму"));
        checkEquals("зона Область стоит 600", 600, findPrice(offers, "По зонам"));
        checkEquals("экспресс 2 кг стоит 1100", 1100, findPrice(offers, "Экспресс"));

        List<DeliveryOffer> heavy = calculator.calculateAll(new Parcel(40.0, 6.0, "Заграница"));
        checkEquals("на 40 кг / 6 м³ / Заграница ответили все 4", 4, heavy.size());
        checkEquals("из них принял только один", 1, countAccepted(heavy));
        checkEquals("и это экспресс", "Экспресс", acceptedCarrier(heavy));

        DeliveryCalculator onlyWeight = new DeliveryCalculator();
        onlyWeight.addStrategy(new WeightStepDelivery());
        DeliveryOffer noWinner = onlyWeight.findBest(new Parcel(40.0, 1.0, "Город"));
        checkTrue("если все отказались, победителя нет", noWinner == null);

        DeliveryCalculator empty = new DeliveryCalculator();
        checkEquals("пустой калькулятор вернул 0 предложений", 0, empty.calculateAll(new Parcel(1.0, 1.0, "Город")).size());
        checkTrue("у пустого калькулятора нет победителя", empty.findBest(new Parcel(1.0, 1.0, "Город")) == null);
    }

    private static void testTieBreak() {
        Parcel parcel = new Parcel(1.0, 1.0, "Город");

        DeliveryCalculator weightFirst = new DeliveryCalculator();
        weightFirst.addStrategy(new WeightStepDelivery());
        weightFirst.addStrategy(new ZoneDelivery());
        checkWinner("ничья 300=300: первым добавлен вес — он и победил",
                weightFirst, 1.0, 1.0, "Город", "По весу (ступени)");

        DeliveryCalculator zoneFirst = new DeliveryCalculator();
        zoneFirst.addStrategy(new ZoneDelivery());
        zoneFirst.addStrategy(new WeightStepDelivery());
        checkWinner("ничья 300=300: первым добавлена зона — она и победила",
                zoneFirst, 1.0, 1.0, "Город", "По зонам");

        checkEquals("в обоих случаях цена победителя 300",
                300, weightFirst.findBest(parcel).getPrice());
    }

    private static void testCalculatorValidation() {
        DeliveryCalculator calculator = new DeliveryCalculator();

        checkThrows("null-стратегию добавить нельзя",
                IllegalArgumentException.class, () -> calculator.addStrategy(null));
        checkThrows("null-посылку посчитать нельзя",
                IllegalArgumentException.class, () -> calculator.calculateAll(null));
        checkThrows("null-посылка в findBest запрещена",
                IllegalArgumentException.class, () -> calculator.findBest(null));

        DeliveryCalculator full = DeliveryCalculatorFactory.createStandard();
        List<DeliveryOffer> offers = full.calculateAll(new Parcel(1.0, 1.0, "Город"));

        checkThrows("список предложений нельзя изменить снаружи",
                UnsupportedOperationException.class,
                () -> offers.add(DeliveryOffer.accepted("Хакер", 1)));
    }

    private static void testParcelValidation() {
        checkThrows("отрицательный вес запрещён",
                IllegalArgumentException.class, () -> new Parcel(-1.0, 1.0, "Город"));
        checkThrows("нулевой вес запрещён",
                IllegalArgumentException.class, () -> new Parcel(0.0, 1.0, "Город"));
        checkThrows("отрицательный объём запрещён",
                IllegalArgumentException.class, () -> new Parcel(1.0, -0.5, "Город"));
        checkThrows("нулевой объём запрещён",
                IllegalArgumentException.class, () -> new Parcel(1.0, 0.0, "Город"));
        checkThrows("пустая зона запрещена",
                IllegalArgumentException.class, () -> new Parcel(1.0, 1.0, ""));
        checkThrows("зона из пробелов запрещена",
                IllegalArgumentException.class, () -> new Parcel(1.0, 1.0, "   "));
        checkThrows("null-зона запрещена",
                IllegalArgumentException.class, () -> new Parcel(1.0, 1.0, null));

        checkEquals("зона \"  Город  \" хранится как \"Город\"",
                "Город", new Parcel(1.0, 1.0, "  Город  ").getZone());
    }

    private static void testOfferInvariants() {
        DeliveryOffer accepted = DeliveryOffer.accepted("Тест", 500);
        checkTrue("принятое предложение считается принятым", accepted.isAccepted());
        checkEquals("у принятого предложения цена 500", 500, accepted.getPrice());
        checkTrue("у принятого предложения нет причины отказа",
                accepted.getRefusalReason() == null);
        checkTrue("в toString принятого есть цена",
                accepted.toString().contains("500"));

        DeliveryOffer refused = DeliveryOffer.refused("Тест", "слишком тяжёлая");
        checkTrue("отказанное предложение не считается принятым", !refused.isAccepted());
        checkEquals("у отказанного предложения цена 0", 0, refused.getPrice());
        checkEquals("причина отказа сохраняется", "слишком тяжёлая", refused.getRefusalReason());
        checkTrue("в toString отказа есть слово \"отказ\"",
                refused.toString().contains("отказ"));
    }

    private static void testRefusedCalculateContract() {
        checkThrows("вес 30.01 кг: calculate бросает IllegalStateException",
                IllegalStateException.class,
                () -> new WeightStepDelivery().calculate(new Parcel(30.01, 1.0, "Город")));
        checkThrows("объём 5.01 м³: calculate бросает IllegalStateException",
                IllegalStateException.class,
                () -> new VolumeDelivery().calculate(new Parcel(1.0, 5.01, "Город")));
        checkThrows("Заграница: calculate бросает IllegalStateException",
                IllegalStateException.class,
                () -> new ZoneDelivery().calculate(new Parcel(1.0, 1.0, "Заграница")));
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

    private static int countAccepted(List<DeliveryOffer> offers) {
        int count = 0;
        for (DeliveryOffer offer : offers) {
            if (offer.isAccepted()) {
                count++;
            }
        }
        return count;
    }

    private static String acceptedCarrier(List<DeliveryOffer> offers) {
        for (DeliveryOffer offer : offers) {
            if (offer.isAccepted()) {
                return offer.getCarrierName();
            }
        }
        return null;
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

    private static void checkThrows(String name, Class<? extends RuntimeException> expected,
                                    Runnable action) {
        try {
            action.run();
            fail(name, "ожидалось " + expected.getSimpleName() + ", но исключения не было");
        } catch (RuntimeException e) {
            if (expected.isInstance(e)) {
                pass(name);
            } else {
                fail(name, "ожидалось " + expected.getSimpleName()
                        + ", получено " + e.getClass().getSimpleName());
            }
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
