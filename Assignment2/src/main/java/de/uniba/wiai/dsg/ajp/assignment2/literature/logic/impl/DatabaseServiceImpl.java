package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.LiteratureDatabaseException;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Author;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Database;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Publication;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.PublicationType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.logic.ValidationHelper.*;

public class DatabaseServiceImpl implements DatabaseService {

	/**
	 * Adds a new publication.
	 *
	 * @param title         the title of the publication. Must not be empty or null.
	 * @param yearPublished the year the publication was first published. Must not
	 *                      be negative.
	 * @param type          the type of the publication. Must not be null.
	 * @param authorIDs       Must not be null; must at least contain one author id;
	 *                      must not contain duplicate ids of authors
	 * @param id            the id of the publication. Must not be null or empty.
	 *                      Must be a valid id and unique within the current
	 *                      database.
	 * @throws LiteratureDatabaseException if any of the above preconditions are not
	 *                                     met
	 */
	@Override
	public void addPublication(String title, int yearPublished, PublicationType type, List<String> authorIDs, String id)
			throws LiteratureDatabaseException {
			// great chance every parameter is valid>> checking in caller.
		if (!isDBready()) {
			throw new LiteratureDatabaseException("# Somehow there's a problem with the database");
		}

		if (authorIDs.size() == 1) {
			String wantedID = authorIDs.get(0);
			Author onlyAuthor = globalDBscope.findByID(wantedID);
			Publication publication = new Publication(id, title, yearPublished, type, onlyAuthor);
			globalDBscope.addPublication(publication);// and we're done: remember cross-ferencing!!!!
			onlyAuthor.addPublication(publication);
			System.out.println("# We successfully added the following publication: ");
			System.out.println(" # Title: " + publication.getTitle() + " # Year: " + publication.getYearPublished() + " by " + publication.getAuthors().get(0).getName());

		} else if (authorIDs.size() > 1) {
			String wantedID = authorIDs.get(0);
			Author firstAuthor = globalDBscope.findByID(wantedID);
			Publication publication = new Publication(id, title, yearPublished, type, firstAuthor); // add the others
			globalDBscope.addPublication(publication);
			int i = 1; // otherwise: duplicate
			do {
				String searchID = authorIDs.get(i);
				Author additionalA = globalDBscope.findByID(searchID);
				publication.addAuthor(additionalA);
				i++;
			} while (i < authorIDs.size());
			// now on the side of the authors? >> access them from jus tcreated publication
			List<Author> updateMe = publication.getAuthors();
			// critical: do the cross-referncing now
			if (updateMe == null || updateMe.isEmpty()) {
				throw new LiteratureDatabaseException("# Somehow a problem when updating the authors occurred");
			}
			for (int k = 0; k < updateMe.size(); k++) {
				Author current = updateMe.get(k);
				current.addPublication(publication);
			}

			System.out.println(" # Title: " + publication.getTitle() + " # Year: " + publication.getYearPublished() + " by First Author " + publication.getAuthors().get(0).getName());
			System.out.println("We now added the publication to the database and also updated all the authors");

		} else {
			throw new LiteratureDatabaseException("A problem occurred");
		}
		showDuplicatePubls(title);
	}


	/**
	 * Removes an existing publication identified by its ID.
	 *
	 * @param id the ID of the publication to be removed. Must not be empty or null.
	 *           Must be a valid ID.
	 * @throws LiteratureDatabaseException if any of the above preconditions are not
	 *                                     met
	 */
	@Override
	public void removePublicationByID(String id) throws LiteratureDatabaseException {
		if (!isDBready()) {
			throw new LiteratureDatabaseException("# Somehow there's a problem with the database");
		}
		if (!isValidPubID(id)) { // validate logically
			throw new LiteratureDatabaseException("# A Publication with this ID isn't part of the dataset");
		} else {
			Publication removeMe = globalDBscope.findPublByID(id);
			if (removeMe == null) {
				throw new LiteratureDatabaseException("# Woops, somehow there is no publication with that ID in our database");
			} else {
				List<Author> removeHere = new ArrayList<>(); // construct, then initialize
				removeHere = removeMe.getAuthors(); // destroy references there

				for (int j = 0; j < removeHere.size(); j++) {
				Author author = removeHere.get(j);
				List<Publication> myPubls = author.getPublications();

				if (myPubls == null || !myPubls.contains(removeMe)) {
				throw new LiteratureDatabaseException("# Strange, there seems to be a data integrity problem");
				} else {
					for (int k = 0; k < myPubls.size(); k++) {
					Publication current = myPubls.get(k);
					if (removeMe.getId().equals(current.getId())) {
					myPubls.remove(current); // finally done dereferencing?
					}
					}
				}
			}
			}
			globalDBscope.removePublic(removeMe);
			System.out.println("# We're finally done with the safe delete. Authors also got updated.");
			}
	}



	/**
	 * Removes an existing author identified by its ID.
	 *
	 * @param id the ID of the author to be removed. Must not be empty or null. Must
	 *           be a valid ID.
	 * @throws LiteratureDatabaseException if any of the above preconditions are not
	 *                                     met
	 */
	@Override
	public void removeAuthorByID(String id) throws LiteratureDatabaseException {
		if (!isDBready() || globalDBscope.getAuthorlistSize() == 0){
			throw new LiteratureDatabaseException("there's a problem with the database. It's empty.");
		} // just have that covered
		Author removeMe = globalDBscope.findByID(id);
		if (removeMe == null || !isValidAuthorID(id)) {
			throw new LiteratureDatabaseException("The Author you wanted to delete doesn't exist in the database");
		} else { //  match found?
			List<Publication> deleteThisAuthorHere = new ArrayList<>(); // first create, then initialize!
			deleteThisAuthorHere = removeMe.getPublications();
			if (deleteThisAuthorHere == null) {
				throw new LiteratureDatabaseException("strange, an author who's publication list is null"); // shouldn't happen
			} else if (deleteThisAuthorHere.isEmpty()) { // no dependencies
				globalDBscope.removeAuthor(removeMe);
				System.out.println("We succeeded in safely deleting the author from the database");
			}
			else {
				for (int i = 0; i < deleteThisAuthorHere.size(); i++) {
					Publication current = deleteThisAuthorHere.get(i);
					if (current == null) {
						throw new LiteratureDatabaseException("Error in Database: Strange, a publication which is null");
					} else {
						Author removeFromPub = current.findAuthorByID(id);
						if (removeMe != removeFromPub) { // final circular check: author above should be the same as coming from publication itself
							throw new LiteratureDatabaseException("Data integrity is at risk. We won't proceed.");
						} else {
							current.deleteAuthor(removeFromPub);
							globalDBscope.removeAuthor(removeMe);
						}
					}
				}
				System.out.println("We succeeded in safely deleting the author from the entire database");
			}
		}
	}


	/**
	 * Adds a new author.
	 *
	 * @param name  the name of the author. Must not be null or empty.
	 * @param email the email address of the author. Must not be null or empty. Must
	 *              be a valid email address
	 * @param id    the id of the author. Must not be null or empty. Must be a valid
	 *              and unique id.
	 *
	 * @throws LiteratureDatabaseException if any of the above preconditions are not
	 *                                     met
	 */
	@Override
	public void addAuthor(String name, String email, String id) throws LiteratureDatabaseException {
		// some validation happened already, but just on the format
		// now on the logical layer
		if (!isDBready()) { // should not happen, but never say never
			throw new LiteratureDatabaseException("The database wasn't created"); // kind like "break"
		}
		if (isValidAuthorID(id)) { // Valid means in this case: such an author with this iD exists
			throw new LiteratureDatabaseException("An author with this ID already exists: " + id +
					" \n >> Enter something else.");
		} else {
			Author newAuthor = new Author(name, email, id);
			globalDBscope.addAuthor(newAuthor);
			System.out.println("We added this author:");
			System.out.println(newAuthor.getName() + " #Email: "
					+ newAuthor.getEmail() + " #Id: " + newAuthor.getId());
		// check duplicate
			showDuplicateAuthors(name);
		}
	}


	/**
	 * Gets a list of publications stored in the database.
	 *
	 * @return a list of publications
	 */
	@Override
	public List<Publication> getPublications() {
		return globalDBscope.getPublications();
	}

	@Override
	public List<Author> getAuthors() {
		return globalDBscope.getAuthors();
	}

	@Override
	public void clear() {
		globalDBscope.deleteAll();
		System.out.println("The database is now totally empty");
	}

	@Override
	public void printXMLToConsole() throws LiteratureDatabaseException {
		// didn't try it out yet
		if (!isDBready()) {
			throw new LiteratureDatabaseException("A problem occured while talking to the database");
		}
		try {
			JAXBContext context = JAXBContext.newInstance(Database.class);
			Marshaller marshaller = context.createMarshaller();
			System.out.println("\n\n");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //for pretty printing. as featured in AJP
			marshaller.marshal(globalDBscope, new PrintWriter(System.out));
			System.out.println("\n\n");
		} catch (jakarta.xml.bind.JAXBException j) {
			throw new LiteratureDatabaseException("We tried to print to the console, but a problem occurred");
		}
	}

	@Override
	public void saveXMLToFile(String path) throws LiteratureDatabaseException {
		if (!isDBready()){
			throw new LiteratureDatabaseException("A problem occured while talking to the database");
		}
		// some valitation
		if (path == null || path.isEmpty()) {
			throw new LiteratureDatabaseException("Hey, you provided an empty path");
		}
		if (!path.endsWith(".xml")) {
			throw new LiteratureDatabaseException("Sorry, we can only save in a file that ends with .xml");
		}
		// logically validate Path: duplicate?
		else if (!newPathCheckerOK(path)){
			throw new LiteratureDatabaseException("This path already exists. Give a new path or discard the old file. \n" +
					"## Remember: without a backup, life can be a cruel experience.");
		}
		else {
			/**
			Path newFilePath = Paths.get(path);
			try {
				Files.createFile(newFilePath);
			} catch (IOException e) {
				throw new LiteratureDatabaseException("A problem occurred when saving to this file");
			}
			 **/
			try {
				JAXBContext context = JAXBContext.newInstance(Database.class);
				Marshaller ms = context.createMarshaller();
				ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // make it look prettier
				ms.marshal(globalDBscope, new File(path));
				System.out.println("# We just created the new file" + path);
				System.out.println("# You should soon see it in the root folder of this project");
			} catch (jakarta.xml.bind.JAXBException j) {
				throw new LiteratureDatabaseException("Something went wrong when we tried to create the file");
			}
		}
	}
}