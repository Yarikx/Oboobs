package com.bytopia.oboobs.model;

import java.io.Serializable;

public class Boobs implements Serializable{
	private static final long serialVersionUID = 3915999632375170121L;
	public String model;
	public String preview;
	public int id;
	public int rank;
	public String author;

	public static String mediaUrl;
	public static String apiUrl;
	
	private static final String BOOBS = "boobs";

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

	public String getFullImageUrl() {
		int slashPos = preview.indexOf('/');
		return new StringBuilder(mediaUrl)
		.append("/")
		.append(BOOBS)
		.append("/")
		.append(preview.substring(slashPos+1))
		.toString();
	}
}
