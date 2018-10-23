package myGame;

import net.java.games.input.Component;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3Pcontroller 
{
	private SceneNode myCameraNode;
	private SceneNode dolphinNode;
	private InputManager inputManager;
	private float cameraAzimuth;
	private float cameraElevation;
	private float radias;
	private Vector3 worldUpVec;
	
	public Camera3Pcontroller (Camera camera, SceneNode cameraNode, SceneNode dolphin, String controller, InputManager im)
	{
		inputManager = im;
		myCameraNode = cameraNode;
		dolphinNode = dolphin;
		cameraAzimuth = 270.0f;
		cameraElevation = 20.0f;
		radias = 2.0f;
		worldUpVec = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setupInput(inputManager, controller);
		updateCameraPosition();
	}

	public void updateCameraPosition() 
	{
		double theta = Math.toRadians(cameraAzimuth);
		double phi = Math.cos(cameraElevation);
		double X = radias * Math.cos(phi) * Math.sin(theta);
		double Y = radias * Math.sin(phi);
		double Z = radias * Math.cos(phi) * Math.cos(theta);
		myCameraNode.setLocalPosition(Vector3f.createFrom
					((float)X, (float)Y, (float)Z).add(dolphinNode.getWorldPosition()));
		myCameraNode.lookAt(dolphinNode, worldUpVec);
	}

	private void setupInput(InputManager inputManager2, String controller) 
	{
		Action azimuthAction = new OrbitAroundAction();
		Action radiasAction = new OrbitRadiasAction();
		Action elevationAction = new OrbitElevationAction();
		
		inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RX, azimuthAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.LEFT, azimuthAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.RIGHT, azimuthAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Axis.Z, radiasAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.Q, radiasAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.E, radiasAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RY, elevationAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.UP, elevationAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(controller, net.java.games.input.Component.Identifier.Key.DOWN, elevationAction,
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	private class OrbitAroundAction extends AbstractInputAction
	{
		public void performAction(float time, net.java.games.input.Event event)
		{
			float rotationAmount = 0.0f;
			if (event.getValue() < -0.2 || event.getComponent().getIdentifier() == Component.Identifier.Key.LEFT)
			{
				rotationAmount = -0.5f;
			}
			else if (event.getValue() > 0.2f || event.getComponent().getIdentifier() == Component.Identifier.Key.RIGHT)
			{
				rotationAmount = 0.5f;
			}
			cameraAzimuth += rotationAmount;
			cameraAzimuth = cameraAzimuth % 360;
			updateCameraPosition();
		}
	}
	private class OrbitRadiasAction extends AbstractInputAction
	{
		public void performAction(float time, net.java.games.input.Event event)
		{
			if ((event.getValue() < -0.2 || event.getComponent().getIdentifier() == Component.Identifier.Key.Q) && radias >= 0.5f)
			{
				radias -= 0.1f;
			}
			else if ((event.getValue() > 0.2f || event.getComponent().getIdentifier() == Component.Identifier.Key.DOWN) && radias <= 3.5f)
			{
				radias += 0.1f;
			}
			updateCameraPosition();
		}
	}
	private class OrbitElevationAction extends AbstractInputAction
	{
		public void performAction(float time, net.java.games.input.Event event)
		{
			if ((event.getValue() < -0.2f || event.getComponent().getIdentifier() == Component.Identifier.Key.UP) && Math.cos(cameraElevation) <= 0.9f)
			{
				cameraElevation -= 0.01f;
			}
			else if ((event.getValue() > 0.2f || event.getComponent().getIdentifier() == Component.Identifier.Key.DOWN) && Math.cos(cameraElevation) >= -0.45f)
			{
				cameraElevation += 0.01f;
			}
			updateCameraPosition();
		}
	}
}
