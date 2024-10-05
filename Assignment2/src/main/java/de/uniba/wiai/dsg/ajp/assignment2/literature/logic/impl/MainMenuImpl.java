package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.impl;

import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.LiteratureDatabaseException;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.MainService;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model.Database;
import de.uniba.wiai.dsg.ajp.assignment2.literature.ui.ConsoleHelper;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.DatabaseMenu;
import de.uniba.wiai.dsg.ajp.assignment2.literature.logic.MainMenu;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

public class MainMenuImpl implements MainMenu {

    private static final int EXIT = 0;
    private static final int LOADVALIDATE = 1;
    private static final int CREATENEWDB = 2;
    private static final List<Integer> allowedOptions = Arrays.asList(0, 1, 2);
    private BufferedReader reader;
    private MainService mainService;
    private boolean exit;


    public MainMenuImpl() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        exit = false;
    }

    private void displayMainMenu() throws IOException {
        System.out.println("\n");
        System.out.println("Main Menu");
        System.out.printf("(%d) Load and Validate Literature Database%n", LOADVALIDATE);
        System.out.printf("(%d) Create New Literature Database%n", CREATENEWDB);
        System.out.printf("(%d) Exit System%n", EXIT);
    }

    @Override
    public void startReadEvalPrint() {
        while (!exit) {
            try {
                displayMainMenu();
            } catch (IOException e) {
                System.err.println("I wasn't able to display the main menu");
                System.out.println("Exiting program.");
                exit();
            }

            int option = readInput();
            evalOption(option);
        }
    }

    private int readInput() {
        int result;
        try {
            String input = reader.readLine();
            result = Integer.parseInt(input);
            if (!allowedOptions.contains(result)){
                System.out.println("Sorry that is no valid input number. Select 0, 1 or 2. Please try again:");
                startReadEvalPrint();
            }
            return result;
        } catch (IOException e) {
            // System.out.println(e.getMessage());
            System.out.println("Sorry, that's not a valid input. You can start again");
            startReadEvalPrint();
        } catch (NumberFormatException e) {
            // System.out.println(e.getMessage());
            System.out.println("Sorry, that's not a number. You can start again");
            startReadEvalPrint();
        }
        return EXIT; // if anything else fails...
    }

    private void evalOption(int option) {
        switch (option) {
            case EXIT:
                exit();
                break;
            case LOADVALIDATE:
                loadvalidate();
                break;
            case CREATENEWDB:
                create();
                break;
            default:
                exit();
        }
    }


    private void exit() {
        exit = true;
        System.out.println("Program shutdown. Goodbye.");
        System.exit(0);
        // even more gracefully possible?
    }


    public void loadvalidate() {
        ConsoleHelper console = ConsoleHelper.build();
        MainService mainService = new MainServiceImpl();
        try {
            String path = inputPath(console); // collect path from user & validate
            if ( path == null ||path.isEmpty()) {
                System.out.println("Hey you provided an empty path");
            } else {
                try {
                    mainService.validate(path);
                } catch (LiteratureDatabaseException e) {
                    System.out.println(e.getMessage());
                    startReadEvalPrint();
                }
                try {
                    DatabaseService handler = mainService.load(path);
                    DatabaseMenu dbMenu = new DBMenuImpl(handler);
                    dbMenu.startDBMenu();
                } catch (LiteratureDatabaseException e) {
                    System.out.println(e.getMessage());
                    startReadEvalPrint();
                }
            }
        } catch (LiteratureDatabaseException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    // create
    public void create() {
        MainService mainService = new MainServiceImpl();
        try {
            DatabaseService handler = mainService.create();
            DatabaseMenu dbMenu = new DBMenuImpl(handler);
            dbMenu.startDBMenu();
        } catch (LiteratureDatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    private String inputPath(ConsoleHelper console) throws LiteratureDatabaseException {
        try {
            String spath = console.askString("Please enter the file you want to load and validate \n (Note: upper- or lowercases doesn't make a differenc).");
            // does a file exist with this Path?
            if (spath == null || spath.trim().isEmpty()) {
                throw new LiteratureDatabaseException("Hey, you provided an empty path!");
            }
            Path path = new File(spath).toPath();

            if (Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path)) {
                System.out.println("Cool, this file really exists and we can read it.");
                return spath;
            } else {
                throw new LiteratureDatabaseException("Check for a typo in the path you provided: " + spath);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new LiteratureDatabaseException("Let's start again", e);
        }
    }
}
