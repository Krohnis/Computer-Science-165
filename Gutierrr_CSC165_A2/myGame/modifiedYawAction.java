package myGame;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;
import ray.rml.*;
import net.java.games.input.Event;

public class modifiedYawAction extends AbstractInputAction
{
	private SceneNode dolphinNode;
	private float inputDirection;
	private ProtocolClient clientProtocol;
	
	public modifiedYawAction(SceneNode dolphin, ProtocolClient cp, float direction)
	{
		dolphinNode = dolphin;
		inputDirection = direction;
		clientProtocol = cp;
	}
	
	@Override
	public void performAction(float time, Event event) 
	{
		dolphinNode.yaw(Degreef.createFrom(inputDirection * time));
		clientProtocol.sendMoveMessage(dolphinNode.getWorldForwardAxis());
	}
}