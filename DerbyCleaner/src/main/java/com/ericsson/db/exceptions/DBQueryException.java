package com.ericsson.db.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Karol Piątek
 */
public class DBQueryException extends Exception {

    /**
	 * Umożliwienie przekazania
	 * tylko SQLException do konstruktooa
	 * @param ex
	 */
    public DBQueryException(SQLException ex) {
		super(ex);
    }

	/**
	 * Zabroniony dostęp do głównego konstruktora
	 * należy używać konstrukcji
	 * public DBQueryException(SQLException ex)
	 */
	private DBQueryException(){
	}


	/**
	 * Zabroniony dostęp do głównego konstruktora
	 * należy używać konstrukcji
	 * public DBQueryException(SQLException ex)
	 * @param msg
	 */
    private DBQueryException(String msg) {
        super(msg);
    }

	/**
	 * Zabroniony dostęp do głównego konstruktora
	 * należy używać konstrukcji
	 * public DBQueryException(SQLException ex)
	 * @param msg
	 * @param ex
	 */
    private DBQueryException(String msg, Throwable ex) {
        super(msg,ex);
    }
}
