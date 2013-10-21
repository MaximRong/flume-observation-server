package org.n3r.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadMain {

	private Thread thread;
	private static Logger log = LoggerFactory.getLogger(ThreadMain.class);

	public void run() {
		thread = new Thread(new ThreadSub());
		thread.start();
	}

	public void interrupt() {
		thread.interrupt();
	}

	public static void main(String[] args) {
		ThreadMain threadMain = new ThreadMain();
		threadMain.run();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		threadMain.interrupt();
		log.info("ThreadMain has interrupted! ");
//		System.exit(0);
	}
}
