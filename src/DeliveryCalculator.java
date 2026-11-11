import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Контекст паттерна Strategy (его ещё называют Context).
 *
 * Калькулятор хранит список перевозчиков и умеет:
 *  - посчитать цену у каждого;
 *  - выбрать самого дешёвого.
 *
 * Он не знает ни одного конкретного тарифа. Всё, что ему нужно, —
 * интерфейс DeliveryStrategy. Именно поэтому добавление нового
 * перевозчика не требует менять этот класс.
 */
public class DeliveryCalculator {
    private final List<DeliveryStrategy> strategies = new ArrayList<>();

    public void addStrategy(DeliveryStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Стратегия не может быть null");
        }
        strategies.add(strategy);
    }

    /**
     * Считает предложение каждого перевозчика для одной и той же посылки.
     * Для отказавшего перевозчика в списке будет причина отказа.
     */
    public List<DeliveryOffer> calculateAll(Parcel parcel) {
        if (parcel == null) {
            throw new IllegalArgumentException("Посылка не может быть null");
        }

        List<DeliveryOffer> offers = new ArrayList<>();
        for (DeliveryStrategy strategy : strategies) {
            String refusal = strategy.getRefusalReason(parcel);
            if (refusal != null) {
                offers.add(DeliveryOffer.refused(strategy.getName(), refusal));
            } else {
                offers.add(DeliveryOffer.accepted(strategy.getName(), strategy.calculate(parcel)));
            }
        }
        return Collections.unmodifiableList(offers);
    }

    /**
     * Возвращает самое дешёвое предложение или null, если все отказались.
     *
     * Если несколько перевозчиков дали одинаковую минимальную цену,
     * побеждает тот, который был добавлен раньше. Это осознанное правило:
     * в задании ничья не описана, поэтому мы её просто фиксируем.
     */
    public DeliveryOffer findBest(Parcel parcel) {
        DeliveryOffer best = null;
        for (DeliveryOffer offer : calculateAll(parcel)) {
            if (!offer.isAccepted()) {
                continue;
            }
            if (best == null || offer.getPrice() < best.getPrice()) {
                best = offer;
            }
        }
        return best;
    }
}
