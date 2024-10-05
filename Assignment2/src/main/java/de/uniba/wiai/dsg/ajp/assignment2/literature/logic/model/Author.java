package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model;

import jakarta.xml.bind.annotation.*;
// import jakarta.xml.bind.annotation.XmlAttribute;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
// easteregg.
import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

@XmlType(propOrder = {"email", "id", "name", "publications"})
// challenge: render publication list to several elements in a row
public class Author {

	private String id;
	private String name;
	private String email;
	private List<Publication> publications = new LinkedList<>();

	public Author() {
		super();
	} // why super instead of empty? try it out.

	// explicit author constructor with param's
	public Author(String name, String email, String id) {
		this.name = name;
		this.email = email;
		this.id = id;
		this.publications = new LinkedList<>(); // stays empty
	}

	@XmlElement(name = "name", required= true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "email", required= true)
	// why does java not recognize required??
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlID //(name = "id", required= true)
	@XmlElement(name = "id", required= true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlIDREF //
	@XmlElement(name = "publication", required= false)
	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	public void addPublication(Publication publication) {this.publications.add(publication);}
	public void removePublication(Publication pub) {this.publications.remove(pub);}

	@Override  // diese Klasse darf auch verändert werden. sonst nur hinzufügen erlaubt.
	public String toString() {
		return String.format("[%s] %s (%s) published %d publication(s): %s",
				id, name, email, publications.size(), getPublicationIds());
	}

	// XmlIDREF(required= false)
	// @XmlIDREF
	private String getPublicationIds() {
		StringJoiner result = new StringJoiner(", ");
		for (int i = 0; i < publications.size(); i++) {
			result.add(publications.get(i).getId());
		}
		return result.toString();
	}

}
