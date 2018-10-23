package myGame;


import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.*;
import net.java.games.input.Event;

public class rotateYawAction extends AbstractInputAction
{
	private Camera myCamera;
	private SceneNode dolphinNode;
	private modifiedYawAction modYawAction;
	private float inputDirection;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public rotateYawAction(modifiedYawAction modYaw, Camera camera, SceneNode dolphin, float direction)
	{
		myCamera = camera;
		dolphinNode = dolphin;
		modYawAction = modYaw;
		inputDirection = direction;
	}

	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		if (myCamera.getMode() == 'n' && (event.getValue() > 0.1 || event.getValue() < -0.1))
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