package com.bytopia.oboobs.utils;

import com.bytopia.oboobs.model.Order;

public class RequestBuilder {

	private static final String SEPARATOR = "/";
	private static final String DESC_SIGN = "-";
	public static String apiUrl;
	public static String boobsPart;
	public static String noisePart;
	public static String modelPart;
	public static String authorPart;

	public static String makeBoobs(int offset, int limit, Order order, boolean desc) {
		return new StringBuilder(apiUrl)
		.append(SEPARATOR)
		.append(boobsPart)
		.append(SEPARATOR)
		.append(offset)
		.append(SEPARATOR)
		.append(limit)
		.append(SEPARATOR)
		.append(desc?DESC_SIGN:"")
		.append(order)
		.append(SEPARATOR)
		.toString();
	}
	
	public static String makeNoise(int limit) {
		return new StringBuilder(apiUrl)
		.append(SEPARATOR)
		.append(noisePart)
		.append(SEPARATOR)
		.append(limit)
		.append(SEPARATOR)
		.toString();
	}
	
	public static String makeModelSearch(String modelName) {
		return new StringBuilder(apiUrl)
		.append(SEPARATOR)
		.append(modelPart)
		.append(SEPARATOR)
		.append(modelName)
		.append(SEPARATOR)
		.toString();
	}
	
	public static String makeAuthorSearch(String authorName) {
		return new StringBuilder(apiUrl)
		.append(SEPARATOR)
		.append(authorPart)
		.append(SEPARATOR)
		.append(authorName)
		.append(SEPARATOR)
		.toString();
	}
}