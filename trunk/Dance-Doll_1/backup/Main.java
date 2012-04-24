package danceDoll;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import com.jme3.scene.Node;
import com.jme3.shadow.PssmShadowRenderer;

import com.jme3.renderer.RenderManager;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    protected Node doll;
    protected MouseInput mouse;
    protected Node root = new Node("root");
    protected double rotationAngle = 0;
    protected double rotationSpeed = 0.5;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(18);
        flyCam.setEnabled(false);  
        cam.lookAt(new Vector3f(0, -6, 0), Vector3f.UNIT_Y);

        inputManager.setCursorVisible(true);
       
        // Szene laden
        Scenery sc = new Scenery();
        Node scenery = sc.getNode();
        root.attachChild(scenery);
                
        rootNode.attachChild(root);
        
        //Set Renderer to show Shadows
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 1024,1);
        pssmRenderer.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        //pssmRenderer.setLambda(0.3f);
        pssmRenderer.setShadowIntensity(0.6f);
        pssmRenderer.setFilterMode(PssmShadowRenderer.FilterMode.PCF8);   
        pssmRenderer.setEdgesThickness(5);
        viewPort.addProcessor(pssmRenderer);  
        
        //mouse.setInputListener(inputManager);
    }

    @Override
    public void simpleUpdate(float tpf) {
        rotationAngle += rotationSpeed;
        root.setLocalRotation(new Quaternion(new float[] {0,(float)(rotationAngle*FastMath.DEG_TO_RAD),0}));
    }

    @Override
    public void simpleRender(RenderManager rm) {

    }
}
