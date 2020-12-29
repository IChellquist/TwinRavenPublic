import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwinRavenHelper {

    //Removes Certain three letter phrases on the names of stocks that cause lousy search results...
    public static ArrayList<String> removeINC(ArrayList<String> stocks) {


        for (int i = 0; i < stocks.size(); i++) {
            String temp = stocks.get(i); //A temp array to allow for temporary manipulation
            temp = temp.replaceAll("(?!_-)\\p{Punct}", "");
            temp = temp.replaceAll("\\bInc\\b", "");
            temp = temp.replaceAll("\\bCorp\\b", "");
            temp = temp.replaceAll("\\bLLC\\b", "");
            temp = temp.replaceAll("\\bCo\\b", "");
            temp = temp.replaceAll("\\bplc\\b", "");
            temp = temp.replaceAll("\\bLtd\\b", "");

            //temp = temp.replace("/\s{2,}/g"," ");
            /*
                //Uses the Java regex library to define the pattern of what needs to be removed from the raw URL for the article
                Pattern clean = Pattern.compile("(?<!bing)www(?!\\.bing\\.com)\\S+?(?=&p=DevEx)");
                Matcher findpattern = clean.matcher(temp);
                String interimlink;

                //If the pattern exists than assign interimlink the semi cleaned string
                if (findpattern.find()){
                    interimlink = findpattern.group();
                }
                */
            stocks.set(i, temp); //Reassigns the original 2d array with the one with the modified URL link
        }
        return stocks;

    }


    //This method removes certain characters that interfere with database entry
    public static ArrayList<String[][]> SearchSanitizer(ArrayList<String[][]> newssearchresults) {

        //this.newssearchresults = newssearchresults;

        for (int i = 0; i < newssearchresults.size(); i++) {

            String[][] tempentry = newssearchresults.get(i);

            for (int j = 0; j < newssearchresults.get(i).length; j++) {

                for (int k = 0; k < 4; k++) {

                    String tempelement = tempentry[j][k];
                    tempelement = tempelement.replace("'", "''");
                    tempentry[j][k] = tempelement;
                }


            }


            newssearchresults.set(i, tempentry);
        }

        return newssearchresults;

    }

    //This method removes unicode from search results for PDF generation.
    public static ArrayList<String[][]> removeUnicode(ArrayList<String[][]> newssearchresults) {


        for (int i = 0; i < newssearchresults.size(); i++) {

            String[][] tempentry = newssearchresults.get(i);

            for (int j = 0; j < newssearchresults.get(i).length; j++) {

                for (int k = 0; k < 4; k++) {

                    String tempelement = tempentry[j][k];
                    tempelement = tempelement.replaceAll("\\P{Print}", "");
                    tempentry[j][k] = tempelement;
                }


            }


            newssearchresults.set(i, tempentry);
        }

        return newssearchresults;

    }

    public static void writeFileForDatabase(ArrayList<String> stocks,
                                            ArrayList<String> tickers,
                                            ArrayList<String> exchanges,
                                            ArrayList<String[][]> newssearchresults) throws FileNotFoundException {


        //The block of code will write the report to a file.
        PrintWriter writer = new PrintWriter("sampleoutput.txt");

        // i loop is for each stock
        for (int i = 0; i < stocks.size(); i++) {
            writer.println(stocks.get(i));
            writer.println(tickers.get(i));
            writer.println(exchanges.get(i));
            String[][] tempstringarray = newssearchresults.get(i);
            writer.println(tempstringarray.length);
            //j loop is for each news article
            for (int j = 0; j < tempstringarray.length; j++) {
                //k loops is for each attribute of that news article
                for (int k = 0; k < 4; k++) {
                    writer.println(tempstringarray[j][k]);
                    writer.flush();

                }


            }
            writer.println("ENDLINE");
        }
    }
}//ends class



