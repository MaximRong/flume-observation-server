package org.n3r.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class FlumeServer {

	private static final int PORT = 9123;

	public static void main(String[] args) throws IOException {

		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"),
						LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.setHandler(new FlumeServeHandler());

		acceptor.bind(new InetSocketAddress(PORT));


		NioSocketAcceptor fileAcceptor = new NioSocketAcceptor();
		fileAcceptor.getFilterChain();
		fileAcceptor.setHandler(new DownloadFileHandler());
		fileAcceptor.bind(new InetSocketAddress(9124));

		System.out.println("Server started...");

	}
}
