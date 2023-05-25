import com.beust.ah.A;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import utils.Deserialisze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.apache.catalina.manager.Constants.CHARSET;


public class ParserFromTopVK{
    private static String vkUrl ="https://vk.com/";

    private static HttpURLConnection getHttpURLConnection(URL url, String artist, String cookie) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        String postData = "act=section&" +
                "al=1&" +
                "claim=0&" +
                "is_layer=0&" +
                "owner_id=411548195&" +
                "q=" + artist + "&" +
            "query_id=-2821194504393715889&" +
                "section=search";
        con.setDoOutput(true);
        con.setRequestProperty("accept","*/*");
        con.setRequestProperty("accept-encoding","gzip, deflate, br");
        con.setRequestProperty("accept-language","ru,en;q=0.9,en-GB;q=0.8,en-US;q=0.7");
        con.setRequestProperty("content-type","application/x-www-form-urlencoded");
        con.setRequestProperty("content-length", Integer.toString(postData.length()));
        con.setRequestProperty("cookie",cookie);
        con.setRequestProperty("origin","https://vk.com");
        con.setRequestProperty("sec-ch-ua","\"Chromium\";v=\"113\", \"Microsoft Edge\";v=\"113\", \"Not:A-Brand\";v=\"24\"");
        con.setRequestProperty("sec-ch-ua-mobile","?0");
        con.setRequestProperty("sec-ch-ua-platform","\"Windows\"");
        con.setRequestProperty("sec-fetch-dest","empty");
        con.setRequestProperty("sec-fetch-mode","cors");
        con.setRequestProperty("sec-fetch-site","same-origin");
        con.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.35");
        con.setRequestProperty("x-requested-with","XMLHttpRequest");
        con.getOutputStream().write(postData.getBytes(StandardCharsets.UTF_8));
        return con;
    }

    private static HttpURLConnection getAllHttpURLConnection(URL url, String cookie) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("cookie", cookie);
        con.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.58");
        return con;
    }

    public static HashMap<String, List<String>> getSoundsForArtists(WebDriver driver, List<String> artists, String cookie) throws IOException {
        HashMap<String, List<String>> finalHM = new HashMap<>();
        for(String artist : artists){
            //запрос ради кнопки показать все(получаю укороченный кусок html и парсинг)
            HttpURLConnection con = getHttpURLConnection(new URL("https://vk.com/al_audio.php?act=section"), artist, cookie);
            try(BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream()), CHARSET))){

                StringBuilder stringBuilder = new StringBuilder();
                String content;
                while((content = in.readLine()) !=null){
                    stringBuilder.append(content);
                }
                if (stringBuilder.length() == 0) continue;
                //json
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String html = (String) ((ArrayList<Object>)mapper.readValue(stringBuilder.toString(), Deserialisze.class).getPayload().get(1)).get(0);
                //html
                Document document = Jsoup.parse(html, vkUrl);
                URL urlAllMusic = new URL(vkUrl + document.getElementsByClass("audio_page_block__show_all_link").get(0).attr("href"));
                //повторный запрос на всю музыку
                HttpURLConnection conAll = getAllHttpURLConnection(urlAllMusic, cookie);
                //запрос на кнопку показать все(принадлежащей одному автору)
                try(BufferedReader inAll = new BufferedReader(new InputStreamReader(conAll.getInputStream()))){
                    ArrayList<String> titlesAudio = new ArrayList<>();
                    StringBuilder stringBuilderAll = new StringBuilder();
                    String contentAll;
                    while((contentAll = inAll.readLine()) !=null){
                        stringBuilderAll.append(contentAll);
                    }
                    if (stringBuilderAll.length() == 0) continue;
                    //html
                    Document documentAll = Jsoup.parse(stringBuilderAll.toString(), vkUrl);
                    //сохранение музыки и автора в HashMap
                    for(Element elem : documentAll.getElementsByClass("_audio_row__title_inner"))
                        titlesAudio.add(elem.text());
                    finalHM.put(artist, titlesAudio);
                }
            }
        }
        return finalHM;
    }
}
