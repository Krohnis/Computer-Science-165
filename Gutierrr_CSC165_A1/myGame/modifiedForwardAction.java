package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import a1.MyGame;
import net.java.games.input.Event;

public class modifiedForwardAction extends AbstractInputAction
{
	private MyGame myGame;
	private SceneNode dolphinNode;
	private float movementSpeed;
	
	public modifiedForwardAction(MyGame game, float movement)
	{
		myGame = game;
		dolphinNode = myGame.getDolphinNode();
		movementSpeed = movement;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.moveForward(movementSpeed * time);
	}
}