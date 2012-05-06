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
import java.io.IOException;

/**
 *
 * @author master
 */
public class Initialize extends DanceDoll {

    protected DanceDoll danceDoll;
    //protected Node rootNode = new Node("Root Node");
    //protected Node guiNode = new Node("Gui Node"); 
    protected Skeleton skelett;
    private NiftyJmeDisplay niftyDisplay;

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
        niftyDisplay.getNifty().fromXml("Interface/initialize.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);

        // Model laden
        Model m = sc.getModel();
        skelett = m.getSkeletton();

        // BVH- Einlesen
        BVHController bvh = new BVHController(assetManager, "Animations/bvh-test-01.bvh");

        // Animation laden & starten
        AnimationControl ani = new AnimationControl(m);
        ani.createAnimation(bvh);
        ani.startAnimation(1);
    }

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
}
