package com.reactiveandroid.query.join;

import com.reactiveandroid.query.Select;
import com.reactiveandroid.test.BaseTest;
import com.reactiveandroid.test.TestUtils;

import org.junit.Test;

import java.util.List;

import static com.reactiveandroid.test.TestUtils.assertSqlEquals;
import static org.junit.Assert.assertEquals;

public class JoinTest extends BaseTest {

    private static final String SELECT_PREFIX = "SELECT * FROM OrderModel ";

    @Test
    public void testSingleJoinSql() {
        TestUtils.assertSqlEquals(SELECT_PREFIX + "JOIN CustomerModel ON OrderModel.customer_id = CustomerModel.id",
                from().join(CustomerModel.class).on("OrderModel.customer_id = CustomerModel.id"));

        TestUtils.assertSqlEquals(SELECT_PREFIX + "AS a JOIN CustomerModel AS b ON a.id = b.id",
                from().as("a").join(CustomerModel.class).as("b").on("a.id = b.id"));

        TestUtils.assertSqlEquals(SELECT_PREFIX + "JOIN CustomerModel USING (id, other)",
                from().join(CustomerModel.class).using("id", "other"));
    }

    @Test
    public void testJoinsSql() {
        TestUtils.assertSqlEquals(SELECT_PREFIX + "AS Order " +
                        "JOIN CustomerModel AS Customer ON Order.customer_id = Customer.id " +
                        "JOIN CityModel ON Customer.customer_id = CityModel.id",
                from().as("Order")
                        .join(CustomerModel.class).as("Customer")
                        .on("Order.customer_id = Customer.id")
                        .join(CityModel.class)
                        .on("Customer.customer_id = CityModel.id"));
    }

    @Test
    public void testJoinTypesSql() {
        TestUtils.assertSqlEquals(SELECT_PREFIX + "INNER JOIN CustomerModel ON",
                from().innerJoin(CustomerModel.class).on(""));
        TestUtils.assertSqlEquals(SELECT_PREFIX + "LEFT OUTER JOIN CustomerModel ON",
                from().leftOuterJoin(CustomerModel.class).on(""));
        TestUtils.assertSqlEquals(SELECT_PREFIX + "CROSS JOIN CustomerModel ON",
                from().crossJoin(CustomerModel.class).on(""));
    }

    @Test
    public void testJoinWithCustomClass() {
        CityModel city = new CityModel("London");
        city.save();
        CustomerModel customer = new CustomerModel("John", city.id);
        customer.save();
        OrderModel orderGooglePixel = new OrderModel("Google Pixel", customer.id);
        orderGooglePixel.save();
        OrderModel orderIphone = new OrderModel("iPhone 9", customer.id);
        orderIphone.save();

        List<OrderInfoQueryModel> orderInfos = Select.columns("OrderItem.id AS order_id",
                "OrderItem.name AS order_name",
                "Customer.name AS customer_name",
                "City.name AS city_name")
                .from(OrderModel.class).as("OrderItem")
                .join(CustomerModel.class).as("Customer")
                .on("OrderItem.customer_id = Customer.id")
                .join(CityModel.class).as("City")
                .on("City.id = Customer.id")
                .fetchCustom(OrderInfoQueryModel.class);

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

    @Test
    public void testJoinWithEmptyResults() {
        List<OrderInfoQueryModel> orderInfos = Select.columns("OrderItem.id AS order_id",
                "OrderItem.name AS order_name",
                "Customer.name AS customer_name",
                "City.name AS city_name")
                .from(OrderModel.class).as("OrderItem")
                .join(CustomerModel.class).as("Customer")
                .on("OrderItem.customer_id = Customer.id")
                .join(CityModel.class).as("City")
                .on("City.id = Customer.id")
                .fetchCustom(OrderInfoQueryModel.class);
    }

    private Select.From from() {
        return Select.from(OrderModel.class);
    }

}
