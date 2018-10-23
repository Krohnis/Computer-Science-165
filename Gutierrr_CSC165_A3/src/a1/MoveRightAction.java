package a1;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rml.Vector3f;

public class MoveRightAction extends AbstractInputAction {

	   private MillionYears game;
	   private float localTimeElapsed;
	   private float localSpeed;

	   //constructor for game and timeInterval
	   public MoveRightAction(MillionYears g)
	   { game = g;
	   localTimeElapsed=0.0f;
	   localSpeed = 0.0f;
	   }
	     
	   public void performAction(float time, Event e)
	   {   
	      localTimeElapsed = time/1000.0f;
	      localSpeed = ( (Player)game.gameColl.localPlayer).getSpeed()*0.5f;
	    
	      if ( ( (Player)game.gameColl.localPlayer).isAlive() )
	      {
	    	  game.gameColl.localPlayerNode.moveLeft(localTimeElapsed*localSpeed); 
	    	  game.updateVerticalPosition();
	      }
	    	 
	   } 
}
