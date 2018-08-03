package lrp;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerFactory {
	
	private static Logger logger = null;
	
	public synchronized static Logger getLogger() {
		if(logger == null) {
			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			logger.setUseParentHandlers(false);
			Handler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					return record.getMessage() + "\n";
				}
			});
			logger.addHandler(consoleHandler);
		}
		return logger;
	}

}
