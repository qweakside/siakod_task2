import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DeliveryCalculator calculator = DeliveryCalculatorFactory.createStandard();

        if (args.length == 3) {
            Parcel parcel = new Parcel(
                    parseNumber(args[0]),
                    parseNumber(args[1]),
                    args[2]);
            printResult(calculator, parcel);
            return;
        }
        if (args.length != 0) {
            System.out.println("Использование: java Main <вес> <объём> <зона>");
            return;
        }

        System.out.println("=== Стоимость доставки ===");
        System.out.println("Зоны: Город, Область, Заграница или любая другая строка.");
        System.out.println("Для выхода введите exit.");
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                Double weight = readNumber(scanner, "Вес, кг: ");
                if (weight == null) {
                    break;
                }

                Double volume = readNumber(scanner, "Объём, м³: ");
                if (volume == null) {
                    break;
                }

                System.out.print("Зона: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String zone = scanner.nextLine().trim();
                if (zone.equalsIgnoreCase("exit")) {
                    break;
                }
                if (zone.isEmpty()) {
                    System.out.println("Зона не указана, попробуйте снова.");
                    System.out.println();
                    continue;
                }

                printResult(calculator, new Parcel(weight, volume, zone));
            }
        }
    }

    private static Double readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return null;
            }
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                return null;
            }
            try {
                double value = parseNumber(line);
                if (value <= 0) {
                    System.out.println("Число должно быть больше нуля.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Не понял число. Пример: 3,5 или 3.5");
            }
        }
    }

    private static double parseNumber(String text) {
        return Double.parseDouble(text.trim().replace(',', '.'));
    }

    private static void printResult(DeliveryCalculator calculator, Parcel parcel) {
        System.out.println();
        System.out.println(parcel);

        List<DeliveryOffer> offers = calculator.calculateAll(parcel);
        for (DeliveryOffer offer : offers) {
            System.out.println("  " + offer);
        }

        DeliveryOffer best = calculator.findBest(parcel);
        if (best == null) {
            System.out.println("  Итог: никто не берётся за эту посылку.");
        } else {
            System.out.println("  Итог: выгоднее всего — " + best);
        }
        System.out.println();
    }
}
