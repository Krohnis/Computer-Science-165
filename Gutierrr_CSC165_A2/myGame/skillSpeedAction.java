package myGame;

import ray.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class skillSpeedAction extends AbstractInputAction
{
	private MyGame myGame;
	
	public skillSpeedAction(MyGame game)
	{
		myGame = game;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		int skillPoints = myGame.getCounter();
		if (skillPoints != 0)
		{
			myGame.setMovementSpeed();
			myGame.setCounter();
		}
		else
		{
			System.out.println("NOT ENOUGH POINTS");
		}
	}
}