# Java Database Project 
We had to write a literature database program, for command-line based interaction. It was required to never let Exceptions reach the user, but keep a consistent and reasonable flow of interaction anyway. There also had to be two menus, a main menu and a submenu. 

#### The project is ready to be downloaded and run.
#### It implements a simple software for keeping a literature database in xml format.

- Lines of code given with boilerplate: 763
- Lines of code in final solution: 2250

### The menus had to offer the following options:

Main Menu:

- (1) Load and Validate Literature Database (2) Create New Literature Database
- (0) Exit System

Database Menu/ Submenu:
- (1) Add Author
- (2) Remove Author
- (3) Add Publication
- (4) Remove Publication
- (5) List Authors
- (6) List Publications
- (7) Print XML on Console
- (8) Save XML to File
- (0) Back to main menu / close without saving

### The Sub-tasks we had to implement were these:
- make the correct XML annotations for marshalling/ unmarshalling the file with JAXB
- generate a XML schema to validate files
- implement the logic for manipulating the database (see above) -implement the interface for the user

Code lines given/ added per class:

#### Given Code:  763

- 10 (main class)
+ 247 (ConsoleHelper)
+ 100 (DatabaseService Specification in comments: required to not be changed)
+ 28 (LiteratureDatabaseException)
+ 34 (MainService Specification in comments)
+ 62 (ValidationHelper)
+ 68 (DatabaseServiceImpl: empty method stubs) + 38 (MainServiceImpl: empty method stubs)
+ 64 (Author)
+ 31 (Database)
+ 73 (Publication)
+ 8 PublicationType


#### Final Code: 2250

18 (main class) (added: 3)
+ 250 (ConsoleHelper) (added: 3)
+ 100 (DatabaseService Spec. in comments: required to not be changed. Added: 0)
+ 28 (LiteratureDatabaseException) (added: 0)
+ 34 (MainService Spec. In comment: required to not be changed. Added: 0) + 242 
(ValidationHelper) (added: 180)
+ 295 (DatabaseServiceImpl) (added: 227)
+ 174 (MainServiceImpl) (added: 136)
+ 91 (Author) (added: 27)
+ 110 (Database) (added: 79)
+ 114 (Publication) (added: 41)
+ 16 PublicationType (aded: 8)
+ 5 (DatabaseMenu) (added: 5)
+ 6 (MainMenu) (added: 6)
+ 593 (DBMenuImpl) (added: 593) 
+ 174 (MainMenuImpl) (added: 174)