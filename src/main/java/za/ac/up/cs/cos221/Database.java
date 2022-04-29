/*
* Database class is used to implement all the SQL queries in order to return the data for the Java app.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import java.sql.*;
import java.util.*;

public class Database {
	private static String driver = System.getenv("SAKILA_DB_PROTO");
	private static String host = System.getenv("SAKILA_DB_HOST");
	private static String port = System.getenv("SAKILA_DB_PORT");
	private static String database = System.getenv("SAKILA_DB_NAME");
	private static String username = System.getenv("SAKILA_DB_USERNAME");
	private static String password = System.getenv("SAKILA_DB_PASSWORD");

	private static String url = new StringBuilder()
			.append(driver).append("://")
			.append(host).append(":").append(port).append("/")
			.append(database)
			.toString();

	public Vector<Vector<String>> getStaffData() {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();) {
			String query = new StringBuilder()
					.append("SELECT first_name, last_name, address, address2, district, city, CONCAT(city, ', ', country), ")
					.append("CASE WHEN active = 1 THEN 'Yes' ELSE 'No' END AS active ")
					.append("FROM staff ")
					.append("JOIN address ON (staff.address_id = address.address_id) ")
					.append("JOIN city ON (address.city_id = city.city_id) ")
					.append("JOIN country on (city.country_id = country.country_id)")
					.toString();

			try (ResultSet result = statement.executeQuery(query)) {
				return convertData(result);
			}

		} catch (Exception e) {
			return null;
		}
	}

	public Vector<Vector<String>> getFilmsData() {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();) {
			String query = new StringBuilder()
					.append("SELECT title, rating, length, name AS language, release_year, ")
					.append("rental_duration, rental_rate, replacement_cost ")
					.append("FROM film ")
					.append("JOIN language ON (film.language_id = language.language_id) ")
					.toString();

			try (ResultSet result = statement.executeQuery(query)) {
				return convertData(result);
			}

		} catch (Exception e) {
			return null;
		}
	}

	public Vector<Vector<String>> getInventoryData() {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();) {
			String query = new StringBuilder()
					.append("SELECT store, name, COUNT(inventory.store_id) AS movies ")
					.append("FROM inventory ")
					.append("JOIN film_category ON (inventory.film_id = film_category.film_id) ")
					.append("JOIN category ON (film_category.category_id = category.category_id) ")
					.append("JOIN (")
					.append("SELECT store_id, CONCAT(city, ', ', country) AS store ")
					.append("FROM store ")
					.append("JOIN address ON (store.address_id = address.address_id) ")
					.append("JOIN city ON (address.city_id = city.city_id) ")
					.append("JOIN country ON (city.country_id = country.country_id)) ")
					.append("AS s ON inventory.store_id = s.store_id ")
					.append("GROUP BY inventory.store_id, name")
					.toString();

			try (ResultSet result = statement.executeQuery(query)) {
				return convertData(result);
			}

		} catch (Exception e) {
			return null;
		}
	}

	private Vector<Vector<String>> convertData(ResultSet result) throws SQLException {
		// Making use of vectors because JTable constructor does not accept Array Lists
		Vector<Vector<String>> data = new Vector<>();
		Vector<String> record;
		ResultSetMetaData metadata = result.getMetaData();
		int numFields = metadata.getColumnCount();

		while (result.next()) {
			record = new Vector<String>(numFields);

			for (int i = 0; i < numFields; i++) {
				String field = result.getString(i + 1);
				field = (field == null) ? "" : field;

				record.add(field);
			}

			data.add(record);
		}

		return data;
	}

}
