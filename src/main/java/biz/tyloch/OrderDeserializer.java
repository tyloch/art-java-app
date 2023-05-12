package biz.tyloch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.atomic.AtomicInteger;


import java.io.IOException;


/**
 * The OrderDeserializer class is a custom JSON deserializer for the Order class. This deserializer
 * is used to convert JSON strings into Order objects while handling special cases, such as the
 * nested structure of the JSON input and different fields depending on the order type.
 *
 * The purpose of having this custom deserializer is to control how the JSON is parsed and how the
 * Order object is constructed, especially for handling Iceberg orders with an additional "peak"
 * field.
 */
public class OrderDeserializer extends JsonDeserializer<Order> {

    private final AtomicInteger nextSequenceNumber;

    public OrderDeserializer(AtomicInteger sequenceGenerator) {
        this.nextSequenceNumber = sequenceGenerator;
    }

    @Override
    public Order deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type").asText();
        JsonNode orderNode = node.get("order");

        String direction = orderNode.get("direction").asText();
        int id = orderNode.get("id").asInt();
        int price = orderNode.get("price").asInt();
        int quantity = orderNode.get("quantity").asInt();
        Integer peak = null;
        if (orderNode.has("peak")) {
            peak = orderNode.get("peak").asInt();
        }

        int sequenceNumber = nextSequenceNumber.getAndIncrement();
        OrderDetails orderDetails =
                new OrderDetails(direction, id, price, quantity, peak, sequenceNumber);

        if ("Iceberg".equalsIgnoreCase(type) && peak != null) {
            orderDetails.remainingQuantity = quantity - peak;
        }

        return new Order(type, orderDetails);
    }
}
