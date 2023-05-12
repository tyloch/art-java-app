package biz.tyloch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OrderBook {
    public static ObjectMapper mapper = new ObjectMapper();

    private static AtomicInteger sequenceGenerator = new AtomicInteger(0);

    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Order.class, new OrderDeserializer(sequenceGenerator));
        mapper.registerModule(module);
    }


    public PriorityQueue<OrderDetails> sellOrders = new PriorityQueue<>((a, b) -> {
        if (a.price != b.price) {
            return a.price - b.price;
        }
        return a.sequenceNumber - b.sequenceNumber; // Sort by sequence number ascending
    });



    public PriorityQueue<OrderDetails> buyOrders = new PriorityQueue<>((a, b) -> {
        if (a.price != b.price) {
            return b.price - a.price;
        }
        return a.sequenceNumber - b.sequenceNumber; // Sort by sequence number ascending
    });



    public static void main(String[] args) throws IOException {
        OrderBook ob = new OrderBook();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine;
        try {
        } finally {
        }
        while (true) {
            inputLine = reader.readLine();
            if (inputLine == null || inputLine.trim().isEmpty()) {
                break;
            }
            try {
                Order order = mapper.readValue(inputLine, Order.class);
                List<Transaction> transactionHistory = ob.processOrder(order);
                printOrderBookAndTransactions(ob, transactionHistory);
            } catch (JsonParseException e) {
                System.err.println("Invalid JSON: " + e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("Error reading JSON: " + e.getMessage());
                break;
            }
        }
    }



    List<Transaction> processOrder(Order order) {
        OrderDetails details = order.order;
        List<Transaction> transactionHistory = new ArrayList<>();

        if (details.direction == OrderDirection.BUY) {
            transactionHistory = processOrderGeneric(details, sellOrders, buyOrders, true);
        } else { // The order is a sell order
            transactionHistory = processOrderGeneric(details, buyOrders, sellOrders, false);
        }
        return transactionHistory;
    }


    /**
     * Processes an order and returns a list of transactions.
     *
     * @param details the order details
     * @param oppositeOrders the priority queue of orders in the opposite direction
     * @param sameDirectionOrders the priority queue of orders in the same direction
     * @param isBuyOrder whether the order is a buy order
     * @return a list of transactions
     */
    List<Transaction> processOrderGeneric(OrderDetails details,
            PriorityQueue<OrderDetails> oppositeOrders,
            PriorityQueue<OrderDetails> sameDirectionOrders, boolean isBuyOrder) {
        List<Transaction> transactionHistory = new ArrayList<>();
        boolean executedTrade;

        do {
            executedTrade = false;

            // Loop while a trade can be executed
            while (canExecuteTrade(details, oppositeOrders, isBuyOrder)) {
                OrderDetails matchedOppositeOrder = oppositeOrders.poll();
                int tradeQuantity = Math.min(matchedOppositeOrder.quantity, details.quantity);

                updateOrderQuantities(details, matchedOppositeOrder, tradeQuantity);
                transactionHistory.add(createTransaction(details, matchedOppositeOrder,
                        tradeQuantity, isBuyOrder));

                // If the order should be refilled, refill it and add it back to the queue
                if (shouldRefillOrder(matchedOppositeOrder)) {
                    refillOrder(matchedOppositeOrder);
                    oppositeOrders.add(matchedOppositeOrder);
                } else if (matchedOppositeOrder.quantity > 0) {
                    matchedOppositeOrder.sequenceNumber = sequenceGenerator.incrementAndGet();
                    oppositeOrders.add(matchedOppositeOrder);
                }
                executedTrade = true;
            }

            // Refill the order if needed
            if (shouldRefillOrder(details)) {
                refillOrder(details);
                executedTrade = true;
            }

        } while (executedTrade);

        // If the order is still valid, add it to the same direction orders queue
        if (orderStillValid(details)) {
            sameDirectionOrders.add(details);
        }
        return transactionHistory;
    }

    /**
     * Determines if a trade can be executed based on the given order details and opposite orders
     * queue.
     *
     * @param details the order details
     * @param oppositeOrders the priority queue of orders in the opposite direction
     * @param isBuyOrder whether the order is a buy order
     * @return true if a trade can be executed, false otherwise
     */
    private static boolean canExecuteTrade(OrderDetails details,
            PriorityQueue<OrderDetails> oppositeOrders, boolean isBuyOrder) {
        return !oppositeOrders.isEmpty() && details.quantity > 0
                && (isBuyOrder ? oppositeOrders.peek().price <= details.price
                        : oppositeOrders.peek().price >= details.price);
    }

    /**
     * Updates the quantities of the given orders based on the executed trade quantity.
     *
     * @param details the order details
     * @param matchedOppositeOrder the matched opposite order
     * @param tradeQuantity the executed trade quantity
     */
    private static void updateOrderQuantities(OrderDetails details,
            OrderDetails matchedOppositeOrder, int tradeQuantity) {
        details.quantity -= tradeQuantity;
        matchedOppositeOrder.quantity -= tradeQuantity;
    }

    /**
     * Creates a new transaction based on the given order details and trade quantity.
     * 
     * @param details the details of the current order
     * @param matchedOppositeOrder the details of the matched opposite order
     * @param tradeQuantity the quantity of the trade
     * @param isBuyOrder a boolean indicating whether the current order is a buy order
     * @return a new Transaction object
     */
    private static Transaction createTransaction(OrderDetails details,
            OrderDetails matchedOppositeOrder, int tradeQuantity, boolean isBuyOrder) {
        return new Transaction(isBuyOrder ? details.id : matchedOppositeOrder.id,
                isBuyOrder ? matchedOppositeOrder.id : details.id, matchedOppositeOrder.price,
                tradeQuantity);
    }


    /**
     * Determines if an order should be refilled based on its remaining quantity.
     * 
     * @param order the order details
     * @return true if the order should be refilled, false otherwise
     */
    private boolean shouldRefillOrder(OrderDetails order) {
        return order.remainingQuantity != null && order.remainingQuantity > 0
                && order.quantity <= 0;
    }

    /**
     * Refills an order based on its peak and remaining quantity.
     * 
     * @param order the order details
     */
    private void refillOrder(OrderDetails order) {
        int refillQuantity = Math.min(order.peak, order.remainingQuantity);
        order.quantity += refillQuantity;
        order.remainingQuantity -= refillQuantity;
        order.sequenceNumber = sequenceGenerator.incrementAndGet();
    }

    /**
     * Determines if an order is still valid based on its quantity and remaining quantity.
     * 
     * @param order the order details
     * @return true if the order is still valid, false otherwise
     */
    private boolean orderStillValid(OrderDetails order) {
        return order.quantity > 0
                || (order.remainingQuantity != null && order.remainingQuantity > 0);
    }

    static void printTransactionHistory(List<Transaction> transactionHistory) {
        if (!transactionHistory.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();

            for (Transaction transaction : transactionHistory) {
                try {
                    String jsonString = objectMapper.writeValueAsString(transaction);
                    System.out.println(jsonString);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void printOrderBookAndTransactions(OrderBook ob, List<Transaction> transactionHistory)
            throws JsonProcessingException {
        System.out.println(ob.getOrderBookString());
        OrderBook.printTransactionHistory(transactionHistory);
    }

    public String getOrderBookString() throws JsonProcessingException {
        ObjectNode orderBook = mapper.createObjectNode();
        orderBook.set("sellOrders", mapper.valueToTree(sellOrders));
        orderBook.set("buyOrders", mapper.valueToTree(buyOrders));
        return orderBook.toString();
    }
}


