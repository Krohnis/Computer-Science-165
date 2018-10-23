package myGame;


import ray.input.action.AbstractInputAction;
import ray.rage.scene.Camera;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;

public class rotateRollAction extends AbstractInputAction
{
	private MyGame myGame;
	private Camera myCamera;
	private modifiedRollAction modRollAction;
	private float inputDirection;
	private float previousTime = 0.0f;
	private float currentTime;
	
	public rotateRollAction(MyGame game, modifiedRollAction modRoll, float direction)
	{
		myGame = game;
		myCamera = myGame.getCamera();
		modRollAction = modRoll;
		inputDirection = direction;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		currentTime = time / 1000;
		float timeScale = ((currentTime - previousTime) % 0.1f) * event.getValue();
		if (myGame.getIsMounted())
		{
			modRollAction.performAction(timeScale, event);
		}
		else
		{
			Vector3 Y = myCamera.getFd();
			Vector3 X = myCamera.getRt();
			Vector3 Z = myCamera.getUp();
			Vector3 XRt = (X.rotate(Degreef.createFrom(inputDirection * timeScale), Y)).normalize();
			Vector3 ZUp = (Z.rotate(Degreef.createFrom(inputDirection * timeScale), Y)).normalize();
			myCamera.setRt((Vector3f)Vector3f.createFrom(XRt.x(),XRt.y(),XRt.z()));
			myCamera.setUp((Vector3f)Vector3f.createFrom(ZUp.x(), ZUp.y(), ZUp.z()));
		}
		previousTime = currentTime;
	}
}