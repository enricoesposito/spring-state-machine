package eelab.ssm.order;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
}


// Already known states
enum OrderStates {
	SUBMITTED,
	PAID,
	FULLFILLED,
	CANCELLED
}

// Already known events
enum OrderEvents {
	FULLFILL,
	PAY,
	CANCEL
}

// Am I forced to use Spring Boot? No, You can use Spring State Machine just like a library

// Spring state machine is a DSL ??? WTF

@Log
@Configuration // So, this is a spring configuration class
@EnableStateMachineFactory // Go, go, go spring create a new instance of state machine
class SimpleEnumStateMachineConfiguration extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {



	// Here we have the engine!
	@Override
	public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {

		StateMachineListenerAdapter<OrderStates, OrderEvents> stateMachineListener = getOrderStatesOrderEventsStateMachineListenerAdapter();

		config.withConfiguration()
			.autoStartup(false) // WTF?
			.listener(stateMachineListener); // Add a listener to the state machine
	}

	// State machine talk to me by listener

	private StateMachineListenerAdapter<OrderStates, OrderEvents> getOrderStatesOrderEventsStateMachineListenerAdapter() {

		return new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
				@Override
				public void stateChanged(State from, State to) {
					// Just a listener when state changes, but see better..
					System.out.println(String.format("MachineListener: stateChanged from %s to %s", from, to));
				}

				// ...I'm the StateMachineListenerAdapter and I known everything about the state machine
				// so please look at my methods that you can override


				@Override
				public void stateMachineError(StateMachine<OrderStates, OrderEvents> stateMachine, Exception exception) {
					System.out.println(String.format("MachineListener: hey dummy you got an error %s", exception.getMessage()));
				}

				@Override
				public void stateMachineStarted(StateMachine<OrderStates, OrderEvents> stateMachine) {
					System.out.println(String.format("MachineListener: machine start from state %s", stateMachine.getInitialState()));
				}

				@Override
				public void stateMachineStopped(StateMachine<OrderStates, OrderEvents> stateMachine) {
					System.out.println(String.format("MachineListener: stop this machine started with state %s", stateMachine.getInitialState()));
				}

				@Override
				public void extendedStateChanged(Object key, Object value) {
					System.out.println(String.format("MachineListener: WTF means this key %s and value %s", key, value));
				}

				@Override
				public void stateContext(StateContext<OrderStates, OrderEvents> stateContext) {
					String allStateContextAttribute = Stream.of(stateContext.getClass().getMethods())
						.filter(method -> method.getName().startsWith("get"))
						.map(method -> {
							try {
								return method.getName() + ":" + method.invoke(stateContext).toString();
							} catch (Exception e) {
								e.printStackTrace();
								return "Arrggghh you got an error at this method";
							}
						})
						.collect(Collectors.joining(", "));
					System.out.println(String.format("MachineListener: state context got super power and can see all this " +
						"attribute about the machine {%s}", allStateContextAttribute));
				}

				@Override
				public void stateEntered(State<OrderStates, OrderEvents> state) {
					super.stateEntered(state);
				}

				@Override
				public void stateExited(State<OrderStates, OrderEvents> state) {
					super.stateExited(state);
				}

				@Override
				public void eventNotAccepted(Message<OrderEvents> event) {
					super.eventNotAccepted(event);
				}

				@Override
				public void transition(Transition<OrderStates, OrderEvents> transition) {
					super.transition(transition);
				}

				@Override
				public void transitionEnded(Transition<OrderStates, OrderEvents> transition) {
					super.transitionEnded(transition);
				}

				@Override
				public void transitionStarted(Transition<OrderStates, OrderEvents> transition) {
					super.transitionStarted(transition);
				}
			};
	}
}