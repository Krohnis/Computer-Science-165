package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;

public class modifiedRollAction extends AbstractInputAction
{
	private MyGame myGame;
	private SceneNode dolphinNode;
	private float inputDirection;
	
	public modifiedRollAction(MyGame game, float direction)
	{
		myGame = game;
		dolphinNode = myGame.getDolphinNode();
		inputDirection = direction;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.roll(Degreef.createFrom(inputDirection * time));
	}
}