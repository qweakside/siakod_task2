import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeliveryCalculator {
    private final List<DeliveryStrategy> strategies = new ArrayList<>();

    public void addStrategy(DeliveryStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Стратегия не может быть null");
        }
        strategies.add(strategy);
    }

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
