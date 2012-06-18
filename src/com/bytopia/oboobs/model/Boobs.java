package com.bytopia.oboobs.model;

public class Boobs {
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
		return mediaUrl + preview;
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
