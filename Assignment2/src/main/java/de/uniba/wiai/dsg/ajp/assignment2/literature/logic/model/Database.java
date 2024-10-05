package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.LiteratureDatabaseException;
import jakarta.xml.bind.annotation.*;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

@XmlRootElement
@XmlType(propOrder = {"authors", "publications"})

public class Database {

	private List<Author> authors = new LinkedList<>();
	private List<Publication> publications = new LinkedList<>();

	public Database() {
		super();
	}

	@XmlElement(name = "author", required= false)
	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	@XmlElement(name="publication", required= false)
	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	// additional setters/ remover >> modifiers
	public void addAuthor(Author a) {this.authors.add(a);}
	public void addPublication(Publication p) {this.publications.add(p);}

	public void deleteAll() {
		this.authors.clear();
		this.publications.clear();
	}

	// discard elements from outside
	public void removePublic(Publication pubID) {
		publications.remove(pubID);
	}

	public void removeAuthor(Author authorID) {
		authors.remove(authorID);
	}

	// utilities for iterating
	// report size for iterating from outside
	// @XmlTransient // tell JAXB to ignore this
	public int getAuthorlistSize(){
		int size = authors.size();
		return size;
	}

	// @XmlTransient
	public int getPublistSize(){
		int size = publications.size();
		return size;
	}

	// for iteraring authors
	//@XmlTransient
	public Author getAuthorByIndex(int index){
		Author author = authors.get(index);
		return author;
	}

	//@XmlTransient
	public Publication getPublByIndex(int index){
		Publication publ = publications.get(index);
		return publ;
	}

	public Author findByID(String id) throws LiteratureDatabaseException{
		for (int i = 0; i < this.getAuthorlistSize(); i++) {
			Author candidate = this.getAuthorByIndex(i);
			if (id.equals(candidate.getId())) { // we've eradicated duplicates upstream
				Author returnMe = candidate;
				return returnMe;
			}
		} // in any other case
		throw new LiteratureDatabaseException("It shouldn't happen, but there was no author with this id");
	}

	public Publication findPublByID(String id) throws LiteratureDatabaseException{
		for (int i = 0; i < this.getAuthorlistSize(); i++) {
			Publication candidate = this.getPublByIndex(i);
			if (id.equals(candidate.getId())) { // we've eradicated duplicates upstream
				Publication returnMe = candidate;
				return returnMe;
			}
		}// in any other case
	throw new LiteratureDatabaseException("We couldn't find a publication with this id");
	}



}