package eelab.ssm.order;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/**
 * 2018/11/28
 *
 * @author Sirius
 */
@Component
public class Runner /*implements ApplicationRunner*/ {

    private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;

    public Runner(StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

//    @Override
    public void run(ApplicationArguments args) throws Exception {
        String orderId = "OrderId:1";
        StateMachine<OrderStates, OrderEvents> stateMachine = stateMachineFactory.getStateMachine(orderId);// Return an isntace of state machine with setted id.
        // And now you can set your orderId how state machine variables
        stateMachine.getExtendedState().getVariables().putIfAbsent("orderId", orderId);

        // At starting machine move through the first state and set UUID (WTF)
        stateMachine.start();
        System.out.println("Current state : " + stateMachine.getState().getId().name());

        // Then raise an event
        stateMachine.sendEvent(OrderEvents.PAY);
        System.out.println("Current state : " + stateMachine.getState().getId().name());

        // ... wait a moment, we wanna move on FULLFILLED just if the preconditions are satisfied
        Message<OrderEvents> eventsMessage = MessageBuilder
            .withPayload(OrderEvents.FULLFILL)
            .setHeader("a", "b")
            .build();
        stateMachine.sendEvent(eventsMessage);
        System.out.println("Current state : " + stateMachine.getState().getId().name());
    }
}
