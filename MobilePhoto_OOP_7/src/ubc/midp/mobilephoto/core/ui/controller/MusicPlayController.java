// #ifdef includeMMAPI
// [NC] Added in the scenario 07
package ubc.midp.mobilephoto.core.ui.controller;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.List;
import javax.microedition.rms.RecordStoreFullException;

import lancs.midp.mobilephoto.lib.exceptions.ImageNotFoundException;
import lancs.midp.mobilephoto.lib.exceptions.ImagePathNotValidException;
import lancs.midp.mobilephoto.lib.exceptions.InvalidImageDataException;
import lancs.midp.mobilephoto.lib.exceptions.PersistenceMechanismException;
import ubc.midp.mobilephoto.core.ui.MainUIMidlet;
import ubc.midp.mobilephoto.core.ui.datamodel.AlbumData;
import ubc.midp.mobilephoto.core.ui.datamodel.MediaData;
import ubc.midp.mobilephoto.core.ui.screens.AddMediaToAlbum;
import ubc.midp.mobilephoto.core.ui.screens.AlbumListScreen;
import ubc.midp.mobilephoto.core.ui.screens.PlayMediaScreen;
import ubc.midp.mobilephoto.core.util.Constants;

public class MusicPlayController extends AbstractController{
	 // #ifdef includeCopyPhoto
	  // [NC] Added in the scenario 07
	private String mediaName;
	//#endif
	
	private PlayMediaScreen pmscreen;
	public MusicPlayController(MainUIMidlet midlet, AlbumData albumData,
			List albumListScreen, PlayMediaScreen pmscreen) {
		super(midlet, albumData, albumListScreen);
		this.pmscreen = pmscreen;
		// TODO Auto-generated constructor stub
	}

	public boolean handleCommand(Command command) {
		String label = command.getLabel();
		System.out.println( "<* MusicPlayController.handleCommand() *> " + label);

		/** Case: Copy photo to a different album */
		if (label.equals("Start")) {
			pmscreen.startPlay();
			return true;
		}else if (label.equals("Stop")) {
			pmscreen.pausePlay();
				return true;
		}else if ((label.equals("Back"))||(label.equals("Cancel"))){
			pmscreen.pausePlay();
			// [NC] Changed in the scenario 07: just the first line below to support generic AbstractController
			((AlbumListScreen) getAlbumListScreen()).repaintListAlbum(getAlbumData().getAlbumNames());
			setCurrentScreen( getAlbumListScreen() );
			ScreenSingleton.getInstance().setCurrentScreenName(Constants.ALBUMLIST_SCREEN);
			return true;
		}
		 // #ifdef includeCopyPhoto
	  	 // [NC] Added in the scenario 07
		/** Case: Copy photo to a different album */
		else if (label.equals("Copy")) {
			AddMediaToAlbum copyPhotoToAlbum = new AddMediaToAlbum("Copy Media to Album");
			copyPhotoToAlbum.setItemName(mediaName);
			copyPhotoToAlbum.setLabePath("Copy to Album:");
			copyPhotoToAlbum.setCommandListener(this);
	        Display.getDisplay(midlet).setCurrent(copyPhotoToAlbum);
			
			return true;
		} else if (label.equals("Save Item")) {
			try {
				MediaData imageData = null;	
				try {
						imageData = getAlbumData().getMediaInfo(mediaName);
				} catch (ImageNotFoundException e) {
						Alert alert = new Alert("Error", "The selected photo was not found in the mobile device", null, AlertType.ERROR);
						Display.getDisplay(midlet).setCurrent(alert, Display.getDisplay(midlet).getCurrent());
				}
				String albumname = ((AddMediaToAlbum) getCurrentScreen()).getPath();
				getAlbumData().addMediaData(imageData, albumname); 
			} catch (InvalidImageDataException e) {
				Alert alert = null;
				if (e instanceof ImagePathNotValidException)
					alert = new Alert("Error", "The path is not valid", null, AlertType.ERROR);
				else
					alert = new Alert("Error", "The music file format is not valid", null, AlertType.ERROR);
				Display.getDisplay(midlet).setCurrent(alert, Display.getDisplay(midlet).getCurrent());
				return true;
				// alert.setTimeout(5000);
			} catch (PersistenceMechanismException e) {
				Alert alert = null;
				if (e.getCause() instanceof RecordStoreFullException)
					alert = new Alert("Error", "The mobile database is full", null, AlertType.ERROR);
				else
					alert = new Alert("Error", "The mobile database can not add a new music", null, AlertType.ERROR);
				Display.getDisplay(midlet).setCurrent(alert, Display.getDisplay(midlet).getCurrent());
			}
			//((PhotoController)this.getNextController()).showImageList(ScreenSingleton.getInstance().getCurrentStoreName(), false, false);
		    //ScreenSingleton.getInstance().setCurrentScreenName(Constants.IMAGELIST_SCREEN);
			// [NC] Changed in the scenario 07: just the first line below to support generic AbstractController
			((AlbumListScreen) getAlbumListScreen()).repaintListAlbum(getAlbumData().getAlbumNames());
			setCurrentScreen( getAlbumListScreen() );
			ScreenSingleton.getInstance().setCurrentScreenName(Constants.ALBUMLIST_SCREEN);
			return true;
		}
		//#endif
		
		return false;
	}
	 // #ifdef includeCopyPhoto
	  // [NC] Added in the scenario 07
	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}
	//#endif
}
//#endif
