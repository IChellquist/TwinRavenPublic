import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class WebInteractor {




    //webpages to get the webaddresses that have the tables
    private Document[] webpages = new Document[2]; //holds the webpage of stockcharts.com

    /*
    User selected options so they can select the exchange, the sector, the industry
     */
    private String exchange; //holds the exchange selected by the user
    private String sector; //holds the sector selected by the user
    private String industry; //holds the industry

    /*
    Arraylists for images, stocks, tickers, etc
     */
    private ArrayList<String> stocks = new ArrayList<String>();
    private ArrayList<String> stocktickers = new ArrayList<String>();
    private ArrayList<String[][]> newssearchresult = new ArrayList<String[][]>();
    private ArrayList<String> exchanges = new ArrayList<String>();
    private ArrayList<BufferedImage> stockimages = new ArrayList<BufferedImage>();

    //Random number generator to fool computer
    private Random randomgen = new Random();

    /*
    Strings that hold the addresses for the various tables
     */
    private final String currentvolumegainers = "http://www.stockcharts.com/def/servlet/SC.scan?s=I.Y|TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tv0_gt_as1,20,tv*4]![tc0_gt_tc1]&report=predefalli";
    private final String currentvolumedecliners = "http://www.stockcharts.com/def/servlet/SC.scan?s=I.Y|TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tv0_gt_as1,20,tv*4]![tc0_lt_tc1]&report=predefalli";
    private final String previousvolumegainers = "http://www.stockcharts.com/def/servlet/SC.scan?s=I.Y|TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tv0_gt_as1,20,tv*4]![tc0_gt_tc1]&report=predefalli";
    private final String previousvolumedecliners = "http://www.stockcharts.com/def/servlet/SC.scan?s=I.Y|TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tv0_gt_as1,20,tv*4]![tc0_lt_tc1]&report=predefalli";

    private final String currentbollingerupbreaks = "http://www.stockcharts.com/def/servlet/SC.scan?s=TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tc1_le_ab1]![tc0_gt_ab0]&report=predefall";
    private final String currentbollingerdownbreaks = "http://www.stockcharts.com/def/servlet/SC.scan?s=TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tc1_ge_ac1]![tc0_lt_ac0]&report=predefall";
    private final String previousbollingerupbreaks = "http://www.stockcharts.com/def/servlet/SC.scan?s=TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tc1_le_ab1]![tc0_gt_ab0]&report=predefall";
    private final String previousbollingerdownbreaks = "http://www.stockcharts.com/def/servlet/SC.scan?s=TSAL[t.t_eq_s]![as0,20,tv_gt_40000]![tc1_ge_ac1]![tc0_lt_ac0]&report=predefall";


    /*
    Getters and Setters
    /////////////////
    /////////////////
    /////////////////
     */
    //Setters
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
    public void setSector(String sector) {this.sector = sector;}
    public void setIndustry(String industry) {this.industry = industry;}

    //getters
    public ArrayList<String> getStocks(){return stocks;}
    public ArrayList<String> getTickers(){return stocktickers;}
    public ArrayList<String> getExchanges(){return exchanges;}
    public ArrayList<String[][]> getNewssearchresult(){return newssearchresult;}
    public ArrayList<BufferedImage> getStockimages(){return stockimages;}



    /*
    This method sets all the webpages to the tables that I am interested in. Currently,
    I only have four. Also, contains randomly generated numbers between getting the webpages to prevent
    the website from blocking me.
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
     */
    public void setTable(int closechoice) throws IOException, InterruptedException {
        if (closechoice == 1){
                this.webpages[0] = Jsoup.connect(currentvolumegainers).get();
                int random = randomgen.nextInt(5);
                Thread.sleep(4000*random);
                this.webpages[1] = Jsoup.connect(currentvolumedecliners).get();
                random = randomgen.nextInt(5);
                Thread.sleep(4000*random);
            }
        if (closechoice == 2){
            this.webpages[0] = Jsoup.connect(previousvolumegainers).get();
            this.webpages[1] = Jsoup.connect(previousvolumedecliners).get();
            this.webpages[2] = Jsoup.connect(previousbollingerupbreaks).get();
            this.webpages[3] = Jsoup.connect(previousbollingerdownbreaks).get();
        }
        } //ends set table method


    /*
    Method goes to each table for the various predefined scans, and fills the appropriate arraylist with the information needed.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public void getUserSelectedExchangesandSectors(){

        for (int j = 0; j < 2; j++) {
            Element table = webpages[j].select("table").get(0);
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                if (cols.size() < 5){continue;}
                if (cols.get(3).text().equals(exchange) || exchange == null)
                    if (cols.get(4).text().equals(sector) || sector == null)
                        if (cols.get(5).text().equals(industry) || industry == null) {
                            stocktickers.add(cols.get(1).text());
                            stocks.add(cols.get(2).text());
                            exchanges.add(cols.get(3).text());
                        }
            }
        }
    } //end method


    /*
    This method gets the newssearch results for each stock.
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////
     */
    public void doSearch(String azurekey) {
        HttpClient httpclient = HttpClients.createDefault();
        String results = null;

        for (int j = 0; j < stocks.size();j++) { //j loop for each stock
            String search = stocks.get(j);
            try {
                //Creates a URIbuilder object to interface with BingSearch API
                URIBuilder builder = new URIBuilder("https://api.bing.microsoft.com/v7.0/news/search");

                //Sets the parameters for the search
                builder.setParameter("q", search);
                builder.setParameter("count", "10"); //The number of news articles returned
                builder.setParameter("offset", "0");
                builder.setParameter("mkt", "en-us"); //The language that will be returned
                builder.setParameter("safeSearch", "Moderate"); //Prevents my search results from being crowded by porn.

                //Creates the URI from the previously declared constraints
                URI uri = builder.build();
                //Creates an HTTPRequest using the URI built by builder
                HttpGet request = new HttpGet(uri);
                //The keys set here are necessary to interface with the BingSearch API, only 1000 are given per month.
                request.setHeader("Ocp-Apim-Subscription-Key", azurekey);
                //gets an HTTP response from the HTTPGet Request
                HttpResponse response = httpclient.execute(request);

                //Creates an HTTP entity from the response
                HttpEntity entity = response.getEntity();

                //This code turns the entity into a string of JSON
                if (entity != null) {
                    results = EntityUtils.toString(entity);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            //create JSON object with the string of JSON search results created from entitity
            JSONObject obj = new JSONObject(results);
            //create JSON array with the JSON data linked to the ["value"] key,

            if (results.contains("value")) {
                JSONArray searchData = obj.getJSONArray("value");

                //This creates a String array to store the name, url, description, and datePublished for each
                // news article in the search
                String[][] newsarticles = new String[searchData.length()][4];

        /*
        The loop below goes through each "value" object in the JSON returned by the HTTP request and gets the
        nested objects that compose it.
         */
                for (int i = 0; i < searchData.length(); ++i) {

                    //Represents an individual news article pulled from the "value" array created above.
                    JSONObject person = searchData.getJSONObject(i);

                    newsarticles[i][0] = person.getString("name"); //The name of the news article
                    newsarticles[i][1] = person.getString("url"); //A url linking to the news article
                    newsarticles[i][2] = person.getString("description"); //A brief description fo the news article
                    newsarticles[i][3] = person.getString("datePublished"); //The date the news article was published
                }
                newssearchresult.add(newsarticles); //adds it to the arraylist
            } else {
               String[][] blanksnewsarticles = new String[1][4];
               blanksnewsarticles[0][0] = "THERES NOTHING HERE";
                blanksnewsarticles[0][1] = "THERES NOTHING HERE";
                blanksnewsarticles[0][2] = "THERES NOTHING HERE";
                blanksnewsarticles[0][3] = "THERES NOTHING HERE";
               newssearchresult.add(blanksnewsarticles);
            }

        }
    }


    /*
    This method will get the images for each stock and get the image from the website. Random integer is used to trick website into believing that
    everything is coming from userinput
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public void getImages() throws IOException, InterruptedException {

        for (int i = 0; i < stocktickers.size(); i++){
            int random = randomgen.nextInt(5);
            Thread.sleep(4000*random);
            Document doc = Jsoup.connect("http://stockcharts.com/h-sc/ui?s=" + stocktickers.get(i)).get();
            Elements imgs = doc.getElementsByTag("img");
            String chartlink = null;

            if (stocktickers.get(i).contains("/")){
                String taco = stocktickers.get(i);
                String taco1 = taco.replace("/","%2F");
                stocktickers.set(i,taco1 );
            }

            for (Element el : imgs) {
                String src = el.absUrl("src");
                if (src.contains("/c-sc/sc?s="+ stocktickers.get(i) + "&p=D&b=5&g=0&i=0&r=")) {
                    chartlink = src;
                    break;
                }
            }

            if(chartlink != null) {

                String urlstream = chartlink + "/sc.png";
                URL url = new URL(urlstream);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
                BufferedImage img = ImageIO.read(connection.getInputStream());
                stockimages.add(img);
            }
            if (chartlink == null){
                BufferedImage img = null;
                stockimages.add(img);
            }

        }//ends the for loop that
    } //ends method

} //ENDS THE CLASS FILE!!!!!!!
