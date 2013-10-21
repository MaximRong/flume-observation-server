package org.n3r.thread;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadSub implements Runnable {

	private static Logger log = LoggerFactory.getLogger(ThreadSub.class);

	@Override
	public void run() {
		String[] cmd = new String[] { "tail", "-500f",
				"/home/maria/Templates/flumelogs/AOP.log" };
		BufferedReader infoBr = null;
		String msg;
		try {
			Process process = new ProcessBuilder(cmd).start();
//			infoBr = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			while ((msg = infoBr.readLine()) != null) {
//				log.info(msg);
//			}

			int val = process.waitFor();
			if (val != 0) {
			}

			if (null != process) {
				process.destroy();
				process = null;
			}

		} catch (IOException e) {
			log.error("IOException...", e);
		} catch (InterruptedException e) {
			log.error("InterruptedException...", e);
		} finally {
			IOUtils.closeQuietly(infoBr);
		}
	}

}
