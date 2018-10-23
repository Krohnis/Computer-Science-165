package myGame;

import ray.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class skillRotateAction extends AbstractInputAction
{
	private MyGame myGame;
	
	public skillRotateAction(MyGame game)
	{
		myGame = game;
	}

	@Override
	public void performAction(float time, Event event) 
	{
		int skillPoints = myGame.getCounter();
		if (skillPoints != 0)
		{
			myGame.setRotationSpeed();
			myGame.setCounter();
		}
		else
		{
			System.out.println("NOT ENOUGH POINTS");
		}
	}
}