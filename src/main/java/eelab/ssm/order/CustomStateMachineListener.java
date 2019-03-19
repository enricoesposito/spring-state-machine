package eelab.ssm.order;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 2018/11/28
 *
 * @author Sirius
 */
@Component
public class CustomStateMachineListener extends StateMachineListenerAdapter<OrderStates, OrderEvents> {

    // You dislike to implement every method of the lister? No worry, we also have StateMachineListenerAdapter

    @Override
    public void stateChanged(State from, State to) {
        // Just a listener when state changes, but see better..
        System.out.println(String.format("stateChangedListener: stateChanged from %s to %s", Optional.ofNullable(from).orElse(null), to));
    }

    // ...I'm the StateMachineListenerAdapter and I known everything about the state machine
    // so please look at my methods that you can override


//    @Override
//    public void stateMachineError(StateMachine<OrderStates, OrderEvents> stateMachine, Exception exception) {
//        System.out.println(String.format("stateMachineErrorListener: hey dummy you got an error %s on state machine %s", exception.getMessage()));
//    }
//
//    @Override
//    public void stateMachineStarted(StateMachine<OrderStates, OrderEvents> stateMachine) {
//        System.out.println(String.format("stateMachineStartedListener: machine start from state %s", stateMachine.getState().getId()));
//    }
//
//    @Override
//    public void stateMachineStopped(StateMachine<OrderStates, OrderEvents> stateMachine) {
//        System.out.println(String.format("stateMachineStoppedListener: stop this machine with state %s", stateMachine.getState().getId()));
//    }
//
//    @Override
//    public void extendedStateChanged(Object key, Object value) {
//        System.out.println(String.format("extendedStateChangedListener: WTF means this key %s and value %s", key, value));
//    }
//
//    @Override
//    public void stateContext(StateContext<OrderStates, OrderEvents> stateContext) {
//        System.out.println(String.format("stateContextListener: event {%s}", stateContext.getEvent()));
//    }
//
//    @Override
//    public void stateEntered(State<OrderStates, OrderEvents> state) {
//        System.out.println(String.format("stateEnteredListener: statesEntered %s", state.getEntryActions()));
//    }
//
//    @Override
//    public void stateExited(State<OrderStates, OrderEvents> state) {
//        System.out.println(String.format("stateExitedListener: stateExited %s", state.getExitActions()));
//    }
//
//    @Override
//    public void eventNotAccepted(Message<OrderEvents> event) {
//        System.out.println(String.format("eventNotAcceptedListener: eventNotAcepted %s", allGetters(event)));
//    }
//
//    @Override
//    public void transition(Transition<OrderStates, OrderEvents> transition) {
//        System.out.println(String.format("transitionListener: transion from %s to %s", Optional.ofNullable(transition.getSource()).orElse(null), transition.getTarget().getId()));
//    }
//
//    @Override
//    public void transitionEnded(Transition<OrderStates, OrderEvents> transition) {
//        System.out.println(String.format("transitionEndedListener: transitionEnded %s", transition.getTarget()));
//    }
//
//    @Override
//    public void transitionStarted(Transition<OrderStates, OrderEvents> transition) {
//        System.out.println(String.format("transitionStartedListener: transitionStarted %s", transition.getSource()));
//    }

    private String allGetters(Object object) {

        return Stream.of(object.getClass().getMethods())
            .filter(method -> method.getName().startsWith("get") && method.getParameterCount()==0)
            .map(method -> {
                try {
                    return method.getName() + ":" + method.invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Arrggghh you got an error at this method";
                }
            })
            .collect(Collectors.joining(", "));
    }
}
