package com.rbc.exchange.dao;

import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.User;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * DAO to Handler Order data.
 * Provide api to get Orders.
 */
public class OrderDao {

    private static final Logger LOG = LoggerFactory.getLogger(OrderDao.class);

    private Map<String, Map<Side, Queue<Order>>> orderMap = new ConcurrentHashMap<>();

    /**
     * Adds order.
     *
     * @param order the order
     */
    public void addOrder(Order order) {
        LOG.info("Add order {}", order);
        String ric = order.getRic();

        Map<Side, Queue<Order>> sideConcurrentLinkedQueueMap = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue orderQueue = new ConcurrentLinkedQueue();
        orderQueue.add(order);
        sideConcurrentLinkedQueueMap.put(order.getSide(), orderQueue);

        orderMap.merge(ric, sideConcurrentLinkedQueueMap, (sideQueueMap, sideQueueMap2) -> {
            sideQueueMap.merge(order.getSide(), orderQueue, (orders, orders2) -> {
                orders.addAll(orders2);
                return orders;
            });
            return sideQueueMap;
        });
    }

    /**
     * Get Order book by Ric and User
     * @param ric
     * @param user
     * @return list of orders
     */
    public List<Order> getOrderBookByRicAndUser(String ric, User user){

        ArrayList<Order> orderList = new ArrayList<>();
        if(orderMap.containsKey(ric)){
            Map<Side, Queue<Order>> sideQueueMap = orderMap.get(ric);
            sideQueueMap.values().forEach(orders -> orders.forEach(order -> {
                if(StringUtils.equals(ric, order.getRic()) && user.equals(order.getUser())){
                    orderList.add(order);
                }
            }));
        }
        return orderList;

    }

    /**
     * Get the order book as of now.
     * @return order book
     */
    public Map<String, Map<Side, Queue<Order>>> getOrderBook(){
        return orderMap;
    }

    /**
     * Get Order book mapped by side.
     * @param ric
     * @return order book
     */
    public Optional<Map<Side, Queue<Order>>> getOrderBookByRic(String ric){
        return Optional.of(orderMap.get(ric));
    }

    /**
     * Get the order list by ric and side.
     * @param ric
     * @param side
     * @return order list
     */
    public Optional<Queue<Order>> getOrderBookByRicAndSide(String ric, Side side){

        if(orderMap.containsKey(ric)){
            return Optional.ofNullable(orderMap.get(ric).get(side));
        }

        return Optional.empty();
    }

}
