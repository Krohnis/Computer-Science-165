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


public abstract class GameObject {

	private Vector3f location;
	private String objectName;
   
   public void setObjectName (String s)
   {
      this.objectName = s; 
   }

   String getObjectName()
   {
      return this.objectName;
   }
	
	
	public void setLocation()
	{
		Random rn = new Random();
      Random rnSign = new Random();
      float x = (float) ( (rnSign.nextInt(2)*2-1) * (rn.nextInt(MillionYears.DimentionOfWorld)) );
      float y = (float) (MillionYears.HeightOfWorld );
      float z = (float) ( (rnSign.nextInt(2)*2-1) * (rn.nextInt(MillionYears.DimentionOfWorld)) );
		location = (Vector3f)Vector3f.createFrom(x, y, z);
		return;
	}
	
	public void setLocation(float x, float y, float z)
	{
		location = (Vector3f)Vector3f.createFrom(x, y, z);
		return;
	}
	
	public void setLocation(Vector3f vect)
	{
		location = vect;
		return;
	}

		
	Vector3f getLocation()
	{
		return this.location;
	}
   
}
