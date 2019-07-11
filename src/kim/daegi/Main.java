package kim.daegi;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static final String API_ENDPOINT="https://query1.finance.yahoo.com/v7/finance/quote?lang=ko-KR&region=KR&corsDomain=finance.yahoo.com";

    public static void main(String[] args) {

        String interval = args[0];
        String symbols = Arrays.stream(args).skip(1).collect(Collectors.joining(","));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(API_ENDPOINT + "&symbols=" + URLEncoder.encode(symbols, StandardCharsets.UTF_8));

            while (true) {
                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity(), "UTF-8");

                JSONObject jsonObject = new JSONObject(json);

                print(jsonObject);
                try {
                    Thread.sleep(Long.parseLong(interval));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void print(JSONObject jsonObject) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        JSONArray result = jsonObject.getJSONObject("quoteResponse").getJSONArray("result");
//        result = sort(result);

        System.out.printf(ConsoleColors.CYAN_UNDERLINED+"%-15s%-10s%20s%10s%10s%12s %-20s\033[0m\n", "Name", "Symbol", "Low-High", "Price", "Diff", "Percent", "longName");


        for (int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);

            String shortName = data.getString("shortName");
            String longName = shortName;
            if(data.has("longName")) {
                longName = data.getString("longName");
            }
            shortName = shortName.length()>14?shortName.substring(0, 14):shortName;
            String symbol = data.getString("symbol");
            String regularMarketDayRange = data.getString("regularMarketDayRange");
            double regularMarketPrice = data.getDouble("regularMarketPrice");
            double regularMarketDayHigh = data.getDouble("regularMarketDayHigh");
            double regularMarketDayLow = data.getDouble("regularMarketDayLow");
            double regularMarketChange = data.getDouble("regularMarketChange");
            double regularMarketChangePercent = data.getDouble("regularMarketChangePercent");

            String color = regularMarketChange==0?"":regularMarketChange>0?ConsoleColors.GREEN_BOLD_BRIGHT:ConsoleColors.RED_BOLD_BRIGHT;

            System.out.printf("%-15s", shortName);
            System.out.printf("%-10s", symbol);
            System.out.printf("%20s", regularMarketDayRange);
            if(regularMarketDayHigh == regularMarketPrice || regularMarketDayLow == regularMarketPrice) {
                System.out.printf(ConsoleColors.WHITE_BOLD+color+"%10.2f"+ConsoleColors.RESET, regularMarketPrice);
            } else {
                System.out.printf(ConsoleColors.WHITE_BOLD+"%10.2f"+ConsoleColors.RESET, regularMarketPrice);
            }
            System.out.printf(color+"%10.2f"+ConsoleColors.RESET, regularMarketChange);
            System.out.printf(color+"%12s"+ConsoleColors.RESET, String.format("(%.2f%%)", regularMarketChangePercent));
            System.out.printf(" %-20s\n", longName);
        }
    }

    private static JSONArray sort(JSONArray result) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            jsonValues.add(result.getJSONObject(i));
        }

        Collections.sort( jsonValues, (a, b) -> {
            double valA = a.getDouble("regularMarketChangePercent");
            double valB = b.getDouble("regularMarketChangePercent");

            if (valA < valB) return 1;
            if (valA > valB) return -1;
            return 0;
        });

        for (int i = 0; i < jsonValues.size(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
}
