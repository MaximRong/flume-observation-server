package org.n3r.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {

	@Test
	public void test() {
		assertEquals("e:/TEMP", Config.getString("flumeFolder"));
		assertEquals("e:/TEMP2", Config.getString("flumeHistoryFolder"));
	}


}
