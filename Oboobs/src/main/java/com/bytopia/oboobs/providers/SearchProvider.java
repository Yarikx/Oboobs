package com.bytopia.oboobs.providers;


public abstract class SearchProvider implements ImageProvider{

	private static final long serialVersionUID = -219584674145126349L;
	
	protected String searchText;
	
	public SearchProvider(String text) {
		searchText = text;
	}

	@Override
	public boolean hasOrder() {
		return false;
	}

	@Override
	public void setOrder(int order) {}

	@Override
	public boolean isInfinitive() {
		return false;
	}

}
