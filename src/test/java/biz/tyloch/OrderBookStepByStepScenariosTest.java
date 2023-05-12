package biz.tyloch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;


public class OrderBookStepByStepScenariosTest {
        private OrderBook orderBook;

        @BeforeEach
        public void setup() {
                orderBook = new OrderBook();
        }

        @Test
        public void testScenario1() throws JsonMappingException, JsonProcessingException {
                String scenario = "test1.in";

                // Order 1
                String inputJson1 =
                                "{\"type\": \"Limit\", \"order\": {\"direction\": \"Buy\", \"id\":1, \"price\": 14, \"quantity\":20}}";
                String expectedOutputJson1 =
                                "{\"buyOrders\": [{\"id\": 1, \"price\": 14, \"quantity\": 20}], \"sellOrders\": []}";
                step(scenario, 1, inputJson1, expectedOutputJson1, null);

                // Order 2
                String inputJson2 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Buy\", \"id\":2, \"price\": 15, \"quantity\":50, \"peak\": 20}}";
                String expectedOutputJson2 =
                                "{\"buyOrders\": [{\"id\": 2, \"price\": 15, \"quantity\": 20}, {\"id\": 1, \"price\": 14, \"quantity\": 20}], \"sellOrders\": []}";
                step(scenario, 2, inputJson2, expectedOutputJson2, null);


                // Order 3
                String inputJson3 =
                                "{\"type\": \"Limit\", \"order\": {\"direction\": \"Sell\", \"id\":3, \"price\": 16, \"quantity\": 15}}";
                String expectedOutputJson3 =
                                "{\"buyOrders\": [{\"id\": 2, \"price\": 15, \"quantity\": 20}, {\"id\": 1, \"price\": 14, \"quantity\": 20}], \"sellOrders\": [{\"id\": 3, \"price\": 16, \"quantity\": 15}]}";
                step(scenario, 3, inputJson3, expectedOutputJson3, null);

                // Order 4
                String inputJson4 =
                                "{\"type\": \"Limit\", \"order\": {\"direction\": \"Sell\", \"id\":4, \"price\": 13, \"quantity\": 60}}";
                String[] expectedTransactionJsons4 = {
                                "{\"buyOrderId\": 2, \"sellOrderId\": 4, \"price\": 15, \"quantity\": 20}",
                                "{\"buyOrderId\": 2, \"sellOrderId\": 4, \"price\": 15, \"quantity\": 20}",
                                "{\"buyOrderId\": 2, \"sellOrderId\": 4, \"price\": 15, \"quantity\": 10}",
                                "{\"buyOrderId\": 1, \"sellOrderId\": 4, \"price\": 14, \"quantity\": 10}"};
                String expectedOutputJson4 =
                                "{\"buyOrders\": [{\"id\": 1, \"price\": 14, \"quantity\": 10}], \"sellOrders\": [{\"id\": 3, \"price\": 16, \"quantity\": 15}]}";
                step(scenario, 4, inputJson4, expectedOutputJson4, expectedTransactionJsons4);
                System.out.println("SCENARIO 1 OK\n");

        }

        @Test
        public void testScenario2() throws JsonMappingException, JsonProcessingException {
                String scenario = "test2.in";
                // Order 1
                String inputJson1 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Sell\", \"id\": 1, \"price\": 100, \"quantity\": 200, \"peak\": 100}}";
                String expectedOutputJson1 =
                                "{\"buyOrders\": [], \"sellOrders\": [{\"id\": 1, \"price\": 100, \"quantity\": 100}]}";
                step(scenario, 1, inputJson1, expectedOutputJson1, null);

                // Order 2
                String inputJson2 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Sell\", \"id\": 2, \"price\": 100, \"quantity\": 300, \"peak\": 100}}";
                String expectedOutputJson2 =
                                "{\"buyOrders\": [], \"sellOrders\": [{\"id\": 1, \"price\": 100, \"quantity\": 100}, {\"id\": 2, \"price\": 100, \"quantity\": 100}]}";
                step(scenario, 2, inputJson2, expectedOutputJson2, null);

                // Order 3
                String inputJson3 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Sell\", \"id\": 3, \"price\": 100, \"quantity\": 200, \"peak\": 100}}";
                String expectedOutputJson3 =
                                "{\"buyOrders\": [], \"sellOrders\": [{\"id\": 1, \"price\": 100, \"quantity\": 100}, {\"id\": 2, \"price\": 100, \"quantity\": 100}, {\"id\": 3, \"price\": 100, \"quantity\": 100}]}";
                step(scenario, 3, inputJson3, expectedOutputJson3, null);

                // Order 4
                String inputJson4 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Buy\", \"id\": 4, \"price\": 100, \"quantity\": 500, \"peak\": 100}}";
                String[] expectedTransactionJsons4 = {
                                "{\"buyOrderId\": 4, \"price\": 100, \"quantity\": 100, \"sellOrderId\": 1}",
                                "{\"buyOrderId\": 4, \"price\": 100, \"quantity\": 100, \"sellOrderId\": 2}",
                                "{\"buyOrderId\": 4, \"price\": 100, \"quantity\": 100, \"sellOrderId\": 3}",
                                "{\"buyOrderId\": 4, \"price\": 100, \"quantity\": 100, \"sellOrderId\": 1}",
                                "{\"buyOrderId\": 4, \"price\": 100, \"quantity\": 100, \"sellOrderId\": 2}"};
                String expectedOutputJson4 =
                                "{\"buyOrders\": [], \"sellOrders\": [{\"id\": 3, \"price\": 100, \"quantity\": 100}, {\"id\": 2, \"price\": 100, \"quantity\": 100}]}";
                step(scenario, 4, inputJson4, expectedOutputJson4, expectedTransactionJsons4);
                System.out.println("SCENARIO 2 OK\n");
        }

        @Test
        public void testScenario4() throws JsonMappingException, JsonProcessingException {
                String scenario = "test4.in";

                // Order 1
                String inputJson1 =
                                "{\"type\": \"Limit\", \"order\": {\"direction\": \"Sell\", \"id\":1, \"price\": 14, \"quantity\":20}}";
                String expectedOutputJson1 =
                                "{\"sellOrders\":[{\"id\":1,\"price\":14,\"quantity\":20}],\"buyOrders\":[]}";
                step(scenario, 1, inputJson1, expectedOutputJson1, null);

                // Order 2
                String inputJson2 =
                                "{\"type\": \"Iceberg\", \"order\": {\"direction\": \"Buy\", \"id\":2, \"price\": 14, \"quantity\":10, \"peak\": 5}}";
                String expectedOutputJson2 =
                                "{\"sellOrders\":[{\"id\":1,\"price\":14,\"quantity\":10}],\"buyOrders\":[]}";
                // Transactions
                String[] expectedTransactionJsons = {
                                "{\"buyOrderId\":2,\"sellOrderId\":1,\"price\":14,\"quantity\":5}",
                                "{\"buyOrderId\":2,\"sellOrderId\":1,\"price\":14,\"quantity\":5}"};
                step(scenario, 2, inputJson2, expectedOutputJson2, expectedTransactionJsons);

                System.out.println("SCENARIO 4 OK\n");
        }



        private void step(String scenario, int no, String newOrderInputJson,
                        String expectedOutputOrderBookJson, String[] expectedOutputTransactionsJson)
                        throws JsonProcessingException, JsonMappingException {

                System.out.println("[" + scenario + " step " + no + "]:\n" + newOrderInputJson);

                Order order = OrderBook.mapper.readValue(newOrderInputJson, Order.class);
                List<Transaction> transactionHistory = orderBook.processOrder(order);
                // Parse the expected JSON and actual output into JsonNode objects

                JsonNode expectedOrderBook = OrderBook.mapper.readTree(expectedOutputOrderBookJson);
                JsonNode actualOrderBook =
                                OrderBook.mapper.readTree(orderBook.getOrderBookString());

                // Use zjsonpatch to get the differences between the two JSONs
                JsonNode diff = JsonDiff.asJson(expectedOrderBook, actualOrderBook);

                // Print the differences to the console
                /*
                 * System.out.println("-> Exp Order Book:");
                 * System.out.println(expectedOutputOrderBookJson);
                 * System.out.println("-> Act Order Book:");
                 */
                System.out.println(orderBook.getOrderBookString());
                if (!diff.isEmpty()) {
                        System.out.println("FAIL ! Differences:");
                        System.err.println(diff.toString());
                }

                if (expectedOutputTransactionsJson != null) {
                        // Compare expected transactions and actual transactions
                        System.out.println("EXP Transactions: ");
                        for (String transaction : expectedOutputTransactionsJson) {
                                System.out.println(transaction);
                        }
                        System.out.println("ACT Transactions: ");
                        OrderBook.printTransactionHistory(transactionHistory);

                        assertEquals(expectedOutputTransactionsJson.length,
                                        transactionHistory.size());
                        for (int i = 0; i < expectedOutputTransactionsJson.length; i++) {
                                JsonNode expectedTransaction = OrderBook.mapper
                                                .readTree(expectedOutputTransactionsJson[i]);
                                JsonNode actualTransaction = OrderBook.mapper
                                                .readTree(OrderBook.mapper.writeValueAsString(
                                                                transactionHistory.get(i)));

                                assertTrue(expectedTransaction.equals(actualTransaction));

                                String expectedTransactionJson = OrderBook.mapper
                                                .writeValueAsString(expectedTransaction);
                                String actualTransactionJson = OrderBook.mapper
                                                .writeValueAsString(actualTransaction);
                                if (!expectedTransactionJson.equals(actualTransactionJson)) {
                                        System.err.println("Transaction mismatch, expected:\n"
                                                        + expectedTransaction + ", actual:"
                                                        + actualTransaction);
                                }
                        }
                        // Compare the expected JSON and actual output using the JsonNode.equals() method
                        // COMPARED HERE TO ENSURE WE WILL PRINT TRANSACTIONS
                        assertTrue(expectedOrderBook.equals(actualOrderBook));
                }
        }
}
