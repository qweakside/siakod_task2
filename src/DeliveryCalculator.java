import java.util.ArrayList;
import java.util.List;
public class DeliveryCalculator {
    private final List<DeliveryStrategy> strategies = new ArrayList<>();
    public void addStrategy(DeliveryStrategy strategy){
        strategies.add(strategy);
    }
    public void calculateBest(double weight, double volume, String zone) {
        List<String> mestakes = new ArrayList<>();
        int minCost = Integer.MAX_VALUE;
        String best = null;
        for (DeliveryStrategy strategy : strategies) {
            String reason = strategy.getRefusalReason(weight, volume, zone);

            String name = strategy.getName();
            if (reason != null) {
                mestakes.add(name + ": " + reason);
            } else if
            int cost = strategy.calculate(weight, volume, zone);
            (cost < minCost) {
                minCost = cost;
                best = name;
            }
        }
        if (best!=null){
            System.out.println(best + " — " + minCost + " руб.");
        }
        else{
            System.out.println("Никто не может доставить"+mestakes);
        }
    }
