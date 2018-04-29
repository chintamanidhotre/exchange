package com.rbc.exchange.model;

import com.rbc.exchange.util.OrderIdGenerator;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Order model class.
 */
public class Order implements Comparable {

    private long id;
    private Side side;
    private String ric;
    private long quantity;
    private BigDecimal price;
    private User user;
    private boolean executed = false;
    private BigDecimal executedPrice;

    public Order(OrderIdGenerator orderIdGenerator, Side side, String ric, long quantity, BigDecimal price, User user) {
        this.id = orderIdGenerator.getNextId();
        this.side = side;
        this.ric = ric;
        this.quantity = quantity;
        this.price = price;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public Side getSide() {
        return side;
    }

    public String getRic() {
        return ric;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public User getUser() {
        return user;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public BigDecimal getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(BigDecimal executedPrice) {
        this.executedPrice = executedPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && side == order.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", side=" + side +
                ", ric='" + ric + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", user=" + user +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if(o != null && o instanceof Order) {
            Order other = (Order)o;
            if(other.getSide() == Side.SELL) {
                return this.getPrice().compareTo(other.getPrice());
            } else {
                return other.getPrice().compareTo(this.getPrice());
            }
        }
        return -1;
    }
}
