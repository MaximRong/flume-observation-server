package org.n3r.common;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.n3r.common.base.Configable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigableImpl implements Configable {

	private static Logger log = LoggerFactory.getLogger(ConfigableImpl.class);
	private static Properties properties;

	public ConfigableImpl() {
		InputStream openStream = null;
		try {
			URL resource = getClass().getClassLoader().getResource(
					"flume.properties");
			openStream = resource.openStream();
			properties = new Properties();
			properties.load(openStream);

		} catch (Exception ex) {
			log.error("init config error...", ex);
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(openStream);
		}
	}

	@Override
	public String getString(String key) {
		return properties.getProperty(key);
	}

}
