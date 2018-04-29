package com.rbc.exchange;

import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.User;
import com.rbc.exchange.util.OrderIdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Test class.
 */
public abstract class BaseOrderTest {

    private OrderIdGenerator orderIdGenerator = new OrderIdGenerator();
    public static final String RIC = "VOD.L";

    public Order createOrder(String ric, Side direction) {
        Order order = new Order(orderIdGenerator, direction, ric, Long.MAX_VALUE, BigDecimal.TEN, new User(1));
        return order;
    }

    public Order createOrder(String ric, Side direction, long quantity, BigDecimal price, long userId) {
        Order order = new Order(orderIdGenerator, direction, ric, quantity, price, new User(userId));
        return order;
    }

}
