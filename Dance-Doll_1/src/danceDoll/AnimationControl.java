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

    /**
     * Creats an Animation by using the SkelettonDebog & AnimControl from the Model
     * @param m Model with Skeletton and AnimControl
     */
    AnimationControl(Model m) {
        this.m = m;
        skeletonDebug = m.skeletonDebug();
        control = m.doll.getControl(AnimControl.class);
        channel = control.createChannel();
    }

    /**
     * Creats from the BVH-List a HashMap and put it onto the AnimControl from the Model
     * @param bvh BVH-List
     */
    public void pushAnimation(BVHController bvh) {
        if(bvh.data != null) {
            // Anzahl der Knochen überprüfen
            if(this.m.getSkeletton().getBoneCount() >= bvh.data.getSkeleton().getBoneCount()) {
                Mesh[] meshes = new Mesh[2];
                meshes[0] = skeletonDebug.getWires();
                bvh.createBindPose(skeletonDebug.getWires());
                meshes[1] = skeletonDebug.getPoints();
                bvh.createBindPose(skeletonDebug.getPoints());

                String animationName = bvh.data.getAnimation().getName();

                // Hashmap mit BVH-Daten erstellen
                HashMap<String, Animation> anims = new HashMap<String, Animation>();          
                anims.put(animationName, bvh.data.getAnimation());

                // HashMap übergeben
                control.addAnim(bvh.data.getAnimation());
            } else {
                   System.out.println("Das BVH-Skelett \""+bvh.data.getAnimation().getName().toString()+"\" hat mehr Knochen als das Model-Skelett und kann von daher nicht benutzt werden!");
            }
        }
    }

    /**
     * Method starts Animation with given Speed
     * @param speed 
     */
    public void startAnimation(String name) {
        if(control.getAnim(name) != null) {
            channel.setAnim(name);
        } else {
            System.out.println("Die gewählte Animation \""+name+"\" wurde nicht gefunden!");
        }
    }
    
    /**
     * Removes the Animation from the Animationchannel
     * @param name Name of the Animation to remove
     */
    public void popAnimation(String name) {
        control.removeAnim(control.getAnim(name));
    }
}
