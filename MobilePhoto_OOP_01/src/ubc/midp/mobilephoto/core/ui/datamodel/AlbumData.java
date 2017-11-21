/*
 * Created on Sep 28, 2004
 */
package ubc.midp.mobilephoto.core.ui.datamodel;

import java.util.Hashtable;

import javax.microedition.lcdui.Image;

/**
 * @author tyoung
 * 
 * This class represents the data model for Photo Albums. A Photo Album object
 * is essentially a list of photos or images, stored in a Hashtable. Due to
 * constraints of the J2ME RecordStore implementation, the class stores a table
 * of the images, indexed by an identifier, and a second table of image metadata
 * (ie. labels, album name etc.)
 * 
 * This uses the ImageAccessor class to retrieve the image data from the
 * recordstore (and eventually file system etc.)
 */
public class AlbumData {

	private ImageAccessor imageAccessor;

	//imageInfo holds image metadata like label, album name and 'foreign key' index to
	// corresponding RMS entry that stores the actual Image object
	protected Hashtable imageInfoTable = new Hashtable();

	public boolean existingRecords = false; //If no records exist, try to reset

	/**
	 *  Constructor. Creates a new instance of ImageAccessor
	 */
	public AlbumData() {
		imageAccessor = new ImageAccessor(this);
	}

	/**
	 *  Load any photo albums that are currently defined in the record store
	 */
	public String[] getAlbumNames() {

		//Shouldn't load all the albums each time
		//Add a check somewhere in ImageAccessor to see if they've been
		//loaded into memory already, and avoid the extra work...
		imageAccessor.loadAlbums();
		return imageAccessor.getAlbumNames();
	}

	/**
	 *  Get the names of all images for a given Photo Album that exist in the Record Store.
	 */
	public String[] getImageNames(String recordName) {

		String[] result = imageAccessor.loadImageDataFromRMS(recordName);

		return result;

	}

	/**
	 *  Define a new user photo album. This results in the creation of a new
	 *  RMS Record store.
	 */
	public void createNewPhotoAlbum(String albumName) {
		imageAccessor.createNewPhotoAlbum(albumName);
	}
	
	public void deletePhotoAlbum(String albumName){
		imageAccessor.deletePhotoAlbum(albumName);
	}

	/**
	 *  Get a particular image (by name) from a photo album. The album name corresponds
	 *  to a record store.
	 */
	public Image getImageFromRecordStore(String recordStore, String imageName) {

		ImageData imageInfo = imageAccessor.getImageInfo(imageName);

		//Find the record ID and store name of the image to retrieve
		int imageId = imageInfo.getForeignRecordId();
		String album = imageInfo.getParentAlbumName();

		//Now, load the image (on demand) from RMS and cache it in the hashtable

		Image imageRec = imageAccessor.loadSingleImageFromRMS(album, imageName, imageId); //rs.getRecord(recordId);
		return imageRec;

	}
	public void addNewPhotoToAlbum(String label, String path, String album){
		imageAccessor.addImageData(label, path, album);
	}
	
	/**
	 *  Delete a photo from the photo album. This permanently deletes the image from the record store
	 */
	public boolean deleteImage(String imageName, String storeName) {
		return imageAccessor.deleteSingleImageFromRMS(imageName, storeName); 
	}
	
	/**
	 *  Reset the image data for the application. This is a wrapper to the ImageAccessor.resetImageRecordStore
	 *  method. It is mainly used for testing purposes, to reset device data to the default album and photos.
	 */
	public void resetImageData() {
		imageAccessor.resetImageRecordStore();
	}

	/**
	 * Get the hashtable that stores the image metadata in memory.
	 * @return Returns the imageInfoTable.
	 */
	public Hashtable getImageInfoTable() {
		return imageInfoTable;
	}

	/**
	 * Update the hashtable that stores the image metadata in memory
	 * @param imageInfoTable
	 *            The imageInfoTable to set.
	 */
	public void setImageInfoTable(Hashtable imageInfoTable) {
		this.imageInfoTable = imageInfoTable;
	}
}