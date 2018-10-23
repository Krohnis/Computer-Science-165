package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class moveRightAction extends AbstractInputAction
{
	private Camera myCamera;
	private SceneNode dolphinNode;
	private modifiedRightAction modRightAction;
	private float movementSpeed;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public moveRightAction(modifiedRightAction modRight, Camera camera, SceneNode dolphin, float movement)
	{
		myCamera = camera;
		dolphinNode = dolphin;
		modRightAction = modRight;
		movementSpeed = movement;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		float rightSpeed = movementSpeed * timeScale;
		if (myCamera.getMode() == 'n' && (event.getValue() > 0.1 || event.getValue() < -0.1))
		{
			modRightAction.performAction(timeScale, event);
		}
		else
		{
			Vector3f Y = myCamera.getRt();
			Vector3f P = myCamera.getPo();
			Vector3f P1 = (Vector3f) Vector3f.createFrom(rightSpeed * Y.x(), rightSpeed * Y.y(), rightSpeed * Y.z());
			Vector3f P2 = (Vector3f) P.add((Vector3)P1);
			myCamera.setPo((Vector3f)Vector3f.createFrom(P2.x(),P2.y(),P2.z()));	
		}
		previousTime = currentTime;
	}
}