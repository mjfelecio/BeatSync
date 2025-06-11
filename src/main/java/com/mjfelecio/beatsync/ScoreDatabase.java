package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public class ScoreDatabase {
    private static final String DB_FILE_NAME = "beatsync.db";
    private static Connection conn;

    public static void connect() throws Exception {
        // Locate directory of the running JAR
        Path jarDir = getJarDirectory();
        if (jarDir == null) {
            throw new RuntimeException("Could not determine JAR directory");
        }

        // Construct path to the SQLite DB
        String dbPath = jarDir.resolve(DB_FILE_NAME).toAbsolutePath().toString();
        String jdbcUrl = "jdbc:sqlite:" + dbPath;

        // If we are in dev env, just make the db inside the root of the project folder
        if (GameConfig.ENVIRONMENT.equals("DEV")) jdbcUrl = "jdbc:sqlite:beatsync.db";

        conn = DriverManager.getConnection(jdbcUrl);
    }

    private static Path getJarDirectory() throws URISyntaxException {
        // Gets the path of the running JAR or class
        File jarFile = new File(ScoreDatabase.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
        return jarFile.getParentFile().toPath();
    }
}
