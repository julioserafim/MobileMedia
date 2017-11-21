/*
 * Created on Sep 28, 2004
 *
 */
package ubc.midp.mobilephoto.core.ui.controller;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import ubc.midp.mobilephoto.core.ui.MainUIMidlet;
import ubc.midp.mobilephoto.core.ui.datamodel.AlbumData;
import ubc.midp.mobilephoto.core.ui.screens.AddPhotoToAlbum;
import ubc.midp.mobilephoto.core.ui.screens.AlbumListScreen;
import ubc.midp.mobilephoto.core.ui.screens.NewAlbumScreen;
import ubc.midp.mobilephoto.core.ui.screens.PhotoListScreen;
import ubc.midp.mobilephoto.core.ui.screens.PhotoViewScreen;
import ubc.midp.mobilephoto.core.util.Constants;

/**
 * @author tyoung
 *
 * This is the base controller class used in the MVC architecture.
 * It controls the flow of screens for the MobilePhoto application.
 * Commands handled by this class should only be for the core application
 * that runs on any MIDP platform. Each device or class of devices that supports
 * optional features will extend this class to handle feature specific commands.
 * 
 */
public class BaseController implements CommandListener, ControllerInterface {
    
	private MainUIMidlet midlet;
	private Display display;
	private AlbumData model;
	
	//Define a successor to implement the Chain of Responsibility design pattern
	private BaseController nextController;
	
	//Define the basic screens
	private AlbumListScreen albumListScreen;

	//Keep track of the navigation so we can go 'back' easily
	private String currentScreenName;
	

	//Keep track of the current photo album being viewed
	//Ie. name of the currently active J2ME record store
	private String currentStoreName = "My Photo Album";
	
	/**
	 * Pass a handle to the main Midlet for this controller
	 * @param midlet
	 */
	public BaseController(MainUIMidlet midlet) {
		super();
		this.midlet = midlet;
	}
	
	/**
	 * Pass a handle to the main Midlet for this controller
	 * @param midlet
	 */
	public BaseController(MainUIMidlet midlet, AlbumData model) {
		super();
		this.midlet = midlet;
		this.model = model;
	}

	/**
	 * Initialize the controller
	 */
	public void init(AlbumData model) {
	    
		this.display = Display.getDisplay(midlet);
		
		//Don't think I need this here anymore
		//this.model = model;
		
		//initialize all the screens
		albumListScreen = new AlbumListScreen();
		
		//Get all MobilePhoto defined albums from the record store
		String[] albumNames = model.getAlbumNames();
		for (int i = 0; i < albumNames.length; i++) {
			if (albumNames[i] != null) {
				//Add album name to menu list
				albumListScreen.append(albumNames[i], null);
				
			}
		}
		
		albumListScreen.initMenu();
		albumListScreen.setCommandListener(this);
		
		//Set the current screen to the photo album listing
		setCurrentScreen(albumListScreen);
		currentScreenName = Constants.ALBUMLIST_SCREEN;
		
	}

    public boolean handleCommand(Command c, Displayable d) {

		//Can this controller handle the desired action?
		//If yes, handleCommand will return true, and we're done
		//If no, handleCommand will return false, and postCommand
		//will pass the request to the next controller in the chain if one exists.
		
		String label = c.getLabel();
      	System.out.println( this.getClass().getName() + "::handleCommand: " + label);
      	

		/** Case: Exit Application **/
		if (label.equals("Exit")) {
			midlet.destroyApp(true);
			return true;
		/** Case: Reset PhotoAlbum data **/
		} else if (label.equals("Reset")) {
		    resetImageData();
			currentScreenName = Constants.ALBUMLIST_SCREEN;
			return true;
		}else if (label.equals("New Photo Album")) {
			System.out.println("Create new Photo Album here");			
			currentScreenName = Constants.NEWALBUM_SCREEN;
			NewAlbumScreen canv = new NewAlbumScreen("Add new Photo Album");
			canv.setCommandListener(this);
			setCurrentScreen(canv);
			canv = null;
			return true;
			/** Case: Delete Album Photo**/
		}else if (label.equals("Delete Album")) {
			System.out.println("Delete Photo Album here");
			List down = (List) display.getCurrent();
			currentScreenName = Constants.CONFIRMDELETEALBUM_SCREEN;
			currentStoreName = down.getString(down.getSelectedIndex());
			Alert deleteConfAlert = new Alert("Delete Photo Album", "Would you like to remove the album "+currentStoreName,null,AlertType.CONFIRMATION);
			deleteConfAlert.setTimeout(Alert.FOREVER);
			deleteConfAlert.addCommand(new Command("Yes - Delete", Command.OK, 2));
			deleteConfAlert.addCommand(new Command("No - Delete", Command.CANCEL, 2));
			setCurrentScreen(deleteConfAlert, albumListScreen);
			deleteConfAlert.setCommandListener(this);
			return true;	
		/** Case: Yes delete Photo Album  **/
		}else if (label.equals("Yes - Delete")) {
			model.deletePhotoAlbum(currentStoreName);
			goToPreviousScreen();
			return true;	
		/** Case: Save new Photo Album  **/	
			/** Case: No delete Photo Album  **/
		}else if (label.equals("No - Delete")) {
			goToPreviousScreen();
			return true;	
		/** Case: Save new Photo Album  **/
		} else if (label.equals("Save")) {
			model.createNewPhotoAlbum(((NewAlbumScreen)getCurrentScreenName()).getAlbumName());
			goToPreviousScreen();
			return true;
			/** Case: Select PhotoAlbum to view **/
		} else if (label.equals("Select")) {
			//Get the name of the PhotoAlbum selected, and load image list from RecordStore
			List down = (List) display.getCurrent();
			currentStoreName = down.getString(down.getSelectedIndex());
			showImageList(currentStoreName);
			currentScreenName = Constants.IMAGELIST_SCREEN;
			return true;
			/** Case: View Image **/
		} else if (label.equals("View")) {
			showImage();
			currentScreenName = Constants.IMAGE_SCREEN;
			return true;
		
		/** Case: Add photo  **/
		} else if (label.equals("Add")) {
			currentScreenName = Constants.ADDPHOTOTOALBUM_SCREEN;
			AddPhotoToAlbum form = new AddPhotoToAlbum("Add new Photo to Album");
			form.setCommandListener(this);
			setCurrentScreen(form);
			return true;
			/** Case: Save Add photo  **/
		} else if (label.equals("Save Add Photo")) {
			model.addNewPhotoToAlbum(((AddPhotoToAlbum)getCurrentScreenName()).getPhotoName(), ((AddPhotoToAlbum)getCurrentScreenName()).getPath(), currentStoreName);
			goToPreviousScreen();
			return true;

		/** Case: Delete selected Photo from recordstore **/
		} else if (label.equals("Delete")) {
	      	String selectedImageName = getSelectedImageName();
	      	model.deleteImage(currentStoreName, selectedImageName);
	      	showImageList(currentStoreName);
		    currentScreenName = Constants.IMAGELIST_SCREEN;
			return true;
	      	
		/** Case: Go to the Previous or Fallback screen **/
		} else if (label.equals("Back")) {
		    
		    goToPreviousScreen();
			return true;
			
		/** Case: Cancel the current screen and go back one**/
		} else if (label.equals("Cancel")) {
			
			goToPreviousScreen();
			return true;
			
		}
		
		//If we couldn't handle the current command, return false
        return false;
    }
    
    public void postCommand(Command c, Displayable d) {

        System.out.println("BaseController::postCommand - Current controller is: " + this.getClass().getName());
        
        //If the current controller cannot handle the command, pass it to the next
        //controller in the chain.
        if (handleCommand(c,d) == false) {
            BaseController next = getNextController();
            if (next != null) {
                System.out.println("Passing to next controller in chain: " + next.getClass().getName());
                next.postCommand(c,d);
            } else {
                System.out.println("BaseController::postCommand - Reached top of chain. No more handlers for command: " + c.getLabel());
            }
        }

    }

	/**
	 * Handle events. For now, this just passes control off to a 'wrapper'
	 * so we can ensure , in order to use it in the aspect advice
	 */
	public void commandAction(Command c, Displayable d) {

	    postCommand(c,d);
		
	}

    /**
	 * This option is mainly for testing purposes. If the record store
	 * on the device or emulator gets into an unstable state, or has too 
	 * many images, you can reset it, which clears the record stores and
	 * re-creates them with the default images bundled with the application 
	 */
	private void resetImageData() {
        model.resetImageData();
        
        //Clear the names from the album list
        for (int i = 0; i < albumListScreen.size(); i++) {
        	albumListScreen.delete(i);
        }
        
        //Get the default ones from the album
        String[] albumNames = model.getAlbumNames();
        for (int i = 0; i < albumNames.length; i++) {
        	if (albumNames[i] != null) {
        		//Add album name to menu list
        		albumListScreen.append(albumNames[i], null);
        		
        	}
        }
        
        setCurrentScreen(albumListScreen);
    }

    /**
	 * Set the current screen for display
	 */
    public void setCurrentScreen(Displayable d) {
        Display.getDisplay(midlet).setCurrent(d);
    } 

    /**
	 * Set the current screen for display, after alert
	 */
    public void setCurrentScreen(Alert a, Displayable d) {
//        display.setCurrent(a, d);
        Display.getDisplay(midlet).setCurrent(a, d);
    } 

    /**
	 * Get the current screen name that is displayed
	 */
    public Displayable getCurrentScreenName() {
//        return display.getCurrent();
        return Display.getDisplay(midlet).getCurrent();
    } 
    
    /**
     * Go to the previous screen
     * TODO: Re-implement. Not working properly yet.
	 */
    private void goToPreviousScreen() {
        
		if (currentScreenName.equals(Constants.ALBUMLIST_SCREEN)) {
		    System.out.println("Can't go back here...Should never reach this spot");
		} else if (currentScreenName.equals(Constants.IMAGE_SCREEN)) {		    
		    //Go to the image list here, not the main screen...
		    showImageList(currentStoreName);
		    currentScreenName = Constants.IMAGELIST_SCREEN;
		} else if (currentScreenName.equals(Constants.IMAGELIST_SCREEN)) {
		    setCurrentScreen(albumListScreen);
		    currentScreenName = Constants.ALBUMLIST_SCREEN;
		}else if (currentScreenName.equals(Constants.NEWALBUM_SCREEN)) {
			albumListScreen.repaintListAlbum(model.getAlbumNames());
		    setCurrentScreen(albumListScreen);
		    currentScreenName = Constants.ALBUMLIST_SCREEN;

		}else if (currentScreenName.equals(Constants.CONFIRMDELETEALBUM_SCREEN)) {
			albumListScreen.repaintListAlbum(model.getAlbumNames());
			setCurrentScreen(albumListScreen);
		    currentScreenName = Constants.ALBUMLIST_SCREEN;
		}else if (currentScreenName.equals(Constants.ADDPHOTOTOALBUM_SCREEN)) {
			showImageList(currentStoreName);
		    currentScreenName = Constants.IMAGELIST_SCREEN;
		}
		
	
    } 

    /**
     * Show the current image that is selected
	 */
	public void showImage() {
		List selected = (List) display.getCurrent();
		String name = selected.getString(selected.getSelectedIndex());
		
		//First check if it is in the hashtable alrady
		Image storedImage = model.getImageFromRecordStore(currentStoreName, name);

		//If it's not in the table, load it from the recordstore
		if (storedImage == null) {
			storedImage = model.getImageFromRecordStore(currentStoreName, name);
		}
				
		//We can pass in the image directly here, or just the name/model pair and have it loaded
		PhotoViewScreen canv = new PhotoViewScreen(storedImage);
		
		canv.setCommandListener(this);
		setCurrentScreen(canv);
	}

    /**
     * Show the list of images in the record store
	 * TODO: Refactor - Move this to ImageList class
	 */
	public void showImageList(String recordName) {

		if (recordName == null)
			recordName = currentStoreName;
		
		PhotoListScreen imageList = new PhotoListScreen();
		
		//Command selectCommand = new Command("Open", Command.ITEM, 1);
		imageList.initMenu();
		imageList.setCommandListener(this);
		
		String[] labels = model.getImageNames(recordName);
		
		//loop through array and add labels to list
		for (int i = 0; i < labels.length; i++) {
			if (labels[i] != null) {
				//Add album name to menu list
				imageList.append(labels[i], null);
				
			}
		}
		setCurrentScreen(imageList);
		//currentMenu = "list";
	}

    /**
     * Get the last selected image from the Photo List screen.
	 * TODO: This really only gets the selected List item. 
	 * So it is only an image name if you are on the PhotoList screen...
	 * Need to fix this
	 */
	public String getSelectedImageName() {
		
	    if (display == null) {
	        System.out.println("BaseController::getSelectedImageName: Current display is NULL! Trying to get from Midlet");
	        display = Display.getDisplay(midlet);
	    }
	    
		List selected = (List) display.getCurrent();

		if (selected == null)
		    System.out.println("Current List from display is NULL!");
		
		String name = selected.getString(selected.getSelectedIndex());
		
		return name;
		
	}
	
	/**
	 * @return Returns the model.
	 */
	public AlbumData getModel() {
		return model;
	}
	
	/**
	 * @return Returns the currentStoreName.
	 */
	public String getCurrentStoreName() {
		return currentStoreName;
	}
	
	/**
	 * @param currentScreenName The currentScreenName to set.
	 */
	public void setCurrentScreenName(String currentScreenName) {
		this.currentScreenName = currentScreenName;
	}
	
    /**
     * @return
     */
    public BaseController getNextController() {
        return nextController;
    }
    
    /**
     * @param nextController
     */
    public void setNextController(BaseController nextController) {
        this.nextController = nextController;
    }
}
