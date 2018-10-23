package a1;

import net.java.games.input.Event;
import ray.input.action.Action;
import ray.rml.Angle;
import ray.rml.Degreef;

public class TurnRightAction implements Action {

	private MillionYears game;
	   private float localTimeElapsed;

	   //constructor for game and timeInterval
	   public TurnRightAction(MillionYears g)
	   { game = g;
	   localTimeElapsed=0.0f;
	   }
	     
	   public void performAction(float time, Event e)
	   {   
		   localTimeElapsed = time/50.0f;
		   Angle rotAmt = Degreef.createFrom(-localTimeElapsed);

		   game.gameColl.localPlayerNode.yaw(rotAmt);
		   
		   game.orbitController.setCameraAzimuth(game.orbitController.getCameraAzimuth() + localTimeElapsed);
		   game.orbitController.setCameraAzimuth(game.orbitController.getCameraAzimuth() % 360);	
	   }     
}
