package a1;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.asset.texture.TextureManager;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.ManualObject;
import ray.rage.scene.ManualObjectSection;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.util.BufferUtil;
import ray.rml.Degreef;

public class GameObjectCollection {
	
	public static int AmountOfStones;
	public static int AmountOfSpears;
	public static int AmountOfPlants;
	public static int AmountOfRocks;
	public static String StoneFile = "sphere.obj";
	public static String SpearFile = "cone.obj";
	public static String PlantFile = "earth.obj";
	public static String RockFile = "cube.obj";
	public static String PlayerFile = "dolphinHighPoly.obj";
	
	
	///Declaring stone and Arrays of stone entities and stone SceneNodes
	protected GameObject stoneArray [] = new Stone [AmountOfStones];
	protected Entity [] stoneEntityArray = new Entity [AmountOfStones];
	protected SceneNode [] stoneNodeArray = new SceneNode[AmountOfStones];
	   
	//declaring spear and arrays of Spear entities and array of Spear SceneNodes(in our case 2 dolphins)
	protected GameObject spearArray[] = new Spear [AmountOfSpears];
	protected Entity [] spearEntityArray = new Entity [AmountOfSpears];
	protected SceneNode [] spearNodeArray = new SceneNode [AmountOfSpears];
	   
	///Declaring Plant and arrays of plant entities and Plant SceneNodes
	protected GameObject plantArray [] = new Plant [AmountOfPlants] ;
	protected Entity [] plantEntityArray = new Entity [AmountOfPlants];
	protected SceneNode [] plantNodeArray = new SceneNode [AmountOfPlants];
	
	///Declaring Rocks and arrays of Rock entities and Rock SceneNodes
	protected GameObject rockArray [] = new Rock [AmountOfRocks] ;
	protected Entity [] rockEntityArray = new Entity [AmountOfRocks];
	protected SceneNode [] rockNodeArray = new SceneNode [AmountOfRocks];
	
	//Declaring parent Nodes for Stones, Spears and Plants, and Rocks
	protected SceneNode spearParentNode, stoneParentNode, plantParentNode, rockParentNode;
	
	//Declaring Local Player
	protected GameObject localPlayer;
	protected Entity localPlayerEntity;
	protected SceneNode localPlayerNode;
	//declaring vector of ghost players;
	protected Vector<GameObject> ghostPlayerVector = new Vector<GameObject>();
	protected Vector<Entity> ghostPlayerEntityVector = new Vector<Entity>();
	protected Vector<SceneNode> ghostPlayerNodeVector = new Vector<SceneNode>();
	

	
	public GameObjectCollection (Engine eng, SceneManager sm, boolean male) throws IOException 
	{
		 //Making Arrays Stones------------------------------
			//making parent node for Stones
		stoneParentNode = sm.getRootSceneNode().createChildSceneNode( "stoneParentNode");
		
        for (int i =0; i<AmountOfStones; i++)
        {
        		//making array of stones
        	stoneArray[i] = new Stone (StoneFile);
            	//Create entity array for stones 
        	stoneEntityArray[i] = sm.createEntity("myStoneEnt"+i,StoneFile );
        	stoneEntityArray[i].setPrimitive(Primitive.TRIANGLES);
            	//Create Node array for Stones
        	stoneNodeArray[i] = stoneParentNode.createChildSceneNode(stoneEntityArray[i].getName() + "Node"+i);
        	stoneNodeArray[i].attachObject(stoneEntityArray[i]);
            	//Set locations for stone Nodes
        	stoneNodeArray[i].setLocalPosition(stoneArray[i].getLocation() );
        	//float scaleFactor = 1.0f/(float)( (Stone)stoneArray[i]).getSize() );
        	//stoneNodeArray[i].scale(scaleFactor,scaleFactor,scaleFactor);
        }
        
        //Making arrays of Spears----------------------------------------
        	//making parent node for Spears
        spearParentNode = sm.getRootSceneNode().createChildSceneNode( "spearParentNode");
        for (int i=0; i<AmountOfSpears;i++)
        {
             	//making array of spears
        	spearArray[i] = new Spear(SpearFile);
        		//making Entity array for Spears
        	spearEntityArray[i] = sm.createEntity("mySpearEnt"+i,SpearFile );
        	spearEntityArray[i].setPrimitive(Primitive.TRIANGLES);
        		//Making Node Array for Spears
        	spearNodeArray[i] = spearParentNode.createChildSceneNode(spearEntityArray[i].getName() + "Node"+i);
        	spearNodeArray[i].attachObject(spearEntityArray[i]);
        		//Set locations for spears Nodes
        	spearNodeArray[i].setLocalPosition(spearArray[i].getLocation() );
        	
        }
        
        //Making arrays of Plants----------------------------------------
    		//making parent node for Plants
	    plantParentNode = sm.getRootSceneNode().createChildSceneNode( "plantParentNode");
	    for (int i=0; i<AmountOfPlants;i++)
	    {
	         	//making array of spears
	    	plantArray[i] = new Plant(PlantFile);
	    		//making Entity array for Plants
	    	plantEntityArray[i] = sm.createEntity("myPlantEnt"+i,PlantFile );
	    	plantEntityArray[i].setPrimitive(Primitive.TRIANGLES);
	    		//Making Node Array for Plants
	    	plantNodeArray[i] = plantParentNode.createChildSceneNode(plantEntityArray[i].getName() + "Node"+i);
	    	plantNodeArray[i].attachObject(plantEntityArray[i]);
	    		//Set locations and different scales for Plant Nodes
	    	plantNodeArray[i].setLocalPosition(plantArray[i].getLocation() );
	    	float scaleFactor = 1.0f/(float)( (Plant)plantArray[i]).getSize() ;
	    	plantNodeArray[i].scale(scaleFactor,scaleFactor,scaleFactor);	
	    }
          
	    //Making arrays of Rocks----------------------------------------
			//making parent node for Rocks
	    rockParentNode = sm.getRootSceneNode().createChildSceneNode( "rockParentNode");
	    for (int i=0; i<AmountOfRocks;i++)
	    {
	         	//making array of rocks
	    	rockArray[i] = new Rock(RockFile);
	    		//making Entity array for Rocks
	    	rockEntityArray[i] = sm.createEntity("myRockEnt"+i,RockFile );
	    	rockEntityArray[i].setPrimitive(Primitive.TRIANGLES);
	    		//Making Node Array for Rocks
	    	rockNodeArray[i] = rockParentNode.createChildSceneNode(rockEntityArray[i].getName() + "Node"+i);
	    	rockNodeArray[i].attachObject(rockEntityArray[i]);
	    		//Set locations and different scales for Plant Nodes
	    	rockNodeArray[i].setLocalPosition(rockArray[i].getLocation() );
	    	float scaleFactor = (float)( (Rock)rockArray[i]).getSize() ;
	    	rockNodeArray[i].scale(scaleFactor,scaleFactor,scaleFactor);	
	    }
	      
	    
        //Making Local Player
	    localPlayer = new Player(PlayerFile, male, 0);
	    //making Local Player Entity
	    localPlayerEntity = sm.createEntity("myLocalPlayerEnt",PlayerFile );
	    localPlayerEntity.setPrimitive(Primitive.TRIANGLES);
	    //making local Player Node
	    localPlayerNode = sm.getRootSceneNode().createChildSceneNode(localPlayerEntity.getName() + "Node");
	    localPlayerNode.attachObject(localPlayerEntity);
	    	//set location for local player 
	    localPlayerNode.setLocalPosition(localPlayer.getLocation() );
          	
	}
	   

}
