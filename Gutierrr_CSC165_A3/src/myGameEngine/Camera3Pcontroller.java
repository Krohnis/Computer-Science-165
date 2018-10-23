package myGameEngine;

import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3Pcontroller {
	
	private Camera camera; //the camera being controlled
	private SceneNode cameraNode; //the node the camera is attached to
	private SceneNode target; //the target the camera looks at
	private float cameraAzimuth; //rotation of camera around Y axis
	private float cameraElevation; //elevation of camera above target
	private float radius; //distance between camera and target
	private Vector3 targetPos; //target’s position in the world
	private Vector3 worldUpVec;
	
	public Camera3Pcontroller(Camera cam, SceneNode camN, SceneNode targ, String controllerName, InputManager im)
	{ 	
		System.out.println(controllerName);
		camera = cam;
		cameraNode = camN;
		target = targ;
		setCameraAzimuth(180.0f);// start from BEHIND and ABOVE the target
		cameraElevation = 20.0f; // elevation is in degrees
		radius = 2.0f;
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setupInput(im, controllerName);
		updateCameraPosition();
	}
	
	public void updateCameraPosition() {
		// TODO Auto-generated method stub
		double theta = Math.toRadians(getCameraAzimuth()); // rot around target
		double phi = Math.toRadians(cameraElevation); // altitude angle
		double x = radius * Math.cos(phi) * Math.sin(theta);
		double y = radius * Math.sin(phi);
		double z = radius * Math.cos(phi) * Math.cos(theta);
		cameraNode.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));
		cameraNode.lookAt(target, worldUpVec);
		
	}

		//attach orbitAroundAction
	private void setupInput(InputManager im, String controllerName) {
		/*
		//attach orbitAroundAction
		Action orbitAroundAction = new OrbitAroundAction();
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RX, orbitAroundAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	
		*/
		//attach orbitElevationAction
		Action orbitElevationAction = new OrbitElevationAction();
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RY, orbitElevationAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	
		
		//attach orbitRadiusAction
		Action orbitRadiusAction = new OrbitRadiusAction();
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Z, orbitRadiusAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);	
		
	}
	
	public float getCameraAzimuth() {
		return cameraAzimuth;
	}

	public void setCameraAzimuth(float cameraAzimuth) {
		this.cameraAzimuth = cameraAzimuth;
	}

	private class OrbitAroundAction extends AbstractInputAction
	{ 
		float localTimeElapsed = 0.0f;
		// Moves the camera around the target (changes camera azimuth).
		public void performAction(float time, net.java.games.input.Event evt)
		{ 
			localTimeElapsed = time/50.0f;
			float rotAmount;
			if (evt.getValue() < -0.2)
				{ rotAmount=-localTimeElapsed; }
			else
			{ 	if (evt.getValue() > 0.2)
					{ rotAmount=localTimeElapsed; }
				else
				{ rotAmount=0.0f; }
			}
			
		setCameraAzimuth(getCameraAzimuth() + rotAmount);
		setCameraAzimuth(getCameraAzimuth() % 360);
		updateCameraPosition();
		} 
	}
	
	private class OrbitElevationAction extends AbstractInputAction
	{ 
		float localTimeElapsed = 0.0f;
		// Moves the camera up-down around the target (changes camera altitude).
		public void performAction(float time, net.java.games.input.Event evt)
		{ 
			localTimeElapsed = time/50.0f;
			float rotAmount ;
			if (evt.getValue() < -0.2 && cameraElevation>0.0 && cameraElevation<180)
				{ rotAmount=-localTimeElapsed; }
			else
			{ 	if (evt.getValue() > 0.2 && cameraElevation>-90.0 && cameraElevation<80.0 )
					{ rotAmount=localTimeElapsed; }
				else
				{ rotAmount=0.0f; }
			}
			
		cameraElevation += rotAmount;
		cameraElevation = cameraElevation % 360;
		updateCameraPosition();
		} 
	}
	
	private class OrbitRadiusAction extends AbstractInputAction
	{ 
		 float localTimeElapsed = 0.0f;
		// Moves the camera closer-further to the target (changes camera radius).
		public void performAction(float time, net.java.games.input.Event evt)
		{ 
			localTimeElapsed = time/1000.0f;
			float moveAmount ;
			if (evt.getValue() < -0.2 && radius>0.5)
				{ moveAmount=-localTimeElapsed; }
			else
			{ 	if (evt.getValue() > 0.2 && radius<5)
					{ moveAmount=localTimeElapsed; }
				else
				{ moveAmount=0.0f; }
			}
			
		radius += moveAmount;
		
		updateCameraPosition();
		} 
	}
}
