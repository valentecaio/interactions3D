package fr.etma.navigator.control.joystick;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import fr.etma.navigator.control.Navigator;

public class ControllerJoystick extends Thread {
	// calibration
	double trans_speed = 0.1;
	double rotat_speed = 0.02;
	int mode = 2;

	// internal attributes
	private Component[] components;
	protected boolean finished ;
	Controller[] ca;
	int gp=0; //to store controller number of the gamepad
	
	protected Vector3d deltaT = new Vector3d () ;
	protected Quat4d deltaR = new Quat4d () ;
	protected Navigator navigator ;
	
	// button names
	final static String BUT_1 = "Trigger";
	final static String BUT_2 = "Thumb";
	final static String BUT_3 = "Thumb 2";
	final static String BUT_4 = "Top";
	final static String BUT_L1 = "Top 2";
	final static String BUT_R1 = "Pinkie";
	final static String BUT_L2 = "Base";
	final static String BUT_R2 = "Base 2";
	final static String BUT_L3 = "Base 5";
	final static String BUT_R3 = "Base 6";
	final static String BUT_SELECT = "Base 3";
	final static String BUT_START = "Base 4";

	final static String BUT_LEFT_ANALOG_HORIZONTAL = "z"; // -1 to left, 1 to right
	final static String BUT_LEFT_ANALOG_VERTICAL = "rx"; // -1 to up, 1 to down
	final static String BUT_RIGHT_ANALOG_HORIZONTAL = "y"; // -1 to left, 1 to right
	final static String BUT_RIGHT_ANALOG_VERTICAL = "z"; // -1 to up, 1 to down	

	public ControllerJoystick (Navigator navigator) {
		super();

		this.navigator = navigator ;

		this.showGamepadInfo();

		this.initComponents();
	}

	public void initComponents(){
		ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		Controller joypad = ca[0];
		this.components = joypad.getComponents();
	}

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
			
			// sniffer here
			if(comp.isAnalog()) {
				buffer.append(value);
			} else {
				if(value==1.0f) {
					buffer.append("On");
				} else {
					buffer.append("Off");
				}
			}
			
			// joystick actions here
			double transValue = value * trans_speed;
			double rotationValue = value * rotat_speed;
			
			if(this.mode == 1) {
				/*
				 * I commented these two because of the problem with same identifiers in my gamepad
				 * BUT_LEFT_ANALOG_HORIZONTAL == BUT_RIGHT_ANALOG_VERTICAL
				if(comp.getName().equals(BUT_RIGHT_ANALOG_HORIZONTAL)){
					deltaT.x = transValue;
				} else if(comp.getName().equals(BUT_RIGHT_ANALOG_VERTICAL)){
					deltaT.z = transValue;
				}  
				*/
				if(comp.getName().equals(BUT_LEFT_ANALOG_VERTICAL)){
					deltaR.set(new AxisAngle4d (new Vector3d (1, 0, 0), -rotationValue));
				} else if(comp.getName().equals(BUT_LEFT_ANALOG_HORIZONTAL)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 1, 0), -rotationValue));
				} else if(comp.getName().equals(BUT_R1)){
					deltaT.z = (value>0) ? -trans_speed : 0;
				} else if(comp.getName().equals(BUT_R2)){
					deltaT.z = (value>0) ? trans_speed : 0;
				} else if(comp.getName().equals(BUT_L1)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 0, 1), -rotationValue));
				} else if(comp.getName().equals(BUT_L2)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 0, 1), rotationValue));
				} else if(comp.getName().equals(BUT_1)){
					deltaT.y = (value>0) ? trans_speed : 0;
				} else if(comp.getName().equals(BUT_2)){
					deltaT.x = (value>0) ? trans_speed : 0;
				} else if(comp.getName().equals(BUT_3)){
					deltaT.y = (value>0) ? -trans_speed : 0;
				} else if(comp.getName().equals(BUT_4)){
					deltaT.x = (value>0) ? -trans_speed : 0;
				}
			} else if(this.mode == 2) {
				/*
				 * I commented these two because of the problem with same identifiers in my gamepad
				 * BUT_LEFT_ANALOG_HORIZONTAL == BUT_RIGHT_ANALOG_VERTICAL
				if(comp.getName().equals(BUT_RIGHT_ANALOG_HORIZONTAL)){
					deltaT.x = transValue;
				} else if(comp.getName().equals(BUT_RIGHT_ANALOG_VERTICAL)){
					deltaT.z = transValue;
				}  
				*/
				if(comp.getName().equals(BUT_LEFT_ANALOG_VERTICAL)){
					deltaT.y = -transValue;
				} else if(comp.getName().equals(BUT_LEFT_ANALOG_HORIZONTAL)){
					deltaT.x = transValue;
				} else if(comp.getName().equals(BUT_R1)){
					deltaT.z = (value>0) ? -trans_speed : 0;
				} else if(comp.getName().equals(BUT_R2)){
					deltaT.z = (value>0) ? trans_speed : 0;
				} else if(comp.getName().equals(BUT_L1)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 0, 1), -rotationValue));
				} else if(comp.getName().equals(BUT_L2)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 0, 1), rotationValue));
				} else if(comp.getName().equals(BUT_1)){
					deltaR.set(new AxisAngle4d (new Vector3d (1, 0, 0), rotationValue));
				} else if(comp.getName().equals(BUT_2)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 1, 0), -rotationValue));
				} else if(comp.getName().equals(BUT_3)){
					deltaR.set(new AxisAngle4d (new Vector3d (1, 0, 0), -rotationValue));
				} else if(comp.getName().equals(BUT_4)){
					deltaR.set(new AxisAngle4d (new Vector3d (0, 1, 0), rotationValue));
				}
			}
			
			System.out.println(buffer.toString());
		}
	}

	private int indexOfComponent(Component comp){
		for(int i=0; i<this.components.length; i++){
			if(comp.equals(components[i])){
				return i;
			}
		}
		return -1;
	}
	
	public void finish () {
		finished = true ;
	}

	@Override
	public void run () {
		while (!finished) {
			synchronized (deltaT) {
				synchronized (deltaR) {
					pollInput();
					navigator.supportRotateInHeadFrame (deltaR.x, deltaR.y, deltaR.z, deltaR.w) ;
					navigator.supportTranslateInHeadFrame (deltaT.x, deltaT.y, deltaT.z) ; 
				}
			}
			try {
				sleep (20) ;
			} catch (InterruptedException e) {
			}
		}
	}

}

