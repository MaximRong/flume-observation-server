package org.n3r.flume.jobs.migration;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.common.Config;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiveMigration implements Job {
	private static Logger log = LoggerFactory.getLogger(ArchiveMigration.class);

	public void excuteJob() {
		try {
			log.info("------- ArchiveMigration job Initializing -------------------");
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();

			JobDetail job = JobBuilder.newJob(ArchiveMigration.class)
					.withIdentity("ArchiveMigrationJob", "org.n3r").build();

			SimpleTrigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("ArchiveMigrationTrigger", "org.n3r")
					.startNow()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInHours(2).repeatForever())
					.build();

			Date scheduleJob = sched.scheduleJob(job, trigger);

			sched.start();
			log.info(
					"------- ArchiveMigration job schedule at {} -------------------",
					scheduleJob);


		} catch (SchedulerException ex) {
			log.error("excute ArchiveMigration job error...", ex);
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			log.info("ArchiveMigration executing at " + new Date());

			String flumeFolderStr = Config.getString("flumeFolder");
			String historyFolderStr = Config.getString("flumeHistoryFolder");
			File[] allArchiveFiles = getArchiveFiles(flumeFolderStr);
			if (ArrayUtils.isEmpty(allArchiveFiles))
				return;

			File historyFolder = new File(historyFolderStr);
			log.info("start to move file to folder {}...", historyFolderStr);
			for (File archiveFile : allArchiveFiles) {
				FileUtils.moveFileToDirectory(archiveFile, historyFolder, true);
			}

			log.info("move finish, had move {} files...",
					allArchiveFiles.length);
		} catch (Exception ex) {
			log.error("move file error...", ex);
		}

	}

	private File[] getArchiveFiles(String flumeFolderStr) {
		File flumeFolder = new File(flumeFolderStr);
		File[] allArchiveFiles = flumeFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				return StringUtils.endsWith(fileName, ".gz");
			}
		});
		return allArchiveFiles;
	}

	public static void main(String[] args) throws Exception {
		ArchiveMigration archiveMigration = new ArchiveMigration();
		archiveMigration.excuteJob();
	}
}
