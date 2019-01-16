package io.github.apoterenko.apps.manager;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class WorkingDirectory {
    private static Logger logger = LoggerFactory.getLogger(WorkingDirectory.class);

    WorkingDirectory() {
        String workingDir = null;
        try {
            Utils.createDirectoryIfNotExists(workingDir = Utils.getWorkingDirectory());

            logger.info("The working directory has been created: {}", workingDir);
        } catch (Exception e) {
            logger.error("Can't start the app because of working directory. Working directory: {}. Error: {}",
                    workingDir,
                    e.getMessage());

            throw new RuntimeException("Can't start the app because of working directory. See logs for more details.");
        }
    }
}