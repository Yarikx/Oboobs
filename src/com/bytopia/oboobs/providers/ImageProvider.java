package com.bytopia.oboobs.providers;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.bytopia.oboobs.model.Boobs;

public interface ImageProvider extends Serializable {
	
	public static final int ASC = 0, DESK =1;
	
	public List<Boobs> getBoobs(int from) throws IOException;
	public boolean hasOrder();
	public void setOrder(int order);
	
	
	

}
