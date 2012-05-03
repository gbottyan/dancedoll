package danceDoll;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.MouseInput;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import com.jme3.scene.Node;

import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class DanceDoll extends SimpleApplication {

    //protected Node doll;
    protected MouseInput mouse;
    protected double rotationAngle = 0;
    protected double rotationSpeed = 2.0;
    protected Initialize init;
    private static final Logger logger = Logger.getLogger(DanceDoll.class.getName());

    public static void main(String[] args) {
        // Nur Warnungen ausgeben
        logger.getLogger("").setLevel(Level.WARNING);
        DanceDoll app = new DanceDoll();
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void start(JmeContext.Type contextType) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1000, 600);
        settings.setFrameRate(40);
        settings.setTitle("Dance Doll Alpha V. 0.2");
        settings.setSamples(0); // Kantenglättung
        settings.setVSync(true);
        setSettings(settings);
        super.start(contextType);
        this.setPauseOnLostFocus(false);
    }

    @Override
    public void simpleInitApp() {
        try {
            init = new Initialize(this, this.settings);
        } catch (IOException ex) {
            Logger.getLogger(DanceDoll.class.getName()).log(Level.SEVERE, null, ex);
        }
        flyCam.setMoveSpeed(18);
        flyCam.setEnabled(false);
    }

    public Node getRoot() {
        return rootNode;
    }

    @Override
    public void simpleUpdate(float tpf) {
        rotationAngle += rotationSpeed;
        rootNode.setLocalRotation(new Quaternion(new float[]{0, (float) (rotationAngle * FastMath.DEG_TO_RAD), 0}));

        stateManager.update(tpf);
        stateManager.render(renderManager);
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
