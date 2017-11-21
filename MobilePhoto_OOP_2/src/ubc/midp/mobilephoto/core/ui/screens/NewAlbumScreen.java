package ubc.midp.mobilephoto.core.ui.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

/**
 * @author tyoung
 * This form is displayed when the user defines a new photo album (from the AlbumListScreen)
 * TODO: This feature is not fully implemented. The commands are in place to create
 * a new album, but currently it just creates a hard-coded album name for testing.
 *
 */
public class NewAlbumScreen extends Form {
	
	TextField albumName = new TextField("Album Name", "", 15, TextField.ANY);
	
	Command ok;
	Command cancel;
	
	/**
	 * @param arg0
	 */
	public NewAlbumScreen(String name) {

		super(name);
		this.append(albumName);
		ok = new Command("Save", Command.SCREEN, 0);
		cancel = new Command("Cancel", Command.EXIT, 1);
		this.addCommand(ok);
		this.addCommand(cancel);
		
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public NewAlbumScreen(String title, Item[] items) {

		super(title, items);
		// TODO Auto-generated constructor stub
	}	
	
	/**
	 * @return Returns the new Album Name.
	 */
	public String getAlbumName() {
		return albumName.getString();
	}
}
