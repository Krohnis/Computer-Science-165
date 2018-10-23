package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.*;
import net.java.games.input.Event;

public class rotatePitchAction extends AbstractInputAction
{
	private Camera myCamera;
	private SceneNode dolphinNode;
	private modifiedPitchAction modPitchAction;
	private float inputDirection;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public rotatePitchAction(modifiedPitchAction modPitch, Camera camera, SceneNode dolphin, float direction)
	{
		myCamera = camera;
		dolphinNode = dolphin;
		modPitchAction = modPitch;
		inputDirection = direction;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		if (dolphinNode.getChildCount() == 1 && (event.getValue() > 0.1 || event.getValue() < -0.1))
		{
			modPitchAction.performAction(timeScale, event);
		}
		else if ((event.getValue() > 0.1 || event.getValue() < -0.1))
		{
			Vector3 Y = myCamera.getFd();
			Vector3 X = myCamera.getRt();
			Vector3 Z = myCamera.getUp();
			Vector3 YFd = (Y.rotate(Degreef.createFrom(inputDirection * timeScale), X)).normalize();
			Vector3 ZUp = (Z.rotate(Degreef.createFrom(inputDirection * timeScale), X)).normalize();
			myCamera.setFd((Vector3f)Vector3f.createFrom(YFd.x(),YFd.y(),YFd.z()));
			myCamera.setUp((Vector3f)Vector3f.createFrom(ZUp.x(), ZUp.y(), ZUp.z()));
		}
		previousTime = currentTime;
	}
}