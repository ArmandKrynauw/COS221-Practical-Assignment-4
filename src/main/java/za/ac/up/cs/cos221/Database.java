/*
* Database class is used to implement all the SQL queries in order to return the data for the Java app.
*
* @author Armand Krynauw
*/

package za.ac.up.cs.cos221;

import java.sql.*;
import java.util.*;

public class Database {
	private static Database instance = null;

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

	private Database() {
	}

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}

		return instance;
	}

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

	// ======================================================================================
	// Film Functions
	// ======================================================================================

	public Vector<Vector<String>> getFilmData() {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();) {
			String query = new StringBuilder()
					.append("SELECT title, rating, length, name AS language, release_year, ")
					.append("rental_duration, rental_rate, replacement_cost ")
					.append("FROM film ")
					.append("JOIN language ON (film.language_id = language.language_id) ")
					.append("ORDER BY title")
					.toString();

			try (ResultSet result = statement.executeQuery(query)) {
				return convertData(result);
			}

		} catch (Exception e) {
			return null;
		}
	}

	public boolean addNewFilm(LinkedHashMap<String, String> data) {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			/*-----------------------------RETRIEVE LANGUAGE ID-----------------------------*/
			String query = new StringBuilder()
					.append("SELECT language_id FROM language WHERE name = ?")
					.toString();

			int languageID;
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, data.get("Language"));
				statement.executeQuery();

				ResultSet result = statement.getResultSet();
				result.first();
				languageID = result.getInt(1);
			}

			/*-----------------------------RETRIEVE CATEGORY ID-----------------------------*/
			query = new StringBuilder()
					.append("SELECT category_id FROM category WHERE name = ?")
					.toString();

			int categoryID;
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, data.get("Category"));
				statement.executeQuery();

				ResultSet result = statement.getResultSet();
				result.first();
				categoryID = result.getInt(1);
			}

			/*---------------------------------INSERT FILM---------------------------------*/
			query = new StringBuilder()
					.append("INSERT INTO film ")
					.append("(title, description, release_year, language_id, rental_duration, ")
					.append("rental_rate, length, replacement_cost, rating, special_features)  ")
					.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.toString();

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, data.get("Title").toUpperCase());
				if (data.get("Description").equals("")) {
					statement.setNull(2, Types.VARCHAR);
				} else {
					statement.setString(2, data.get("Description"));
				}
				if (data.get("Release Year").equals("")) {
					statement.setNull(3, Types.DATE);
				} else {
					statement.setString(3, data.get("Release Year"));
				}
				statement.setInt(4, languageID);
				statement.setInt(5, Integer.parseInt(data.get("Rental Duration")));
				statement.setDouble(6, Double.parseDouble(data.get("Rental Rate")));
				if (data.get("Length").equals("")) {
					statement.setNull(7, Types.INTEGER);
				} else {
					statement.setInt(7, Integer.parseInt(data.get("Length")));
				}
				statement.setDouble(8, Double.parseDouble(data.get("Replacement Cost")));
				statement.setString(9, data.get("Rating"));
				if (data.get("Special Features").equals("")) {
					statement.setNull(10, Types.VARCHAR);
				} else {
					statement.setString(10, data.get("Special Features"));
				}
				statement.execute();
			}

			/*----------------------------RETRIEVE NEW FILM ID----------------------------*/
			query = new StringBuilder()
					.append("SELECT film_id FROM film WHERE title = ? ORDER BY film_id DESC")
					.toString();

			int filmID;
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, data.get("Title").toUpperCase());
				statement.executeQuery();

				ResultSet result = statement.getResultSet();
				result.first();
				filmID = result.getInt(1);
			}

			/*----------------------------INSERT FILM CATEGORY----------------------------*/
			query = new StringBuilder()
					.append("INSERT INTO film_category ")
					.append("(film_id, category_id) ")
					.append("VALUES (?, ?)")
					.toString();

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, filmID);
				statement.setInt(2, categoryID);
				statement.execute();
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	// ======================================================================================
	// Customer Functions
	// ======================================================================================

	public Vector<Vector<String>> getCustomerData() {
		try (Connection connection = DriverManager.getConnection(url, username, password);
				Statement statement = connection.createStatement();) {
			String query = new StringBuilder()
					.append("SELECT ID, name, phone, email, address, city, country, `zip code`, ")
					.append("CASE WHEN active = 1 THEN 'Yes' ELSE 'No' END AS active, SID ")
					.append("FROM customer_list ")
					.append("JOIN customer ON (customer_list.ID = customer.customer_id) ")
					.append("ORDER BY ID")
					.toString();

			try (ResultSet result = statement.executeQuery(query)) {
				return convertData(result);
			}

		} catch (Exception e) {
			return null;
		}
	}

	public boolean deleteCustomer(String customerID) {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			String query = "DELETE FROM customer WHERE customer_id = ?";

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setInt(1, Integer.parseInt(customerID));
				statement.executeQuery();

				return (statement.getUpdateCount() != 0);
			}

		} catch (Exception e) {
			return false;
		}
	}

	// ======================================================================================
	// Helper Functions
	// ======================================================================================

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