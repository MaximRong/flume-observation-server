package org.n3r.nio.server.file;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ServerMain {
	/**
	 * @author daijun ,Nov 26, 2009
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SocketAcceptor acceptor = new NioSocketAcceptor();
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		acceptor.setHandler(new FileReceiveHandler());
		acceptor.bind(new InetSocketAddress(3333));
	}
}
