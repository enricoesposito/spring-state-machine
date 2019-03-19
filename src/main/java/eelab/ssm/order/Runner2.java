package eelab.ssm.order;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * 2018/11/28
 *
 * @author Sirius
 */
@Component
public class Runner2 implements ApplicationRunner {

    private final OrderService orderService;

    public Runner2(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Order order = orderService.create(new Date());
        System.out.println("Order at creation: " + orderService.byId(order.getId()));

        StateMachine<OrderStates, OrderEvents> payStateMachine = orderService.pay(order.getId(), UUID.randomUUID().toString());
        System.out.println("After payment: " + payStateMachine.getState().getId().name());
        System.out.println("Order at payment: " + orderService.byId(order.getId()));

        StateMachine<OrderStates, OrderEvents> fulfillStateMachine = orderService.fulfill(order.getId(), UUID.randomUUID().toString());
        System.out.println("After fullfill: " + fulfillStateMachine.getState().getId().name());
        System.out.println("Order at fullfill: " + orderService.byId(order.getId()));

    }
}
