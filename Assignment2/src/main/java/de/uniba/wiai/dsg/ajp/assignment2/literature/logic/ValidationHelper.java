package de.uniba.wiai.dsg.ajp.assignment2.literature.logic;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Author;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Publication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

/**
 * Helps to validate IDs and email addresses.
 * 
 */
public class ValidationHelper {
	private static final String ZERO_OR_MORE = "*";
	private static final String ONE_OR_MORE = "+";
	private static final String ANY_LETTER_OR_CONSTANT = "[a-zA-Z0-9]";
	private static final String START_WITH_LETTER = "[a-zA-Z]";
	private static final String VALID_NAME = "[a-zA-Z0-9._%-]" + ONE_OR_MORE;
	private static final String AT_SIGN = "[@]";
	private static final String VALID_DOMAIN = "[a-zA-Z0-9.-]" + ONE_OR_MORE;
	private static final String DOT = "[.]";
	private static final String COUNTRY_CODE = "[a-zA-Z]{2,4}";

	// year is roughly modelled. just as 4 digits.
	private static final String DIGITS_YEAR = "^\\d{4}";
	private static final String AUTHOR_NAME = "[a-zA-Z\\s\\p{Z}]{1,25}";
	private static final Pattern VALID_ID_REGEX = Pattern
			.compile(START_WITH_LETTER + ANY_LETTER_OR_CONSTANT + ZERO_OR_MORE);
	private static final Pattern VALID_EMAIL_REGEX = Pattern
			.compile(VALID_NAME + AT_SIGN + VALID_DOMAIN + DOT + COUNTRY_CODE);

	private static final Pattern VALID_NAME_REGEX = Pattern.compile(VALID_NAME);
	private static final Pattern VALID_TITLE_REGEX = Pattern.compile(VALID_NAME);
	private static final Pattern VALID_YEAR_REGEX = Pattern.compile(DIGITS_YEAR);

	private static final Pattern VALID_AUTHORNAME_REGEX = Pattern.compile(AUTHOR_NAME);


	/**
	 * Validates an ID.
	 * 
	 * An ID has to start with a letter followed by zero or more letters or numbers.
	 * 
	 * @param id the id to be checked. must not be null.
	 * @return true if the id is valid, false otherwise
	 * 
	 * @throws NullPointerException if id is null
	 */
	public static boolean isId(String id) {
		id = Objects.requireNonNull(id, "input must not be null");
		Matcher idMatcher = VALID_ID_REGEX.matcher(id);
		return idMatcher.matches();
	}

	/**
	 * Validates an email address.
	 * 
	 * This is a simple validation and does not conform to all correctness criteria
	 * for email addresses.
	 * 
	 * @param email the email to be validated. must not be null.
	 * @return true if the email is valid, false otherwise
	 * 
	 * @throws NullPointerException if email is null
	 */
	public static boolean isEmail(String email) {
		email = Objects.requireNonNull(email, "email passed is null");

		Matcher emailMatcher = VALID_EMAIL_REGEX.matcher(email);
		return emailMatcher.matches();
	}

	public static boolean isName(String name) {
		name = Objects.requireNonNull(name, "name you gave is null");
		Matcher nameMatcher = VALID_NAME_REGEX.matcher(name);
		return nameMatcher.matches();
	}

	public static boolean isAuthorName(String name) {
		name = Objects.requireNonNull(name, "name you gave is null");
		Matcher nameMatcher = VALID_AUTHORNAME_REGEX.matcher(name);
		return nameMatcher.matches();
	}

// maybe move these methods to another class, they're special.
// they actually search the DB, they not only compare patterns

	public static boolean isValidAuthorID(String authorID) { // means: an author with this ID exists in the DB
		if (globalDBscope == null || globalDBscope.getAuthors() == null)
		{return false;}  // shouldn't happen. but just capture it.
		if (globalDBscope.getAuthorlistSize() == 1){
			return false; // no need to ask further
		}
		List<Author> trim2One = new ArrayList<>();
		// DB ready? >> search whole DB
		for (int i = 0; i < globalDBscope.getAuthorlistSize(); i++) {
			Author current = globalDBscope.getAuthorByIndex(i);
			if (authorID.equals(current.getId())) {
				trim2One.add(current);
			}
		}
		if (trim2One.size() == 1 && trim2One.get(0) != null){
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValidPubID(String pubID) { // here valid means: there actually IS a pub with this ID
		List<Publication> trim2One = new ArrayList<>();
		if (!isDBready()){
			System.out.println("A global problem with the database happened");
		}
		// search whole DB
		if (globalDBscope.getPublistSize() == 0){
			return false; // no need to ask further
		}
		for (int i = 0; i < globalDBscope.getPublistSize(); i++) {
			Publication current;
			if (i == 0 && globalDBscope.getPublByIndex(i) == null || globalDBscope.getPublistSize() == 0) {
				return false; // should be the same: empty list, no publication element accessible
			} else if (globalDBscope.getPublistSize() > 0 || globalDBscope.getPublByIndex(i) != null) {
				current = globalDBscope.getPublByIndex(i);
				if (pubID.equals(current.getId())) { // now now can compare, as it's sure there's actually an element
					trim2One.add(current);
				}
			}
		} // end evaluation adter loop
		if (trim2One.size() == 0){
			return false; // no publications there, so cant be valid anyway
		} else if (trim2One.get(0) != null && trim2One.size() == 1){
			return true; // match found
		} else {
			return false;
		}
	}

	public static boolean isTitle(String title) {
		title = Objects.requireNonNull(title, "the title you provided is null");
		Matcher titleMatcher = VALID_TITLE_REGEX.matcher(title);
		return titleMatcher.matches();
	}

	public static boolean isYear(int year) {
		year = Objects.requireNonNull(year, "the year you provided is null");
		String yearString = Integer.toString(year);
		Matcher titleMatcher = VALID_YEAR_REGEX.matcher(yearString);
		return titleMatcher.matches();
	}


	// gets false as soon there's any invalid id in a list
	public static boolean isIDlist(List<String> ids) {
		for (int i = 0; i < ids.size(); i++) {
			if (!isId(ids.get(i))){
				return false;
			}
		}
		return true;
	}

	public static boolean isDBready(){
		if (globalDBscope != null && globalDBscope.getAuthors() != null &&  globalDBscope.getPublications() != null){
			return true;
		} else {
			return false;
		}
	}

	public static boolean newPathCheckerOK(String path){
		Path truePath = Path.of(path);
      		if (Files.exists(truePath)) {
				return false;
			}
			else {
				return true;
            }
	}

	public static void showDuplicateAuthors(String name){
		if (!isDBready()) {
			System.out.println("Somehow the database doesn't communicate");
			return;
		}
		List<Author> duplicates = new ArrayList<>();
		for (int i = 0; i < globalDBscope.getAuthorlistSize(); i++) {
			Author check = globalDBscope.getAuthorByIndex(i);
			if (name.equals(check.getName())){
				duplicates.add(check);
			}
		}
		if (duplicates.size() > 1) {
			System.out.println("\n ### We found something you might consider as relevant: \n " +
					"currently, there are several people with this name in the database: ");
			for (int i = 0; i < duplicates.size(); i++) {
				Author current = duplicates.get(i);
				System.out.println("#Name: " + current.getName() + " #Email: " + current.getEmail() + " Id: " + current.getId());
			}
		System.out.println("If this is what you want, safely ignore this information");
		System.out.println("Otherwise I suggest to delete the duplicates");
		}

	}



	public static void showDuplicatePubls(String title){
		if (!isDBready()) {
			System.out.println("Somehow the database doesn't communicate");
			return;
		}
		List<Publication> duplicates = new ArrayList<>();
		for (int i = 0; i < globalDBscope.getPublistSize(); i++) {
			Publication check = globalDBscope.getPublByIndex(i);
			if (title.equals(check.getTitle())){
				duplicates.add(check);
			}
		}
		if (duplicates.size() > 1) {
			System.out.println("\n ### We found something you might consider as relevant: \n " +
					"currently, there are several publications with this name in the database: ");
			for (int i = 0; i < duplicates.size(); i++) {
				Publication current = duplicates.get(i);
				System.out.println("#Title: " + current.getTitle() + " #Email: " +
						current.getType() + " #Year published: " + Integer.toString(current.getYearPublished()) + " # Id: " + current.getId());
			}
			System.out.println("If this is what you want, safely ignore this information");
			System.out.println("Otherwise I suggest to delete the duplicates");
		}

	}


	}
