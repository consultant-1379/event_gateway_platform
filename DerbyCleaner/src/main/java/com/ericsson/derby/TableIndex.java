package com.ericsson.derby;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author EKARPIA_local
 */
public class TableIndex implements ISqlGenerator {

	/**
	 * 
	 */
	private String indexName;

	/**
	 * 
	 */
	private String tableName;

	/**
	 * 
	 */
	private List<String> tableColumnMapping = new ArrayList<String>();

	/**
	 * Is index unique
	 */
	private boolean isUnique;

	/**
	 * 
	 * @param indexName
	 */
	public TableIndex(String indexName, String tableName, boolean isUnique) {
		this.indexName = indexName;
		this.tableName = tableName;
		this.isUnique = isUnique;

	}

	/**
	 * 
	 * @param tableName
	 * @param columnName
	 */
	public void addMapping(String columnName) {
		this.tableColumnMapping.add(columnName);
	}

	/**
	 * 
	 * 
	 * Create the sql definition string
	 * 
	 * e.g.
	 * 
	 * DROP INDEX <indexname>
	 * 
	 * @return
	 */
	@Override
	public String generateSqlDropDefinition() throws CodeGenerationException {

		int count = this.tableColumnMapping.size();

		if (count < 0) {
			throw new CodeGenerationException("No proper index definition");
		}

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("DROP INDEX ").append(this.indexName).append("\n");

		return strBuilder.toString();
	}

	/**
	 * 
	 * 
	 * Create the sql definition string
	 * 
	 * e.g.
	 * 
	 * CREATE UNIQUE INDEX host_stat_u_idx on host_stat(host_name, stat_date,
	 * resolution);
	 * 
	 * @return
	 */
	@Override
	public String generateSqlCreateDefinition() throws CodeGenerationException {

		int count = this.tableColumnMapping.size();

		if (count < 0) {
			throw new CodeGenerationException("No proper index definition");
		}

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("CREATE ");
		strBuilder.append(this.isUnique ? "UNIQUE INDEX " : "INDEX ");
		strBuilder.append(this.indexName);
		strBuilder.append(" ON ");
		strBuilder.append(this.tableName);
		strBuilder.append("");
		strBuilder.append("(");

		/**
		 * Append all column names separated by colon, a
		 */
		for (String columnName : this.tableColumnMapping) {
			strBuilder.append(columnName);
			strBuilder.append(",");
		}

		// remove last colon
		strBuilder.setLength(strBuilder.length() - 1);

		strBuilder.append(")\n");

		return strBuilder.toString();
	}
}
