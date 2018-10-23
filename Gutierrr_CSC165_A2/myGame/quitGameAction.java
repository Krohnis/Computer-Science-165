package myGame;

import ray.input.action.AbstractInputAction;
import a2.MyGame;
import net.java.games.input.Event;

public class quitGameAction extends AbstractInputAction
{
	private MyGame myGame;
	private ProtocolClient clientProtocol;
	
	public quitGameAction(MyGame game, ProtocolClient cp)
	{
		myGame = game;
		clientProtocol = cp;
	}

	@Override
	public void performAction(float time, Event event) 
	{
		System.out.println("SYSTEM SHUTTING DOWN...");
		clientProtocol.sendByeMessage();
		myGame.shutdown();
	}
}