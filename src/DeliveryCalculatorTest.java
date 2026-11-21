import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DeliveryCalculatorTest {

    @Test
    void testWeightStepDelivery() {
        DeliveryStrategy strategy = new WeightStepDelivery();
        Parcel parcel1 = new Parcel(1.0, 1.0, "Город");
        assertEquals(300, strategy.calculate(parcel1));
        Parcel parcel2 = new Parcel(3.0, 1.0, "Город");
        assertEquals(550, strategy.calculate(parcel2));
        Parcel parcel3 = new Parcel(5.0, 1.0, "Город");
        assertEquals(750, strategy.calculate(parcel3));
        Parcel parcel4 = new Parcel(6.0, 1.0, "Город");
        assertEquals(900, strategy.calculate(parcel4));
    }

    @Test
    void testVolumeDelivery() {
        DeliveryStrategy strategy = new VolumeDelivery();
        Parcel parcel1 = new Parcel(1.0, 0.1, "Город");
        assertEquals(250, strategy.calculate(parcel1));
        Parcel parcel2 = new Parcel(1.0, 0.5, "Город");
        assertEquals(450, strategy.calculate(parcel2));
        Parcel parcel3 = new Parcel(1.0, 5.0, "Город");
        assertEquals(4500, strategy.calculate(parcel3));
    }

    // Добавьте дополнительные тесты для ZoneDelivery и ExpressDelivery
    // ... (подходите по аналогии)
}