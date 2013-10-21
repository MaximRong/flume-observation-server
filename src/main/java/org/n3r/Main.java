package org.n3r;

import org.n3r.server.BaseInfoServer;
import org.n3r.server.FileDownloadServer;
import org.n3r.server.base.FlumeServer;

public class Main {

	public static void main(String[] args) {
		FlumeServer baseInfoServer = new BaseInfoServer();
		baseInfoServer.startUp();

		FlumeServer fileDownloadServer = new FileDownloadServer();
		fileDownloadServer.startUp();
	}

}
