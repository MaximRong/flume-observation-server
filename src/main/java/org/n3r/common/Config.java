package org.n3r.common;

import org.n3r.common.base.Configable;

public class Config {

	private static Configable configimpl;

	static {
		configimpl = new ConfigableImpl();
	}

	public static String getString(String key) {
		return configimpl.getString(key);
	}

}
