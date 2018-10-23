package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import ray.rml.*;
import net.java.games.input.Event;

public class modifiedPitchAction extends AbstractInputAction
{
	private SceneNode dolphinNode;
	private float inputDirection;
	
	public modifiedPitchAction(SceneNode dolphin, float direction)
	{
		dolphinNode = dolphin;
		inputDirection = direction;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.pitch(Degreef.createFrom(inputDirection * time));
	}
}