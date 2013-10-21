package org.n3r.nio.test;

import java.net.InetSocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class TcpClient extends IoHandlerAdapter {
	/** The connector */
	private IoConnector connector;

	/** The session */
	private static IoSession session;

	private boolean received = false;

	/**
	 * Create the UdpClient's instance
	 */
	public TcpClient() {
		connector = new NioSocketConnector();

		connector.setHandler(this);
		SocketSessionConfig dcfg = (SocketSessionConfig) connector
				.getSessionConfig();

		ConnectFuture connFuture = connector.connect(new InetSocketAddress(
				"localhost", TcpServer.PORT));

		connFuture.awaitUninterruptibly();

		session = connFuture.getSession();
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
		received = true;
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
		TcpClient client = new TcpClient();

		long t0 = System.currentTimeMillis();

		for (int i = 0; i <= TcpServer.MAX_RECEIVED; i++) {
			// if (i % 2 == 0) {
			// Thread.sleep(1);
			// }

			IoBuffer buffer = IoBuffer.allocate(4);
			buffer.putInt(i);
			buffer.flip();
			WriteFuture future = session.write(buffer);

			while (client.received == false) {
				Thread.sleep(1);
			}

			client.received = false;

			if (i % 10000 == 0) {
				System.out.println("Sent " + i + " messages");
			}
		}

		long t1 = System.currentTimeMillis();

		System.out.println("Sent messages delay : " + (t1 - t0));

		Thread.sleep(100000);

		client.connector.dispose(true);
	}
}
