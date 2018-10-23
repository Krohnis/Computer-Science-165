package myGame;


import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;

public class rotateYawAction extends AbstractInputAction
{
	private MyGame myGame;
	private Camera myCamera;
	private modifiedYawAction modYawAction;
	private float inputDirection;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public rotateYawAction(MyGame game, modifiedYawAction modYaw, float direction)
	{
		myGame = game;
		myCamera = myGame.getCamera();
		modYawAction = modYaw;
		inputDirection = direction;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		if (myGame.getIsMounted())
		{
			modYawAction.performAction(timeScale, event);
		}
		else
		{
			Vector3 Y = myCamera.getFd();
			Vector3 X = myCamera.getRt();
			Vector3 Z = myCamera.getUp();
			Vector3 YFd = (Y.rotate(Degreef.createFrom(inputDirection * timeScale), Z)).normalize();
			Vector3 XRt = (X.rotate(Degreef.createFrom(inputDirection * timeScale), Z)).normalize();
			myCamera.setFd((Vector3f)Vector3f.createFrom(YFd.x(), YFd.y(), YFd.z()));
			myCamera.setRt((Vector3f)Vector3f.createFrom(XRt.x(), XRt.y(), XRt.z()));
		}
		previousTime = currentTime;
	}
}