package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import a1.MyGame;
import net.java.games.input.Event;

public class modifiedRightAction extends AbstractInputAction
{
	private MyGame myGame;
	private SceneNode dolphinNode;
	private float movementSpeed;
	
	public modifiedRightAction(MyGame game, float movement)
	{
		myGame = game;
		dolphinNode = myGame.getDolphinNode();
		movementSpeed = movement;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.moveRight(movementSpeed * time);
	}
}