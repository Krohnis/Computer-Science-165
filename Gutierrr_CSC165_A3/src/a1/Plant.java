package a1;

import java.util.Random;

import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;


public class Plant extends GameObject{
	
	public static int MaxPlantSize;
	public static int MinPlantSize;
	private int size;
	
	public Plant(String s)
	{
		setLocation();
		setSize();
      setObjectName(s);
	}
   
   public Plant(Vector3f positionVect, int size, String s)
	{
		setLocation(positionVect);
		setSize(size);
      setObjectName(s);
	}

    public Plant(float x, float y, float z, int size, String s)
	{
		setLocation(x,y,z);
		setSize(size);
      setObjectName(s);
	}

	
	public void setSize()
	{
		Random rn = new Random();
		this.size = MinPlantSize+rn.nextInt(MaxPlantSize);
		return;
	}
   
   public void setSize(int size)
	{
		this.size = size;
		return;
	}

   
   	
	public int getSize ()
	{	return size;}
	

   
	public void destroy(Plant a)
	{
		a = null;		
	}

}
