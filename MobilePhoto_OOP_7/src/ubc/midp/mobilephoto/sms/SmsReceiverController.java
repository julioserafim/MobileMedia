// #if includeSmsFeature
package ubc.midp.mobilephoto.sms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

import ubc.midp.mobilephoto.core.ui.MainUIMidlet;
import ubc.midp.mobilephoto.core.ui.controller.AbstractController;
import ubc.midp.mobilephoto.core.ui.controller.PhotoViewController;
import ubc.midp.mobilephoto.core.ui.controller.ScreenSingleton;
import ubc.midp.mobilephoto.core.ui.datamodel.AlbumData;
import ubc.midp.mobilephoto.core.ui.screens.AlbumListScreen;
import ubc.midp.mobilephoto.core.ui.screens.PhotoViewScreen;
import ubc.midp.mobilephoto.core.util.Constants;

public class SmsReceiverController extends AbstractController {
	byte[] incomingImageData;
	
	public SmsReceiverController(MainUIMidlet midlet, AlbumData albumData, AlbumListScreen albumListScreen) {
		super(midlet, albumData, albumListScreen);
	}

 	/**
	 * Handle SMS specific events.
	 * If we are given a standard command that is handled by the BaseController, pass 
	 * the handling off to our super class with the else clause
	 */

	public boolean handleCommand(Command c) {

		String label = c.getLabel();
      	System.out.println("SmsReceiverController::handleCommand: " + label);
		
		   /** Case: ... **/
      	if (label.equals("Accept Photo")) {
	
      	
	      	Image image = Image.createImage(incomingImageData, 0, incomingImageData.length);
	       	Image copy = Image.createImage(image.getWidth(), image.getHeight()); 
	     	PhotoViewScreen canv = new PhotoViewScreen(copy);
			canv.setFromSMS(true);
			// [NC] Changed in the scenario 07: just the first line below to support generic AbstractController
			canv.setCommandListener(new PhotoViewController(this.midlet, getAlbumData(), (AlbumListScreen) getAlbumListScreen(), "NoName"));
			this.setCurrentScreen(canv);
	   		return true;

	      } else if (label.equals("Reject Photo")) {
	      	
	      	//TODO: Go back to whatever screen they were previously on?
	      	System.out.println("Reject Photo command");
	     // [NC] Changed in the scenario 07: just the first line below to support generic AbstractController
	      	((AlbumListScreen) getAlbumListScreen()).repaintListAlbum(getAlbumData().getAlbumNames());
			setCurrentScreen( getAlbumListScreen() );
			ScreenSingleton.getInstance().setCurrentScreenName(Constants.ALBUMLIST_SCREEN);
			return true;
      					
	      /* For All commands not handled here, send them to the super class */
	      } else if (label.equals("Ok"))
	      {
	    	// [NC] Changed in the scenario 07: just the first line below to support generic AbstractController
	    	   	((AlbumListScreen) getAlbumListScreen()).repaintListAlbum(getAlbumData().getAlbumNames());
				setCurrentScreen( getAlbumListScreen() );
				ScreenSingleton.getInstance().setCurrentScreenName(Constants.ALBUMLIST_SCREEN);
				return true;
	      }
	      
  		return false;
	}
	public void setIncommingData(byte[] incomingImageData){
		this.incomingImageData = incomingImageData;
	}
}
//#endif