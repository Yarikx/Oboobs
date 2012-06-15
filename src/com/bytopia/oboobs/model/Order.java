package com.bytopia.oboobs.model;

public enum Order {
	ID, RANK, INTEREST;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}