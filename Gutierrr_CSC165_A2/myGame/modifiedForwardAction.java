package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import net.java.games.input.Event;

public class modifiedForwardAction extends AbstractInputAction
{
	private SceneNode dolphinNode;
	private float movementSpeed;
	private ProtocolClient clientProtocol;
	
	public modifiedForwardAction(SceneNode dolphin, ProtocolClient cp, float movement)
	{
		dolphinNode = dolphin;
		movementSpeed = movement;
		clientProtocol = cp;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.moveForward(movementSpeed * time);
		clientProtocol.sendMoveMessage(dolphinNode.getWorldPosition());
	}
}