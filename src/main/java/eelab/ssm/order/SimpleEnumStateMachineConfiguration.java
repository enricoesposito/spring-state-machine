package eelab.ssm.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * 2018/11/28
 *
 * @author Sirius
 */
@Configuration // So, this is a spring configuration class
@EnableStateMachineFactory // Go, go, go spring create a new instance of state machine
public class SimpleEnumStateMachineConfiguration extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    private final CustomStateMachineListener stateMachineListener;

    public SimpleEnumStateMachineConfiguration(CustomStateMachineListener stateMachineListener) {
        this.stateMachineListener = stateMachineListener;
    }

    // Here we have the state machine engine configuration!
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {

        config.withConfiguration()
            .autoStartup(false) // WTF?
            .listener(stateMachineListener); // Add a listener to the state machine
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {

        states.withStates()
            .initial(OrderStates.SUBMITTED) // When State machine start pass in this state
            .stateEntry(OrderStates.SUBMITTED, new Action<OrderStates, OrderEvents>() {
                @Override
                public void execute(StateContext<OrderStates, OrderEvents> stateContext) {
                    // Here you can make action of any type, when the state entry here
                    // You have the orderId and you can call your business logic here
                    Long orderId = (Long) stateContext.getExtendedState().getVariables().getOrDefault("orderId", -1L);
                    System.out.println("Current orderId = " + orderId);
                }
            })
            .state(OrderStates.PAID)
            .end(OrderStates.FULLFILLED) // Many possible terminal state
            .end(OrderStates.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {

        // I don't know how can be this util withLocal
        // WTF means all transition types?
        transitions
            .withExternal()
            .source(OrderStates.SUBMITTED)
            .target(OrderStates.PAID)
            .event(OrderEvents.PAY)

            .and()
            .withExternal()
            .source(OrderStates.PAID)
            .target(OrderStates.FULLFILLED)
            .event(OrderEvents.FULLFILL)

            .and()
            .withExternal()
            .source(OrderStates.SUBMITTED)
            .target(OrderStates.CANCELLED)
            .event(OrderEvents.CANCEL)

            .and()
            .withExternal()
            .source(OrderStates.PAID)
            .target(OrderStates.CANCELLED)
            .event(OrderEvents.CANCEL);
    }
}
