/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.derby;

/**
 *
 * @author EKARPIA_local
 */
public interface ISqlGenerator {

	//public String generateSqlDefinition() throws CodeGenerationException;

	String generateSqlDropDefinition() throws CodeGenerationException;

	String generateSqlCreateDefinition() throws CodeGenerationException;
}
