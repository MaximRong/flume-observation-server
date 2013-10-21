package org.n3r.server;

import java.net.InetSocketAddress;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.n3r.server.base.FlumeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloadServer implements FlumeServer {

	private static Logger log = LoggerFactory.getLogger(FileDownloadServer.class);
	private static final int PORT = 26698;

	@Override
	public void startUp() {
		try {
			NioSocketAcceptor fileAcceptor = new NioSocketAcceptor();
			fileAcceptor.getFilterChain();
			fileAcceptor.setHandler(new FileDownloadHandler());
			fileAcceptor.bind(new InetSocketAddress(PORT));
			log.info("flume file download server has bean startup!");

		} catch (Exception ex) {
			log.error("flume download server run with error...", ex);
		}

	}

}
