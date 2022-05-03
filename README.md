# **COS 221 Practical Assignment 4**
### **Armand Krynauw u04868286**
### **May 2022**
---

**Setting Up and Running the Project**
---
1. Make sure the `sakila` database is configured on your MariaDB server. If it is not follow the following steps:

	- Open up your terminal.

	- Log in to your MariaDB server which has create database privileges. For example:
		```
		mysql -u root -p
		```

	- Create a new database to import the `sakila` database into. For example:
		```
		CREATE DATABASE sakila;
		```

	- Exit the MariaDB monitor and the import the `sakila` database dump into the newly created database schema. For example:
		```
		mysql -u root -p sakila < u04868286_sakila.sql
		```
2. Make sure the specified environment variables listed in the Practical specification document are set with correct credentials to connect to the `sakila` database.
3. Open up your terminal and browse to the directory with the stored `prac04.jar` file.
4. Run the following command in the terminal: `java -jar prac04.jar`

### Check out the following link for a demo of the final project: [COS 221 Practical 4](https://drive.google.com/file/d/1As6TbeqajCRnCM9wy309yacO9MSaVWbF/view)