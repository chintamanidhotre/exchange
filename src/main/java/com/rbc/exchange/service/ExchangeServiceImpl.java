package com.rbc.exchange.service;

import com.rbc.exchange.dao.OrderDao;
import com.rbc.exchange.model.Order;
import com.rbc.exchange.model.Side;
import com.rbc.exchange.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation class for Exchange Service .
 */
public class ExchangeServiceImpl implements ExchangeService {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeServiceImpl.class);

    private OrderDao orderDao;

    public ExchangeServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order addOrder(Order order) {
        orderDao.addOrder(order);
        Optional<Order> matchingOrder = findMatchingOpenOrders(order);

        if(matchingOrder.isPresent()){
            markExecuted(order, matchingOrder.get());
        }
        return order;
    }

    @Override
    public Map<BigDecimal, Long> getOpenInterest(String ric, Side side) {
        LOG.debug("OpenInterest RIC {}, direction {}", ric, side);

        Optional<Queue<Order>> orderQueue = orderDao.getOrderBookByRicAndSide(ric, side);
        if(!orderQueue.isPresent()){
            return new HashMap<>();
        }

        Map<BigDecimal, Long> result = new HashMap<>();
        orderQueue.get().stream().collect(Collectors.groupingBy(Order::getPrice)).entrySet().stream().forEach(entry -> {
            result.put(entry.getKey(), entry.getValue().stream().mapToLong(Order::getQuantity).sum());
        });

        return result;
    }

    @Override
    public BigDecimal getAverageExecutionPrice(String ric) {
        LOG.debug("Averageg Execution Price for instrument {}", ric);

        Optional<Map<Side,Queue<Order>>> ordersByRic = orderDao.getOrderBookByRic(ric);
        if(!ordersByRic.isPresent()){
            return BigDecimal.ZERO;
        }
        BigDecimal totalTradedAmount = BigDecimal.ZERO;
        BigDecimal totalTradedQuantity = BigDecimal.ZERO;

        Map<Side, Queue<Order>> sideOrderMap = ordersByRic.get();
        for(Queue<Order> orderQueue : sideOrderMap.values()){

            List<Order> orderList = new ArrayList(orderQueue);
            for(Order order : orderList){
                if(order.isExecuted()){
                    BigDecimal orderQty = new BigDecimal(order.getQuantity());
                    totalTradedAmount = totalTradedAmount.add(order.getExecutedPrice().multiply(orderQty));
                    totalTradedQuantity = totalTradedQuantity.add(orderQty);

                }
            }
        }

        if(totalTradedQuantity.compareTo(BigDecimal.ZERO) != 0) {
            return totalTradedAmount.divide(totalTradedQuantity, 4, BigDecimal.ROUND_DOWN);
        }
       return BigDecimal.ZERO;
    }

    @Override
    public long getExecutedQuantity(String ric, User user) {
        LOG.debug("ExecutedQuantity for RIC {}, user {}", ric, user);

        List<Order> allUserOrdersForRic = orderDao.getOrderBookByRicAndUser(ric, user);

        if(allUserOrdersForRic == null || allUserOrdersForRic.isEmpty()){
            return 0;
        }

        return allUserOrdersForRic.stream().filter(order -> order.isExecuted()).mapToLong(order -> {
            if(order.getSide() == Side.SELL){
                return order.getQuantity() * -1;
            }
            return order.getQuantity();
        }).sum();
    }

    private Optional<Order> findMatchingOpenOrders(Order order) {


        Optional<Queue<Order>> orderQueue = orderDao.getOrderBookByRicAndSide(order.getRic(), order.getSide().getOppositeSide());
        if(!orderQueue.isPresent()){
            return Optional.empty();
        }

         Optional<Order> matchingOrder = orderQueue.get().stream().filter(order1 -> {
             if(!order1.isExecuted()) {
                 int priceCompResult = order.getPrice().compareTo(order1.getPrice());
                 if (order.getSide() == Side.BUY) {
                     if (priceCompResult >= 0 && order1.getQuantity() == order.getQuantity()) {
                         return true;
                     }
                 } else {
                     if (priceCompResult <= 0 && order1.getQuantity() == order.getQuantity()) {
                         return true;
                     }
                 }
             }
             return false;

         }).findFirst();


        return matchingOrder;

    }

    private void markExecuted(Order order, Order matchedOrder) {
        matchedOrder.setExecuted(true);
        matchedOrder.setExecutedPrice(matchedOrder.getPrice());
        order.setExecuted(true);
        order.setExecutedPrice(matchedOrder.getExecutedPrice());
    }
}
