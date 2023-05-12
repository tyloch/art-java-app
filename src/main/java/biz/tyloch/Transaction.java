package biz.tyloch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class Transaction {
    @JsonProperty("buyOrderId")
    int buyOrderId;

    @JsonProperty("sellOrderId")
    int sellOrderId;

    @JsonProperty("price")
    int price;

    @JsonProperty("quantity")
    int quantity;

    @JsonCreator
    public Transaction(int buyOrderId, int sellOrderId, int price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    

}


