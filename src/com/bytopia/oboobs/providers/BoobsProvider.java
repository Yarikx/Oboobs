package com.bytopia.oboobs.providers;

import java.io.IOException;
import java.util.List;

import com.bytopia.oboobs.model.Boobs;
import com.bytopia.oboobs.model.Order;
import com.bytopia.oboobs.utils.NetworkUtils;
import com.bytopia.oboobs.utils.Utils;

public abstract class BoobsProvider implements ImageProvider{
	
	private int order = DESK;

	@Override
	public List<Boobs> getBoobs(int from) throws IOException {
		return NetworkUtils.downloadBoobsList(from, Utils.getBoobsChunk(),
				getBoobsOrder(),order == DESK);
	}
	
	protected abstract Order getBoobsOrder();

	@Override
	public boolean hasOrder() {
		return true;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

}
