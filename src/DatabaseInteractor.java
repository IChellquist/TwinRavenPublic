import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseInteractor {

    //Holds all of the data for the database
    private ArrayList<String> stocks = new ArrayList<String>();
    private ArrayList<String[][]> newssearchresults = new ArrayList<String[][]>();
    private ArrayList<String> tickers = new ArrayList<String>();
    private ArrayList<String> exchanges = new ArrayList<String>();

    //Database parameters
    private Connection connection = null;
    private Statement statement;
    private DatabaseMetaData checktable;
    private ResultSet checktable2;

    //Various strings that go with the database
    private final String dbName="StockSearches";
    private final String connectionURL = "jdbc:derby://localhost:1527/StockSearches;create=true";
    private final String  apachedriver = "org.apache.derby.jdbc.ClientDriver"; //Dont think I need this
    private final  String createtableSQL = "CREATE TABLE STOCK_SEARCHES  " //To create the table if it does not already exist
            +  "(ARTICLE_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "
            +  "   CONSTRAINT ARTICLE_PK PRIMARY KEY, "
            +  " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            +  "STOCK_TICKER VARCHAR(10) NOT NULL," +
            "STOCK_EXCHANGE VARCHAR(10) NOT NULL," +
            "STOCK_NAME VARCHAR(50) NOT NULL," +
            "ARTICLE_TITLE VARCHAR(2048) NOT NULL," +
            "ARTICLE_URL VARCHAR(2048) NOT NULL," +
            "ARTICLE_DESCRIPTION VARCHAR(2048) NOT NULL," +
            "ARTICLE_DATE VARCHAR(50) NOT NULL)";



    //Shell for a constructor I might end up using
    public DatabaseInteractor(){}

    //Method for setting all of the parameters from application program interface
    public void setParameters(ArrayList<String> stocks,
                              ArrayList<String> tickers,
                              ArrayList<String> exchanges,
                              ArrayList<String[][]> newssearchresults){

        this.stocks = stocks;
        this.tickers = tickers;
        this.exchanges = exchanges;
        this.newssearchresults = newssearchresults;
    }

    //Sets the parameters from the file
    public void setParametersFromFile(){
        try{
            //Loads the file
            File testreport = new File("C:\\CSP\\TwinRaven\\sampleoutput.txt");
            Scanner scanner = new Scanner(testreport);

            //Loop goes through everyline of the file
            while ((scanner.hasNextLine() == true)){
                int i = 1; //counter to keep track of what line I am on. Incremented after every readLine
                stocks.add(scanner.nextLine());
                tickers.add(scanner.nextLine());
                exchanges.add(scanner.nextLine());
                i++;
                String[][] temparray = new String[Integer.parseInt(scanner.nextLine())][4];
                i++;
                if (i == 3 && scanner.hasNextLine() == true){
                    for (int j = 0;j<temparray.length;j++ ){
                        for (int k = 0;k<4;k++){
                            if(scanner.hasNextLine() == true) {
                                temparray[j][k] = scanner.nextLine();
                                i++;
                            }
                        }
                    }//ends outer for loop
                }//ends conditional
                if (scanner.hasNextLine() == true && scanner.nextLine() == "ENDLINE"){
                    i = 0;
                }
                newssearchresults.add(temparray);
            } //ends while loop

            int l = 1;
        }catch(IOException ex) {
            System.out.println( "Error reading file");
        }
        //Cleans up the search results for database insertion
        newssearchresults = TwinRavenHelper.SearchSanitizer(newssearchresults);
    }//ends from file method

public void openDataBaseConnection() throws SQLException{
        connection = DriverManager.getConnection(connectionURL);
}

//Closes the connection with the database
public void closeDataBaseConnection() throws SQLException {
        connection.close();
}

public void checkExistenceOfTable() throws SQLException {
        statement = connection.createStatement();
        checktable = connection.getMetaData();
        checktable2 = checktable.getTables(null,null,"STOCK_SEARCHES", null);

        if (checktable2.next()){
        System.out.println(".....table STOCK_SEARCHES exists");
        }
}

public void insertIntoTable() throws SQLException {

        //create a statement just for this method
        Statement s = connection.createStatement();

        //take the values in the strings and load them into the table
    for (int i = 0; i < stocks.size(); i++){
        for (int j = 0; j < newssearchresults.get(i).length; j++ ) {
            String loadtable = "INSERT INTO STOCK_SEARCHES (STOCK_TICKER,STOCK_EXCHANGE,STOCK_NAME,ARTICLE_TITLE,ARTICLE_URL,ARTICLE_DESCRIPTION,ARTICLE_DATE) " +
                    " VALUES ('" + tickers.get(i) + "','" + exchanges.get(i) + "','" + stocks.get(i) + "','" + newssearchresults.get(i)[j][0] + "','"
                    + newssearchresults.get(i)[j][1] + "','" + newssearchresults.get(i)[j][2] + "','" + newssearchresults.get(i)[j][3] + "')";

            s.executeUpdate(loadtable);
            s.clearBatch();
        }

    }
    s.close(); //close out the statement
}




    }//ends class file



