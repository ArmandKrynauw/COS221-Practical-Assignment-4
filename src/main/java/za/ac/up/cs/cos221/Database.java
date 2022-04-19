/*
* Database class is used to implement all the SQL queries in order to return the data for the Java app.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import java.sql.*;

public class Database {
	private static String driver = System.getenv("SAKILA_DB_PROTO");
	private static String host = System.getenv("SAKILA_DB_HOST");
	private static String port = System.getenv("SAKILA_DB_PORT");
	private static String database = System.getenv("SAKILA_DB_NAME");
	private static String username = System.getenv("SAKILA_DB_USERNAME");
	private static String password = System.getenv("SAKILA_DB_PASSWORD");

	private static Connection connection = null;

	public static Connection getConnection() throws SQLException {
		if (connection == null) {
			String url = new StringBuilder()
					.append(driver).append("://")
					.append(host).append(":").append(port).append("/")
					.append(database)
					.toString();

			try (Connection connection = DriverManager.getConnection(url, username, password)) {
				try (Statement statement = connection.createStatement()) {

					// Test whether a successful connection has been made to the DB instance
					try (ResultSet result = statement.executeQuery("SELECT 1")) {
						Database.connection = connection;
					}
				}
			}
		}

		return Database.connection;
	}

	public static void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

}
