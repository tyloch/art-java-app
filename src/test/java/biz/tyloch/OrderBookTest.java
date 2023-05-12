package biz.tyloch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class OrderBookTest {

    @Test
    @Disabled("not implemented")
    void testOrderBook1() throws IOException {
        testOrderBook("1");
    }

    @Test
    @Disabled
    void testOrderBook2() throws IOException {
        testOrderBook("2");
    }

    @Test
    void testOrderBook3() throws IOException {
        testOrderBook("3");
    }

    @Test
    void testOrderBook3again() throws IOException {
        testOrderBook("3");
    }

    @Test
    @Disabled
    void testOrderBook4() throws IOException {
        testOrderBook("4");
    }

    private void testOrderBook(String testID) throws IOException {
        String inputFile = "test" + testID + ".in";
        String expectedOutputFile = "test" + testID + ".out";
        System.out.println("\ntestOrderBook, testID=" + testID);
        System.out.println(inputFile + "/" + expectedOutputFile);


        // read input file
        InputStream inputInputStream = getClass().getClassLoader().getResourceAsStream(inputFile);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputInputStream));

        // read expected output file
        InputStream expectedOutputStream =
                getClass().getClassLoader().getResourceAsStream(expectedOutputFile);
        BufferedReader expectedOutputReader =
                new BufferedReader(new InputStreamReader(expectedOutputStream));

        // initialize order book
        OrderBook orderBook = new OrderBook();

        // read input lines and process orders
        String inputLine;
        while ((inputLine = inputReader.readLine()) != null) {
            Order order = OrderBook.mapper.readValue(inputLine, Order.class);
            List<Transaction> transactionHistory = orderBook.processOrder(order);
            OrderBook.printOrderBookAndTransactions(orderBook, transactionHistory);
        }

        // read expected output and convert to string
        StringBuilder expectedOutputBuilder = new StringBuilder();
        String expectedOutputLine;
        while ((expectedOutputLine = expectedOutputReader.readLine()) != null) {
            expectedOutputBuilder.append(expectedOutputLine);
        }

        // convert actual output to string
        String actualOutput = orderBook.getOrderBookString();

        // compare actual and expected output strings
        System.err.println("COMPARITION IF THERE ARE ANY TRANSACTION not implemented yet");
        assertEquals(expectedOutputBuilder.toString().replace(" ", ""), actualOutput);
    }
}


