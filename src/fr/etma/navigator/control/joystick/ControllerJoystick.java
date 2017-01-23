package fr.etma.navigator.control.joystick;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerJoystick {

	public static void main(String[] args) {
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for(int i =0;i<ca.length;i++){
			/* Get the name of the controller */
			System.out.println(ca[i].getName());

			for(Component c: ca[0].getComponents()){
				System.out.println(c);
			}
		}

	}
}