package myGame;

import ray.input.action.AbstractInputAction;
import a1.MyGame;
import net.java.games.input.Event;

public class quitGameAction extends AbstractInputAction
{
	private MyGame myGame;
	
	public quitGameAction(MyGame game)
	{
		myGame = game;
	}

	@Override
	public void performAction(float time, Event event) 
	{
		System.out.println("SYSTEM SHUTTING DOWN...");
		myGame.shutdown();
	}
}