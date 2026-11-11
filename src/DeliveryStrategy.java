public interface DeliveryStrategy {
    /**
     * Возвращает причину отказа (например, "Превышен вес")
     * или null, если перевозчик готов взять заказ.
     */
    String getRefusalReason(double weight, double volume, String zone);

    /**
     * Возвращает стоимость доставки в рублях.
     */
    int calculate(double weight, double volume, String zone);

    /**
     * Возвращает название перевозчика для вывода в консоль.
     */
    String getName();
}
