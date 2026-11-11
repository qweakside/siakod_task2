import java.util.Locale;

/**
 * Параметры одной посылки: вес, объём и зона доставки.
 *
 * Объект неизменяемый: после создания его поля не меняются.
 * Это удобно, потому что одна и та же посылка передаётся всем стратегиям,
 * и ни одна из них не может случайно испортить данные для остальных.
 */
public class Parcel {
    private final double weight;
    private final double volume;
    private final String zone;

    public Parcel(double weight, double volume, String zone) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Вес должен быть больше нуля");
        }
        if (volume <= 0) {
            throw new IllegalArgumentException("Объём должен быть больше нуля");
        }
        if (zone == null || zone.trim().isEmpty()) {
            throw new IllegalArgumentException("Зона доставки не указана");
        }
        this.weight = weight;
        this.volume = volume;
        this.zone = zone.trim();
    }

    public double getWeight() {
        return weight;
    }

    public double getVolume() {
        return volume;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Посылка: вес %.2f кг, объём %.2f м³, зона \"%s\"",
                weight, volume, zone);
    }
}
