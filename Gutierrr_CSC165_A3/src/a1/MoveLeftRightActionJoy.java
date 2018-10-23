package a1;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rml.Vector3f;

public class MoveLeftRightActionJoy extends AbstractInputAction
{
   private MillionYears game;
   float localTimeElapsed;
   float localSpeed;
   //constructor for game and timeInterval
   public MoveLeftRightActionJoy(MillionYears g)
   { game = g;
   localTimeElapsed = 0.0f;
   localSpeed = 0.0f;
   }
       
   public void performAction(float time, Event e)
   {  
      localTimeElapsed = time/1000.0f;
      localSpeed = ( (Player)game.gameColl.localPlayer).getSpeed()*0.5f;
      
      if ( ( (Player)game.gameColl.localPlayer).isAlive() )
      {
    	  if (e.getValue() < -0.5)
          { 
    		  game.gameColl.localPlayerNode.moveRight( localTimeElapsed*localSpeed);
    		  game.updateVerticalPosition();
          }
          
          if (e.getValue() > 0.5)
          { 
    		  game.gameColl.localPlayerNode.moveLeft( localTimeElapsed*localSpeed);
    		  game.updateVerticalPosition();
          }
      }
  
   }    
}
