/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package danceDoll;

/**
 *
 * @author master
 */
import com.jme3.animation.AnimControl;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;

public class Model {

    protected AssetManager assetManager;
    protected Node root;
    protected Node doll;
    private AnimControl control;
    private SkeletonDebugger skeletonDebug;

    public Model(AssetManager assetManager) {
        this.assetManager = assetManager;
        root = new Node("Root Node");
    }

    public Node getModel() {
        doll = new Node("doll");

        doll = (Node) assetManager.loadModel("Models/Stu/stu.j3o");
        skeletonDebug = new SkeletonDebugger("skeleton", getSkeletton());
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat);

        doll.attachChild(skeletonDebug);
        doll.setLocalScale(0.03f);
        root.attachChild(doll);
        control = doll.getControl(AnimControl.class);

        root.setLocalRotation(new Quaternion(new float[]{0, 180 * FastMath.DEG_TO_RAD, 0}));
        root.setLocalTranslation(0f, -8f, 0f);
        return root;
    }

    public Node getModelNode() {
        return root;
    }
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public Skeleton getSkeletton() {
        AnimControl ctl = (AnimControl) doll.getControl(0);
        return ctl.getSkeleton();
    }

    public SkeletonDebugger skeletonDebug() {
        return skeletonDebug;
    } 
}
