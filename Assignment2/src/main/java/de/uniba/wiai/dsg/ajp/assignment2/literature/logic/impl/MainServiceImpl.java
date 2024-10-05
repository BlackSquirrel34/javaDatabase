package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.impl;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.LiteratureDatabaseException;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.MainService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Database;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseMenu;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.logic.ValidationHelper.isDBready;

public class MainServiceImpl implements MainService {

	/**
	 * Default constructor required for grading.
	 */
	public MainServiceImpl() {
		/*
		 * DO NOT REMOVE - REQUIRED FOR GRADING
		 *
		 * YOU CAN EXTEND IT BELOW THIS COMMENT
		 */
	}

	@Override
	public void validate(String path) throws LiteratureDatabaseException {
		// validation against which scheme: hard-coded
		// checking on userInputPath already took place
		if (path == null || path.isEmpty()) {
			throw new LiteratureDatabaseException("Hey, you provided an empty path!");
		}

		Path turnover = new File(path).toPath();
		File inputfile = turnover.toFile();

		System.out.println("We're now validating the file...");
		// generate SchemaFactory and Schema
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;

		try {
			schema = sf.newSchema(new File("schema1.xsd"));
		} catch (SAXException e) {
			throw new LiteratureDatabaseException("I somehow wasn't able to load the schema. Check if the .xsd File is where it should", e);
		}
		// Validator object zur Validierung erzeugen
		Validator validator = schema.newValidator(); // nutzt Klasse javax.xml.validation.Validator.

		try {
			validator.validate(new StreamSource(inputfile)); // if in spite of checks sth is wrong wit hthe path/ file, it'll appear here
		} catch (SAXException e) { // sth went wrong?
			System.out.println("The file you provided doesn't comply with the scheme.");
			throw new LiteratureDatabaseException("Maybe try another file or make some changes.", e);
		} catch (IOException e) {
			System.out.println("I direct you back to main menu");
			throw new LiteratureDatabaseException("A problem occurred when reading from the file", e);
		}
		System.out.println("Congrats, the file you provided just fits perfectly into the scheme");
	}

	@Override
	public DatabaseService load(String path) throws LiteratureDatabaseException {
		// validation and collecting has already happened
		globalDBscope = new Database();
		if (!isDBready()) {
			throw new LiteratureDatabaseException("There's a global problem with the database");
		}
		if (path == null || path.isEmpty()) {
			throw new LiteratureDatabaseException("Hey, you provided an empty path!");
		}

		System.out.println("We're now loading the file");
		Path dbFilePath = new File(path).toPath();
		File loadfile = dbFilePath.toFile();
		try {
			JAXBContext context = JAXBContext.newInstance(Database.class);
			Unmarshaller um = context.createUnmarshaller();
			globalDBscope = (Database) um.unmarshal(loadfile);
			DatabaseService handler = new DatabaseServiceImpl();
			// or some method of MainService that yields a new DatabaseService: load or create
			return handler;
		} catch (jakarta.xml.bind.JAXBException j) {
			throw new LiteratureDatabaseException("A problem occurred when loading the file", j);
		}
	}


	/**
	 * Creates a new and empty literature database.
	 *
	 * @return a service handle (<code>DatabasService</code>) for manipulating the
	 *         literature database
	 * @throws LiteratureDatabaseException
	 */
	@Override
	public DatabaseService create() throws LiteratureDatabaseException {
		DatabaseService handler = new DatabaseServiceImpl();
		globalDBscope = new Database();
		if (!isDBready()) {
			throw new LiteratureDatabaseException("Something went wrong when creating the new Database");
		} else if (globalDBscope.getAuthors().isEmpty() && globalDBscope.getPublications().isEmpty()) {
			System.out.println("An empty database is ready");
			return handler;
		} else {
			throw new LiteratureDatabaseException("A problem occurred with the database");
		}
	}

}
