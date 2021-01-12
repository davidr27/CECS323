import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Brandon Hernandez 
 * @author David Ramirez
 * @author George Vargas
 */
public class JDBCSampleSource {
    //  Database credentials
    static String USER;
    static String PASS;
    static String DBNAME;
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are
    //strings, but that won't always be the case.
    static final String displayFormat="%-20s%-60s%-30s%-10s%-10s\n";
    static final String displayFormat1="%-20s%-20s%-20s%-20s\n";
    static final String displayFormat2="%-40s%-50s%-20s%-20s\n";
    static final String displayFormat3="%-20s%-60s%-50s%-10s%-10s\n";
    static final String displayFormat4="%-27s%-8s%-10s%-23s%-45s%-19s%-35s\n";
    static final String displayFormat5="%-65s%-10s%-10s%-25s%-25s%-15s%-30s\n";
    static final String displayFormat6="%-20s%-7s%-7s%-17s%-15s%-6s%-13s%-25s%-38s%-20s%-20s\n";//Fixed according to computer's size
	static final String displayFormat7="%-50s%-50s%-40s%-10s%-10s\n";
	
	// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";
//            + "testdb;user=";
	
	/**
	 * Takes the input string and outputs "N/A" if the string is empty or null.
	 * @param input The string to be mapped.
	 * @return  Either the input string or "N/A" as appropriate.
	 */
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Name of the database (not the user account): ");
        DBNAME = in.nextLine();
        System.out.print("Database user name: ");
        USER = in.nextLine();
        System.out.print("Database password: ");
        PASS = in.nextLine();
        //Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME + ";user="+ USER + ";password=" + PASS;
        Connection conn = null; //Initialize the connection
        Statement stmt = null;  //Initialize the statement that we're using
        try {
           //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...\n");
            conn = DriverManager.getConnection(DB_URL);
			
			int userSelection = 0;
			do {
				printMenu();
				try {
					userSelection = in.nextInt();
					switch(userSelection){
						case 1:
							listGroups(conn);
							break;
						case 2:
							listPublishers(conn);
							break;
						case 3:
							listBooks(conn);
							break;
						case 4:
							System.out.println("\nEnter a writing group to display data for: ");
							in.nextLine();
							String group = in.nextLine();
							listGroupData(conn, group);
							break;
						case 5:
							System.out.println("\nEnter a publisher to display data for: ");
							in.nextLine();
							String pub = in.nextLine();
							listPublisherData(conn, pub);
							break;
						case 6:
							System.out.println("\nEnter a book to display data for: ");
							in.nextLine();
							String book = in.nextLine();
							
							System.out.println("\nEnter the book's writing group: ");
							String wGroup = in.nextLine();
							
							listBookData(conn, book, wGroup);
							break;
						case 7:
							System.out.println("\nEnter a book to insert: ");
							in.nextLine();
							String bookToInsert = in.nextLine();
							
							System.out.println("\nEnter a publisher already registered in the database:");
							String publisher = in.nextLine();
							
							System.out.println("\nEnter a writing group already registered in our database:");
							String writingGroup = in.nextLine();
							
							System.out.println("\nYear Published:");
							String year = in.nextLine();
							
							System.out.println("\nNumber Of Pages:");
							String pages = in.nextLine();
							
							insertBook(conn, bookToInsert, publisher, writingGroup, year, pages);
							break;
						case 8:
							System.out.println("\nEnter the new publisher's name: ");
							in.nextLine();
							String pubName = in.nextLine();

							System.out.println("\nEnter the new publisher's address:");
							String pubAddress = in.nextLine();

							System.out.println("\nEnter the new publisher's phone:");                                            
							String pubPhone = in.nextLine();

							System.out.println("\nEnter the new publisher's email:");
							String pubEmail = in.nextLine();

							System.out.println("\nEnter the publisher to be replaced:");
							String pubChange = in.nextLine();

							if(publisherExists(conn, pubChange)){
								if(insertPublisher(conn, pubName, pubAddress, pubPhone, pubEmail)){
									replacePublisher(conn, pubChange, pubName);
									removePublisher(conn, pubChange);
								} else {
									System.out.println("Replace failed.\n");
								}
							} else {
								System.out.println("\nReplace failed. The publisher to be replaced does not exist in the database\n");
							}
							break;
						case 9:
							System.out.println("\nEnter a book to remove: ");
							in.nextLine();
							String bookToRemove = in.nextLine();
							
							System.out.println("\nEnter the book's writing group: ");
							String wtgGroup = in.nextLine();
							
							removeBook(conn, bookToRemove, wtgGroup);
							break;
						default:
							System.out.println("\nPlease enter a valid number from the menu options\n");
					}
				} catch(InputMismatchException e){
					System.out.println("\nPlease enter a number\n");
					in.nextLine();
				}
			} while(userSelection != 10);
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }
	
	public static void printMenu(){
		System.out.println("Select an option from the following:");
		System.out.println("1. List all writing groups");
		System.out.println("2. List all publishers");
		System.out.println("3. List all books");
		System.out.println("4. List all data for a writing group");
		System.out.println("5. List all data for a publisher");
		System.out.println("6. List all data for a book");
		System.out.println("7. Insert a new book");
		System.out.println("8. Replace an existing publisher with a new one");
		System.out.println("9. Remove a book");
		System.out.println("10. Quit\n");
	}
	
	public static void listGroups(Connection conn){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM writingGroup";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat1, "GROUP NAME", "HEAD WRITER", "YEAR FORMED", "SUBJECT");
			while(rs.next()){
				String name = rs.getString("groupName");
				String head = rs.getString("headWriter");
				String year = Integer.toString(rs.getInt("yearFormed"));
				String subject = rs.getString("subject");
				System.out.printf(displayFormat1, dispNull(name), dispNull(head), dispNull(year), dispNull(subject));
			}
            System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* List all publishers from the publisher Table
	*/
	public static void listPublishers(Connection conn){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM publisher";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat2, "PUBLISHER NAME", "PUBLISHER ADDRESS", "PUBLISHER PHONE", "PUBLISHER EMAIL");
			while(rs.next()){
				String name = rs.getString("publisherName");
				String address = rs.getString("publisherAddress");
				String phone = rs.getString("publisherPhone");
				String subject = rs.getString("publisherEmail");
				System.out.printf(displayFormat2, dispNull(name), dispNull(address), dispNull(phone), dispNull(subject));
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* List all books from book Table
	*/
	public static void listBooks(Connection conn){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM book";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat3, "GROUP NAME", "BOOK TITLE", "PUBLISHER NAME", "YEAR", "PAGES");
			while(rs.next()){
				String name = rs.getString("groupName");
				String book = rs.getString("bookTitle");
				String publisher = rs.getString("publisherName");
				String year = Integer.toString(rs.getInt("yearPublished"));
				String pages = Integer.toString(rs.getInt("numberOfPages"));
				System.out.printf(displayFormat3, dispNull(name), dispNull(book), dispNull(publisher), dispNull(year), dispNull(pages));
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* List all the data from a specific Group
	*/
	public static void listGroupData(Connection conn, String group){
		Statement stmt = null;
		if(!groupExists(conn, group)){
			System.out.println("\nThe writing group " + group + " does not exist in the database\n");
			return;
		}
		try {
			stmt = conn.createStatement();
			//Writing group
			String sqlGetGroup = "SELECT * FROM writingGroup WHERE groupName = '" + group + "'";
			ResultSet rSet = stmt.executeQuery(sqlGetGroup);
			System.out.printf(displayFormat1, "GROUP NAME", "HEAD WRITER", "YEAR FORMED", "SUBJECT");
			while(rSet.next()){
				String name = rSet.getString("groupName");
				String head = rSet.getString("headWriter");
				String year = Integer.toString(rSet.getInt("yearFormed"));
				String subject = rSet.getString("subject");
				System.out.printf(displayFormat1, dispNull(name), dispNull(head), dispNull(year), dispNull(subject));
			}
			//Associated data
			System.out.println("\nThese are the books (and publisher associated with each book) written by the writing group " + group);
			String sql;
			sql = "SELECT bookTitle, book.publisherName, yearPublished, publisherAddress, publisherPhone,"
					+ " publisherEmail, numberOfPages " +
					 "FROM book INNER JOIN publisher ON book.publisherName = publisher.publisherName " +
					 "WHERE groupName = '"+group +"'";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat4, "BOOK","PAGES" ,"YEAR", "PUBLISHER", "PUBLISHER ADDRESS", "PUBLISHER PHONE", "PUBLISHER EMAIL");
			while(rs.next()){
				String book = rs.getString("bookTitle");
				String year = Integer.toString(rs.getInt("yearPublished"));
				String publisher = rs.getString("publisherName");
				String address = rs.getString("publisherAddress");
				String phone = rs.getString("publisherPhone");
				String email = rs.getString("publisherEmail");
				String pages = Integer.toString(rs.getInt("numberOfPages"));
				System.out.printf(displayFormat4, dispNull(book), dispNull(pages),dispNull(year),dispNull(publisher), dispNull(address), dispNull(phone), dispNull(email));
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* List all the data that belongs to an specific publisher
	*/
	public static void listPublisherData(Connection conn, String pub){
		Statement stmt = null;
		if(!publisherExists(conn, pub)){
			System.out.println("\nThe publisher " + pub + " does not exist in the database\n");
			return;
		}
		try {
			stmt = conn.createStatement();
			//Publisher
			String sqlGetGroup = "SELECT * FROM publisher WHERE publisherName = '" + pub + "'";
			ResultSet rSet = stmt.executeQuery(sqlGetGroup);
			System.out.printf(displayFormat2, "PUBLISHER NAME", "PUBLISHER ADDRESS", "PUBLISHER PHONE", "PUBLISHER EMAIL");
			while(rSet.next()){
				String name = rSet.getString("publisherName");
				String address = rSet.getString("publisherAddress");
				String phone = rSet.getString("publisherPhone");
				String subject = rSet.getString("publisherEmail");
				System.out.printf(displayFormat2, dispNull(name), dispNull(address), dispNull(phone), dispNull(subject));
			}
			//Associated data
			System.out.println("\nThese are the books (and writing group associated with each book) published by " + pub);
			String sql;
			sql = "SELECT bookTitle, yearPublished, numberOfPages, writingGroup.groupName, headWriter, yearFormed, subject "+
				  "FROM writingGroup INNER JOIN book ON writingGroup.groupName = book.groupName "+
				  "WHERE publisherName = '"+pub+"'";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat5, "BOOK", "PAGES", "YEAR", "GROUP NAME", "HEAD WRITER", "YEAR FORMED", "SUBJECT");
			while(rs.next()){
				String book = rs.getString("bookTitle");
				String pYear = Integer.toString(rs.getInt("yearPublished"));
				String pages = Integer.toString(rs.getInt("numberOfPages"));
				String group = rs.getString("groupName");
				String head = rs.getString("headWriter");
				String year = Integer.toString(rs.getInt("yearFormed"));
				String subject = rs.getString("subject");
				System.out.printf(displayFormat5, dispNull(book), dispNull(pages), dispNull(pYear), dispNull(group), dispNull(head), dispNull(year), dispNull(subject));
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* List all data that belongs to an specific book
	*/
	public static void listBookData(Connection conn, String book, String writingGroup){
		Statement stmt = null;
		if(!bookExists(conn, book, writingGroup)){
			System.out.println("\nThe book " + book + " (by " + writingGroup + ") does not exist in the database\n");
			return;
		}
		if(!groupExists(conn, writingGroup)){
			System.out.println("\nThe writing group " + writingGroup + " does not exist in the database\n");
			return;
		}
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT yearPublished, numberOfPages, writingGroup.groupName, headWriter, yearFormed, "+
				   "subject, book.publisherName, publisherAddress, publisherPhone, publisherEmail "+
				   "FROM writingGroup INNER JOIN book "+
				   "ON writingGroup.groupName = book.groupName "+
				   "INNER JOIN publisher "+
				   "ON book.publisherName = publisher.publisherName "+
				   "WHERE bookTitle = '"+book+"' AND book.groupName = '" +writingGroup+"'";
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf(displayFormat6,"BOOK" ,"PAGES", "YEAR", "GROUP NAME", "HEAD WRITER", "YEAR", 
                                           "SUBJECT", "PUBLISHER NAME", "PUBLISHER ADDRESS", "PUBLISHER PHONE", "PUBLISHER EMAIL");
			while(rs.next()){
				String year = Integer.toString(rs.getInt("yearPublished"));
				String pages = Integer.toString(rs.getInt("numberOfPages"));
				String group = rs.getString("groupName");
				String head = rs.getString("headWriter");
				String formed = Integer.toString(rs.getInt("yearFormed"));
				String subject = rs.getString("subject");
				String publisher = rs.getString("publisherName");
				String address = rs.getString("publisherAddress");
				String phone = rs.getString("publisherPhone");
				String email = rs.getString("publisherEmail");
				System.out.printf(displayFormat6, dispNull(book), dispNull(pages),dispNull(year), dispNull(group), dispNull(head), dispNull(formed), dispNull(subject), dispNull(publisher), dispNull(address)
                                                    , dispNull(phone), dispNull(email));
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	* In order to create a book, the publisher has to be valid and also the groupName. According to Mimi,
	* we are assuming such thing.
	*/
	public static void insertBook(Connection conn, String bookToInsert, String publisher, String writingGroup, String year, String pages){
		Statement stmt = null;
		if(!publisherExists(conn, publisher)){
			System.out.println("\nInsert failed. The publisher " + publisher + " does not exist in the database\n");
			return;
		}
		if(!groupExists(conn, writingGroup)){
			System.out.println("\nInsert failed. The writing group " + writingGroup + " does not exist in the database\n");
			return;
		}
		if(!isNumeric(year)){
			System.out.println("\nInsert failed. The year must be a number\n");
			return;
		}
		if(!isNumeric(pages)){
			System.out.println("\nInsert failed. The amount of pages must be a number\n");
			return;
		}
		System.out.println("\nThe data to be inserted: ");
		System.out.printf(displayFormat7, "BOOK", "PUBLISHER", "WRITING GROUP", "YEAR", "PAGES");
		System.out.printf(displayFormat7, bookToInsert, publisher, writingGroup, year, pages);
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "INSERT INTO book (groupName, bookTitle, publisherName, yearPublished, numberOfPages) "+
					"VALUES ('"+writingGroup+"', '"+bookToInsert+"', '"+publisher+"', "+year+", "+ pages+")";
			stmt.executeUpdate(sql);
			System.out.println("\nInsert successful! The book " + bookToInsert + " has been inserted into the database\n");
		} catch (SQLException e) {
			System.out.println("\nThere was a problem with inserting into the database\n");
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/*
	* Replaces the publisher from a specific book for another publisher
	*/
	public static void replacePublisher(Connection conn, String oldPub, String newPub){
        Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "UPDATE book " +
				  "SET book.publisherName = '"+newPub+"' " +
				  "WHERE book.publisherName = '"+oldPub+"'";   
			stmt.executeUpdate(sql);
			System.out.println("\nReplace successful!");
			System.out.println("The old publisher " + oldPub + " has been replaced with the new publisher " + newPub + "\n");
		} catch (SQLException e) {
			System.out.println("\nThere was a problem with updating the database\n");
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	 * Removes the given publisher from the database
	 */
	public static void removePublisher(Connection conn, String pub){
		Statement stmt = null;
		if(!publisherExists(conn, pub)){
			System.out.println("\nRemoval failed. The publisher " + pub + " does not exist in the database\n");
			return;
		}
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "DELETE FROM publisher " +
				   "WHERE publisherName = '"+pub+"'";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("\nThere was a problem trying to remove the publisher from the databse\n");
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/*
	* Deletes book from the book table
	*/
	public static void removeBook(Connection conn, String book, String group){
		Statement stmt = null;
		if(!bookExists(conn, book, group)){
			System.out.println("\nRemoval failed. The book " + book + " (by " + group + ") does not exist in the database\n");
			return;
		}
		try {
			stmt = conn.createStatement();
			String sql;
			sql = "DELETE FROM book " +
				   "WHERE bookTitle = '"+book+"'" +
					"AND groupName = '"+group+"'";
			stmt.executeUpdate(sql);
			System.out.println("\nRemoval successful! The book " + book + " (by " + group + ") has been removed from the database\n");
		} catch (SQLException e) {
			System.out.println("\nThere was a problem trying to remove the book from the databse\n");
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
    
	/*
	* Insert a publisher into the publisher table 
	*/
	public static boolean insertPublisher(Connection conn, String name, String address, String phone, String email){
		Statement stmt = null;
		try {
            //Statement for publisher
			stmt = conn.createStatement();
			String sql;
			sql = "INSERT INTO publisher (publisherName, publisherAddress, publisherPhone, publisherEmail) "+
					"VALUES ('"+name+"', '"+address+"', '"+phone+"', '"+email+"')";
			stmt.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			System.out.println("\nThere was a problem with inserting into the database");
			return false;
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	 * Checks if the given writing group exists in the database
	 */
	public static boolean groupExists(Connection conn, String group){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sqlGetGroup = "SELECT * FROM writingGroup WHERE groupName = '" + group + "'";
			ResultSet rs = stmt.executeQuery(sqlGetGroup);
			boolean exists = rs.next();
			rs.close();
			stmt.close();
			return exists;
		} catch (SQLException e) {
			System.out.println("There was a problem accessing the database");
			return false;
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	 * Checks if the given publisher exists in the database
	 */
	public static boolean publisherExists(Connection conn, String pub){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sqlGetPub = "SELECT * FROM publisher WHERE publisherName = '" + pub + "'";
			ResultSet rs = stmt.executeQuery(sqlGetPub);
			boolean exists = rs.next();
			rs.close();
			stmt.close();
			return exists;
		} catch (SQLException e) {
			System.out.println("There was a problem accessing the database");
			return false;
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	/**
	 * Checks if the given book exists in the database
	 */
	public static boolean bookExists(Connection conn, String book, String group){
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sqlGetBook = "SELECT * FROM book WHERE bookTitle = '" 
					+ book + "' AND groupName = '" + group + "'";
			ResultSet rs = stmt.executeQuery(sqlGetBook);
			boolean exists = rs.next();
			rs.close();
			stmt.close();
			return exists;
		} catch (SQLException e) {
			System.out.println("There was a problem accessing the database");
			return false;
		} finally {
            //finally block used to close resources
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {}
		}
	}
	
	public static boolean isNumeric(String str) { 
		try {  
		  Integer.parseInt(str);  
		  return true;
		} catch(NumberFormatException e){  
		  return false;  
		}
	}
}

