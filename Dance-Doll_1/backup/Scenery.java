/* ---------------------------------------------------------
 *          Projekt:  Dancing Doll
 *          Package:  danceDoll
 *         Filename:  Scenery.java
 * ---------------------------------------------------------
 *             Name:  Vincent Bosche
 *             Date:  Mar 22, 2012
 * ---------------------------------------------------------
 **/

package danceDoll;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.JmeSystem;

/**
 *
 * @author Vincent Bosche
 */
public class Scenery {
    private Node root;
    private AssetManager assetManager;
    
    public Scenery() {
        root = new Node("root");
        root.attachChild(drawScenery());
    }    
            
    private Node drawScenery() {
        // Asset Manager erstellen
        assetManager = JmeSystem.newAssetManager(
                    Thread.currentThread().getContextClassLoader()
                    .getResource("com/jme3/asset/Desktop.cfg"));
        
        Node mainNode = new Node("mainNode");   
                
        // Scheibe erstellen        
        Cylinder dFloor = new Cylinder(10,55,5.5f,0.1f, true, false);
        Geometry dFloorGeom = new Geometry("Dancefloor", dFloor);
        Material dFloorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");      
        dFloorMat.setColor("m_Color", ColorRGBA.Gray);
        dFloorGeom.setMaterial(dFloorMat);
        dFloorGeom.setLocalRotation(new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0}));
        dFloorGeom.setLocalTranslation(0f, -8f, 0f);
        dFloorGeom.setShadowMode(ShadowMode.Receive);
        mainNode.attachChild(dFloorGeom);

         // Knoten für die Puppe erstellen
        Node danceDoll = new Node("danceDoll");
        Node doll = new Node("doll");               

        doll = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        //doll = (Node) assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        
        doll.setLocalScale(0.03f);
        danceDoll.attachChild(doll); 
        danceDoll.setLocalRotation(new Quaternion(new float[] {0,180*FastMath.DEG_TO_RAD,0}));
        danceDoll.setLocalTranslation(0f, -8f, 0f);

        mainNode.attachChild(danceDoll);   
        danceDoll.setShadowMode(ShadowMode.CastAndReceive);   
        
        // Punktlich hinzufügen
	PointLight bulb = new PointLight();
	bulb.setColor(ColorRGBA.White);
	bulb.setRadius(20f);
	bulb.setPosition(new Vector3f(0f, 2f, 10f));
        mainNode.addLight(bulb);

        return mainNode;
    }

    public Node getNode() {
        return root;
    }
}
