
package GuiController;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import danceDoll.AnimationControl;
import danceDoll.BVHController;
import danceDoll.DanceDoll;
import danceDoll.Initialize;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Christian Braun
 */

public class GuiController extends AbstractAppState implements ScreenController {
 
  private Nifty nifty;
  private Screen screen;
  private SimpleApplication app;
  private Vector3f posBuffer;
  private Camera cam;
  private float yTilt = -4.0f;
  private Vector3f home;
  private AnimationControl ani;
 
  
 
  /** custom methods */ 
 
  public GuiController(String data) { 
    /** Your custom constructor, can accept arguments */ 
  } 
  
  
  // Constructor with option to change camera-settings
  public GuiController(Nifty nifty, DanceDoll dd, Initialize init) { 
    this.app = dd;
    this.nifty = nifty;
    this.posBuffer = dd.getCamera().getLocation();
    this.cam = dd.getCamera();
    this.home = new Vector3f(.0f,-4.0f,-20.0f);
    this.ani = init.getAnimationControl();
    //home();
  }
  
  public GuiController(Nifty nifty) { 
    this.nifty = nifty;
    
  } 
 
  /** Nifty GUI ScreenControl methods */ 
 
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
  }
 
  public void onStartScreen() { }
 
  // Controlls on left side
  
  //play file 1
  public void playOne() { 
        try {
            BVHController bvh = new BVHController(app.getAssetManager(), "Animations/01_1_Kopf_kratzen.bvh");
            ani.pushAnimation(bvh);
            if(bvh.chkBVH())
                ani.startAnimation(bvh.getBVHName());
        } catch (IOException ex) {
            Logger.getLogger(GuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
  }  
  
  // Controlls on right side
  public void home(){
      cam.setLocation(home);
      cam.lookAt(home, new Vector3f(0.f,0.f,0.f));
      cam.setFrustumPerspective(30.f, 1.f, 10.f, 1000.f);
      posBuffer = app.getCamera().getLocation();
      nifty.fromXml("Interface/Gui.xml","start");
  }
  
  @NiftyEventSubscriber(id="zoom")
  public void zoom(final String id, final SliderChangedEvent event) { 
      cam.setFrustumPerspective(event.getValue()+20, 1.f, 10.f, 1000.f); 
  }
  
    @NiftyEventSubscriber(id="height")
  public void hight(final String id, final SliderChangedEvent event) {
      cam.setLocation (cam.getLocation().setY((event.getValue()-50.f)/10.f)); 
      cam.lookAt(new Vector3f (0,yTilt,0), new Vector3f (0.f,0.f,0.f));
      posBuffer.setY((event.getValue()-50)/10.f);
  }
 
  @NiftyEventSubscriber(id="tilt")
  public void tilt(final String id, final SliderChangedEvent event) {
      yTilt = (event.getValue()-50)/10.f;
      cam.lookAt(new Vector3f (0.f,yTilt,0.f), new Vector3f (0.f,0.f,0.f));
  }  
  
  @NiftyEventSubscriber(id="turn")
  public void turn(final String id, final SliderChangedEvent event) {
      float length = (float)Math.sqrt(Math.pow(cam.getLocation().getX(),2)+Math.pow(cam.getLocation().getZ(), 2));
  
      Vector3f posTurn = new Vector3f ((float)(Math.sin(event.getValue()*3.6*2*Math.PI/3600.*length)),   
                                        cam.getLocation().getY(),
                                       (float)(Math.cos(event.getValue()*3.6*2*Math.PI/3600.*length)));
      
      cam.setLocation(new Vector3f (posTurn.getX()*length,cam.getLocation().getY(),posTurn.getZ()*length));
      cam.lookAt(new Vector3f (0,yTilt,0), new Vector3f (0.f,0.f,0.f));
      posBuffer = app.getCamera().getLocation();
    
  }
  
  public void onEndScreen() { }
 
  /** jME3 AppState methods */ 
 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
  }
 
  @Override
  public void update(float tpf) { 
    /** jME update loop! */ 
  }
  
  public void quitGame() {
  app.stop(); 
  }
 
  public void startGame(String nextScreen) {
  nifty.gotoScreen("test");  // switch to another screen
  }

  //ersetzt ein Bild im laufenden GUI
  public void changePic(String infos){
    
    //trennt die infos im dem String
    String[] temp = infos.split(";");
    String oldPic = temp[0]; 
    String nextPic = temp [1];
    // find the element
    Element imageElement = nifty.getCurrentScreen().findElementByName(oldPic);
    // get the ImageRenderer
    ImageRenderer imageRenderer = imageElement.getRenderer(ImageRenderer.class);
    // change the image
    imageRenderer.setImage(nifty.getRenderEngine().createImage(nextPic,false));
  }
}