package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import ray.rml.*;
import net.java.games.input.Event;

public class mountDolphinAction extends AbstractInputAction
{
	private Camera myCamera;
	private SceneNode dolphinNode;
	
	public mountDolphinAction(Camera camera, SceneNode cameraN, SceneNode dolphinN)
	{
		myCamera = camera;
		dolphinNode = dolphinN;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		if (myCamera.getMode() == 'n')
		{
			Vector3f dolphinN = (Vector3f) dolphinNode.getLocalPosition();
			myCamera.setPo((Vector3f) Vector3f.createFrom(dolphinN.x(), (0.3f + dolphinN.y()), dolphinN.z()));
			myCamera.setMode('c');
		}
		else
		{
			myCamera.setMode('n');
		}
	}
}