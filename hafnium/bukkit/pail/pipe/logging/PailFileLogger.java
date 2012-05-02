package hafnium.bukkit.pail.pipe.logging;

import hafnium.bukkit.pail.pipe.PailPipe;
import hafnium.bukkit.pail.pipe.plugins.PailPlugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PailFileLogger {
	private final PailPlugin plugin;
	private StreamHandler handler;

	private boolean logging = false;

	public PailFileLogger(PailPlugin plugin) {
		this.plugin = plugin;
		if (PailPipe.getInstance().getConfig().getBoolean("logging"))
			this.startLogging();
	}

	public void startLogging() {
		try {
			this.handler = new StreamHandler(new FileOutputStream(this.getNextLog()), new XMLFormatter());
			this.handler.setLevel(Level.ALL);
			this.plugin.getLogger().addHandler(this.handler);
			this.logging = true;
		} catch (FileNotFoundException e) {
			PailPipe.getInstance().getLogger()
					.log(java.util.logging.Level.WARNING, "Could not start a logger for " + this.plugin.getDescription().getName() + ".");
		}
	}

	public void stopLogging() {
		if (!this.logging)
			return;

		this.handler.flush();
		this.handler.close();

		this.logging = false;
	}

	private static final Pattern logFile = Pattern.compile("^([0-9]+)\\.log$");

	private File getNextLog() {
		File logFolder = new File(this.plugin.getDataFolder(), "logs");
		if (!logFolder.exists())
			logFolder.mkdirs();

		File[] logs = logFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return logFile.matcher(file.getName()).matches();
			}
		});

		int maxLog = -1;

		for (File log : logs) {
			Matcher logMatcher = logFile.matcher(log.getName());
			if (!logMatcher.matches())
				continue;
			int logNumber = Integer.parseInt(logMatcher.group(1));
			if (logNumber > maxLog)
				maxLog = logNumber;
		}

		for (File log : logs) {
			Matcher logMatcher = logFile.matcher(log.getName());
			if (!logMatcher.matches())
				log.delete();
			else {
				int logNumber = Integer.parseInt(logMatcher.group(1));
				if (logNumber <= maxLog - 9)
					log.delete();
			}
		}

		return new File(logFolder, (maxLog + 1) + ".log");
	}
}
