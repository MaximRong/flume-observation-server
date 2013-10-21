package org.n3r.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.n3r.sender.ClientSender;
import org.n3r.sender.ClientSenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseInfoServerHandler extends IoHandlerAdapter {
	private static Logger log = LoggerFactory
			.getLogger(BaseInfoServerHandler.class);
	ClientSender sender;

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String theMessage = (String) message;
		String[] comands = StringUtils.split(theMessage, "^");
		String comand = comands[0];
		if (isClientCloseSession(comand)) {
			log.info("received client sended quit commond!");
			sender.close();
			return;
		}

		String param = comands.length > 1 ? comands[1] : null;
		sender = ClientSenderFactory.make(comand);
		sender.send(session, comand, param);

	}

	private boolean isClientCloseSession(String comand) {
		return RequestCommand.QUIT == RequestCommand.valueOf(comand).toInt();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("server run error...", cause);
		session.close(true);
	}


}
