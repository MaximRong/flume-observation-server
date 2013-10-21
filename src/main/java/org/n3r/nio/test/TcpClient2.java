package org.n3r.nio.test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class TcpClient2 extends IoHandlerAdapter {
	

	private static final int PORT = 9123;

	/**
	 * Create the UdpClient's instance
	 */
	public TcpClient2() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println(message.toString());
		session.close(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}

	/**
	 * The main method : instanciates a client, and send N messages. We sleep
	 * between each K messages sent, to avoid the server saturation.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		IoConnector connector = new NioSocketConnector();

		connector.setHandler(new TcpClient2());
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"), LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		SocketSessionConfig dcfg = (SocketSessionConfig) connector
				.getSessionConfig();

		ConnectFuture connFuture = connector.connect(new InetSocketAddress(
				"localhost", PORT));
		
		connFuture.awaitUninterruptibly();
		IoSession session = connFuture.getSession();
		session.write("fileTree");
		
//		if(write.isWritten()) {
//			System.out.println("is done!");
//		}
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose(true);		
	}
}
