package a1;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Invocable;
import java.io.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import net.java.games.input.Event;

import myGameEngine.Camera3Pcontroller;

import ray.rage.asset.texture.*;
import ray.rage.util.*;
import java.awt.geom.*;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.game.Game;
import ray.rage.game.VariableFrameRateGame;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.RenderWindow;
import ray.rage.rendersystem.Renderable.DataSource;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.*;
import ray.rage.scene.Camera;
import ray.rage.scene.Entity;
import ray.rage.scene.Light;
import ray.rage.scene.ManualObject;
import ray.rage.scene.ManualObjectSection;
import ray.rage.scene.Node;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.SkyBox;
import ray.rage.scene.Tessellation;
import ray.rage.scene.Camera.Frustum.Projection;
import ray.rage.scene.controllers.RotationController;
import ray.rage.util.BufferUtil;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class MillionYears extends VariableFrameRateGame  {
	// to minimize variable allocation in update()
		GL4RenderSystem rs;
	    float updateTime;
		float elapsTime = 0.0f; 	
		String elapsTimeStr, dispStr;
	 	int elapsTimeSec;
	  	
	   
	   //game static Logic variables
	   public static float HeightOfWorld ;
	   public static int DimentionOfWorld;
	   public static final float CameraElevation = 5.0f;
	   
	   //game logic variables
	   private boolean male = true;
	   
	   //skyBox variables
	   private static final String SKYBOX_NAME = "SkyBox";
	   private boolean skyBoxVisible = true;
	   
	   //gameCollection variable
	   public GameObjectCollection gameColl;
	   
	   //declaring scene nodes
	   protected SceneNode rootNode, skyBoxNode;
	 
	   
	   //declaring camera node
	   private Camera myCamera;
	   private SceneNode cameraNode;
	   public Camera3Pcontroller orbitController;
	   
	   //Declaring input manager and actions 
	   private InputManager im;
	   private Action quitGameAction, moveForwardAction, moveForwardBackwardActionJoy, moveBackwardAction;
	   private Action moveLeftAction, moveLeftRightActionJoy, moveRightAction; 
	   private Action turnLeftAction, turnRightAction, turnLeftRightActionJoy;
	   private Action fireAction, switchWeaponAction;
	   
	   //Declaring Javascript variables
	   protected ScriptEngine jsEngine;
	   protected File scriptFile;
	   
		public MillionYears() 
	    {
	        super();
	    }
		

		
		public static void main(String[] args) {
	    	 Game game = new MillionYears();
	        try {
	            game.startup();
	            game.run();
	        } catch (Exception e) {
	            e.printStackTrace(System.err);
	        } finally {
	            game.shutdown();
	            game.exit();
	        }
	    }
		
		@Override
		protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
			rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		}

	//Camera setup Function
	    @Override
	    protected void setupCameras(SceneManager sm, RenderWindow rw) {
	        rootNode = sm.getRootSceneNode();
	        myCamera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
	        rw.getViewport(0).setCamera(myCamera);
			
			getCamera().setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
			getCamera().setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
			getCamera().setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
			getCamera().setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, CameraElevation));
	     
	      cameraNode = rootNode.createChildSceneNode(myCamera.getName() + "Node");
	      cameraNode.attachObject(myCamera);
	      myCamera.getFrustum().setFarClipDistance(1000.0f);
	      getCamera().setMode('n');
	    }
	    
	  //Orbit camera setup Function--------------------------------------------------------------
	    protected void setupOrbitCameras(Engine eng, SceneManager sm) {
	    	
	    	//creating and attaching orbit camera moved by joystick or keyboard
	    	SceneNode localPlayerNode = gameColl.localPlayerNode;
	    	String localPlayerControl;
	    	if (im.getFirstGamepadName()!=null)
	    		localPlayerControl = im.getFirstGamepadName();
	    	else localPlayerControl = im.getKeyboardName();
	    	orbitController = new Camera3Pcontroller(myCamera,cameraNode,localPlayerNode,localPlayerControl, im);	    		
	    }
	    
	//Scene setup Function----------------------------------------------------------------
	    @Override
	    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
	    		    	 
	    	//setup Javascript -----------------------------------
	    		// prepare script engine
	    	ScriptEngineManager factory = new ScriptEngineManager();
	    	List <ScriptEngineFactory> list = factory.getEngineFactories();
	    	jsEngine = factory.getEngineByName("js");
	    		//print out script engines
	    	System.out.println("Script Engine Factories found:");
	    	for (ScriptEngineFactory f : list)
	    	{ System.out.println(" Name = " + f.getEngineName() + " language = " + f.getLanguageName() + " extensions = " + f.getExtensions()); }
	    		//initialize scriptFile and run the script and get values for initial world setup
	    	scriptFile = new File("InitParams.js");
	    	this.runScript(jsEngine, scriptFile);
	    	DimentionOfWorld = ((int)(jsEngine.get("dimentionOfWorld"))) ;
	    	HeightOfWorld = ((Double)(jsEngine.get("heightOfObjects"))).floatValue() ;
	    	GameObjectCollection.AmountOfStones = ((int)(jsEngine.get("stones")));
	    	GameObjectCollection.AmountOfSpears = ((int)(jsEngine.get("spears")));
	    	GameObjectCollection.AmountOfPlants = ((int)(jsEngine.get("plants")));
	    	GameObjectCollection.AmountOfRocks = ((int)(jsEngine.get("rocks")));
	    	Plant.MinPlantSize = ((int)(jsEngine.get("minPlantSize")));
	    	Plant.MaxPlantSize = ((int)(jsEngine.get("maxPlantSize")));
	    	Rock.MinRockSize = ((int)(jsEngine.get("minRockSize")));
	    	Rock.MaxRockSize = ((int)(jsEngine.get("maxRockSize")));
	    	Player.Life = ((int)(jsEngine.get("life")));
	    	Player.MaleStrength = ((int)(jsEngine.get("maleStrength")));
	    	Player.FemaleStrength = ((int)(jsEngine.get("femaleStrength")));
	    	Player.MaleSpeed = ((Double)(jsEngine.get("maleSpeed"))).floatValue() ;
	    	Player.FemaleSpeed = ((Double)(jsEngine.get("femaleSpeed"))).floatValue() ;
	    	
	    	
	    	 gameColl = new GameObjectCollection(eng, sm, male);
		    
		      //Making SkyBox---------------
		    Configuration conf = eng.getConfiguration();
		    TextureManager tm = getEngine().getTextureManager();
		    tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.path"));
		    Texture front = tm.getAssetByPath("front.jpg");
		    Texture back = tm.getAssetByPath("back.jpg");
		    Texture left = tm.getAssetByPath("left.jpg");
		    Texture right = tm.getAssetByPath("right.jpg");
		    Texture top = tm.getAssetByPath("top.jpg");
		    Texture bottom = tm.getAssetByPath("bottom.jpg");
		    tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));
		    // cubemap textures are flipped upside-down.
		    // All textures must have the same dimensions, so any image’s
		    // heights will work since they are all the same height
		    AffineTransform xform = new AffineTransform();
		    xform.translate(0, front.getImage().getHeight());
		    xform.scale(1d, -1d);
		    front.transform(xform);
		    back.transform(xform);
		    left.transform(xform);
		    right.transform(xform);
		    top.transform(xform);
		    bottom.transform(xform);
		    SkyBox sb = sm.createSkyBox(SKYBOX_NAME);
		    sb.setTexture(front, SkyBox.Face.FRONT);
		    sb.setTexture(back, SkyBox.Face.BACK);
		    sb.setTexture(left, SkyBox.Face.LEFT);
		    sb.setTexture(right, SkyBox.Face.RIGHT);
		    sb.setTexture(top, SkyBox.Face.TOP);
		    sb.setTexture(bottom, SkyBox.Face.BOTTOM);
		    sm.setActiveSkyBox(sb);
		   /* 	//moving skybox up
		    skyBoxNode = sm.getRootSceneNode().createChildSceneNode(sb.getName() + "Node");
		    skyBoxNode.attachObject(sb);
		    //skyBoxNode.translate(Vector3f.createFrom(0.0f, -200000.0f, 0.0f));
		   */
		    
	
		      //Making Terrain-----
		    Tessellation tessE = sm.createTessellation("tessE", 7);
			// subdivisions per patch: min=0, try up to 32
			//tessE.setSubdivisions(8f);
			SceneNode tessNode =sm.getRootSceneNode().createChildSceneNode("tessN");
			tessNode.attachObject(tessE);
			// to move it, note that X and Z must BOTH be positive OR negative
			tessNode.translate(Vector3f.createFrom(0.0f, 0.0f, 0.0f));
			// tessN.yaw(Degreef.createFrom(37.2f));
			tessNode.scale(150, 20, 150);
			tessE.setHeightMap(this.getEngine(), "heightMapContrast.jpg");
			tessE.setTexture(this.getEngine(), "bottomText.jpg");
		    
			
	   //Setup light
	        sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
			Light plight = sm.createLight("testLamp1", Light.Type.POINT);
			plight.setAmbient(new Color(0.7f, 0.7f, 0.7f));
	        plight.setDiffuse(new Color(1.0f, 1.0f, 1.0f));
			plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
	        plight.setRange(200f);
			
			SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
	        plightNode.attachObject(plight);
	        plightNode.translate(0.0f, 20.0f, 0.0f);

	        RotationController rc = new RotationController(Vector3f.createUnitVectorY(), .01f);
	        //rc.addNode(earthNode);
	        sm.addController(rc);
	        
	        setupInputs();
	        setupOrbitCameras(eng, sm);
	    }
	    
	//Update Function
	    @Override
	    protected void update(Engine engine) {
			
			updateTime = engine.getElapsedTimeMillis();
			elapsTime += updateTime;
			elapsTimeSec = Math.round(elapsTime/1000.0f);
			elapsTimeStr = Integer.toString(elapsTimeSec);
			
			// build and set HUD
			rs = (GL4RenderSystem) engine.getRenderSystem();
			int windowHeight = (int) (rs.getCanvas().getHeight() * 1.05);
			int windowWidth = rs.getCanvas().getWidth();
			rs.setHUD( elapsTimeStr + ((Player)gameColl.localPlayer).getDispStr(), windowWidth / 100, windowHeight / 70);
		    
	      //Check collision-----------------------
	     // collisionDetection(engine);
	      
	      //game over if Player is not dead then process inputs--------------------
	      if( ((Player)gameColl.localPlayer).isAlive())
	      {
		      im.update(updateTime);
	      }
	      else { ((Player)gameColl.localPlayer).setStatusStr("  GAME OVER - you died.");}
	      
	      
	      //update camera  position
	      if (orbitController != null)
			{
				orbitController.updateCameraPosition();
			}
	      
	      //update input 
	      im.update(updateTime);
		}


	//SetupInputs Function   
	    protected void setupInputs()
	   { 
	      im = new GenericInputManager();
	      String kbName = im.getKeyboardName();
	      String gpName = im.getFirstGamepadName();
	      
	      // build some action objects for doing things in response to user input
	      quitGameAction = new QuitGameAction(this);
	      moveForwardAction = new MoveForwardAction(this);
	      moveForwardBackwardActionJoy = new MoveForwardBackwardActionJoy(this);
	      moveBackwardAction = new MoveBackwardAction(this);
	      moveLeftAction = new MoveLeftAction(this);
	      moveRightAction = new MoveRightAction(this);
	      moveLeftRightActionJoy = new MoveLeftRightActionJoy(this);
	      turnLeftAction = new TurnLeftAction(this);
	      turnRightAction = new TurnRightAction(this);
	      turnLeftRightActionJoy = new TurnLeftRightActionJoy(this);
	    
	      
	      // attach the action objects to keyboard and gamepad components
	         //attach quit game action
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.Z,
	      quitGameAction,
	      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	      
	         //attach MoveForward action
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.W,
	      moveForwardAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	           
	         //attach MoveBackward action
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.S,
	      moveBackwardAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	      
	         //attach MoveLeft action
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.A,
	      moveLeftAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	     
	         //attach MoveRight action
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.D,
	      moveRightAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	      
	         //attach TurnLeft action "yaw"
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.LEFT,
	      turnLeftAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	         //attach TurnRight action "yaw"
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.RIGHT,
	      turnRightAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
/*
	         //attach TurnUp action "pitch"
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.UP,
	      turnUpAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	      
	         //attach TurnDown action "pitch"
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.DOWN,
	      turnDownAction,
	      InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	         //attach Toggle action 
	      im.associateAction(kbName,
	      net.java.games.input.Component.Identifier.Key.SPACE,
	      toggleAction,
	      InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
*/
	      
	      //check if first controller is pluged in and if yes enable controller actions
	      if(im.getFirstGamepadName()!=null)
	      {
	            //attach quitGame  action on button of joystic
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Button._7,
	         quitGameAction,
	         InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	         
	            //attach MoveForwardBackwardActionJoy
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Axis.Y,
	         moveForwardBackwardActionJoy,
	         InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	   
	            //attach MoveLeftRightActionJoy
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Axis.X,
	         moveLeftRightActionJoy,
	         InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	         
	            //attach TurnLeftRightActionJoy
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Axis.RX,
	         turnLeftRightActionJoy,
	         InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	         /*
	          //attach TurnUpDownActionJoy
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Axis.RY,
	         turnUpDownActionJoy,
	         InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	           //attach toggle action on button A of joystic
	         im.associateAction(gpName,
	         net.java.games.input.Component.Identifier.Button._5,
	         toggleAction,
	         InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			*/
	      }
	      
	   }

	    //Function executes JavaScript-----------------------------------------
	    private void runScript(ScriptEngine engine, File scriptFileName)
	    {
		    try
		    { FileReader fileReader = new FileReader(scriptFileName);
		    engine.eval(fileReader); //execute the script statements in the file
		    fileReader.close();
		    }
		    catch (FileNotFoundException e1)
		    { System.out.println(scriptFileName + " not found " + e1); }
		    catch (IOException e2)
		    { System.out.println("IO problem with " + scriptFileName + e2); }
		    catch (ScriptException e3)
		    { System.out.println("ScriptException in " + scriptFileName + e3); }
		    catch (NullPointerException e4)
		    { System.out.println ("Null ptr exception in " + scriptFileName + e4); }
	    }   
	

	//Function returns distanse between camera and node----------------
	static public float distance (Camera camera, Node node)
	{
	   Vector3f cameraPos = camera.getPo();
	   Vector3f nodePos = (Vector3f)node.getWorldPosition();
	   Vector3 differense = cameraPos.sub(nodePos);
	   
	   return (float)differense.lengthSquared();
	}

	//Function returns distanse between 2 nodes----------------------
	static float distance (Node node1, Node node2)
	{
	   Vector3f node1Pos = (Vector3f)node1.getWorldPosition();
	   Vector3f node2Pos = (Vector3f)node2.getWorldPosition();
	   Vector3 differense = node1Pos.sub(node2Pos);
	   
	   return (float)differense.lengthSquared();
	}

	//Function returns distanse between camera and Vector---------------
	static float distance (Camera camera, Vector3 vector)
	{
	   Vector3f cameraPos = camera.getPo();
	   Vector3 differense = cameraPos.sub(vector);
	   
	   return (float)differense.lengthSquared();
	}

	   /*
	   void collisionDetection(Engine myEngine)
	   {
	      if (!dolphinToggle)
	      {
	         Iterator <GameObject> trashCanIter = trashCanVector.iterator();
	         Iterator <ManualObject> trashCanObjIter = trashCanObjVector.iterator();
	         Iterator <SceneNode> trashCanNodeIter = trashCanNodeVector.iterator();
	         TrashCan trashCanCurrent;
	         ManualObject trashCanObjCurrent;
	         SceneNode trashCanNodeCurrent;

	   		while ( trashCanObjIter.hasNext() ) 
	   		{
	            trashCanCurrent = (TrashCan)trashCanIter.next();
	   			trashCanObjCurrent= trashCanObjIter.next();
	            trashCanNodeCurrent= trashCanNodeIter.next();
	    
	            if(OneMillonYearsBC_Game.distance(getCamera(),trashCanNodeCurrent)<0.5f)
	            {
	               score+=10;
	               trashCanIter.remove();
	               trashCanObjIter.remove();
	               myEngine.getSceneManager().destroySceneNode( trashCanNodeCurrent);
	               trashCanNodeIter.remove();
	               System.out.println("Trash cans were removed.");
	               return;
	            }
	   		}
				
	         return;
		   }
	   }
*/

	//Update Vertical position of Player according to Height mam-----------------------
	protected void updateVerticalPosition()
	{ 
		SceneNode tessNode = this.getEngine().getSceneManager().getSceneNode("tessN");
		Tessellation tessE = ((Tessellation) tessNode.getAttachedObject("tessE"));
		// Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = gameColl.localPlayerNode.getWorldPosition();
		Vector3 localAvatarPosition = gameColl.localPlayerNode.getLocalPosition();
		// use avatar World coordinates to get coordinates for height
		Vector3 newAvatarPosition = Vector3f.createFrom(localAvatarPosition.x(), tessE.getWorldHeight(worldAvatarPosition.x(), worldAvatarPosition.z()), localAvatarPosition.z() );
		// use avatar Local coordinates to set position, including height
		gameColl.localPlayerNode.setLocalPosition(newAvatarPosition);
	}
	

//----Getters and Setters---------------------------------
	public Camera getCamera() {
		return myCamera;
	}


	public void setCamera(Camera camera) {
		this.myCamera = camera;
	}


	public SceneNode getCameraNode() {
		return cameraNode;
	}


	public void setCameraNode(SceneNode cameraNode) {
		this.cameraNode = cameraNode;
	}


}
