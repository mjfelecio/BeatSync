package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.judgement.JudgementResult;
import com.mjfelecio.beatsync.object.Score;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

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

    public static void createScoresTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS scores ("
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	beatmap_id INTEGER NOT NULL,"
                + "	rank TEXT NOT NULL,"
                + "	score INTEGER NOT NULL,"
                + "	accuracy REAL NOT NULL,"
                + "	max_combo INTEGER NOT NULL,"
                + "	perfect_count INTEGER NOT NULL,"
                + "	great_count INTEGER NOT NULL,"
                + "	meh_count INTEGER NOT NULL,"
                + "	miss_count INTEGER NOT NULL,"
                + "	submitted_at TEXT NOT NULL"
                + ");";
        conn.createStatement().execute(sql);
    }

    public static void insertScore(Score score) throws SQLException {
        String sql = "INSERT INTO scores(beatmap_id, rank, score, accuracy, max_combo, perfect_count, great_count, meh_count, miss_count, submitted_at)" +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Map<JudgementResult, Integer> judgementCounts = score.getJudgementCounts();
        int perfectCount = judgementCounts.get(JudgementResult.PERFECT);
        int greatCount = judgementCounts.get(JudgementResult.GREAT);
        int mehCount = judgementCounts.get(JudgementResult.MEH);
        int missCount = judgementCounts.get(JudgementResult.MISS);

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, score.getBeatmapID());
        pstmt.setString(2, score.getRank().toString());
        pstmt.setLong(3, score.getScore());
        pstmt.setDouble(4, score.getAccuracy());
        pstmt.setInt(5, score.getMaxCombo());
        pstmt.setInt(6, perfectCount);
        pstmt.setInt(7, greatCount);
        pstmt.setInt(8, mehCount);
        pstmt.setInt(9, missCount);
        pstmt.setString(10, score.getSubmittedAt().toString());

        pstmt.executeUpdate();
    }
}
