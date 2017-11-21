// #ifdef includeMMAPI
// [NC] Added in the scenario 07
package ubc.midp.mobilephoto.core.ui.datamodel;

public class MultiMediaData extends MediaData {
	private String typemedia;
	public MultiMediaData(int foreignRecordId, String parentAlbumName,
			String mediaLabel, String type) {
		super(foreignRecordId, parentAlbumName, mediaLabel);
		typemedia = type;
	}
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
	
	public String getTypeMedia() {
		return typemedia;
	}
	public void setTypeMedia(String type) {
		this.typemedia = type;
	}

}
//#endif