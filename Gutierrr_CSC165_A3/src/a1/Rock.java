package a1;

import java.util.Random;

import ray.rml.Vector3f;

public class Rock extends GameObject{
	
	public static int MaxRockSize;
	public static int MinRockSize;
	
private int size;
	
	public Rock(String s)
	{
		setLocation();
		setSize();
      setObjectName(s);
	}
   
   public Rock(Vector3f positionVect, int size, String s)
	{
		setLocation(positionVect);
		setSize(size);
		setObjectName(s);
	}

    public Rock(float x, float y, float z, int size, String s)
	{
		setLocation(x,y,z);
		setSize(size);
      setObjectName(s);
	}

	
	public void setSize()
	{
		Random rn = new Random();
		this.size = MinRockSize+rn.nextInt(MaxRockSize);
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
