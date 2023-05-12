package biz.tyloch;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("type")
    String type;

    @JsonProperty("order")
    OrderDetails order;

    public Order() {}

    public Order(String type, OrderDetails order) {
        this.type = type;
        this.order = order;
    }
}