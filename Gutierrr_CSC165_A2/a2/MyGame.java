package a2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import myGame.BounceController;
import myGame.Camera3Pcontroller;
import myGame.GhostAvatar;
import myGame.ProtocolClient;
import myGame.StretchController;
import myGame.modifiedForwardAction;
import myGame.modifiedYawAction;
import myGame.mountDolphinAction;
import myGame.moveForwardAction;
import myGame.quitGameAction;
import myGame.rotateYawAction;
import myGame.skillBurstAction;
import myGame.skillRotateAction;
import myGame.skillSpeedAction;

import net.java.games.input.Event;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.networking.IGameConnection.ProtocolType;
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
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.physics.PhysicsEngineFactory;

public class MyGame extends VariableFrameRateGame 
{
	private GL4RenderSystem renderSystem;
	private SceneManager sceneManager;
	private GenericInputManager inputManager;
	private ScriptEngine jsEngine;

	private Camera myCamera; //myCamera2;
	private Camera3Pcontroller orbitController; //orbitController2;
	private Entity myDolphin; //myDolphin2;
	private SceneNode cameraNode; //cameraNode2;
	private SceneNode dolphinNode; //dolphinNode2;
	
	private float elapsTime = 0.0f;
	private String elapsTimeStr, counterStr, dispStr, serverAddress;
	private File scriptFile3;
	private boolean isConnected;
	private int elapsTimeSec, counter = 0;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient clientProtocol;
	private float rotationSpeed = 45.0f;
	private float movementSpeed = 3.0f;
	private float bb = 0;
	private Random rand = new Random();
	
    private Entity[] entityArray = new Entity[15];
    private SceneNode[] sceneArray = new SceneNode[15];
    private Vector<UUID> gameObjectsToRemove;
    
//PHYSICS TRIAL
    private SceneNode ball1Node, ball2Node, groundNode;
    private final static String GROUND_E = "Ground";
    private final static String GROUND_N = "GroundNode";
    private PhysicsEngine physicsEng;
    private PhysicsObject ball1PhysObj, ball2PhysObj, gndPlaneP;
    private boolean running = false;
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Game Loop //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public MyGame(String serverAddr, int sPort) 
    {
        super();
        this.serverAddress = serverAddr;
        this.serverPort = sPort;
        this.serverProtocol = ProtocolType.UDP;
    }

    public static void main(String[] args) 
    {
        Game myGame = new MyGame(args[0], Integer.parseInt(args[1]));
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
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Network //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void setupNetworking()
    {
    	gameObjectsToRemove = new Vector<UUID>();
    	isConnected = false;
    	try
    	{
    		clientProtocol = new ProtocolClient(InetAddress.getByName(serverAddress), 
    				serverPort, serverProtocol, this);
    	}
    	catch(UnknownHostException e)
    	{
    		e.printStackTrace();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    	if (clientProtocol == null)
    	{
    		System.out.println("Missing protocol host");
    	}
    	else
    	{
    		clientProtocol.sendJoinMessage();
    		System.out.println("SENT JOIN MESSAGE");
    	}
    }
    
    public void processNetworking(float elapsTime)
    {
    	if (clientProtocol != null)
    	{
    		clientProtocol.processPackets();
    	}
    	Iterator<UUID> iterator = gameObjectsToRemove.iterator();
    	while(iterator.hasNext())
    	{
    		sceneManager.destroySceneNode(iterator.next().toString());
    	}
    	gameObjectsToRemove.clear();
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Window //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	@Override
	protected void setupWindow(RenderSystem renderSystem, GraphicsEnvironment graphicsEnviroment) 
	{
		renderSystem.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}
	protected void setupWindowViewports (RenderWindow renderWindow)
	{
		renderWindow.addKeyListener(this);
		Viewport topViewPort = renderWindow.getViewport(0);
	}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Setup Orbit Camera//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	protected void setupOrbitCamera(Engine engine, SceneManager sceneManager)
	{
		String keyboardName = inputManager.getKeyboardName();
		String gamepadName = inputManager.getFirstGamepadName();
		if (keyboardName != null)
		{
			orbitController = new Camera3Pcontroller(myCamera, cameraNode, dolphinNode, keyboardName, inputManager);
		}
		if (gamepadName != null)
		{
			//orbitController2 = new Camera3Pcontroller(myCamera2, cameraNode2, dolphinNode2, gamepadName, inputManager);
		}
	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Setup Cameras //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//	
    @Override
    protected void setupCameras(SceneManager sceneManager, RenderWindow renderWindow) 
    {
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
    	
    	/*
    	myCamera2 = sceneManager.createCamera("MainCamera2", Projection.PERSPECTIVE);
    	//renderWindow.getViewport(1).setCamera(myCamera2);
    	cameraNode2 = rootNode.createChildSceneNode(myCamera2.getName() + "Node");
    	
        myCamera2.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
        myCamera2.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        myCamera2.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
        myCamera2.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));
    	
    	//myCamera2.getFrustum().setFarClipDistance(1000.0f);
    	cameraNode2.attachObject(myCamera2);
    	cameraNode2.moveUp(0.3f);
    	*/
    }
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Scene //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    protected void setupScene(Engine gameEngine, SceneManager sceneMng) throws IOException 
    {
    	sceneManager = sceneMng;
        ScriptEngineManager factory = new ScriptEngineManager();
        java.util.List<ScriptEngineFactory> list = factory.getEngineFactories();
        jsEngine = factory.getEngineByName("js");
        scriptFile3 = new File("UpdateLightColor.js");
        this.runScript(scriptFile3);
        setupNetworking();
        
        //PHYSICS
        //Ball Objects
        Entity ball1Entity = sceneManager.createEntity("ball1", "earth.obj");
        ball1Node = sceneManager.getRootSceneNode().createChildSceneNode("Ball1Node");
        ball1Node.attachObject(ball1Entity);
        ball1Node.setLocalPosition(0, 2, -2);
        
        Entity ball2Entity = sceneManager.createEntity("ball2", "earth.obj");
        ball2Node = sceneManager.getRootSceneNode().createChildSceneNode("Ball2Node");
        ball2Node.attachObject(ball2Entity);
        ball2Node.setLocalPosition(-1, 10, -2);
        
        //GroundPlane
        Entity groundEntity = sceneManager.createEntity(GROUND_E, "cube.obj");
        groundNode = sceneManager.getRootSceneNode().createChildSceneNode(GROUND_N);
        groundNode.attachObject(groundEntity);
        groundNode.setLocalPosition(0, -7, -2);
        
        //EVERYTHING ELSE
        ManualObject pyr = makePyramid(gameEngine, sceneManager);
        SceneNode pyrN = sceneManager.getRootSceneNode().createChildSceneNode("PyrNode");
        pyrN.scale(0.75f, 0.75f, 0.75f);
        pyrN.attachObject(pyr);
        pyrN.moveUp(3.0f);
        
        ManualObject XAxis = makeXAxis(gameEngine, sceneManager);
        SceneNode XNode = sceneManager.getRootSceneNode().createChildSceneNode("XNode");
        XNode.attachObject(XAxis);
        
        ManualObject YAxis = makeYAxis(gameEngine, sceneManager);
        SceneNode YNode = sceneManager.getRootSceneNode().createChildSceneNode("YNode");
        YNode.attachObject(YAxis);
        
        ManualObject ZAxis = makeZAxis(gameEngine, sceneManager);
        SceneNode ZNode = sceneManager.getRootSceneNode().createChildSceneNode("ZNode");
        ZNode.attachObject(ZAxis);
        
        ManualObject Floor = groundFloor(gameEngine, sceneManager);
        SceneNode floorNode = sceneManager.getRootSceneNode().createChildSceneNode("floorNode");
        floorNode.attachObject(Floor);
        floorNode.setLocalScale(100.0f, 1.0f, 100.0f);

        SceneNode prizeNG = sceneManager.getRootSceneNode().createChildSceneNode("myPrizeNG");
        //PRIZE CREATION
        for (int i = 0; i <= 9; i++)
        {
        	entityArray[i] = sceneManager.createEntity("myPrize" + i, "cube.obj");
        	sceneArray[i] = prizeNG.createChildSceneNode(entityArray[i].getName() + "Node");
        	float randomPos = rand.nextInt(40) - 20;
        	sceneArray[i].moveBackward(randomPos);
        	randomPos = rand.nextInt(40) - 20;
        	sceneArray[i].moveRight(randomPos);
        	sceneArray[i].scale(0.5f, 0.5f, 0.5f);
        	sceneArray[i].attachObject(entityArray[i]);
        }
        
        SceneNode mysteryBoxNG = sceneManager.getRootSceneNode().createChildSceneNode("myMysteryBoxNG");
        //MYSTERY BOX CREATION
        for (int j = 10; j <= 14; j++)
        {
        	entityArray[j] = sceneManager.createEntity("myBox" + j, "sphere.obj");
        	sceneArray[j] = mysteryBoxNG.createChildSceneNode(entityArray[j].getName() + "Node");
        	float randomPos = rand.nextInt(30) - 15;
        	sceneArray[j].moveBackward(randomPos);
        	randomPos = rand.nextInt(30) - 15;
        	sceneArray[j].moveRight(randomPos);
        	sceneArray[j].scale(0.5f, 0.5f, 0.5f);
        	sceneArray[j].attachObject(entityArray[j]);
        }
        SceneNode dolphinNG = sceneManager.getRootSceneNode().createChildSceneNode("myDolphinNG");
        
        myDolphin = sceneManager.createEntity("myDolphin", "dolphinHighPoly.obj");
        myDolphin.setPrimitive(Primitive.TRIANGLES);
        dolphinNode = dolphinNG.createChildSceneNode(myDolphin.getName() + "Node");
        dolphinNode.moveBackward(2.0f);
        dolphinNode.attachObject(myDolphin);

       	setupInputs();
    	setupOrbitCamera(gameEngine, sceneManager);
        initPhysicsSystem();
        createRagePhysicsWorld();

        sceneManager.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
		
		Light plight = sceneManager.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);
		
		SceneNode plightNode = sceneManager.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);
        
        BounceController bounceController = new BounceController();
        bounceController.addNode(prizeNG);
        sceneManager.addController(bounceController);
        
        StretchController stretchController = new StretchController();
        stretchController.addNode(mysteryBoxNG);
        sceneManager.addController(stretchController);
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
    protected ManualObject groundFloor(Engine engine, SceneManager sceneManager) throws IOException
    {
    	ManualObject floor = sceneManager.createManualObject("Floor");
    	ManualObjectSection floorSec = floor.createManualSection("FloorSection");
    	floor.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
    	float[] vertices = new float[]
    			{
                    -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
                    1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f //RR
    			};
    	float[] texcoords = new float[]
    			{
    					0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
    					0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f
    			};
    	float[] normals = new float[]
    			{
    					0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
    					1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f
    			};
    	int[] indices = new int[] {0, 1, 2, 3, 4, 5};
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		floorSec.setVertexBuffer(vertBuf);
		floorSec.setTextureCoordsBuffer(texBuf);
		floorSec.setNormalsBuffer(normBuf);
		floorSec.setIndexBuffer(indexBuf);
		Texture tex = engine.getTextureManager().getAssetByPath("earth-night.jpeg");
		TextureState texState = (TextureState)sceneManager.getRenderSystem().
		createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sceneManager.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		faceState.setVertexWinding(FrontFaceState.VertexWinding.CLOCKWISE);
		floor.setDataSource(DataSource.INDEX_BUFFER);
		floor.setRenderState(texState);
		floor.setRenderState(faceState);
		
		return floor;
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
		int windowHeight = (int) (renderSystem.getCanvas().getHeight() * 1.05);
		int windowWidth = renderSystem.getCanvas().getWidth();
		
		processNetworking(elapsTime);
		
		//PHYSICS
		if (running)
		{
			Matrix4 mat;
			physicsEng.update(elapsTime);
			for (SceneNode s : gameEngine.getSceneManager().getSceneNodes())
			{
				if (s.getPhysicsObject() != null)
				{
					mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
					s.setLocalPosition(mat.value(0, 3),mat.value(1, 3), mat.value(2, 3));
				}
			}
		}
		
		renderSystem.setHUD(dispStr, windowWidth / 100, windowHeight / 70);
		if (orbitController != null)
		{
			orbitController.updateCameraPosition();
		}
		this.collision();
		inputManager.update(elapsTime);
	}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Ghost Avatar //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws IOException
    {
    	if (avatar != null)
    	{
    		Entity ghostE = sceneManager.createEntity(avatar.getID().toString(), "dolphinHighPoly.obj");
    		avatar.setEntity(ghostE);
    		ghostE.setPrimitive(Primitive.TRIANGLES);
    		SceneNode ghostN = sceneManager.getRootSceneNode().createChildSceneNode(avatar.getID().toString() + "Node");
    		avatar.setNode(ghostN);
    		ghostN.attachObject(ghostE);
    		ghostN.setLocalPosition(avatar.getPosition());
    		avatar.setNode(ghostN);
    		avatar.setEntity(ghostE);
    		avatar.setPosition(ghostN.getLocalPosition());
    		System.out.println("CLIENT HAS JOINED");
    	}
    }
    
    public void moveGhostAvatarAroundGameWorld(GhostAvatar ghostA, Vector3 pos)
    {
    	System.out.println("Action taken");
    	ghostA.getSceneNode().setLocalPosition(pos);
    }
    
    public void removeGhostAvatarFromGameWorld(GhostAvatar ghostID)
    {
    	if (ghostID != null)
    	{
    		gameObjectsToRemove.add(ghostID.getID());
    	}
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// SetUp Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void setupInputs()
    {	
    	inputManager = new GenericInputManager();
    	SceneManager sceneManager = getEngine().getSceneManager();
    	String keyboardName = inputManager.getKeyboardName();
    	String gamepadName = inputManager.getFirstGamepadName();
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Modified Action Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    	modifiedYawAction modYawLeftAction = new modifiedYawAction(dolphinNode, clientProtocol, rotationSpeed);
    	modifiedYawAction modYawRightAction = new modifiedYawAction(dolphinNode, clientProtocol, -rotationSpeed);
    	modifiedForwardAction modBackwardAction = new modifiedForwardAction(dolphinNode,  clientProtocol, -movementSpeed);
    	modifiedForwardAction modForwardAction = new modifiedForwardAction(dolphinNode,  clientProtocol, movementSpeed);
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Action Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    	moveForwardAction moveBackwardAction = new moveForwardAction(modBackwardAction, myCamera, -movementSpeed);
    	moveForwardAction moveForwardAction = new moveForwardAction(modForwardAction, myCamera, movementSpeed);
    	rotateYawAction yawLeftAction = new rotateYawAction(modYawLeftAction, myCamera, dolphinNode, rotationSpeed);
    	rotateYawAction yawRightAction = new rotateYawAction(modYawRightAction, myCamera, dolphinNode, -rotationSpeed);
    	mountDolphinAction mountDolphinAction = new mountDolphinAction(myCamera, cameraNode, dolphinNode);
    	skillSpeedAction skillSpeed = new skillSpeedAction(this);
    	skillRotateAction skillRotate = new skillRotateAction(this);
    	skillBurstAction skillBurst = new skillBurstAction(this, dolphinNode);
    	ColorAction colorAction = new ColorAction(sceneManager);
    	quitGameAction quitGame = new quitGameAction(this, clientProtocol);
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Player 2 Gamepad Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    	/*
    	modifiedForwardAction modForwardTwo = new modifiedForwardAction(dolphinNode2, -movementSpeed);
    	modifiedYawAction modYawTwo = new modifiedYawAction(dolphinNode2, -rotationSpeed);
    	
    	moveForwardAction moveBackwardTwo = new moveForwardAction(modForwardTwo, myCamera2, -movementSpeed);
    	rotateYawAction yawRightTwo = new rotateYawAction(modYawTwo, myCamera2, dolphinNode2, -rotationSpeed);
    	mountDolphinAction mountDolphinTwo = new mountDolphinAction(myCamera2, cameraNode2, dolphinNode2);
    	skillBurstAction skillBurstTwo = new skillBurstAction(this, dolphinNode2);
    	*/
    	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Keyboard Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    if (keyboardName != null)
    {
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.A, yawLeftAction, 
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.D, yawRightAction, 
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
    	inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.R, colorAction, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    }
	else
    {
    	System.out.println("KEYBOARD NOT READY");
    }
    	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Gamepad Inputs //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    /*
    if (gamepadName != null)
    {
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.Y, moveBackwardTwo, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName,  net.java.games.input.Component.Identifier.Axis.X, yawRightTwo, 
    			InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    	inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Button._0, mountDolphinTwo, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Button._1, skillSpeed, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Button._2, skillRotate, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    	inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Button._3, skillBurst, 
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
    }
    else
    {
    	System.out.println("GAMEPAD NOT READY");
    }
    */
}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Collision Loop //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public void collision()
    {
    	SceneManager sceneManager = getEngine().getSceneManager();
    	for (int i = 0; i <= 9; i++)
    	{
    		if (sceneArray[i] != null)
    		{
				if (collidesWith(sceneArray[i], dolphinNode, 1.0f)) //collidesWith(sceneArray[i], dolphinNode2, 0.5f))
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
				if (collidesWith(sceneArray[i], dolphinNode, 1.0f)) //collidesWith(sceneArray[i], dolphinNode2, 0.5f))
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
    public boolean collidesWith(SceneNode node, SceneNode dolphin, float fNo)
    {
    	Vector3f dolphinPo = (Vector3f) dolphin.getLocalPosition();
    	Vector3f nodePo = (Vector3f) node.getLocalPosition();
    	
    	float dX = Math.abs(dolphinPo.x() - nodePo.x());
    	float dY = Math.abs(dolphinPo.y() - nodePo.y());
    	float dZ = Math.abs(dolphinPo.z() - nodePo.z());
    	float distance = (dX + dY + dZ);
    	
    	if (distance < fNo)
    	{
    		return true;
    	}	
    	return false;
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Physics //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private void initPhysicsSystem()
    {
    	String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
    	float[] gravity = {0, -3f, 0};
    	
    	physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
    	physicsEng.initSystem();
    	physicsEng.setGravity(gravity);
    }
    
    private void createRagePhysicsWorld()
    {
    	float mass = 1.0f;
    	float up[] = {0, 1f, 0};
    	double[] temptf;
    	
    	temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
    	ball1PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(), mass, temptf, 2.0f);
    	ball1PhysObj.setBounciness(1.0f);
    	ball1Node.setPhysicsObject(ball1PhysObj);
    	
    	temptf = toDoubleArray(ball1Node.getLocalTransform().toFloatArray());
    	ball2PhysObj = physicsEng.addSphereObject(physicsEng.nextUID(), mass, temptf, 2.0f);
    	ball2PhysObj.setBounciness(1.0f);
    	ball2Node.setPhysicsObject(ball2PhysObj);
    	
    	temptf = toDoubleArray(groundNode.getLocalTransform().toFloatArray());
    	gndPlaneP = physicsEng.addStaticPlaneObject(physicsEng.nextUID(), temptf, up, 0.0f);
    	gndPlaneP.setBounciness(1.0f);
    	groundNode.scale(3f, .05f, 3f);
    	groundNode.setLocalPosition(0, -7, -2);
    	groundNode.setPhysicsObject(gndPlaneP);
    }
    
    private float[] toFloatArray(double[] arr)
    {
    	if (arr == null)
    	{
    		return null;
    	}
    	else
    	{
    		int n = arr.length;
    		float[] ret = new float[n];
    		for (int i = 0; i < n; i++)
    		{
    			ret[i] = (float)arr[i];
    		}
    		return ret;
    	}
    }
    
    private double[] toDoubleArray(float[] arr)
    {
    	if (arr == null)
    	{
    		return null;
    	}
    	else
    	{
    		int n = arr.length;
    		double[] ret = new double[n];
    		for (int i = 0; i < n; i++)
    		{
    			ret[i] = (double)arr[i];
    		}
    		return ret;
    	}
    }
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Scripting //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private void runScript(File scriptFile)
    {
    	try
	    { 
    		FileReader fileReader = new FileReader(scriptFile);
    		jsEngine.eval(fileReader);
    		fileReader.close();
	    }
	    catch (FileNotFoundException e1)
	    { 
	    	System.out.println(scriptFile + " not found " + e1); 
	    }
	    catch (IOException e2)
	    { 
	    	System.out.println("IO problem with " + scriptFile + e2); 
	    }
	    catch (ScriptException e3)
	    { 
	    	System.out.println("Script Exception in " + scriptFile + e3); 
	    }
	    catch (NullPointerException e4)
	    {
	    	System.out.println ("Null ptr exception reading " + scriptFile + e4); 
	    }
    }
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Setters //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
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
	public void setIsConnected(boolean b) 
	{
		isConnected = b;
	}
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Getters //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
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
	public Vector3 getPlayerPosition() 
	{
		return (Vector3)dolphinNode.getWorldPosition();
	}
	
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// Nested Class //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	public class ColorAction extends AbstractInputAction
    { 
        private SceneManager sceneManager;
        public ColorAction(SceneManager s) 
        { 
        	sceneManager = s; 
        } // constructor

		public void performAction(float time, Event e) 
		{
			{
	            Invocable invocableEngine = (Invocable) jsEngine ;
	            //get the light to be updated
	            
	            //Light lgt = sm.getLight("testLamp1");
	            Light lgt = sceneManager.getLight("testLamp1");

	            // invoke the script function
	            try
	            { 
	                invocableEngine.invokeFunction("updateAmbientColor", lgt); 
	            } catch (ScriptException e1) { 
	                System.out.println("ScriptException in " + scriptFile3 + e1); 
	            } catch (NoSuchMethodException e2) { 
	                System.out.println("No such method in " + scriptFile3 + e2); 
	            } catch (NullPointerException e3) { 
	                System.out.println ("Null ptr exception reading " + scriptFile3 + e3); }
	        }
		}
    }
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{
		public void performAction(float time, Event e)
		{
			if (clientProtocol != null && isConnected == true)
			{
				clientProtocol.sendByeMessage();
			}
		}
	}
	
	//KEY LISTENER
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_SPACE:
			System.out.println("START PHYSICS");
			running = true;
			break;
		}
		super.keyPressed(e);
	}
}