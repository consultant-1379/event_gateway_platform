/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ericsson.db.exceptions;

/**
 *
 * @author krezos
 */
public class DBConnectionException extends Exception {

    /**
     * Creates a new instance of <code>DBConnectionException</code> without detail message.
     */
    public DBConnectionException() {
    }


    /**
     * Constructs an instance of <code>DBConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DBConnectionException(String msg) {
        super(msg);
    }
}
