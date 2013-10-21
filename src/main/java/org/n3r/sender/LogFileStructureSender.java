package org.n3r.sender;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.n3r.common.Config;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

public class LogFileStructureSender implements ClientSender {

	@Override
	public void send(IoSession session, String comand, String param) {
		String flumeFolderStr = Config.getString("flumeFolder");
		File flumeFolder = new File(flumeFolderStr);
		if (!flumeFolder.exists()) {
			throw new RuntimeException("can't not found the flumeFolder :"
					+ flumeFolderStr);
		}
		if (!flumeFolder.isDirectory()) {
			throw new RuntimeException("flumeFolder '" + flumeFolderStr
					+ "' is not a folder!");
		}

		List<Object> folderList = addChildFiles(flumeFolder);
		String treeFileJsonStr = JSONArray.toJSONString(folderList);
		session.write(treeFileJsonStr);
	}

	private List<Object> addChildFiles(File flumeFolder) {
		List<Object> folderList = Lists.newArrayList();
		for (File file : flumeFolder.listFiles(new FlumeLogFileFilter())) {
			folderList.add(StringUtils.split(file.getName(), '.')[0]);
		}
		return folderList;
	}

	class FlumeLogFileFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			String fileName = file.getName();
			return StringUtils.endsWith(fileName, "log");
		}

	}

	@Override
	public void close() {

	}
}
