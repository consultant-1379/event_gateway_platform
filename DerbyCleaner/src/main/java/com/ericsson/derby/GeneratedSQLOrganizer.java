package com.ericsson.derby;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author edamore
 * 
 */
public class GeneratedSQLOrganizer {

	private List<String> createPrimaryTableSQL;
	private List<String> createPrimaryTableIndexSQL;
	private List<String> createReferenceTableConstraintSQL;
	private List<String> createReferenceTableSQL;
	private List<String> createReferenceTableIndexSQL;

	private List<String> dropPrimaryTableSQL;
	private List<String> dropPrimaryTableIndexSQL;
	private List<String> dropReferenceTableSQL;
	private List<String> dropReferenceTableConstraintSQL;
	private List<String> dropReferenceTableIndexSQL;

	public GeneratedSQLOrganizer() {
		dropReferenceTableIndexSQL = new ArrayList<String>();
		dropReferenceTableConstraintSQL = new ArrayList<String>();
		dropReferenceTableSQL = new ArrayList<String>();
		dropPrimaryTableIndexSQL = new ArrayList<String>();
		dropPrimaryTableSQL = new ArrayList<String>();
		createReferenceTableIndexSQL = new ArrayList<String>();
		createReferenceTableSQL = new ArrayList<String>();
		createReferenceTableConstraintSQL = new ArrayList<String>();
		createPrimaryTableIndexSQL = new ArrayList<String>();
		createPrimaryTableSQL = new ArrayList<String>();

	}

	public void addCreatePrimaryTableSQL(String sql) {
		createPrimaryTableSQL.add(sql);
	}

	public void addCreatePrimaryTableIndexSQL(String sql) {
		createPrimaryTableIndexSQL.add(sql);
	}

	public void addCreateReferenceTableSQL(String sql) {
		createReferenceTableSQL.add(sql);
	}

	public void addCreateReferenceTableConstraintSQL(String sql) {
		createReferenceTableConstraintSQL.add(sql);
	}

	public void addCreateReferenceTableIndexSQL(String sql) {
		createReferenceTableIndexSQL.add(sql);
	}

	public void addDropPrimaryTableSQL(String sql) {
		dropPrimaryTableSQL.add(sql);
	}

	public void addDropPrimaryTableIndexSQL(String sql) {
		dropPrimaryTableIndexSQL.add(sql);
	}

	public void addDropReferenceTableSQL(String sql) {
		dropReferenceTableSQL.add(sql);
	}

	public void addDropReferenceTableConstraintSQL(String sql) {
		dropReferenceTableConstraintSQL.add(sql);
	}

	public void addDropReferenceTableIndexSQL(String sql) {
		dropReferenceTableIndexSQL.add(sql);
	}

	/**
	 * 
	 * @return ordered SQL statements
	 */
	public LinkedList<List<String>> getOrderedSQL() {

		// Using a LinkedList as we want the statements to execute in this specific order
		LinkedList<List<String>> toReturn = new LinkedList<List<String>>();
		toReturn.add(dropReferenceTableIndexSQL);
		toReturn.add(dropReferenceTableConstraintSQL);
		toReturn.add(dropReferenceTableSQL);

		toReturn.add(dropPrimaryTableIndexSQL);
		toReturn.add(dropPrimaryTableSQL);

		toReturn.add(createPrimaryTableSQL);
		toReturn.add(createPrimaryTableIndexSQL);

		toReturn.add(createReferenceTableSQL);
		toReturn.add(createReferenceTableConstraintSQL);
		toReturn.add(createReferenceTableIndexSQL);

		return toReturn;

	}

}
