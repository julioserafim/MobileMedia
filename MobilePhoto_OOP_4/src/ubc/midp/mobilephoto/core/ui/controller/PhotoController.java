/**
 * 
 */
package ubc.midp.mobilephoto.core.ui.controller;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import ubc.midp.mobilephoto.core.ui.datamodel.ImageData;
import ubc.midp.mobilephoto.core.ui.screens.NewLabelScreen;
import ubc.midp.mobilephoto.core.util.Constants;

/**
 * @author E Figueiredo
 * Add in the Scenario 02
 */
public class PhotoController implements CommandListener {

	private BaseController nextController;
	private ImageData image;
	private NewLabelScreen screen;

	public PhotoController (ImageData image, BaseController nextController) {
		this.image = image;
		this.nextController = nextController;
	}

	public boolean handleCommand(Command c, Displayable d) {
		String label = c.getLabel();
//      	System.out.println( "<* PhotoController.handleCommand() *> " + label);

		/** Case: Save new Photo Label */
		if (label.equals("Save")) {
	      	System.out.println( "<* PhotoController.handleCommand() *> Save Photo Label = "+this.screen.getLabelName());
			this.getImage().setImageLabel(this.screen.getLabelName());
			this.nextController.updateImage(image);
			goToPreviousScreen();
			return true;
		} else if (label.equals("Cancel")) {
			goToPreviousScreen();
			return true;
		}
		return true;
	}

	public void commandAction(Command c, Displayable d) {
		handleCommand(c, d);
	}

   /**
     * Go to the previous screen
	 */
    private void goToPreviousScreen() {
	    System.out.println("<* PhotoController.goToPreviousScreen() *>");
	    this.nextController.showImageList(null, false, false);
	    this.nextController.setCurrentScreenName(Constants.IMAGELIST_SCREEN);
    } 

	/**
	 * @param image the image to set
	 */
	public void setImage(ImageData image) {
		this.image = image;
	}

	/**
	 * @return the image
	 */
	public ImageData getImage() {
		return image;
	}

	public void setScreen(NewLabelScreen screen) {
		this.screen = screen;
	}

	public NewLabelScreen getScreen() {
		return screen;
	}

}
