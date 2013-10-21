package org.n3r.sender;

import org.apache.mina.core.session.IoSession;

public interface ClientSender {

	void send(IoSession session, String comand, String param);

	void close();

}
