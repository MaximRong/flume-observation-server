package org.n3r.nio.test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;

public class TestWriteLineToFile {

	public static void main(String[] args) throws IOException, InterruptedException {
		while(true) {
			File file = new File("e:/TEMP/EOP4ESS_MSG.log");
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String datetime = tempDate.format(new java.util.Date());
			FileUtils.writeStringToFile(file, "当前时间为 ： " + datetime + "\n", true);
			Thread.sleep(6000);
		}
	}

}
