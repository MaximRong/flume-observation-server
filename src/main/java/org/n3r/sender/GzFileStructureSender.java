package org.n3r.sender;

import java.io.File;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.n3r.common.Config;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

public class GzFileStructureSender implements ClientSender {

	@Override
	public void send(IoSession session, String comand, String param) {
		String flumeFolderStr = Config.getString("flumeHistoryFolder");
		File flumeFolder = new File(flumeFolderStr);
		if (!flumeFolder.exists()) {
			throw new RuntimeException("can't not found the flumeFolder :"
					+ flumeFolderStr);
		}
		if (!flumeFolder.isDirectory()) {
			throw new RuntimeException("flumeHistoryFolder '" + flumeFolderStr
					+ "' is not a folder!");
		}

		List<Object> folderList = addChildFiles(flumeFolder);
		String treeFileJsonStr = JSONArray.toJSONString(folderList);
		session.write(treeFileJsonStr);
	}

	private List<Object> addChildFiles(File flumeFolder) {
		List<Object> folderList = Lists.newArrayList();
		for (File file : flumeFolder.listFiles()) {
			folderList.add(file.getName());
		}
		return folderList;
	}

	@Override
	public void close() {

	}

}
