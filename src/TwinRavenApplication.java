import org.apache.derby.iapi.db.Database;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class TwinRavenApplication {

        public static void main(String[] args) throws IOException, InterruptedException, SQLException {

            /*
            ONLY EDIT THESE TWO LINES
             */
            //File path to save the output PDF
            String pdffilepath = "INSERT FILE PATH HERE";
            //Azure key for Bing News Search
            String azurekey = "INSERT AZURE BING NEWS SEARCH KEY HERE";










            WebInteractor wsi = new WebInteractor(); //interacts with the stockcharts.com website and also does the search queries.
            DatabaseInteractor dbi = new DatabaseInteractor(); //interacts with the database.
            //boolean writetodatabase = false;


            //The console for the user; a simple command line interface allowing the user to select the exchange and sector of the economy.
            Scanner userinput = new Scanner(System.in);
            System.out.print("This program will make a report of the highest volume gainers of the day");

            //Code allowing the user to select what system they are using. Irrelevant to this example.
            /*
            //Have the user select what system they are using
            System.out.println("\n Please select what system you are using:\n[1] Asus Labtop Ubuntu\n[2] Asus Labtop Windows");
            int systemchoice = userinput.nextInt();
            if (systemchoice == 1){
                pdffilepath = "/media/lordofthunder/443E04BA3E04A6D0/DRIVE/DOCUMENTS/DAILY STOCK REPORTS/";
            }
            */

            //Irrelevant database code for this example
            /*
            //Have the user select whether or not to store things into a database. The default is false
            System.out.println("\nWould you like to write the searches to a database?:\n[1] Yes\n[2] No");
            int booleanchoice = userinput.nextInt();
            if (booleanchoice == 1){
                writetodatabase = true;
            }
            */






            System.out.println("\nPlease choose the exchange:\n[1] ALL EXCHANGES\n[2] NASDAQ\n[3] LSE\n[4] AMEX\n[5] NSE\n[6] OTCM\n[7] TSE\n[8] TSXV\n[9] NYSE\n");
            int exchange = userinput.nextInt();


            //Conditionals for the exchange
            if (exchange == 2) {
                wsi.setExchange("NASD");
            }
            if (exchange == 3) {
                wsi.setExchange("LSE");
            }

            if (exchange == 4) {
                wsi.setExchange("AMEX");
            }
            if (exchange == 5) {
                wsi.setExchange("NSE");
            }
            if (exchange == 6) {
                wsi.setExchange("OTCM");
            }
            if (exchange == 7) {
                wsi.setExchange("TSE");
            }
            if (exchange == 8) {
                wsi.setExchange("TSXV");
            }
            if (exchange == 9) {
                wsi.setExchange("NYSE");
            }



            //Conditionals for the Sector
            System.out.println("Please choose a sector:\n[1] ALL SECTORS\n[2] Financial\n[3] Utilities\n[4] Industrial\n[5] Cyclicals\n[6] Technology\n[7] Materials\n" +
                    "[8] Energy\n[9] Consumer Staples\n[10] Health Care\n");
            int sector = userinput.nextInt();

            if (sector == 2) {
                wsi.setSector("Financial");
            }
            if (sector == 3) {
                wsi.setSector("Utilities");
            }
            if (sector == 4) {
                wsi.setSector("Industrial");
            }
            if (sector == 5) {
                wsi.setSector("Cyclicals");
            }
            if (sector == 6) {
                wsi.setSector("Technology");
            }
            if (sector == 7) {
                wsi.setSector("Materials");
            }
            if (sector == 8) {
                wsi.setSector("Energy");
            }
            if (sector == 9) {
                wsi.setSector("Consumer Staples");
            }
            if (sector == 10) {
                wsi.setSector("Health Care");
            }

            //Set for current, will add user input option later
            wsi.setTable(1);
            //Gets all the exchanges/tickers/etc for all of the tables
            wsi.getUserSelectedExchangesandSectors();
            //does the search
            wsi.doSearch(azurekey);
            //Gets all the images
            wsi.getImages();



            //This line creates a MakePDF object, which contains all the code for generating a PDF
            MakePDF2 pdf = new MakePDF2(TwinRavenHelper.removeUnicode(wsi.getNewssearchresult()),TwinRavenHelper.removeINC(wsi.getStocks()),wsi.getStockimages(),pdffilepath);
            //pdf.formatStocks();
            pdf.createPDF();


            //Below contains code for writing all the results to the database. It is disabled for this presentation.
            /*
            //Only writes to the database if the user made the selection to do so
            if (writetodatabase == true) {
                //Writes the search results to a file before continue with the program.
                TwinRavenHelper.writeFileForDatabase(wsi.getStocks(), wsi.getTickers(), wsi.getExchanges(), wsi.getNewssearchresult());

                //Transfers the  Arraylists necessary to interact with the database to the databaseinteractor object
                dbi.setParameters(wsi.getStocks(), wsi.getTickers(), wsi.getExchanges(), TwinRavenHelper.SearchSanitizer(wsi.getNewssearchresult()));

                //The sequence of code below connects to the database and writes the search results to the database.
                dbi.openDataBaseConnection();
                //check to see if table exists
                dbi.checkExistenceOfTable();
                //insert results into table
                dbi.insertIntoTable();
                //close the connection
                dbi.closeDataBaseConnection();

            }
            */



        }
    }

