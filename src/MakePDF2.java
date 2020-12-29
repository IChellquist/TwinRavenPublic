import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;

/**
 * Created by Richard Finn and Ian Chellquist on 4/21/2017.
 * @author Ian Chellquist and Richard Finn
 * @version 1.0
 * @since 2017-04-01
 */
public class MakePDF2 {

    private ArrayList<String[][]> stocks; //This Arraylist contains 2d string arrays with the news results for each stock
    private ArrayList<String> stocknames; //This Arraylist contains the list of stocks
    private ArrayList<String> tickers;
    private ArrayList<BufferedImage> imagearray;
    private int pages; //Counts the number of pages that will be needed to contain the news articles in the PDF document
    private String pdffilepath;


    //Constructor that accepts an arraylist of string arrays for each news search of a stock

    /**
     * This is the constructor. It takes and ArrayList of String[][] representing the news articles. And it takes
     * an ArrayList of Strings the represent the names of each stock represented by a String[][]
     * @param stocks
     * @param stocknames
     */
    public MakePDF2(ArrayList<String[][]> stocks, ArrayList<String> stocknames, ArrayList<BufferedImage> imagearray, String pdffilepath){

        this.stocks = stocks;
        this.stocknames = stocknames;
        this.imagearray = imagearray;
        this.pdffilepath = pdffilepath;

    }


    /**
     * This method formats the URL so it can be clicked on in the PDF document. It removes the part of the URL generated
     by Bing and attached infront of the address. It is incomplete.
     */
    public void formatStocks(){


            //The outer loop goes down each of the arrays in the stocks arraylist
            for (int i =0; i < this.stocks.size();i++) {
                String[][] temp = this.stocks.get(i); //A temp array to allow for temporary manipulation

                //This loop goes through all of the rows of the arraylist, and modifies the URL which is in column with index 1
                for (int j = 0; j < temp.length;j++) {

                        //Uses the Java regex library to define the pattern of what needs to be removed from the raw URL for the article
                        Pattern clean = Pattern.compile("(?<!bing)www(?!\\.bing\\.com)\\S+?(?=&p=DevEx)");
                        Matcher findpattern = clean.matcher(temp[j][1]);
                        String interimlink;

                        //If the pattern exists than assign interimlink the semi cleaned string
                        if (findpattern.find()){
                         interimlink = findpattern.group();
                    } else {//This conditional is necessary to clean URLs without the "www." in the link

                            //This pattern removes the bing part of the URL if there is no "www"
                            Pattern clean2 = Pattern.compile("http(?!://www\\.bing\\.com)\\S+?(?=&p=DevEx)");
                            Matcher findpattern2 = clean2.matcher(temp[j][1]);
                             findpattern2.find();
                             interimlink = findpattern2.group();

                        }

                        //For some reason, the URL has a "2%f" instead of a "/". The code below replaces the "2%f"
                        Pattern replace = Pattern.compile("%2f");
                        Matcher findandreplace = replace.matcher(interimlink);
                        String finallink = findandreplace.replaceAll("/");
                        temp[j][1] = finallink;

                }
                this.stocks.set(i, temp); //Reassigns the original 2d array with the one with the modified URL link
            }
        }

    /**
     * The method below creates a PDF and outputs it to the same directory where the class file is running.
     * @throws IOException
     */
    public void createPDF() throws IOException {


        //Uses the java time library to get the localtime from the machine.
        String gettime = LocalDateTime.now().toString();
        //adds the .pdf to the end of the string so it can be used to save the pdf once it is created
        String pdftime = gettime + ".pdf";


        //String directory = "C:\\Users\\-\\Google Drive\\DOCUMENTS\\DAILY STOCK REPORTS\\";
        String directory = pdffilepath;


        //replaces characters in the time string with ones that will allow a file to be saved.
        pdftime = pdftime.replace(':',';');

        //The line of code below calculates how long the PDF file has to be to accomadte all of the search results
        pages = this.stocks.size() * 10 / 5 + 1;
        //Creates a rectange with the size needed to hold all of the search results
        PDRectangle pagebox = new PDRectangle(1000,1850);
        PDDocument report = null;

        /*
        //Code below is testing creating multiple pages
        PDPage[] pages = new PDPage[stocknames.size()];
        PDRectangle[] rectangles = new PDRectangle[stocknames.size()];
        */



        try {

            //creates a new PDDocument
            report = new PDDocument();

            //Creats a PDFont object by using java.io.File to load a special font that contains ALL OF THE UNICODE characters
            PDFont reportfont = PDType0Font.load(report,new File("ARIALUNICODE.ttf"));
            //Sets the font size
            float fontSize = 12;
            //Sets the space inbetween lines
            float leading = 1.5f * fontSize;
            //species the size of the margin, the units are in points, which I believe are 1/72 of an inch
            float margin = 72;
            //Creates a mathematical definition of the left and right hand borders of a page
            float width = pagebox.getWidth() - 2 * margin;

            //Species the x and y starting position for text placement
            float startX = pagebox.getLowerLeftX() + margin;
            float startY = pagebox.getUpperRightY() - margin;








            //The outer loop goes through each stock, which is represented by an individual 2d array
            for (int i =0;i<stocks.size();i++) {

                //TEST CODE FOR MODIFICATION IMMEDIATELY BELOW


             PDPage page = new PDPage(pagebox);
              report.addPage(page);
             PDPageContentStream contentStream = new PDPageContentStream(report,page);
             contentStream.beginText();
             contentStream.newLineAtOffset(startX,startY);




             /*
             This block here writes the title
              */
                //Creates a 2d array to pull an element from the 2d String Arraylist
                String[][] stock = stocks.get(i);
                //Sets the font to write the company name in a large size before writing out the articles for that stock
                contentStream.setFont(reportfont,20);
                //Writes the name of the stock before the news articles are written
                contentStream.showText(this.stocknames.get(i));
                //Creates the image object that can be placed in the contentstream from the buffered image from the arraylist
                contentStream.endText();



                /*
                This block right here places the image
                */
                    PDImageXObject image;
                    if (imagearray.get(i) != null) {
                         image = LosslessFactory.createFromImage(report, imagearray.get(i));
                    }else{
                         image = LosslessFactory.createFromImage(report, ImageIO.read(new File("nullchart.png")));
                    }

                    //Places the image
                    contentStream.drawImage(image, startX, startY - image.getHeight() - leading);




                contentStream.beginText();
                //Creates a new line below the title that was just printed
                contentStream.newLineAtOffset(startX, startY - image.getHeight() - 100 );








                //This inner loop goes through each news article(IE row) in the array list
                for (int j = 0; j < stock.length; j++) {

                    /*
                    This inner loop is cycling through each column of one of the rows of one of the two dimensional
                    arrays for each stock
                     */
                    for (int k = 0; k < 4; k++) {

                        //The font is reset everytime due to changing the font for the company name
                        contentStream.setFont(reportfont,fontSize);
                        //String that loads the content of one of the columns
                        String description = stock[j][k];
                        //This arraylist holds each line of the PDF document
                        List<String> lines = new ArrayList<String>();



                        /*
                        This monstrosity below goes chops up the string from one of the columns into pieces that fit the width
                       and stores them as an element of an arraylist.
                         */
                        int lastSpace = -1;

                        while (description.length() > 0) {

                            //This keeps track of the position of the next space to provide coordinates where the string should be divided if needed
                            int spaceIndex = description.indexOf(' ', lastSpace + 1);
                            //.index of returns a negative value if no space is found, so unfortunately, this was not able to put the url on the next line
                            if (spaceIndex < 0)
                                spaceIndex = description.length();
                            //Creates a new string from the start of the initial string to the first space
                            String substring = description.substring(0, spaceIndex);
                            //This variable calculates the size of the substring
                            float size = fontSize * reportfont.getStringWidth(substring) / 1000;

                            //This conditional checks the length of the substring to width of the page
                            if (size > width) {

                                //this is needed in case of a really long string that goes off the page until a space is reached...
                                if (lastSpace < 0)
                                    lastSpace = spaceIndex;
                                //Defines where the original string will be divided
                                substring = description.substring(0, lastSpace);
                                //Adds the divided line to the arraylist
                                lines.add(substring);
                                //Trims the original string
                                description = description.substring(lastSpace).trim();
                                //Resets lastspace
                                lastSpace = -1;
                                //This conditional is for when there are no more words left to divide, and what is left of the original string is added to the lines Arraylist
                            } else if (spaceIndex == description.length()) {
                                lines.add(description);
                                //Makes description string empty, thus terminating the loop
                                description = "";
                            } else {
                                //assigns the last space variable the integer of where the next space is
                                lastSpace = spaceIndex;
                            }
                        }

                        //The code below puts all of the divided original string pieces onto a PDF line by line
                        for (String line : lines) {
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -leading);
                        }

                    }
                    //This creates a newline for the next column
                    contentStream.newLineAtOffset(0, -leading);

                }
                /*
                //This creates a new line for the next stock so the PDF is easier to read.
                contentStream.newLineAtOffset(0, -leading);
                */

                contentStream.endText(); //ends text
                contentStream.close(); //closes content stream
            }
            /*
            contentStream.endText(); //ends text
            contentStream.close(); //closes content stream
            */


            //Saves the pdf with the current time the report was generated.
            //report.save(new File(directory + pdftime));
            //Trying to save where the program executes.
            report.save(new File(directory + pdftime));


        }
        finally{
            if (report != null){
                report.close();
            }



        }



    }
}
