package fr.etma.navigator.control.joystick;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class ControllerJoystick {

	Controller[] ca;
	int gp=0; //to store controller number of the gamepad

	public void showGamepadInfo(){
		try {
			ca = ControllerEnvironment.getDefaultEnvironment().getControllers();          
			for(int i=0;i<ca.length;i++){
				/* Get the name of the controller */
				System.out.println(ca[i].getName());
				System.out.println("Type: "+ca[i].getType().toString());                

				/* Get this controllers components (buttons and axis) */
				Component[] components = ca[i].getComponents();
				System.out.println("Component Count: "+components.length);
				for(int j=0;j<components.length;j++){

					/* Get the components name */
					System.out.println("Component "+j+": "+components[j].getName());
					System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
					System.out.print("    ComponentType: ");
					if (components[j].isRelative()) {
						System.out.print("Relative");
					} else {
						System.out.print("Absolute");
					}
					if (components[j].isAnalog()) {
						System.out.print(" Analog");
					} else {
						System.out.print(" Digital");
					}
					System.out.println("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void start() {
		showGamepadInfo();

		while (true) {
			pollInput();
		}
	}

	public void pollInput() {
		ca[gp].poll();
		EventQueue queue = ca[gp].getEventQueue();
		Event event = new Event();

		while(queue.getNextEvent(event)) {
			StringBuffer buffer = new StringBuffer(ca[gp].getName());
			buffer.append(" at ");
			buffer.append(event.getNanos()).append(", ");
			Component comp = event.getComponent();
			buffer.append(comp.getName()).append(" changed to ");
			float value = event.getValue(); 
			if(comp.isAnalog()) {
				buffer.append(value);
			} else {
				if(value==1.0f) {
					buffer.append("On");
				} else {
					buffer.append("Off");
				}
			}
			System.out.println(buffer.toString());
		}

	}

	public static void main(String[] args) {
		ControllerJoystick inputExample = new ControllerJoystick();
		inputExample.start();
	}
}
