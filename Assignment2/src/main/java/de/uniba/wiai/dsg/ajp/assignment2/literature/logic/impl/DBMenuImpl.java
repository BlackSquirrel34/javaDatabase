package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.impl;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.*;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Author;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Publication;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.PublicationType;
import de.uniba.wiai.dsg.ajp.assignment2.literature.ui.ConsoleHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.logic.ValidationHelper.*;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.logic.ValidationHelper.isAuthorName;

public class DBMenuImpl implements DatabaseMenu {

    private static final int ADDAUTHOR = 1;
    private static final int REMOVEAUTHOR = 2;
    private static final int ADDPUB = 3;
    private static final int REMOVEPUB = 4;
    private static final int LISTAUTHORS = 5;
    private static final int LISTPUBS = 6;
    private static final int PRINTXML = 7;
    private static final int SAVEXML = 8;
    private static final int BACKMAIN = 0;
    private static final List<Integer> allowedOptions = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
    private boolean end;
    private BufferedReader reader;
    private DatabaseService handler;

    public DBMenuImpl(DatabaseService handler) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.end = false;
        this.handler = handler;
    }

    @Override
    public void startDBMenu() {
        while (!end) {
            try {
                displayDBMenu();
            } catch (IOException e) {
                System.err.println("IOException. Couldn't display db menu");
                System.out.println("I direct you back to main menu");
                back2Main();
            }
            int option = readUser();
            evalOption(option);
        }
    }

    private void displayDBMenu() throws IOException {
        System.out.println("\n");
        System.out.println("Database Menu");
        System.out.printf("(%d) Add Author%n", ADDAUTHOR);
        System.out.printf("(%d) Remove Author%n", REMOVEAUTHOR);
        System.out.printf("(%d) Add Publication%n", ADDPUB);
        System.out.printf("(%d) Remove Publication%n", REMOVEPUB);
        System.out.printf("(%d) List Authors%n", LISTAUTHORS);
        System.out.printf("(%d) List Publications%n", LISTPUBS);
        System.out.printf("(%d) Print XML on Console%n", PRINTXML);
        System.out.printf("(%d) Save XML to File%n", SAVEXML);
        System.out.printf("(%d) Back to main menu/ close without saving%n", BACKMAIN);
    }

    private int readUser() {
        int result = BACKMAIN; // kinda default behaviour
        try {
            String input = reader.readLine();
            result = Integer.parseInt(input);
            System.out.println("# Echo input: " + result); // todo: remove this
            if (!allowedOptions.contains(result)){
                System.out.println("# Sorry that is no valid input number. Select one between 0 and 8. Please try again:");
                return result;
            }
        } catch (IOException e) {
            System.out.println("# Could not read input, default value is used");
            startDBMenu();
        } catch (NumberFormatException e) {
            System.out.println("# We couldn't parse your input to a number. Please try again:");
            startDBMenu();
        }
        return result;
    }

    private void evalOption(int option) {
        switch (option) {
            case ADDAUTHOR:
                addAuthor();
                break;
            case REMOVEAUTHOR:
                removeAuthor();
                break;
            case ADDPUB:
                addPublication();
                break;
            case REMOVEPUB:
                removePublication();
                break;
            case LISTAUTHORS:
                listAuthors();
                break;
            case LISTPUBS:
                listPublications();
                break;
            case PRINTXML:
                printXML();
                break;
            case SAVEXML:
                saveXML();
                break;
            case BACKMAIN:
                back2Main();
                break;
            default:
                back2Main();
        }
    }

    public void addAuthor(){
        // obtain values as user input and validate.
        System.out.println("# You can now add a new author.");
        ConsoleHelper console = ConsoleHelper.build();
        try {
            String name = console.askString("# Please enter a name");
            String email = console.askString("Please enter an email");
            System.out.println("# Remember, a valid Id in this database follows this pattern:");
            System.out.println("# Letter + any letter or constant, zero or several of them");
            String id = console.askString("Please enter an id for the new author.");
            if (name == null || name.isEmpty() || email == null || email.isEmpty() || id == null || id.isEmpty()) {
                System.out.println("Hey, you provided empty input");
                return;
            }

            if (isAuthorName(name) && isEmail(email) && isId(id)) { // all fine? proceed
                try { // logical validation (check for duplicate id) happens in callee
                    handler.addAuthor(name, email, id);
                } catch (LiteratureDatabaseException e) {
                    System.out.println(e.getMessage());
                    System.out.println("# You can start from scratch");
                    return;
                }
            } else {
                System.out.println("# Please check for typos in your input. You typed:");
                System.out.println("# Name: " + name + "\n Email: " + email + "\n Id: " + id);
                System.out.println("# You can start all over");
                return; // steps outta method back to menu
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("# You can start all over");
            return;
        }
    }

    public void removeAuthor() { // obtain NAME of author from user >> usually humans don't remember arbitrary ids
        System.out.println("# You can now remove an author");
        ConsoleHelper console = ConsoleHelper.build();

        try {
            String removeNameInput = console.askString("Please enter the author's name whom you want to remove from the db: ");
            // validate input
            if (removeNameInput.isEmpty() || removeNameInput == null) {
                System.out.println("# You basically entered nothing. ");
                return;
            }
            if (!isAuthorName(removeNameInput)) { // Just pattern checking, not logically
                System.out.println("# Please check your input. You can start over from the Database Menu");
                System.out.println("# You entered this name: " + removeNameInput);
                return; // no need for further processing if that fails
            } else {
                try { // logically checking
                    String id = getIDofName(removeNameInput);
                    if (id == null || id.isEmpty()) { // no need to proceed if we can't find anything.
                        System.out.println("# Seems like you provided no input");
                        return;
                    } else {
                        try {
                            handler.removeAuthorByID(id);
                        } catch (LiteratureDatabaseException e) {
                            System.out.println(e.getMessage());
                            System.out.println("# I'll direct you back to the Database Menu");
                            return;
                        }
                    }
                } catch (LiteratureDatabaseException e) { //might come from getIDofName
                    System.out.println(e.getMessage());
                    System.out.println("# I'll direct you back to the Database Menu");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("# You can start all over");
        } finally {
            return;
        }
    }

    public void addPublication() {
        ConsoleHelper console = ConsoleHelper.build();
        System.out.println("# You can now enter the new publication");
        String title;
        int yearPublished;
        PublicationType type;
        List<String> authorIDs = new ArrayList<>();
        String id;

        try {
             title = console.askString("# Please enter the Title");
             if (title == null || title.isEmpty()) {
                 System.out.println("# Hey, you entered an empty title. Please start again");
                 return;
             }
             yearPublished = console.askInteger("# Please enter the year.\n" +
                        "# Only a number between 1000 and the current year (included) will be accepted");
             if (yearPublished < 1000) { // 0 = default for unteger
                 System.out.println("# Hey, that can't be added to the database");
                 System.out.println("# I think they didn't even have books back then");
                 return;
            } else if (yearPublished > Year.now().getValue()){
                 System.out.println("# You can't add a publication from the future");
                 return;
            } else if (!isYear(yearPublished)){
                 System.out.println("# Sorry that's not a year");
                 return;
             }
             type = wrapAskType(console);
            if (type == null) {
                System.out.println("# Hey, you entered an empty type. Please start again");
                return;
            }

             authorIDs = wrapAskAuthors(console);
             if (authorIDs == null || authorIDs.isEmpty()){
                 System.out.println("# We couldn't parse your input into something useful, sorry");
             }
             id = wrapAskID(console);


             // we've made it this far? then proceed!
                try {
                    handler.addPublication(title, yearPublished, type, authorIDs, id);
                } catch (LiteratureDatabaseException e) {
                    System.out.println(e.getMessage());
                }

        } catch (LiteratureDatabaseException e) {
                System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException illegal){
            System.out.println(illegal.getMessage());
        }
    }

    public void removePublication() {
        System.out.println("# You can now remove a publication.");
        ConsoleHelper console = ConsoleHelper.build();
        try {
            String removeTitleInput = console.askString("Please enter the name of the publication you want to remove: ");
            // validate input
            if (removeTitleInput.isEmpty() || removeTitleInput == null) {
                System.out.println("# You basically entered nothing. ");
                return;
            }
          // optionally regex-check if its a title
            // logically checking: identify publication by name
                try { // logically checking
                    String id = getIDofTitle(removeTitleInput);
                    if (id == null || id.isEmpty()) { // no need to proceed if we can't find anything.
                        System.out.println("# Seems like you provided no input");
                        return;
                    } else if (ValidationHelper.isId(id) && ValidationHelper.isValidPubID(id)){
                        try {
                            handler.removePublicationByID(id);
                        } catch (LiteratureDatabaseException e) {
                            System.out.println(e.getMessage());
                            System.out.println("# I'll direct you back to the Database Menu");
                            return;
                        }
                    }
                } catch (LiteratureDatabaseException e) { //might come from getIDofName
                    System.out.println(e.getMessage());
                    System.out.println("# I'll direct you back to the Database Menu");
                }
        } catch (IOException iox) {
            System.out.println(iox.getMessage());
        } finally {
            return;
        }
    }

    public void listAuthors() {
        System.out.println("# Here's the overview of authors in this DB:");
        List<Author> allAuthors = handler.getAuthors();
        for (int i = 0; i < allAuthors.size(); i++) {
            Author current = allAuthors.get(i);
            System.out.println(" # Name: " + current.getName() + " # Mail: " + current.getEmail());
            if (current.getPublications().size() > 0) {
                System.out.println("    # Highlighted Publication: " + current.getPublications().get(0).getTitle());
            }
        }
        System.out.println("# That's all there is at the moment. \n");
    }

    public void listPublications() {
        System.out.println("# Here's an overview about the publications:");
        List<Publication> allPublications = handler.getPublications();
        for (int i = 0; i < allPublications.size(); i++) {
            Publication current = allPublications.get(i);
            System.out.println(" # Title: " + current.getTitle() + " # Year: " +
                    current.getYearPublished() + " # First Author: " + current.getAuthors().get(0).getName());
        }
        System.out.println("# That's all there is at the moment. \n");
    }

    public void printXML() {
        System.out.println("# Printing started");
        try {
            handler.printXMLToConsole();
        } catch (LiteratureDatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveXML() {
        String path;
        ConsoleHelper console = ConsoleHelper.build();
        try {
            path = console.askString("# Where do you want to save? Please enter a path");
            try {
                handler.saveXMLToFile(path);
            } catch (LiteratureDatabaseException e) {
                System.out.println(e.getMessage());
                System.out.println("# Let's start all over. No worries, what you've entered so far is still kept in memory.");
                return;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("# You can try again");
            return;
        }
    }

    public void back2Main() {
        MainMenuImpl main = new MainMenuImpl();
        main.startReadEvalPrint();
    }

    // helper methods just for internal use
    private Author disambigAuthor(List<Author> twins)throws LiteratureDatabaseException{ // assume there where several authors with same name
        System.out.println("# I'll display them along with their ids and Emails so you can chose an id");
        List<String> selectIds = new ArrayList<>();
        for (int i = 0; i < twins.size(); i++) {
            String oneID = twins.get(i).getId();
            selectIds.add(oneID); // for validation, see below
            System.out.println(twins.get(i).getName() + " #Email: " + twins.get(i).getEmail() + " #ID: " + twins.get(i).getId());
        }
        ConsoleHelper console = ConsoleHelper.build();
        String id;
        int counter = 0;
        do {
            counter++;
            try {
                id = console.askString("# Please enter one of the ids from above");
                if (id == null || id.isEmpty()) {
                    throw new LiteratureDatabaseException("# Sorry, that's not an input");
                }
            } catch (IOException e) {
                throw new LiteratureDatabaseException("# An error occurred when we tried to parse your input", e);
            }
            if (counter > 4) {
                throw new LiteratureDatabaseException("# Too many times failed. You can start over.");
            }
        } while (!selectIds.contains(id)); // so we get several chances
        Author returnMe = globalDBscope.findByID(id);
        if (returnMe != null) {
            return returnMe;
        } else {
            throw new LiteratureDatabaseException("We couldn't disambiguify the author");
        }
    }

    // todo: under construction
    private Publication disambigPublication(List<Publication> twins) throws LiteratureDatabaseException{
        System.out.println("# There are several publications with that name in the database");
        System.out.println("# I'll display them along with their id and year so you can chose them by id");
        List<String> selectIds = new ArrayList<>();
        for (int i = 0; i < twins.size(); i++) {
            String oneID = twins.get(i).getId();
            selectIds.add(oneID); // for validation, see below
            System.out.println(twins.get(i).getTitle() + twins.get(i).getYearPublished() + " ID: " + twins.get(i).getId());
        }
        ConsoleHelper console = ConsoleHelper.build();
        try {
            String id = console.askString("# Now enter one of these ids");
            if (!selectIds.contains(id) || id.isEmpty() || id == null)
            {
                throw new LiteratureDatabaseException("# We couldn't disambiguify the publication from your input");
            } else {
                Publication returnMe = globalDBscope.findPublByID(id);
                return returnMe;
            }
        } catch (IOException e) {
            System.out.println("# An error occurred when we tried to parse your input");
            throw new LiteratureDatabaseException("# An error occurred when we tried to parse your input", e);
        }
    }

    private String getIDofName(String removeNameInput) throws LiteratureDatabaseException{
        String uniqueID;
        Author findMe;
        List<Author> candidates = new ArrayList<>();// be prepared for several people iwth same name!!
        if (!isDBready() || globalDBscope.getAuthorlistSize() == 0) {
            throw new LiteratureDatabaseException("# Somehow the database wasn't created, or there are no authors yet, " +
                    "so we couldn't search for any author");
            // throw new means also break/ exit/ return. Under normal circumstances, this wouldn't happen but who knows...
        }

        System.out.println("# ***" + removeNameInput + "*** We're now searching the database for corresponding author(s)");
        for (int i = 0; i < globalDBscope.getAuthorlistSize(); i++) {
            Author current = globalDBscope.getAuthorByIndex(i);
            if (removeNameInput.equals(current.getName())) {
                candidates.add(current);
            } // no match? just pass
        }
        // looked at everything and still no match? Tell user.
        if (candidates == null || candidates.isEmpty()) {
            throw new LiteratureDatabaseException("# There is no person with this name in the Database (yet). Check for a typo: "  + removeNameInput);
        }

        if (candidates.size() > 1) {
            System.out.println("# There are several people with this name in the Database");
            try {
                Author uniqueMe = disambigAuthor(candidates);
                uniqueID = uniqueMe.getId();
                return uniqueID;
            } catch (LiteratureDatabaseException e) {
                System.out.println(e.getMessage());
                throw new LiteratureDatabaseException("# I'll direct you back to the Database Menu", e);
            }
        } else if (candidates.size() == 1 && candidates.get(0) != null) { // wohoo name was unique!
            System.out.println("# Wohoo the name was an unique identifier");
            Author thisMe = candidates.get(0);
            uniqueID = thisMe.getId();
            return uniqueID;
        } else { // literally anything else went wrong
            throw new LiteratureDatabaseException("# We couldn't find an ID to that name");
        }
    }

    private String getIDofTitle(String removeTitleInput) throws LiteratureDatabaseException{
        String uniqueID;
        Publication findMe;
        List<Publication> candidates = new ArrayList<>();// might also be several publs with same name!!
        if (!isDBready() || globalDBscope.getPublistSize() == 0) {
            throw new LiteratureDatabaseException("# Somehow the database wasn't created, or there are no publication yet, " +
                    "so we couldn't search for any publication");
        }

        System.out.println("# ***" + removeTitleInput + "*** We're now searching the database for corresponding publication(s)");
        for (int i = 0; i < globalDBscope.getPublistSize(); i++) {
            Publication current = globalDBscope.getPublByIndex(i);
            if (removeTitleInput.equals(current.getTitle())) {
                candidates.add(current);
            } // no match? just pass
        }
        // looked at everything and still no match? Tell user.
        if (candidates == null || candidates.isEmpty()) {
            throw new LiteratureDatabaseException("# There is no publication with this name in the Database (yet). Check for a typo: "  +
                    removeTitleInput);
        }

        if (candidates.size() > 1) {
            try {
                Publication uniqueMe = disambigPublication(candidates);
                uniqueID = uniqueMe.getId();
                return uniqueID;
            } catch (LiteratureDatabaseException e) {
                throw new LiteratureDatabaseException(e.getMessage(), e);
            }
        } else if (candidates.size() == 1 && candidates.get(0) != null) { // wohoo name was unique!
            System.out.println("# Wohoo the name was an unique identifier");
            Publication thisMe = candidates.get(0);
            uniqueID = thisMe.getId();
            return uniqueID;
        } else { // literally anything else went wrong
            throw new LiteratureDatabaseException("# We couldn't find an ID to that name");
        }
    }

    private PublicationType wrapAskType(ConsoleHelper console) throws LiteratureDatabaseException {
        System.out.println("For the publication type, only the following options are accepted:");
        System.out.println("<ARTICLE> or <TECHREPORT> or <BOOK> or " +
                "<MASTERTHESIS> or <PHDTHESIS> or <INPROCEEDINGS>" );
        String input;
            try {
                input = console.askString("# Please chose a type: ");
            } catch (IOException e) {
                throw new LiteratureDatabaseException("# We couldn't get ready for input", e);
            }
            try {
                PublicationType type = PublicationType.valueOf(input.toUpperCase());
                return type;
            } catch (IllegalArgumentException illegal) {
                throw new LiteratureDatabaseException("# Please check your spelling: " + input, illegal);
            }
    }


    private List<String> wrapAskAuthors(ConsoleHelper console) throws LiteratureDatabaseException{
        List<String> authorIDs = new ArrayList<>();
            // people remember names, not ids...
            String authorNamesUnparsed;
            int counter = 0;
            while (counter < 4) {
                counter++;
            try {
                authorNamesUnparsed = console.askString("# Please enter the name of all authors for  this publication.\n" +
                        "# Note: Please separate names of different authors with commas, but  NO whitespace after the comma.");
                if (authorNamesUnparsed == null || authorNamesUnparsed.isEmpty()){
                    throw new LiteratureDatabaseException("# Sorry, it's important to enter at least one author");
                }
            } catch (IOException e) {
                throw new LiteratureDatabaseException("# Somehow we couldn't get ready for input", e);
            }
            List <String> authorNames = new ArrayList<>();
            if (!authorNamesUnparsed.contains(",")){
                System.out.println("# That looks like one author");
                authorNames.add(authorNamesUnparsed);

                String addID = getIDofName(authorNames.get(0));

                authorIDs.add(addID);
                return authorIDs;
            } else {
                // discovering the whitespace problem took like a full hour
                // ... only delete whitespace after comma, not all of them!!
                // String authorTrimNamesUnparsed = authorNamesUnparsed.replace( ",\\s+", "," );
                        // replaceAll("\\s+", "");
                // no regex found for just replacing whitespace after comma
                authorNames = Arrays.asList(authorNamesUnparsed.split(",", -1));
                for (int i = 0; i < authorNames.size(); i++) {
                    String currentName = authorNames.get(i);
                    try {
                        String addID = getIDofName(currentName);
                        if (addID != null) {
                            authorIDs.add(addID);
                        } else {
                            System.out.println("# Please first create this author: " + currentName);
                            System.out.println("# You can add the publication afterwards");
                            throw new LiteratureDatabaseException("# Seems like this author isn't in the database");
                        }
                    } catch (LiteratureDatabaseException e) {
                        System.out.println(e.getMessage());
                        throw new LiteratureDatabaseException("# Please first create this author: " + currentName +
                                "\n # You can add the publication afterwards.");
                    }
                }
                return authorIDs;
            }
        }
            throw new LiteratureDatabaseException("# We didn't succeed in in collecting the authors from the database");
    }

    private String wrapAskID(ConsoleHelper console) throws LiteratureDatabaseException{
        String id;
        System.out.println("# Note: In this database, valid IDs consist of letters");

            try {
                id = console.askString("# Please enter an ID for the new Publication:");
                if(id == null || id.isEmpty()) {
                    throw new LiteratureDatabaseException("# You entered an empty id.");
                }
                if (isValidPubID(id)) { // is validPubID means here: it actually exists > disallow to avoid duplicates
                    throw new LiteratureDatabaseException("# Sorry, that's not a valid ID. It's a duplicate.");
                }
                if (!isId(id)) {
                    throw new LiteratureDatabaseException("# Sorry, that's not the correct format for an id.");
                }
                return id;
            } catch (IOException e) {
                throw new LiteratureDatabaseException("# We somehow couldn't get ready for input", e);
            }
    }
}

