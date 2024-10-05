package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

public class Publication {

	private String id;
	private String title;
	private int yearPublished;
	private PublicationType type;
	private List<Author> authors = new LinkedList<>();

	public Publication() {
		super();
	} // why super instead of just empty?

	// explicit publication constructor with param's
	// how specify there can be one or several authors?
	// at the moment fixed to one, others might be added with setter
	public Publication(String id, String title, int yearPublished, PublicationType type, Author author) {
		this.id = id;
		this.title = title;
		this.yearPublished = yearPublished;
		this.type = type;
		this.authors = new LinkedList<>();
		authors.add(author);
	}

	@XmlAttribute(name = "type", required= true)
	public PublicationType getType() {
		return type;
	}

	public void setType(PublicationType type) {
		this.type = type;
	}

	@XmlAttribute(name = "yearPublished", required= true)
	public int getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}

	@XmlElement(name = "title", required= true)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlID
	@XmlElement(name = "id", required= true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlIDREF
	@XmlElement(name = "author", required= true) // wie sagen: optional mehrere??
	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	public void addAuthor(Author author) {this.authors.add(author);}

	public Author findAuthorByID(String id){
		Author returnMe;
		for (int i = 0; i < this.authors.size(); i++) {
			Author candidate = this.authors.get(i);
			if (id.equals(candidate.getId())){
				returnMe = candidate;
				return returnMe;
			}
		}
		return null;
	}
	public void deleteAuthor(Author author) {
		this.authors.remove(author);
	}

	@Override  // diese Klasse darf auch verändert werden. sonst nur hinzufügen erlaubt.
	public String toString() {
		return String.format(
				"[%s] The author(s) %s published %s as a %s in %d", id,
				getAuthorNames(), title, type, yearPublished);
	}

	// annotate this at all?
	// @XmlIDREF
	private String getAuthorNames() {
		StringJoiner result = new StringJoiner(", ");
		for (int i = 0; i < authors.size(); i++) {
			result.add(authors.get(i).getName());
		}
		return result.toString();
	}
}