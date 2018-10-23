package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;

public class mountDolphinAction extends AbstractInputAction
{
	private MyGame myGame;
	private Camera myCamera;
	private SceneNode cameraNode;
	private SceneNode dolphinNode;
	
	public mountDolphinAction(MyGame game, Camera camera, SceneNode cameraN, SceneNode dolphinN)
	{
		myGame = game;
		myCamera = camera;
		cameraNode = cameraN;
		dolphinNode = dolphinN;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		if (myGame.getIsMounted())
		{
			dolphinNode.detachChild(cameraNode);
			Vector3f dolphinN = (Vector3f) dolphinNode.getLocalPosition();
			myCamera.setPo((Vector3f) Vector3f.createFrom(dolphinN.x(), (0.3f + dolphinN.y()), dolphinN.z()));
			myCamera.setMode('c');
		}
		else
		{
			dolphinNode.attachChild(cameraNode);
			myCamera.setMode('n');
		}
		myGame.setIsMounted();
	}
}