package org.n3r.nio.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class LoginClient {
	private static final int PORT = 3005;
	private static final String HOST = "127.0.0.1";

	public static void main(String[] args) {
		IoConnector connector = new NioSocketConnector();

		connector.setConnectTimeoutMillis(3000);
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));

		connector.setHandler(new LoginClientHandler());

		IoSession session = null;

		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(
					HOST, PORT));

			future.awaitUninterruptibly();
			session = future.getSession();
			session.write("admin,3");
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
	}
}
