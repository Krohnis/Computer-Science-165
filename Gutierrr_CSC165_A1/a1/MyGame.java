package a1;

import java.awt.*;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import myGame.modifiedForwardAction;
import myGame.modifiedPitchAction;
import myGame.modifiedRightAction;
import myGame.modifiedRollAction;
import myGame.modifiedYawAction;
import myGame.mountDolphinAction;
import myGame.moveForwardAction;
import myGame.moveRightAction;
import myGame.quitGameAction;
import myGame.rotatePitchAction;
import myGame.rotateRollAction;
import myGame.rotateYawAction;
import myGame.skillBurstAction;
import myGame.skillRotateAction;
import myGame.skillSpeedAction;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.rage.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.util.BufferUtil;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;

public class MyGame extends VariableFrameRateGame 
{
	private GL4RenderSystem renderSystem;
	private GenericInputManager inputManager;
	
	private Camera myCamera;
	private Entity myDolphin;
	private SceneNode cameraNode;
	private SceneNode dolphinNode;
	
	private float elapsTime = 0.0f;
	private String elapsTimeStr, counterStr, dispStr;
	private int elapsTimeSec, counter = 0;
	private float rotationSpeed = 30.0f;
	private float movementSpeed = 3.0f;
	private boolean isMounted = false;
	private Random rand = new Random();
	
    private Entity[] entityArray = new Entity[15];
    private SceneNode[] sceneArray = new SceneNode[15];
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Game Loop //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public MyGame() 
    {
        super();
    }

    public static void main(String[] args) 
    {
        Game myGame = new MyGame();
        try 
        {
        	myGame.startup();
        	myGame.run();
        } 
        catch (Exception e) 
        {
            e.printStackTrace(System.err);
        } 
        finally 
        {
        	myGame.shutdown();
        	myGame.exit();
        }
    }
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Window //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	@Override
	protected void setupWindow(RenderSystem renderSystem, GraphicsEnvironment graphicsEnviroment) 
	{
		renderSystem.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}

	
    @Override
    protected void setupCameras(SceneManager sceneManager, RenderWindow renderWindow) {
        SceneNode rootNode = sceneManager.getRootSceneNode();
        myCamera = sceneManager.createCamera("MainCamera", Projection.PERSPECTIVE);
        renderWindow.getViewport(0).setCamera(myCamera);
        
        myCamera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
        myCamera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        myCamera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
        myCamera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));

        cameraNode = rootNode.createChildSceneNode(myCamera.getName() + "Node");
        cameraNode.attachObject(myCamera);
    	cameraNode.moveUp(0.3f);
    }
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Scene //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    protected void setupScene(Engine gameEngine, SceneManager sceneManager) throws IOException 
    {
        ManualObject pyr = makePyramid(gameEngine, sceneManager);
        SceneNode pyrN = sceneManager.getRootSceneNode().createChildSceneNode("PyrNode");
        pyrN.scale(0.75f, 0.75f, 0.75f);
        pyrN.attachObject(pyr);
        pyrN.moveDown(2.0f);
        
        ManualObject XAxis = makeXAxis(gameEngine, sceneManager);
        SceneNode XNode = sceneManager.getRootSceneNode().createChildSceneNode("XNode");
        XNode.attachObject(XAxis);
        
        ManualObject YAxis = makeYAxis(gameEngine, sceneManager);
        SceneNode YNode = sceneManager.getRootSceneNode().createChildSceneNode("YNode");
        YNode.attachObject(YAxis);
        
        ManualObject ZAxis = makeZAxis(gameEngine, sceneManager);
        SceneNode ZNode = sceneManager.getRootSceneNode().createChildSceneNode("ZNode");
        ZNode.attachObject(ZAxis);

        //PRIZE CREATION
        for (int i = 0; i <= 9; i++)
        {
        	entityArray[i] = sceneManager.createEntity("myPrize" + i, "cube.obj");
        	sceneArray[i] = sceneManager.getRootSceneNode().createChildSceneNode(entityArray[i].getName() + "Node");
        	float randomPos = rand.nextInt(30) - 15;
        	sceneArray[i].moveBackward(randomPos);
        	randomPos = rand.nextInt(30) - 15;
        	sceneArray[i].moveRight(randomPos);
        	randomPos = rand.nextInt(30) - 15;
        	sceneArray[i].moveDown(randomPos);
        	sceneArray[i].scale(0.5f, 0.5f, 0.5f);
        	sceneArray[i].attachObject(entityArray[i]);
        }
        
        //MYSTERY BOX CREATION
        for (int j = 10; j <= 14; j++)
        {
        	entityArray[j] = sceneManager.createEntity("myBox" + j, "sphere.obj");
        	sceneArray[j] = sceneManager.getRootSceneNode().createChildSceneNode(entityArray[j].getName() + "Node");
        	float randomPos = rand.nextInt(30) - 15;
        	sceneArray[j].moveBackward(randomPos);
        	randomPos = rand.nextInt(30) - 15;
        	sceneArray[j].moveRight(randomPos);
        	randomPos = rand.nextInt(30) - 15;
        	sceneArray[j].moveDown(randomPos);
        	sceneArray[j].scale(0.5f, 0.5f, 0.5f);
        	sceneArray[j].attachObject(entityArray[j]);
        }
        
        myDolphin = sceneManager.createEntity("myDolphin", "dolphinHighPoly.obj");
        myDolphin.setPrimitive(Primitive.TRIANGLES);
        dolphinNode = sceneManager.getRootSceneNode().createChildSceneNode(myDolphin.getName() + "Node");
        dolphinNode.moveBackward(2.0f);
        dolphinNode.attachObject(myDolphin);
        
       	setupInputs();

        sceneManager.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
		
		Light plight = sceneManager.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
		
		SceneNode plightNode = sceneManager.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// ManualObject //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    protected ManualObject makePyramid(Engine eng, SceneManager sm) throws IOException
	{ 
    	ManualObject pyr = sm.createManualObject("Pyramid");
		ManualObjectSection pyrSec =
		pyr.createManualSection("PyramidSection");
		pyr.setGpuShaderProgram(sm.getRenderSystem().
		getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		float[] vertices = new float[]
		{
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //front
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //right
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, //back
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, //left	
			
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, -3.0f, 0.0f, //front
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, -3.0f, 0.0f, //right
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, -3.0f, 0.0f, //back
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, -3.0f, 0.0f //left	
		};
		float[] texcoords = new float[]
		{ 
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
    		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
    		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
    		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
    		
			0.0f, 0.0f, -1.0f, 0.0f, -0.5f, -1.0f,
    		0.0f, 0.0f, -1.0f, 0.0f, -0.5f, -1.0f,
    		0.0f, 0.0f, -1.0f, 0.0f, -0.5f, -1.0f,
    		0.0f, 0.0f, -1.0f, 0.0f, -0.5f, -1.0f
		};
		float[] normals = new float[]
		{ 
			0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
    		1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
    		0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
    		-1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
    		
			0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -3.0f, 1.0f,
    		1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -3.0f, 0.0f,
    		0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -3.0f, -1.0f,
    		-1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, -3.0f, 0.0f
		};
		int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 };
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		pyrSec.setVertexBuffer(vertBuf);
		pyrSec.setTextureCoordsBuffer(texBuf);
		pyrSec.setNormalsBuffer(normBuf);
		pyrSec.setIndexBuffer(indexBuf);
		Texture tex = eng.getTextureManager().getAssetByPath("chain-fence.jpeg");
		TextureState texState = (TextureState)sm.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().
		createRenderState(RenderState.Type.FRONT_FACE);
		pyr.setDataSource(DataSource.INDEX_BUFFER);
		pyr.setRenderState(texState);
		pyr.setRenderState(faceState);
		return pyr;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Axis //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    protected ManualObject makeXAxis(Engine engine, SceneManager sceneManager) throws IOException
    {
        ManualObject XAxis = sceneManager.createManualObject("XAxis");
        ManualObjectSection XAxisSection = XAxis.createManualSection("XSection");
        XAxis.setPrimitive(Primitive.LINES);
        XAxis.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float [] vertices = new float[]
        {
             0.0f, 0.0f, 0.0f, 30.0f, 0.0f, 0.0f
        };
        
        int[] indices = new int[] {0, 1};

	    FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
	    IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	    XAxisSection.setVertexBuffer(vertBuf);
	    XAxisSection.setIndexBuffer(indexBuf);
	    Material material = sceneManager.getMaterialManager().getAssetByPath("default.mtl");
	    material.setEmissive(Color.BLUE);
        Texture texture = sceneManager.getTextureManager().getAssetByPath(material.getTextureFilename());
        TextureState tstate = (TextureState) sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        tstate.setTexture(texture);
        XAxisSection.setRenderState(tstate);
        XAxisSection.setMaterial(material);
        
        return XAxis;
    }
    protected ManualObject makeYAxis(Engine engine, SceneManager sceneManager) throws IOException
    {
        ManualObject YAxis = sceneManager.createManualObject("YAxis");
        ManualObjectSection YAxisSection = YAxis.createManualSection("YSection");
        YAxis.setPrimitive(Primitive.LINES);
        YAxis.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float [] vertices = new float[]
        {
             0.0f, 0.0f, 0.0f, 00.0f, 30.0f, 0.0f
        };
        
        int[] indices = new int[] {0, 1};

	    FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
	    IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	    YAxisSection.setVertexBuffer(vertBuf);
	    YAxisSection.setIndexBuffer(indexBuf);
	    Material material3 = sceneManager.getMaterialManager().getAssetByPath("default.mtl");
	    material3.setEmissive(Color.RED);
        Texture texture3 = sceneManager.getTextureManager().getAssetByPath(material3.getTextureFilename());
        TextureState tstate3 = (TextureState) sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        tstate3.setTexture(texture3);
        YAxisSection.setRenderState(tstate3);
        YAxisSection.setMaterial(material3);
        
        return YAxis;
    }
    protected ManualObject makeZAxis(Engine engine, SceneManager sceneManager) throws IOException
    {
        ManualObject ZAxis = sceneManager.createManualObject("ZAxis");
        ManualObjectSection ZAxisSection = ZAxis.createManualSection("ZAxis");
        ZAxis.setPrimitive(Primitive.LINES);
        ZAxis.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
        float [] vertices = new float[]
        {
             0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 30.0f
        };
        
        int[] indices = new int[] {0, 1};

	    FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
	    IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
	    ZAxisSection.setVertexBuffer(vertBuf);
	    ZAxisSection.setIndexBuffer(indexBuf);
	    Material material = sceneManager.getMaterialManager().getAssetByPath("default.mtl");
	    material.setEmissive(Color.GREEN);
        Texture texture = sceneManager.getTextureManager().getAssetByPath(material.getTextureFilename());
        TextureState tstate = (TextureState) sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        tstate.setTexture(texture);
        ZAxisSection.setRenderState(tstate);
        ZAxisSection.setMaterial(material);
        
        return ZAxis;
    }

    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Update //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    protected void update(Engine gameEngine) 
    {
    	renderSystem = (GL4RenderSystem) gameEngine.getRenderSystem();
		elapsTime += gameEngine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		counterStr = Integer.toString(counter);
		dispStr = "Time = " + elapsTimeStr + " Score = " + counterStr;
		renderSystem.setHUD(dispStr, 15, 15);
		this.collision();
		this.cameraRelocation();
		inputManager.update(elapsTime);
	}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void setupInputs()
    {	
    	inputManager = new GenericInputManager();
    	String keyboardName = inputManager.getKeyboardName();
    	String gamepadName = inputManager.getFirstGamepadName();
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Modified Action Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    	modifiedYawAction modYawLeftAction = new modifiedYawAction(this, rotationSpeed);
    	modifiedYawAction modYawRightAction = new modifiedYawAction(this, -rotationSpeed);
    	modifiedPitchAction modPitchUpAction = new modifiedPitchAction(this, -rotationSpeed);
    	modifiedPitchAction modPitchDownAction = new modifiedPitchAction(this, rotationSpeed);
    	modifiedRollAction modRollLeftAction = new modifiedRollAction(this, -rotationSpeed);
    	modifiedRollAction modRollRightAction = new modifiedRollAction(this, rotationSpeed);
    	modifiedRightAction modRightAction = new modifiedRightAction(this, movementSpeed);
    	modifiedRightAction modLeftAction = new modifiedRightAction(this, -movementSpeed);
    	modifiedForwardAction modBackwardAction = new modifiedForwardAction(this, -movementSpeed);
    	modifiedForwardAction modForwardAction = new modifiedForwardAction(this, movementSpeed);
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Action Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    	moveForwardAction moveBackwardAction = new moveForwardAction(this, modBackwardAction, -movementSpeed);
    	moveForwardAction moveForwardAction = new moveForwardAction(this, modForwardAction, movementSpeed);
    	moveRightAction moveRightAction = new moveRightAction(this, modRightAction, -movementSpeed);
    	moveRightAction moveLeftAction = new moveRightAction(this, modLeftAction, movementSpeed);
    	rotateYawAction yawLeftAction = new rotateYawAction(this, modYawLeftAction, rotationSpeed);
    	rotateYawAction yawRightAction = new rotateYawAction(this, modYawRightAction, -rotationSpeed);
    	rotatePitchAction pitchUpAction = new rotatePitchAction(this, modPitchUpAction, rotationSpeed);
    	rotatePitchAction pitchDownAction = new rotatePitchAction(this, modPitchDownAction, -rotationSpeed);
    	rotateRollAction rollLeftAction = new rotateRollAction(this, modRollLeftAction, -rotationSpeed);
    	rotateRollAction rollRightAction = new rotateRollAction(this, modRollRightAction, rotationSpeed);
    	mountDolphinAction mountDolphinAction = new mountDolphinAction(this, myCamera, cameraNode, dolphinNode);
    	skillSpeedAction skillSpeed = new skillSpeedAction(this);
    	skillRotateAction skillRotate = new skillRotateAction(this);
    	skillBurstAction skillBurst = new skillBurstAction(this);
    	quitGameAction quitGame = new quitGameAction(this);
    	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Keyboard Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    if (keyboardName != null)
    {
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.A, moveRightAction, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.D, moveLeftAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.Q, rollLeftAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.E, rollRightAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.LEFT, yawLeftAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.RIGHT, yawRightAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.UP, pitchUpAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.DOWN, pitchDownAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.SPACE, mountDolphinAction, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.F, skillSpeed, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.G, skillRotate, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.LSHIFT, skillBurst, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    }
    else
    {
    	System.out.println("KEYBOARD NOT READY");
    }
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Gamepad Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    if (gamepadName != null)
    {
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.X, moveLeftAction, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.Y, moveBackwardAction, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.RX, yawRightAction, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.RY, pitchDownAction, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Button._0, mountDolphinAction, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    }
    else
    {
    	System.out.println("GAMEPAD NOT READY");
    }
}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Rebound Loop //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void cameraRelocation()
    {
    	if (!(collidesWith(dolphinNode, 3.0f)))
    	{
			Vector3f dolphinN = (Vector3f) dolphinNode.getLocalPosition();
			myCamera.setPo((Vector3f) Vector3f.createFrom(dolphinN.x(), (0.3f + dolphinN.y()), dolphinN.z()));
    	}
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Collision Loop //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void collision()
    {
    	SceneManager sceneManager = getEngine().getSceneManager();
    	for (int i = 0; i <= 9; i++)
    	{
    		if (sceneArray[i] != null)
    		{
				if (collidesWith(sceneArray[i], 1.0f))
				{
					counter += 1;
					String destroyedNode = sceneArray[i].getName();
	    			sceneArray[i] = null;
	    			sceneManager.destroySceneNode(destroyedNode);
				}
    		}
    	}
    	for (int i = 9; i <= 14; i++)
    	{
    		if (sceneArray[i] != null)
    		{
				if (collidesWith(sceneArray[i], 1.0f))
				{
					int mysteryBox = rand.nextInt(3);
					String destroyedNode = sceneArray[i].getName();
	    			sceneArray[i] = null;
	    			sceneManager.destroySceneNode(destroyedNode);
	    			if (mysteryBox == 0)
	    			{
	    				dolphinNode.setLocalPosition(0.0f, 0.0f, 0.0f);
	    				System.out.println("WARP BOX, BACK TO ORIGIN");
	    			}
	    			else if (mysteryBox == 1)
	    			{
	    				movementSpeed = movementSpeed * 0.5f;
	    				rotationSpeed = rotationSpeed * 0.5f;
	    				System.out.println("DOOM BOX, HALVE YOUR SPEED");
	    			}
	    			else
	    			{
	    				this.setMovementSpeed();
	    				this.setRotationSpeed();
	    				System.out.println("BONUS BOX, SLIGHT STAT INCREASE");
	    			}
				}
    		}
    	}
    }
    
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Collides With //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public boolean collidesWith(SceneNode node, float fNo)
    {
    	Vector3f cameraPo = myCamera.getPo();
    	Vector3f nodePo = (Vector3f) node.getLocalPosition();
    	
    	float dX = Math.abs(cameraPo.x() - nodePo.x());
    	float dY = Math.abs(cameraPo.y() - nodePo.y());
    	float dZ = Math.abs(cameraPo.z() - nodePo.z());
    	float distance = (dX + dY + dZ);
    	
    	if (distance < fNo)
    	{
    		return true;
    	}	
    	return false;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Setters //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void setIsMounted()
    {
    	isMounted = !isMounted;
    }
    
    public void setCounter()
    {
    	counter -= 1;
    }
    
    public void setMovementSpeed()
    {
    	movementSpeed += 1.0f;
    }
	public void setRotationSpeed() 
	{
		rotationSpeed += 3.0f;
	}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Getters //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public boolean getIsMounted()
    {
    	return isMounted;
    }
    
    public int getCounter()
    {
    	return counter;
    }
    
    public Camera getCamera()
    {
    	return myCamera;
    }
    
    public Entity getDolphin()
    {
    	return myDolphin;
    }
    
    public SceneNode getCameraNode()
    {
    	return cameraNode;
    }
    
    public SceneNode getDolphinNode()
    {
    	return dolphinNode;
    }
}