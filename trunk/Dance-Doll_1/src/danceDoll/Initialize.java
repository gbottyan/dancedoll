/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package danceDoll;

import com.jme3.animation.Skeleton;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import GuiController.GuiController;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author master
 */
public class Initialize extends DanceDoll {

    protected DanceDoll danceDoll;
    protected Skeleton skelett;
    private NiftyJmeDisplay niftyDisplay;
    private AnimationControl ani;
    private AudioNode diskoStu;

    public Initialize(DanceDoll dd, AppSettings settings) throws IOException {
        danceDoll = dd;
        SimpleApplication dollApp = (SimpleApplication) danceDoll;

        // Manager speichern
        assetManager = dollApp.getAssetManager();
        audioRenderer = dollApp.getAudioRenderer();
        renderManager = dollApp.getRenderManager();
        stateManager = dollApp.getStateManager();
        listener = dollApp.getListener();
        context = dollApp.getContext();
        mouseInput = context.getMouseInput();
        keyInput = context.getKeyInput();
        inputManager = dollApp.getInputManager();
        

        // Kamera einstellen        
        cam = dollApp.getCamera();
        cam.lookAt(new Vector3f(0, -6, 0), Vector3f.UNIT_Y);

        viewPort = dollApp.getViewPort();

        // Schatten Renderer
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 1024, 1);
        pssmRenderer.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        pssmRenderer.setLambda(0.3f);
        pssmRenderer.setShadowIntensity(0.6f);
        pssmRenderer.setFilterMode(PssmShadowRenderer.FilterMode.PCF8);
        pssmRenderer.setEdgesThickness(5);
        //viewPort.addProcessor(pssmRenderer);
        
        // Turntable laden
        Scenery sc = new Scenery(assetManager);
        Node scenery = sc.getNode();
        rootNode.attachChild(scenery);

        dollApp.getRootNode().attachChild(rootNode);
        dollApp.getRootNode().attachChild(guiNode);

        guiViewPort = dollApp.getGuiViewPort();
        guiViewPort.setClearFlags(false, true, true);

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        guiViewPort.attachScene(guiNode);

       
        niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        niftyDisplay.getNifty().fromXml("Interface/Gui.xml","start", new GuiController(nifty,dd, this));
        guiViewPort.addProcessor(niftyDisplay);

        // Model laden
        Model m = sc.getModel();
        skelett = m.getSkeletton();   
        
        // BVH- Einlesen
        BVHController bvh1 = new BVHController(assetManager, "Animations/01_1_Kopf_kratzen.bvh");        
        BVHController bvh2 = new BVHController(assetManager, "Animations/02_Grundmove.bvh");        
        BVHController bvh3 = new BVHController(assetManager, "Animations/03_Schulterzucken.bvh");        
        BVHController bvh4 = new BVHController(assetManager, "Animations/04_Winken.bvh");        
        BVHController bvh5 = new BVHController(assetManager, "Animations/12_Cooler_shaker.bvh");        
        
        // AnimationCpntrol erstellen
        ani = new AnimationControl(m);
        
        ani.pushAnimation(bvh1);

        ani.pushAnimation(bvh2);
        if(bvh1.chkBVH())
            ani.startAnimation(bvh1.getBVHName());
        
        // Musik starten
        diskoStu = new AudioNode(assetManager, "Sounds/DiskoStu_Hibtin.ogg", false);
        diskoStu.setLooping(false);
        diskoStu.setVolume(2);
        rootNode.attachChild(diskoStu);
        diskoStu.play();
        
        // KeyListener starten
        initKeys();
    }    
        
    // KeyListener  
    private void initKeys() {
        inputManager.addMapping("StopMusic", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addListener(actionListener, "StopMusic");
    }
 
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("StopMusic") && !keyPressed) {
            diskoStu.stop();
            }
        }
    };
    
    
    public DanceDoll getDoll() {
        return danceDoll;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public AudioRenderer getAudioRenderer() {
        return audioRenderer;
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    public Node getGUINode() {
        return guiNode;
    }
    
    /**
     * 
     * @return the Animation Controller
     */
    public AnimationControl getAnimationControl() {
        return this.ani;
    }
    
}
