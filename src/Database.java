import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/treasure_game_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Database() {
        try {
            // Memuat driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver registered!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure you have the connector JAR in your classpath.");
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Membuat tabel players jika belum ada
            String createPlayersTableSQL = "CREATE TABLE IF NOT EXISTS players ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE"
                    + ");";
            stmt.execute(createPlayersTableSQL);
            System.out.println("Table 'players' ensured.");

            // Membuat tabel scores jika belum ada
            String createScoresTableSQL = "CREATE TABLE IF NOT EXISTS scores ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "player_id INT NOT NULL,"
                    + "score INT NOT NULL,"
                    + "game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (player_id) REFERENCES players(id)"
                    + ");";
            stmt.execute(createScoresTableSQL);
            System.out.println("Table 'scores' ensured.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getOrCreatePlayer(String username) {
        String selectSql = "SELECT id FROM players WHERE username = ?";
        String insertSql = "INSERT INTO players (username) VALUES (?)";
        try (Connection conn = connect()) {
            // Coba mendapatkan player yang sudah ada
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, username);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Player '" + username + "' found with ID: " + rs.getInt("id"));
                    return rs.getInt("id");
                }
            }

            // Jika player tidak ada, buat baru
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, username);
                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            System.out.println("New player '" + username + "' created with ID: " + generatedKeys.getInt(1));
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting or creating player: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Mengindikasikan kegagalan
    }

    public void saveScore(int playerId, int score) {
        String insertSql = "INSERT INTO scores (player_id, score) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("Score " + score + " saved for player ID: " + playerId);
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
            e.printStackTrace();
        }
    }
}