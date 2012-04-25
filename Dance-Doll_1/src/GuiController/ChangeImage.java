/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GuiController;

import java.util.logging.Logger;
 
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.render.image.ImageModeFactory;
import de.lessvoid.nifty.render.image.ImageModeHelper;

/**
 *
 * @author http://jmonkeyengine.org/groups/gui/forum/topic/creating-generic-button-effects/
 */
/**
 * This can be applied to an image element. This will change the original image of the
 * element to the image given in the "active" attribute. When the effect gets deactivated
 * the image is being restored to the image given with the "inactive" attribute.
 * @author void
 */
public class ChangeImage implements EffectImpl {
 // private Logger log = Logger.getLogger(ChangeImage.class.getName());
  private Element element;
  private NiftyImage activeImage;
  private NiftyImage inactiveImage;
 
  public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
    this.element = element;
    this.activeImage = loadImage("active", nifty, parameter);
    this.inactiveImage = loadImage("inactive", nifty, parameter);
  }
 
  public void execute(final Element element, final float normalizedTime, final Falloff falloff, final NiftyRenderEngine r) {
    changeElementImage(activeImage);
  }
 
  public void deactivate() {
    changeElementImage(inactiveImage);
    activeImage.dispose();
    inactiveImage.dispose();
  }
 
private NiftyImage loadImage(final String name, final Nifty nifty, final EffectProperties parameter) {
    NiftyImage image = nifty.getRenderEngine().createImage(parameter.getProperty(name), false);
 
    String areaProviderProperty = new ImageModeHelper().getAreaProviderProperty(parameter);
    String renderStrategyProperty = new ImageModeHelper().getRenderStrategyProperty(parameter);
    if ((areaProviderProperty != null) || (renderStrategyProperty != null)) {
        image.setImageMode(ImageModeFactory.getSharedInstance().createImageMode(areaProviderProperty,
                renderStrategyProperty));
    }
 
    return image;
  }
 
  private void changeElementImage(final NiftyImage image) {
    ImageRenderer imageRenderer = element.getRenderer(ImageRenderer.class);
    if (imageRenderer == null) {
     // log.warning("this effect can only be applied to images!");
      return;
    }
    imageRenderer.setImage(image);
  }
}
