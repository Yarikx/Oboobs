package com.bytopia.oboobs.model;

import java.io.File;
import java.io.Serializable;

import com.bytopia.oboobs.utils.Utils;

public class Boobs implements Serializable{
	private static final long serialVersionUID = 3915999632375170121L;
	public String model;
	public String preview;
	public int id;
	public int rank;
	public String author;

	public static String mediaUrl;
	public static String apiUrl;
	
	@Override
	public String toString() {
		return preview + "/" + model;
	}

	public String getPreviewUrl() {
		return new StringBuilder(mediaUrl)
		.append("/")
		.append(preview)
		.toString();
	}
	
	private String getFileName(){
		return preview.substring(preview.indexOf('/')+1);
	}

	public String getFullImageUrl() {
		return getPreviewUrl().replace("_preview", "");
	}
	
	public boolean hasFavoritedFile(){
		return Utils.hasFileInFavorite(getFileName());
	}
	
	public File getSavedFile(){
		return Utils.getFileInFavorites(getFileName());
	}
}
