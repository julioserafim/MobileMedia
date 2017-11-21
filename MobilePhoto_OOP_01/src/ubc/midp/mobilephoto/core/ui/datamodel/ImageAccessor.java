/*
 * Created on Sep 13, 2004
 *
 */
package ubc.midp.mobilephoto.core.ui.datamodel;

import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import ubc.midp.mobilephoto.core.util.ImageUtil;

/**
 * @author trevor
 * 
 * This is the main data access class. It handles all the connectivity with the
 * RMS record stores to fetch and save data associated with MobilePhoto
 * TODO: Refactor into stable interface for future updates. We may want to 
 * access data from RMS, or eventually direct from the 'file system' on devices
 * that support the FileConnection optional API.
 *  
 */
public class ImageAccessor {

	//Note: Our midlet only ever has access to Record Stores it created
	//For now, use naming convention to create record stores used by MobilePhoto
	public static final String ALBUM_LABEL = "mpa-"; //"mpa- all album names are prefixed with this label
	public static final String INFO_LABEL = "mpi-"; //"mpi- all album info stores are prefixed with this label
	public static final String DEFAULT_ALBUM_NAME = "My Photo Album"; //default album name
	
	public static final String IMAGE_LABEL = "ImageList"; //RecordStore name prefixed

	protected String[] albumNames; //User defined names of photo albums

	protected AlbumData model;

	//Record Stores
	private RecordStore imageRS = null; 
	private RecordStore imageInfoRS = null;

	/*
	 * Constructor
	 */
	public ImageAccessor(AlbumData mod) {
		model = mod;
	}

	/**
	 * Load all existing photo albums that are defined in the record store.
	 */
	public void loadAlbums() {

		//Try to find any existing Albums (record stores)

		String[] currentStores = RecordStore.listRecordStores();

		if (currentStores != null) {
			System.out.println("ImageAccessor::loadAlbums: Found: " + currentStores.length + " existing record stores");
			model.existingRecords = true;
			String[] temp = new String[currentStores.length];
			int count = 0;
			
			//Only use record stores that follow the naming convention defined
			for (int i = 0; i < currentStores.length; i++) {
				String curr = currentStores[i];

				//If this record store is a photo album...
				if (curr.startsWith(ALBUM_LABEL)) {

					//Strip out the mpa- identifier
					curr = curr.substring(4);
					//Add the album name to the array
					temp[i] = curr;
					count++;
				}
			}

			//Re-copy the contents into a smaller array now that we know the size
			albumNames = new String[count];
			int count2 = 0;
			for (int i = 0; i < temp.length; i++) {
				if (temp[i] != null) {
					albumNames[count2] = temp[i];
					count2++;
				}
			}
		} else {
			System.out.println("ImageAccessor::loadAlbums: 0 record stores exist. Creating default one.");
			resetImageRecordStore();
			loadAlbums();
		}
	}

	
	/**
	 * Reset the album data for MobilePhoto. This will delete all existing photo data
	 * from the record store and re-create the default album and photos.
	 */
	public void resetImageRecordStore() {

		String storeName = null;
		String infoStoreName = null;

		//remove any existing album stores...
		if (albumNames != null) {
			for (int i = 0; i < albumNames.length; i++) {
				try {
					//Delete all existing stores containing Image objects as well as the associated ImageInfo objects
					//Add the prefixes labels to the info store

					storeName = ALBUM_LABEL + albumNames[i];
					infoStoreName = INFO_LABEL + albumNames[i];
					
				    RecordStore.deleteRecordStore(storeName);
					RecordStore.deleteRecordStore(infoStoreName);

				} catch (RecordStoreException e) {
					System.out.println("No record store named " + storeName + " to delete.");
					System.out.println("...or...No record store named " + infoStoreName + " to delete.");
					System.out.println("Ignoring Exception: " + e);
					// ignore any errors...
				}
			}
		} else {
		    //Do nothing for now
		    System.out.println("ImageAccessor::resetImageRecordStore: albumNames array was null. Nothing to delete.");
		}
		
		//Now, create a new default album for testing
			addImageData("Tucan Sam", "/images/Tucan.png", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add Penguin
			addImageData("Linux Penguin", "/images/Penguin.png", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add Duke
			addImageData("Duke (Sun)", "/images/Duke1.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);
			addImageData("UBC Logo", "/images/ubcLogo.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add Gail
			addImageData("Gail", "/images/Gail1.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add JG
			addImageData("J. Gosling", "/images/Gosling1.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add GK
			addImageData("Gregor", "/images/Gregor1.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);
			//Add KDV
			addImageData("Kris", "/images/Kdvolder1.PNG", ImageAccessor.DEFAULT_ALBUM_NAME);

	}
	
	public void addImageData(String photoname, String path, String albumname){
		try{		
	        imageRS = RecordStore.openRecordStore(ALBUM_LABEL + albumname, true);
	        imageInfoRS = RecordStore.openRecordStore(INFO_LABEL + albumname, true);
	        
			int rid; //new record ID for Image (bytes)
			int rid2; //new record ID for ImageData (metadata)

			ImageUtil converter = new ImageUtil();
			
			//NOTE: For some Siemen's phone, all images have to be less than 16K
			//May have to check for this, or try to convert to a lesser format
			// for display on Siemen's phones (Could put this in an Aspect)

			//Add Tucan
			byte[] data1 = converter.readImageAsByteArray(path);
			rid = imageRS.addRecord(data1, 0, data1.length);
			ImageData ii = new ImageData(rid, ImageAccessor.ALBUM_LABEL + albumname, photoname);
			rid2 = imageInfoRS.getNextRecordID();
			ii.setRecordId(rid2);
			data1 = converter.getBytesFromImageInfo(ii);
			imageInfoRS.addRecord(data1, 0, data1.length);
			
			imageRS.closeRecordStore();
			imageInfoRS.closeRecordStore();
			
		} catch (RecordStoreException e) {
			System.out.println("AddPhoto to album :");
			e.printStackTrace();
		} 

	}
	/**
	 * This will populate the imageInfo hashtable with the ImageInfo object,
	 *  referenced by label name and populate the imageTable hashtable with Image objects
	 *  referenced by the RMS record Id
	 */
	public String[] loadImageDataFromRMS(String recordName) {

		Vector labelVector = new Vector();

		try {

			String infoStoreName = ImageAccessor.INFO_LABEL + recordName;

			RecordStore infoStore = RecordStore.openRecordStore(infoStoreName, false);
			RecordEnumeration isEnum = infoStore.enumerateRecords(null, null,false);

			while (isEnum.hasNextElement()) {
				// Get next record
				int currentId = isEnum.nextRecordId();
				byte[] data = infoStore.getRecord(currentId);

				//Convert the data from a byte array into our ImageData (metadata) object
				ImageUtil converter = new ImageUtil();
				ImageData iiObject = converter.getImageInfoFromBytes(data);

				//Add the info to the metadata hashtable
				String label = iiObject.getImageLabel();
				labelVector.addElement(label);
				model.getImageInfoTable().put(label, iiObject);

			}
			
			infoStore.closeRecordStore();
			
		} catch (RecordStoreException rse) {
			System.out.println("Caught Exception: " + rse);
		}
		
		//Re-copy the contents into a smaller array
		String[] labelArray = new String[labelVector.size()];
		labelVector.copyInto(labelArray);
		return labelArray;
	}

	/**
	 * Update the Image metadata associated with this 
	 * named photo
	 */
	public boolean updateImageInfo(ImageData oldData, ImageData newData) {
	    
		boolean success = false;

		try {

			//Parse the Data store name to get the Info store name
			String infoStoreName = oldData.getParentAlbumName();
			infoStoreName = ImageAccessor.INFO_LABEL + infoStoreName.substring(ImageAccessor.ALBUM_LABEL.length());
			RecordStore infoStore = RecordStore.openRecordStore(infoStoreName, false);
			
			ImageUtil converter = new ImageUtil();
			byte[] imageDataBytes = converter.getBytesFromImageInfo(newData);

			infoStore.setRecord(oldData.getRecordId(), imageDataBytes, 0, imageDataBytes.length);

		} catch (RecordStoreException rse) {
			System.out.println("Caught Exception: " + rse);
		}

		//Update the Hashtable 'cache'
		setImageInfo(oldData.getImageLabel(), newData);
		
		return success;
	}
	
	/**
	 * Retrieve the metadata associated with a specified image (by name) 
	 */
	public ImageData getImageInfo(String imageName) {

	    if (model == null)
	        System.out.println("ImageAccessor - model was NULL!");
	    
		ImageData ii = (ImageData) model.getImageInfoTable().get(imageName);

		if (ii == null)
			System.out.println("ImageAccessor::getImageInfo(): ImageInfo was NULL for " + imageName + " in ImageAccessor Hashtable.");

		return ii;

	}

	/**
	 * Update the hashtable with new ImageInfo data
	 */
	public void setImageInfo(String imageName, ImageData newData) {

		model.getImageInfoTable().put(newData.getImageLabel(), newData);

	}

	/**
	 * Fetch a single image from the Record Store
	 * This should be used for loading images on-demand (only when they are
	 * viewed or sent via SMS etc.) to reduce startup time by loading them all
	 * at once.
	 */
	public Image loadSingleImageFromRMS(String recordName, String imageName,
			int recordId) {

		Image img = null;

		byte[] imageData = loadImageBytesFromRMS(recordName, imageName, recordId);
		img = Image.createImage(imageData, 0, imageData.length);

		return img;
	}

	/**
	 * Get the data for an Image as a byte array. This is useful for sending 
	 * images via SMS or HTTP
	 */
	public byte[] loadImageBytesFromRMS(String recordName, String imageName, int recordId) {

		byte[] imageData = null;

		try {

			RecordStore albumStore = RecordStore.openRecordStore(recordName,false);
			imageData = albumStore.getRecord(recordId);
			albumStore.closeRecordStore();

		} catch (RecordStoreException rse) {
			System.out.println("Caught Exception: " + rse);
		}

		return imageData;
	}

	/**
	 * Delete a single (specified) image from the (specified) record store.
	 * This will permanently delete the image data and metadata from the device.
	 */
	public boolean deleteSingleImageFromRMS(String storeName, String imageName) {

	    boolean success = false;

	    //Open the record stores containing the byte data and the meta data (info)
	    try {
	        
	        //Verify storeName is name without pre-fix
	        imageRS = RecordStore.openRecordStore(ALBUM_LABEL + storeName, true);
	        imageInfoRS = RecordStore.openRecordStore(INFO_LABEL + storeName, true);
	        
	        ImageData imageData = getImageInfo(imageName);
	        int rid = imageData.getForeignRecordId();

	        imageRS.deleteRecord(rid);
	        imageInfoRS.deleteRecord(rid);
	        
	        imageRS.closeRecordStore();
	        imageInfoRS.closeRecordStore();

	    } catch(RecordStoreException rse) {
	        System.out.println("Exeption deleting Record: " + rse);
	    } 
	    
	    //TODO: It's not clear from the API whether the record store needs to be closed or not...
	    
		return success;
	}

	/**
	 * Define a new photo album for mobile photo users. This creates a new record store
	 * to store photos for the album.
	 */
	public void createNewPhotoAlbum(String albumName) {
		try {
			RecordStore.openRecordStore(ALBUM_LABEL + albumName, true);
			RecordStore.openRecordStore(INFO_LABEL + albumName, true);
		} catch (RecordStoreException rse) {
			System.out.println("Problem creating new record store: " + rse);
		}

	}
	public void deletePhotoAlbum(String albumName) {

		try {
			RecordStore.deleteRecordStore(ALBUM_LABEL + albumName);
			RecordStore.deleteRecordStore(INFO_LABEL + albumName);
		} catch (RecordStoreException rse) {
			System.out.println("Problem deleting new record store: " + rse);
		}

	}
	/**
	 * Get the list of photo album names currently loaded.
	 * @return Returns the albumNames.
	 */
	public String[] getAlbumNames() {
		return albumNames;
	}
}