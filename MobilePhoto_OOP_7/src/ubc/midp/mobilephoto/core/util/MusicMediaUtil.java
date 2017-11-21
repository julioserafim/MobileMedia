// #ifdef includeMMAPI
// [NC] Added in the scenario 07
package ubc.midp.mobilephoto.core.util;

import lancs.midp.mobilephoto.lib.exceptions.InvalidArrayFormatException;
import lancs.midp.mobilephoto.lib.exceptions.InvalidImageDataException;
import ubc.midp.mobilephoto.core.ui.datamodel.MediaData;
import ubc.midp.mobilephoto.core.ui.datamodel.MultiMediaData;

public class MusicMediaUtil extends MediaUtil {

	public byte[] getBytesFromMediaInfo(MediaData ii)
			throws InvalidImageDataException {
		try {
			byte[] mediadata = super.getBytesFromMediaInfo(ii);
			if (ii instanceof MultiMediaData) {

				
				String byteString = new String(mediadata);
				byteString = byteString.concat(DELIMITER);

				byteString = byteString.concat(((MultiMediaData) ii).getTypeMedia());
				return byteString.getBytes();

			}
			return mediadata;
		} catch (Exception e) {
			throw new InvalidImageDataException(
					"The provided data are not valid");
		}
		
	}

	public MediaData getMultiMediaInfoFromBytes(byte[] bytes)
			throws InvalidArrayFormatException {
		// TODO Auto-generated method stub
		MediaData mediadata =  super.getMediaInfoFromBytes(bytes);
		String iiString = new String(bytes);
		
		int startIndex = endIndex + 1;
		if (endIndex==iiString.length())
			return mediadata;
		
		endIndex = iiString.indexOf(DELIMITER, startIndex);
		
		if (endIndex == -1)
			endIndex = iiString.length();
		String mediaType = iiString.substring(startIndex, endIndex);
		MultiMediaData mmedi = new MultiMediaData(mediadata,mediaType);
		return mmedi;

	}

}
//#endif