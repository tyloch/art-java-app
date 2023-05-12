package biz.tyloch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDetails {

    OrderDirection direction;

    @JsonProperty("id")
    int id;

    @JsonProperty("price")
    int price;

    @JsonProperty("quantity")
    int quantity;

    int originalQuantity;


    int peak;

    /**
     * remainingQuantity keeps track of the remaining quantity for each iceberg order. This is used
     * to determine when an iceberg order should be refilled.
     */
    Integer remainingQuantity;

    @JsonIgnore
    public int sequenceNumber;


    public OrderDetails() {}


    public OrderDetails(String direction, int id, int price, int quantity, Integer peak,
            int sequenceNumber) {
        this.direction = OrderDirection.valueOf(direction.toUpperCase());
        this.id = id;
        this.price = price;
        this.peak = peak != null ? peak : 0;
        this.originalQuantity = quantity;
        if (peak != null) {
            this.quantity = peak; // Initialize quantity with peak value for iceberg orders
            this.remainingQuantity = quantity - peak; // Initialize remainingQuantity for iceberg
                                                      // orders
        } else {
            this.quantity = quantity;
            this.remainingQuantity = 0;
        }
        this.id = id;
        this.sequenceNumber = sequenceNumber;
    }

    @JsonIgnore
    public String getDirection() {
        return direction.name();
    }

    @JsonProperty
    public void setDirection(String direction) {
        this.direction = OrderDirection.valueOf(direction.toUpperCase());
    }

    @JsonIgnore
    public int getPeak() {
        return peak;
    }

    @JsonProperty
    public void setPeak(int peak) {
        this.peak = peak;
    }
}
