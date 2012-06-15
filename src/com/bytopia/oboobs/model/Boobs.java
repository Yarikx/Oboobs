package com.bytopia.oboobs.model;

public class Boobs {
	public String model;
	public String preview;
	public int id;
	public int rank;
	public String author;
	
	@Override
	public String toString() {
		return preview+"/"+model;
	}
}
