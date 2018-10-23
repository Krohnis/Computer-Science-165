package a1;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;

public class MoveBackwardAction extends AbstractInputAction
{
   private MillionYears game;
   private float localTimeElapsed;
   private float localSpeed;

   //constructor for game and timeInterval
   public MoveBackwardAction(MillionYears g)
   { game = g;
   localTimeElapsed = 0.0f;
   localSpeed = 0.0f;
   }
       
   public void performAction(float time, Event e)
   {   
      localTimeElapsed = time/1000.0f;
      localSpeed = ( (Player)game.gameColl.localPlayer).getSpeed();
    
      if ( ( (Player)game.gameColl.localPlayer).isAlive() )
      {
    	  game.gameColl.localPlayerNode.moveBackward(localTimeElapsed*localSpeed);
    	  game.updateVerticalPosition();
      }
    	  
   }

}
