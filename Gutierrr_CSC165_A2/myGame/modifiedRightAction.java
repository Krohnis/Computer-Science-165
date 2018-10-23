package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import net.java.games.input.Event;

public class modifiedRightAction extends AbstractInputAction
{
	private SceneNode dolphinNode;
	private float movementSpeed;
	
	public modifiedRightAction(SceneNode dolphin, float movement)
	{
		dolphinNode = dolphin;
		movementSpeed = movement;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.moveRight(movementSpeed * time);
	}
}