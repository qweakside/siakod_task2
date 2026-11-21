public interface DeliveryStrategy {

    String getName();

    String getRefusalReason(Parcel parcel);

    int calculate(Parcel parcel);
}
