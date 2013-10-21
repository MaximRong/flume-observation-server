package org.n3r.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHelper {

	private static Logger log = LoggerFactory.getLogger(ProcessHelper.class);

	public static void process(Process process, String[] cmd, IoSession session)
			throws IOException {
		try {
			BufferedReader infoBr = null;
			BufferedReader errorBr = null;

			sendNormalMsgToClient(process, infoBr, session);

			sendErrorMsgToClient(process, errorBr, session);

			int val = process.waitFor();
			if (val != 0) {
				log.error("val still has message {} !", val);
			}

		} catch (InterruptedException e) {
			log.error("process has been destroy by InterruptedException!");
			if (null != process) {
				process.destroy();
				process = null;
			}
			session.getWriteRequestQueue().clear(session);
		} finally {
			if (null != process) {
				process.destroy();
				process = null;
			}

			session.close(false);
			log.info(
					"process has been destroy normal! session is isWriteSuspended? {} , is closed? {}.",
					session.isWriteSuspended(), session.isClosing());
		}

	}

	private static void sendNormalMsgToClient(Process process,
			BufferedReader infoBr, IoSession session) throws IOException {
		sendMsgToClient(process, infoBr, session, true);
	}

	private static void sendErrorMsgToClient(Process process,
			BufferedReader errorBr, IoSession session) throws IOException {
		sendMsgToClient(process, errorBr, session, false);
	}

	private static void sendMsgToClient(Process process, BufferedReader buffer,
			IoSession session, boolean isNormal) throws IOException {
		String msg;
		try {
			buffer = new BufferedReader(new InputStreamReader(
					isNormal ? process.getInputStream()
							: process.getErrorStream()));
			while ((msg = buffer.readLine()) != null) {
				session.write(msg);
			}
			log.info("write msg to client has run over!");
		} catch (IOException e) {
			log.error("write {} msg to client error!", isNormal ? "normal"
					: "error", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(buffer);
		}
	}
}
