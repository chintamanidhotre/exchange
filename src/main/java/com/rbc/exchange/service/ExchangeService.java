package com.rbc.exchange.service;

import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.User;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface for ExchangeService API.
 */
public interface ExchangeService {

    /**
     * Adds given order to the orders list
     *
     * @param order the order
     * @return the order
     */
    Order addOrder(Order order);

    /**
     * Gets total quantity of all open orders by price for the given RIC and direction.
     *
     * @param ric the ric
     * @param direction the direction
     * @return the interest key as price, value as total quantity
     */
    Map<BigDecimal, Long> getOpenInterest(String ric, Side direction);

    /**
     * Gets average price of all executions for the given RIC.
     *
     * @param ric the RIC
     * @return the avg price
     */
    BigDecimal getAverageExecutionPrice(String ric);

    /**
     * Gets sum of quantities of all executions for the given RIC and user.
     * SELL: It will be represented in negative
     *
     * @param ric the RIC
     * @param user the user
     * @return the sum of quantities
     */
    long getExecutedQuantity(String ric, User user);
}
