package de.uniba.wiai.dsg.ajp.assignment2.literature.logic;

public interface MainService {

	/**
	 * Validates the XML file identified by <code>path</code> with an XML Schema
	 * file.
	 * 
	 * @param path the path to the XML file to be validated
	 * @throws LiteratureDatabaseException if the file identified by
	 *                                     <code>path</code> is not valid
	 */
	void validate(String path) throws LiteratureDatabaseException;

	/**
	 * Loads a XML file by unmarshalling it into memory.
	 * 
	 * @param path the path of the XML file to be unmarshalled
	 * @return a service handle (<code>DatabasService</code>) for manipulating the
	 *         literature database
	 * @throws LiteratureDatabaseException
	 */
	DatabaseService load(String path) throws LiteratureDatabaseException;

	/**
	 * Creates a new and empty literature database.
	 * 
	 * @return a service handle (<code>DatabasService</code>) for manipulating the
	 *         literature database
	 * @throws LiteratureDatabaseException
	 */
	DatabaseService create() throws LiteratureDatabaseException;

}
