/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package danceDoll;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.SkeletonDebugger;
import java.util.HashMap;

/**
 *
 * @author master
 */
public class AnimationControl {

    Model m;
    private AnimControl control;
    private AnimChannel channel;
    private SkeletonDebugger skeletonDebug;
    private String animationName;

    /**
     * Creats an Animation by using the SkelettonDebog & AnimControl from the Model
     * @param m Model with Skeletton and AnimControl
     */
    AnimationControl(Model m) {
        this.m = m;
        skeletonDebug = m.skeletonDebug();
        control = m.doll.getControl(AnimControl.class);
    }

    /**
     * Creats from the BVH-List a HashMap and put it onto the AnimControl from the Model
     * @param bvh BVH-List
     */
    public void createAnimation(BVHController bvh) {
        Mesh[] meshes = new Mesh[2];
        meshes[0] = skeletonDebug.getWires();
        bvh.createBindPose(skeletonDebug.getWires());
        meshes[1] = skeletonDebug.getPoints();
        bvh.createBindPose(skeletonDebug.getPoints());

        animationName = bvh.data.getAnimation().getName();

        // Hashmap mit BVH-Daten erstellen
        HashMap<String, Animation> anims = new HashMap<String, Animation>();
        anims.put(animationName, bvh.data.getAnimation());

        // AnimControl laden und HashMap übergeben
        AnimControl ctrl = m.doll.getControl(AnimControl.class);
        ctrl.setAnimations(anims);

        channel = ctrl.createChannel();
    }

    /**
     * Method starts Animation with given Speed
     * @param speed 
     */
    public void startAnimation(float speed) {
        channel.setAnim(animationName);
        channel.setSpeed(speed);
    }
}
