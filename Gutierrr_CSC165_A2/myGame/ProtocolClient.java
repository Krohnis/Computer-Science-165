package myGame;

import a2.MyGame;
import ray.networking.client.GameConnectionClient;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

public class ProtocolClient extends GameConnectionClient
{
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	
	public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game) throws IOException
	{
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	
	public void processPacket(Object msg)
	{
		String strMessage = (String) msg;
		String[] msgTokens = strMessage.split(",");
		
		if (msgTokens.length > 0)
		{
			if (msgTokens[0].compareTo("join") == 0)
			{
				if (msgTokens[2].compareTo("success") == 0)
				{
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());
					System.out.println("JOIN WAS SUCCESSFUL");
				}
				if (msgTokens[2].compareTo("failure") == 0)
				{
					game.setIsConnected(false);
					System.out.println("FAILED TO JOIN");
				}
			}
			if (msgTokens[0].compareTo("bye") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if (msgTokens[0].compareTo("dsfr") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				createGhostAvatar(ghostID, ghostPosition);
			}
			if (msgTokens[0].compareTo("create") == 0)
			{
				System.out.println("MAKE THE GHOST");
				UUID ghostID = UUID.fromString(msgTokens[1]);
				Vector3 position = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				createGhostAvatar(ghostID, position);
			}
			if (msgTokens[0].compareTo("wsds") == 0)
			{
				UUID ghostID = UUID.fromString(msgTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition());
			}
			if (msgTokens[0].compareTo("move") == 0)
			{
				System.out.println("MOVE RECEIVED");
				Vector3 position = Vector3f.createFrom(Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
				UUID ghostID = UUID.fromString(msgTokens[1]);
				moveGhostAvatar(ghostID, position);
			}
		}
	}
	public void createGhostAvatar(UUID ghostID, Vector3 position)
	{
		GhostAvatar ghostAvatar = new GhostAvatar(ghostID, position);
		ghostAvatars.add(ghostAvatar);
		try
		{
			game.addGhostAvatarToGameWorld(ghostAvatar);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void removeGhostAvatar(UUID ghostID)
	{
		Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
		while (iterator.hasNext())
		{
			GhostAvatar ghostAvatar = iterator.next();
			if (ghostAvatar.getID().equals(ghostID))
			{
				System.out.println("WHY");
				ghostAvatars.removeElement(ghostAvatar);
				game.removeGhostAvatarFromGameWorld(ghostAvatar);
				break;
			}
		}
	}
	
	public void moveGhostAvatar(UUID ghostID, Vector3 pos)
	{
		System.out.println("ITERATOR");
		Iterator<GhostAvatar> iterator = ghostAvatars.iterator();
		while (iterator.hasNext())
		{
			System.out.println("ANOTHER ONE");
			GhostAvatar ghostAvatar = iterator.next();
			System.out.println(ghostAvatar.getID() + ", " + ghostID);
			if (ghostAvatar.getID().equals(ghostID))
			{
				System.out.println("FOUND EM");
				ghostAvatar.setPosition(pos);
				game.moveGhostAvatarAroundGameWorld(ghostAvatar, pos);
				break;
			}
		}
	}
	
	public void sendJoinMessage()
	{
		try
		{
			sendPacket(new String("join," + id.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendCreateMessage(Vector3 pos)
	{
		try
		{
			String message = new String("create," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendByeMessage()
	{
		try
		{
			String message = new String("bye," + id.toString());
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendDetailsForMessage(UUID remId, Vector3 pos)
	{
		try
		{
			String message = new String("dsfr," + id.toString() + "," + remId.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void sendMoveMessage(Vector3 pos)
	{
		try
		{
			String message = new String("move," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
