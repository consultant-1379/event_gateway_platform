package com.ericsson.derby;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * @author EKARPIA_local
 */
public class PrimaryTableDefinition {

	/**
	 *
	 */
	private final String primaryTable;

	/**
	 *
	 */
	private LinkedList<String> referencedTables = new LinkedList<String>();

	/**
	 *
	 */
	private final List<ForeignKeyDefinition> foreignKeyList = new ArrayList<ForeignKeyDefinition>();

	private GeneratedSQLOrganizer generatedSQLOrganizer;

	/**
	 * 
	 * @param primaryTable
	 * @param dbMetaData
	 */
	public PrimaryTableDefinition(String primaryTable,

	GeneratedSQLOrganizer generatedSQLOrganizer) {
		this.primaryTable = primaryTable;

		this.generatedSQLOrganizer = generatedSQLOrganizer;
	}

	/**
	 * 
	 * @param table
	 */
	public void addReferencedTable(String table) {
		this.referencedTables.add(table);
	}

	/**
	 * 
	 * @return
	 */
	public String getPrimaryTable() {
		return primaryTable;
	}

	/**
	 * 
	 * @return
	 */
	public LinkedList<String> getReferencedTables() {
		return referencedTables;
	}

	/**
	 * 
	 * @param metaData
	 * @param tableName
	 * @throws SQLException
	 * @throws CodeGenerationException
	 */
	private void generateIndexesDescriptions(DatabaseMetaData metaData,
			String tableName, Type type) throws SQLException,
			CodeGenerationException {
		Collection<TableIndex> generatedIndexesPrimaryTable = this
				.generateIndexes(metaData, tableName);

		if (!generatedIndexesPrimaryTable.isEmpty()) {
			for (TableIndex tableIndex : generatedIndexesPrimaryTable) {

				if (type == Type.PRIMARY) {
					generatedSQLOrganizer
							.addDropPrimaryTableIndexSQL(tableIndex
									.generateSqlDropDefinition());
					generatedSQLOrganizer
							.addCreatePrimaryTableIndexSQL(tableIndex
									.generateSqlCreateDefinition());
				} else {
					generatedSQLOrganizer
							.addDropReferenceTableIndexSQL(tableIndex
									.generateSqlDropDefinition());
					generatedSQLOrganizer
							.addCreateReferenceTableIndexSQL(tableIndex
									.generateSqlCreateDefinition());

				}
			}
		}
	}

	/**
	 * Generate definitions for CREATE and DROP tables and indexes.
	 * 
	 * @param metaData
	 * @throws CodeGenerationException
	 * @throws SQLException
	 */
	public GeneratedSQLOrganizer generateDefinitions(DatabaseMetaData metaData)
			throws CodeGenerationException, SQLException {

		// generate for primary table
		this.generateTableDefinition(metaData, primaryTable, Type.PRIMARY);

		// generate indexes for primary table
		this.generateIndexesDescriptions(metaData, primaryTable, Type.PRIMARY);

		// generate for foreign tables
		for (String referenceTable : getReferencedTables()) {
			this.generateTableDefinition(metaData, referenceTable,
					Type.REFERENCE);
			this.generateIndexesDescriptions(metaData, referenceTable,
					Type.REFERENCE);
		}

		return generatedSQLOrganizer;
	}

	/**
	 * Generate table definition
	 * 
	 * @param metaData
	 * @param tableName
	 * @throws SQLException
	 */
	private void generateTableDefinition(DatabaseMetaData metaData,
			String tableName, Type type) throws SQLException,
			CodeGenerationException {

		// columns of all tables from MZADMIN
		// or particular TABLES structures

		List<String> primaryKeysList = new ArrayList<String>();

		// primary keys and foreign keys
		// we are able only to query for exported keys
		// the table that was referenced
		// e.g.
		// when querying the SYSTEM_LOG we will get results
		// when querying the SYSTEM_LOG_NESTED we will not get results
		ResultSet exportedKeys = metaData.getExportedKeys(null, "MZADMIN",
				tableName);
		while (exportedKeys.next()) {

			String primaryColumn = exportedKeys.getString("PKCOLUMN_NAME");
			String primaryKeyTable = exportedKeys.getString("PKTABLE_NAME");

			// we found primary key column for this table
			if (tableName.equalsIgnoreCase(primaryKeyTable)) {
				primaryKeysList.add(primaryColumn);
			}

			String foreignKeyTable = exportedKeys.getString("FKTABLE_NAME");
			String foreignKeyName = exportedKeys.getString("FK_NAME");

			// if the foreign key name is not null
			// add it to the list
			if (foreignKeyName != null) {

				short deleteRule = exportedKeys.getShort("DELETE_RULE");
				short updateRule = exportedKeys.getShort("UPDATE_RULE");

				// find foreign key consisting of:
				// 1.foreign key name
				// 2.this table column name - this is key
				// 3.foreign table name
				// 4.foreign column name
				// 5.if any ON DELETE cascade is required
				String foreignColumnName = exportedKeys
						.getString("FKCOLUMN_NAME");

				ForeignKeyDefinition foreignKeyDefinition = new ForeignKeyDefinition(
						foreignKeyName, foreignKeyTable, foreignColumnName,
						primaryKeyTable, primaryColumn, deleteRule, updateRule);

				foreignKeyList.add(foreignKeyDefinition);
			}
		}
		exportedKeys.close();

		StringBuilder strBuilder = new StringBuilder();
		String toStr;

		// DROP
		strBuilder.append("DROP TABLE ").append(tableName).append("\n");
		toStr = strBuilder.toString();
		if (type == Type.PRIMARY) {
			generatedSQLOrganizer.addDropPrimaryTableSQL(toStr);
		} else {
			generatedSQLOrganizer.addDropReferenceTableSQL(toStr);
		}
		strBuilder = new StringBuilder();

		// CREATE TABLE
		strBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");

		ResultSet columns = metaData.getColumns(null, "MZADMIN", tableName,
				null);

		// when iterating over columns, before extract the primary keys and
		// foreign keys and constraints
		// e.g. TABLE pico_client_stat, COLUMN host_stat_id
		// CONSTRAINT fk_pico_stat_host_id REFERENCES host_stat(id) ON DELETE
		// CASCADE
		boolean autoIncrement = false;

		List<ForeignKeyDefinition> constraintsList = new ArrayList<ForeignKeyDefinition>();

		if (columns != null) {
			while (columns.next()) {

				autoIncrement = columns.getString("IS_AUTOINCREMENT").equals(
						"YES") ? true : false;

				String columnName = columns.getString("COLUMN_NAME");

				// BIGINT and TIMESTAMP cannot have column size specified.
				// set column size to "" if its of one of these field types
				String columnSize;
				if ((columns.getString("TYPE_NAME").toUpperCase()
						.equals("BIGINT"))
						|| (columns.getString("TYPE_NAME").toUpperCase()
								.equals("TIMESTAMP"))) {
					columnSize = "";
				} else {
					columnSize = "(" + columns.getString("COLUMN_SIZE") + ")";
				}

				strBuilder
						.append("\t")
						.append(columnName)
						.append("\t")
						.append(columns.getString("TYPE_NAME"))
						.append(columnSize)
						.append(columns.getString("NULLABLE").equals("0") ? "\tNOT NULL"
								: "");

				// need both tablename and column name for uniqueness
				ForeignKeyDefinition foreignKeyDefinitionForLookup = new ForeignKeyDefinition(
						tableName, columnName);

				if (foreignKeyList.contains(foreignKeyDefinitionForLookup)) {
					constraintsList.add(foreignKeyDefinitionForLookup);
				}

				if (primaryKeysList.contains(columnName)) {
					strBuilder.append(" PRIMARY KEY");
				}

				strBuilder
						.append(autoIncrement ? "\tGENERATED ALWAYS AS IDENTITY"
								: "");

				// EXTRACT default value,
				// if not null and is not auto-increment, prepend DEFAULT
				// keyword
				if (columns.getString("COLUMN_DEF") != null && !autoIncrement) {
					strBuilder.append("\tDEFAULT ").append(
							columns.getString("COLUMN_DEF"));
				}
				strBuilder.append(",\n");
			}
		}

		columns.close();

		// remove last new line character and colon
		strBuilder.setLength(strBuilder.length() - 2);

		// append ending of table definition
		strBuilder.append(" )\n");
		toStr = strBuilder.toString();

		if (type == Type.PRIMARY) {
			generatedSQLOrganizer.addCreatePrimaryTableSQL(toStr);
		} else {
			generatedSQLOrganizer.addCreateReferenceTableSQL(toStr);
		}
		strBuilder = new StringBuilder();

		// generate constraints definitions
		if (constraintsList.size() > 0) {
			for (ForeignKeyDefinition foreignKeyDefinitionForLookup : constraintsList) {
				ForeignKeyDefinition foreignKey = foreignKeyList
						.get(foreignKeyList
								.indexOf(foreignKeyDefinitionForLookup));
				generatedSQLOrganizer
						.addDropReferenceTableConstraintSQL(foreignKey
								.generateSqlDropDefinition());
				generatedSQLOrganizer
						.addCreateReferenceTableConstraintSQL(foreignKey
								.generateSqlCreateDefinition());
			}

		}
	}

	/**
	 * Generate indexes definitions
	 * 
	 * @param metaData
	 * @param columnName
	 * @throws SQLException
	 */
	private Collection<TableIndex> generateIndexes(DatabaseMetaData metaData,
			String srcTableName) throws SQLException {
		// indexes
		ResultSet dbIndexes = metaData.getIndexInfo(null, "MZADMIN",
				srcTableName, false, true);

		// map consisting of
		// we could use list, but it would require
		// to create new TableIndex every time we would like to compare
		Map<String, TableIndex> tableIndexes = new HashMap<String, TableIndex>();

		if (dbIndexes != null) {
			while (dbIndexes.next()) {

				String indexName = dbIndexes.getString("INDEX_NAME");

				// we skip the autogenerated index
				if (indexName.startsWith("SQL")) {
					continue;
				}

				String tableName = dbIndexes.getString("TABLE_NAME");
				boolean unique = !dbIndexes.getBoolean("NON_UNIQUE");

				TableIndex currentTableIndex;

				if (tableIndexes.containsKey(indexName)) {
					currentTableIndex = tableIndexes.get(indexName);
				} else {
					currentTableIndex = new TableIndex(indexName, tableName,
							unique);
					tableIndexes.put(indexName, currentTableIndex);
				}

				currentTableIndex
						.addMapping(dbIndexes.getString("COLUMN_NAME"));
			}
		}

		dbIndexes.close();

		Collection<TableIndex> indexes = tableIndexes.values();
		return indexes;
	}
}
