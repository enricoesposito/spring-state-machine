package eelab.ssm.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.util.Date;

/**
 * 2018/12/03
 *
 * @author Sirius
 */
@Entity(name = "ORDERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    private Date datetime;
    private String orderStates;
    private String paymentNumber;

    public Order(Date datetime, OrderStates orderStates) {
        setOrderStates(orderStates);
        this.datetime = datetime;
    }

    public OrderStates getOrderStates() {
        return OrderStates.valueOf(orderStates);
    }

    public void setOrderStates(OrderStates orderStates) {
        this.orderStates = orderStates.name();
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Long getId() {
        return id;
    }
}
