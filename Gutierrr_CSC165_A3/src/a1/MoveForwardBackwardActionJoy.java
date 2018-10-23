package a1;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;

public class MoveForwardBackwardActionJoy extends AbstractInputAction
{
   private MillionYears game;
   float localTimeElapsed;
   float localSpeed;
   //constructor for game and timeInterval and localSpeed
   public MoveForwardBackwardActionJoy(MillionYears g)
   { game = g;
   localTimeElapsed=0.0f;
   localSpeed =0.0f;
   }
       
   public void performAction(float time, Event e)
   { 
      localTimeElapsed = time/1000.0f;
      localSpeed = ( (Player)game.gameColl.localPlayer).getSpeed();
      //if player is alive
      if ( ( (Player)game.gameColl.localPlayer).isAlive() )
      {
    	  	//move forward by Joystick
    	  if (e.getValue() < -0.3)
          {   
    		  game.gameColl.localPlayerNode.moveForward(localTimeElapsed*localSpeed);
        	  game.updateVerticalPosition();  
          } 
    	  	//move backward by Joystick
          if (e.getValue() > 0.3)
          {
             game.gameColl.localPlayerNode.moveBackward(localTimeElapsed*localSpeed);
             game.updateVerticalPosition();  
          } 
      }
      
   }                 
}
