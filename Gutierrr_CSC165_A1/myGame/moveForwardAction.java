package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;

public class moveForwardAction extends AbstractInputAction
{
	private MyGame myGame;
	private Camera myCamera;
	private modifiedForwardAction modForwardAction;
	private float movementSpeed;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public moveForwardAction(MyGame game, modifiedForwardAction modForward, float movement)
	{
		myGame = game;
		myCamera = myGame.getCamera();
		modForwardAction = modForward;
		movementSpeed = movement;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		float forwardSpeed = movementSpeed * timeScale;
		if (myGame.getIsMounted())
		{
			modForwardAction.performAction(timeScale, event);
		}
		else
		{
			Vector3f Y = myCamera.getFd();
			Vector3f P = myCamera.getPo();
			Vector3f P1 = (Vector3f) Vector3f.createFrom(forwardSpeed * Y.x(), forwardSpeed * Y.y(), forwardSpeed * Y.z());
			Vector3f P2 = (Vector3f) P.add((Vector3)P1);
			myCamera.setPo((Vector3f)Vector3f.createFrom(P2.x(),P2.y(),P2.z()));	
		}
		previousTime = currentTime;
	}
}