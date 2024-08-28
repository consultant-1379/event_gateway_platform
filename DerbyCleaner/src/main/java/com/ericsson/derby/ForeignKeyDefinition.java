package com.ericsson.derby;

/**
 * 
 * @author EKARPIA_local
 */
public class ForeignKeyDefinition implements ISqlGenerator {

	/**
	 * 
	 */
	private String foreignTableName;

	/**
	 * Foreign key name
	 */
	private String foreignKeyName;

	/**
	 * This is a column name of the table in which primary key is referenced
	 */
	private final String foreignColumnName;

	/**
	 * 
	 */
	private String primaryTableName;

	/**
	 * 
	 */
	private String primaryColumnName;

	/**
	 * 
	 */
	private short deleteRule;

	/**
	 * 
	 */
	private short updateRule;

	/**
	 * 
	 * @param foreignKeyName
	 * @param foreignTableName
	 * @param foreignColumnName
	 * @param primaryTableName
	 * @param primaryColumnName
	 * @param deleteRule
	 * @param updateRule
	 */
	public ForeignKeyDefinition(String foreignKeyName, String foreignTableName,
			String foreignColumnName, String primaryTableName,
			String primaryColumnName, short deleteRule, short updateRule) {
		this.foreignKeyName = foreignKeyName;
		this.foreignTableName = foreignTableName;
		this.foreignColumnName = foreignColumnName;
		this.primaryTableName = primaryTableName;
		this.primaryColumnName = primaryColumnName;
		this.deleteRule = deleteRule;
		this.updateRule = updateRule;

		// System.err.println(this.toString());
	}

	/**
	 * 
	 * @param foreignTableName
	 * @param foreignColumnName
	 */
	public ForeignKeyDefinition(String foreignTableName,
			String foreignColumnName) {
		this.foreignTableName = foreignTableName;
		this.foreignColumnName = foreignColumnName;
	}

	@Override
	public String toString() {

		return "ForeignKeyDefinition{ foreignKeyName=" + foreignKeyName
				+ ", foreignTableName=" + foreignTableName
				+ ", foreignColumnName=" + foreignColumnName
				+ ", primaryTableName=" + primaryTableName
				+ ", primaryColumnName=" + primaryColumnName + ", deleteRule="
				+ deleteRule + ", updateRule=" + updateRule + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForeignKeyDefinition other = (ForeignKeyDefinition) obj;
		if (foreignColumnName == null) {
			if (other.foreignColumnName != null)
				return false;
		} else if (!foreignColumnName.equals(other.foreignColumnName))
			return false;
		if (foreignTableName == null) {
			if (other.foreignTableName != null)
				return false;
		} else if (!foreignTableName.equals(other.foreignTableName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((foreignColumnName == null) ? 0 : foreignColumnName
						.hashCode());
		result = prime
				* result
				+ ((foreignTableName == null) ? 0 : foreignTableName.hashCode());
		return result;
	}

	@Override
	public String generateSqlDropDefinition() throws CodeGenerationException {
		StringBuilder sqlDefBuilder = new StringBuilder();

		sqlDefBuilder.append("ALTER TABLE ").append(this.foreignTableName)
				.append(" DROP CONSTRAINT ").append(foreignKeyName)
				.append("\n");

		return sqlDefBuilder.toString();
	}

	@Override
	public String generateSqlCreateDefinition() throws CodeGenerationException {
		StringBuilder sqlDefBuilder = new StringBuilder();

		sqlDefBuilder.append("ALTER TABLE ").append(this.foreignTableName)
				.append(" ADD CONSTRAINT ").append(foreignKeyName)
				.append(" FOREIGN KEY (").append(this.foreignColumnName)
				.append(")").append(" REFERENCES ")
				.append(this.primaryTableName).append("(")
				.append(this.primaryColumnName).append(")");

		// http://docs.oracle.com/javase/6/docs/api/java/sql/DatabaseMetaData.html#getExportedKeys%28java.lang.String,%20java.lang.String,%20java.lang.String%29
		switch (this.deleteRule) {
		case java.sql.DatabaseMetaData.importedKeyCascade:
			sqlDefBuilder.append(" ON DELETE CASCADE");
			break;
		default:
			break;
		}

		switch (this.updateRule) {
		case java.sql.DatabaseMetaData.importedKeyCascade:
			sqlDefBuilder.append(" ON UPDATE CASCADE");
			break;
		default:
			break;
		}

		sqlDefBuilder.append("\n");
		return sqlDefBuilder.toString();
	}
}
