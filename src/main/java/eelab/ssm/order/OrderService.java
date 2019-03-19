package eelab.ssm.order;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * 2018/12/03
 *
 * @author Sirius
 */
@Service
public class OrderService {
    private static final String ORDER_ID_HEADER = "orderId";
    private static final String PAYMENT_CONFIRMATION_NUMBER = "paymentConfirmationNumber";
    private final OrderRepository orderRepository;

    private final StateMachineFactory<OrderStates, OrderEvents> factory;

    public OrderService(OrderRepository orderRepository, StateMachineFactory<OrderStates, OrderEvents> factory) {
        this.orderRepository = orderRepository;
        this.factory = factory;
    }

    Order byId(Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    Order create(Date when) {
        return orderRepository.save(new Order(when, OrderStates.SUBMITTED));
    }

    // Now we see a real implementation model
    StateMachine<OrderStates, OrderEvents> change(Long orderId, OrderEvents events) {
        StateMachine<OrderStates, OrderEvents> stateMachine = build(orderId);
        return stateMachine;
    }

    StateMachine<OrderStates, OrderEvents> pay(Long orderId, String paymentConfirmationNumber) {

        StateMachine<OrderStates, OrderEvents> stateMachine = build(orderId);

        // You can use message builder to pass extra information to the send event
        Message<OrderEvents> paymentMessage = MessageBuilder
            .withPayload(OrderEvents.PAY)
            .setHeader(ORDER_ID_HEADER, orderId)
            .setHeader("paymentConfirmationNumber", paymentConfirmationNumber)
            .build();
        stateMachine.sendEvent(paymentMessage);
        return stateMachine;
    }

    StateMachine<OrderStates, OrderEvents> fulfill(Long orderId, String paymentConfirmationNumber) {

        StateMachine<OrderStates, OrderEvents> stateMachine = build(orderId);

        // You can use message builder to pass extra information to the send event
        Message<OrderEvents> paymentMessage = MessageBuilder
            .withPayload(OrderEvents.FULLFILL)
            .setHeader(ORDER_ID_HEADER, orderId)
            .setHeader(PAYMENT_CONFIRMATION_NUMBER, paymentConfirmationNumber)
            .build();
        stateMachine.sendEvent(paymentMessage);
        return stateMachine;
    }

    private StateMachine<OrderStates, OrderEvents> build(Long orderId) {
        // Refresh state machine instance by specific id
        Order order = orderRepository.findById(orderId).get();
        String orderIdKey = order.getId().toString();
        StateMachine<OrderStates, OrderEvents> stateMachine = factory.getStateMachine(orderIdKey);
        stateMachine.stop(); // You don't wanna that state machine go ahead // WTF Who can go ahead state machine here?
        stateMachine.getStateMachineAccessor()
            .doWithAllRegions(new StateMachineFunction<StateMachineAccess<OrderStates, OrderEvents>>() {
                @Override
                public void apply(StateMachineAccess<OrderStates, OrderEvents> stateMachineAccess) {

                    // State machine interceptor help you with save data state into database
                    // This respect the transition among the states, here you could break the transition
                    stateMachineAccess.addStateMachineInterceptor(new StateMachineInterceptorAdapter<OrderStates, OrderEvents>() {
                        @Override
                        public void preStateChange(State<OrderStates, OrderEvents> state, Message<OrderEvents> message, Transition<OrderStates, OrderEvents> transition, StateMachine<OrderStates, OrderEvents> stateMachine) {
                            // The message is the same that you have sent by the configuration
                            Optional.ofNullable(message).ifPresent(msg ->{

                                Long orderId = (Long) msg.getHeaders().getOrDefault(ORDER_ID_HEADER, -1L);

                                String paymentNumber = (String)msg.getHeaders().getOrDefault(PAYMENT_CONFIRMATION_NUMBER,"Not found");

                                Order order = orderRepository.findById(orderId).get();
                                order.setOrderStates(state.getId());
                                order.setPaymentNumber(paymentNumber);
                                orderRepository.save(order); // Just save order before state change
                            });
                        }
                    });
                    // WTF reset the state machine?
                    stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderStates(), null, null, null));
                }
            });

        stateMachine.start();
        return stateMachine;
    }
}
