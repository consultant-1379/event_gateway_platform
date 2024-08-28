package com.ericsson;

import com.ericsson.derby.GeneratedSQLOrganizer;
import com.ericsson.derby.PrimaryTableDefinition;

import java.sql.*;
import java.util.*;

/**
 * 
 * @author EKARPIA
 */
public class DerbyCleaner {

	public static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static GeneratedSQLOrganizer generatedSQLOrganizer = new GeneratedSQLOrganizer();

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		boolean successful = false;

		long startTime = System.currentTimeMillis();
		try {

			if (args.length != 1) {
				System.err
						.println("You need to provide 1 parameter -- the path to derby database backup"
								+ "\ne.g. java -jar DerbyCLeaner.jar /eniq/backup/mzdb/mgdb");
				System.exit(1);
			}

			String pathToDerby = args[0];

			// String pathToDerby = "C:/Tools/DR/mz513_derby_testing/mzdb";

			Class.forName(driver).newInstance();
			Properties props = new Properties();

			String protocol = "jdbc:derby:";

			props.setProperty("create", "false");
			props.setProperty("user", "mzadmin");
			props.setProperty("password", "mz");

			Connection connection = DriverManager.getConnection(protocol
					+ pathToDerby, props);
			connection.setAutoCommit(true);

			DatabaseMetaData metaData = connection.getMetaData();

			PrimaryTableDefinition systemLogTable = new PrimaryTableDefinition(
					"SYSTEM_LOG", generatedSQLOrganizer);
			systemLogTable.addReferencedTable("SYSTEM_LOG_NESTED");
			systemLogTable.generateDefinitions(metaData);

			PrimaryTableDefinition hostStatTable = new PrimaryTableDefinition(
					"HOST_STAT", generatedSQLOrganizer);
			hostStatTable.addReferencedTable("WF_QUEUE_STAT");
			hostStatTable.addReferencedTable("PICO_CLIENT_STAT");
			hostStatTable.addReferencedTable("WF_STAT");
			hostStatTable.generateDefinitions(metaData);

			for (List<String> list : generatedSQLOrganizer.getOrderedSQL()) {
				for (String sql : list) {
					System.err.println("Executing SQL:\n" + sql);

					executeCall(connection.prepareCall(sql));
				}

			}

			// COMPACT WFTXN
			CallableStatement preparedCall = connection
					.prepareCall("{CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?,?,?)}");
			preparedCall.setString(1, "MZADMIN");
			preparedCall.setInt(3, 1);
			preparedCall.setString(2, "WF_TXN");
			System.err.println("Compacting WF_TXN ...");
			executeCall(preparedCall);

			// CLOSE CONNECTION
			connection.close();

			successful = true;
			System.out.println("Derby database cleanup complete.");
			System.out.format("\n\nTask took %d seconds ",
					(System.currentTimeMillis() - startTime) / 1000);

		} catch (Exception ex) {
			System.err.println("A problem occured: " + ex.getMessage());
			ex.printStackTrace(System.err);
			successful = false;
		} finally {

			if (successful) {
				System.exit(0);
			} else {
				System.exit(1);
			}

		}
	}

	/**
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public static void disableLogArchiveMode(Connection conn)
			throws SQLException {
		String sqlstmt = "CALL SYSCS_UTIL.SYSCS_DISABLE_LOG_ARCHIVE_MODE(?)";
		CallableStatement cs = conn.prepareCall(sqlstmt);
		cs.setInt(1, 1);
		cs.execute();
	}

	/**
	 * 
	 * @param preparedCall
	 * @throws SQLException
	 */
	private static void executeCall(CallableStatement preparedCall)
			throws SQLException {
		boolean hasResults = preparedCall.execute();

		while (hasResults) {
			ResultSet rs = preparedCall.getResultSet();
			while (rs.next()) {
				System.out.println(rs.toString());
			}
			rs.close();
			hasResults = preparedCall.getMoreResults();
		}
	}

}
