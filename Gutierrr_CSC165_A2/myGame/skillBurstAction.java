package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;
import a2.MyGame;
import net.java.games.input.Event;

public class skillBurstAction extends AbstractInputAction
{
	private MyGame myGame;
	private SceneNode dolphinNode;
	private float lastUse = -10.0f;
	
	public skillBurstAction(MyGame game, SceneNode dolphin)
	{
		myGame = game;
		dolphinNode = dolphin;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		float cooldownTimer = (time / 1000) - lastUse;
		if (dolphinNode.getChildCount() == 1 && cooldownTimer >= 10)
		{
			dolphinNode.moveForward(5.0f);
			lastUse = (time / 1000);
		}
		else if (!(dolphinNode.getChildCount() != 1))
		{
			System.out.println("MUST BE MOUNTED TO BURST");
		}
		else
		{
			System.out.println("BURST ON COOLDOWN");
			System.out.println(cooldownTimer);
		}
	}
}