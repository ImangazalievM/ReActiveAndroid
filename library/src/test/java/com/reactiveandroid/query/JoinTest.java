package com.reactiveandroid.query;

import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.models.JoinModelCity;
import com.reactiveandroid.test.models.JoinModelCustomer;
import com.reactiveandroid.test.models.JoinModelOrder;
import com.reactiveandroid.test.models.OrderInfoQueryModel;

import org.junit.Test;

import java.util.List;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;
import static org.junit.Assert.assertEquals;

public class JoinTest extends BaseTest {

    private static final String SELECT_PREFIX = "SELECT * FROM JoinModelOrder ";

    @Test
    public void testSingleJoinSql() {
        assertSqlEquals(SELECT_PREFIX + "JOIN JoinModelCustomer ON JoinModelOrder.customer_id = JoinModelCustomer.id",
                from().join(JoinModelCustomer.class).on("JoinModelOrder.customer_id = JoinModelCustomer.id"));

        assertSqlEquals(SELECT_PREFIX + "AS a JOIN JoinModelCustomer AS b ON a.id = b.id",
                from().as("a").join(JoinModelCustomer.class).as("b").on("a.id = b.id"));

        assertSqlEquals(SELECT_PREFIX + "JOIN JoinModelCustomer USING (id, other)",
                from().join(JoinModelCustomer.class).using("id", "other"));
    }

    @Test
    public void testJoinsSql() {
        assertSqlEquals(SELECT_PREFIX + "AS Order " +
                        "JOIN JoinModelCustomer AS Customer ON Order.customer_id = Customer.id " +
                        "JOIN JoinModelCity ON Customer.customer_id = JoinModelCity.id",
                from().as("Order")
                        .join(JoinModelCustomer.class).as("Customer")
                        .on("Order.customer_id = Customer.id")
                        .join(JoinModelCity.class)
                        .on("Customer.customer_id = JoinModelCity.id"));
    }

    @Test
    public void testJoinTypesSql() {
        assertSqlEquals(SELECT_PREFIX + "INNER JOIN JoinModelCustomer ON",
                from().innerJoin(JoinModelCustomer.class).on(""));
        assertSqlEquals(SELECT_PREFIX + "LEFT OUTER JOIN JoinModelCustomer ON",
                from().leftOuterJoin(JoinModelCustomer.class).on(""));
        assertSqlEquals(SELECT_PREFIX + "CROSS JOIN JoinModelCustomer ON",
                from().crossJoin(JoinModelCustomer.class).on(""));
    }

    @Test
    public void testJoinWithCustomClass() {
        JoinModelCity city = new JoinModelCity("London");
        city.save();
        JoinModelCustomer customer = new JoinModelCustomer("John", city.id);
        customer.save();
        JoinModelOrder orderGooglePixel = new JoinModelOrder("Google Pixel", customer.id);
        orderGooglePixel.save();
        JoinModelOrder orderIphone = new JoinModelOrder("iPhone 9", customer.id);
        orderIphone.save();

        List<OrderInfoQueryModel> orderInfos = Select.columns("OrderItem.id AS order_id",
                "OrderItem.name AS order_name",
                "Customer.name AS customer_name",
                "City.name AS city_name")
                .from(JoinModelOrder.class).as("OrderItem")
                .join(JoinModelCustomer.class).as("Customer")
                .on("OrderItem.customer_id = Customer.id")
                .join(JoinModelCity.class).as("City")
                .on("City.id = Customer.id")
                .fetchAs(OrderInfoQueryModel.class);

        assertEquals(2, orderInfos.size());

        assertEquals(1, orderInfos.get(0).orderId);
        assertEquals("Google Pixel", orderInfos.get(0).orderName);
        assertEquals("John",  orderInfos.get(0).customerName);
        assertEquals("London",  orderInfos.get(0).cityName);

        assertEquals(2, orderInfos.get(1).orderId);
        assertEquals("iPhone 9", orderInfos.get(1).orderName);
        assertEquals("John", orderInfos.get(1).customerName);
        assertEquals("London", orderInfos.get(1).cityName);
    }

    private Select.From from() {
        return Select.from(JoinModelOrder.class);
    }

}
