// #if includeMMAPI || includeVideo
// [NC] Added in the scenario 07
package ubc.midp.mobilephoto.core.ui.datamodel;

public class MultiMediaData extends MediaData {
	
	private String typemedia;
	
	/**
	 * @param mdata
	 * @param type
	 */
	public MultiMediaData(MediaData mdata, String type){
		super(mdata.getForeignRecordId(), mdata.getParentAlbumName(), mdata.getMediaLabel());
		super.setRecordId(mdata.getRecordId());
		// #ifdef includeFavourites
		super.setFavorite(mdata.isFavorite());
		// #endif
		
		// #ifdef includeCountViews
		super.setNumberOfViews(mdata.getNumberOfViews());
		// #endif
		this.typemedia = type;
	}
	
	/**
	 * @return
	 */
	public String getTypeMedia() {
		return typemedia;
	}
	
	/**
	 * @param type
	 */
	public void setTypeMedia(String type) {
		this.typemedia = type;
	}
}
//#endif