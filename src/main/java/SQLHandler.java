import java.sql.*;

public class SQLHandler {

    private static final String url = "jdbc:mysql://localhost:3306/shutkobot?ftimeCode=false&serverTimezone=UTC&useSSL=false";
    private static final String user = "root";
    private static final String password = "";

    private Connection con;
    private Statement stmt;
    private ResultSet rs;

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            con.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(long chatId, String firstName, String lastName, String userName) {
        String query = "REPLACE INTO users (chatId,firstName,lastName,userName) VALUES ('" + chatId + "','" + firstName + "','" + lastName + "','" + userName + "')";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeState(long chatId, String state) {
        String query = "REPLACE INTO user_state (chatId,state) VALUES ('" + chatId + "','" + state + "')";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getState(long chatId) {
        String query = "SELECT state from user_state where chatId='" + chatId + "'";
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addSuggestion(long chatId, String text) {
        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        String query = "INSERT INTO joke_suggestion (chatId, suggestion, date, isSelected, isUsed) VALUES ('" + chatId + "','" + text + "','" + currentTime + "',0,0)";
        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentJoke() {
        String currentJoke = "";
        String query = "SELECT suggestion from joke_suggestion where isSelected=1";
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                currentJoke=rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentJoke;
    }

    public int getCurrentJokeId() {
        int id=-1;
        String query = "SELECT id from joke_suggestion where isSelected=1";
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                id=rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void addPunchLine(long chatId,String text,int startId){
        String query="INSERT INTO punch_lines (chatId, line, startId) values (?,?,?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, chatId);
            preparedStatement.setString(2, text);
            preparedStatement.setInt(3,startId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}