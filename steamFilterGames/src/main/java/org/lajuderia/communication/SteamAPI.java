/*
 * The MIT License
 *
 * Copyright 2015 Sergio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.lajuderia.communication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class SteamAPI {
    private static final String STEAM_DEV_KEY = java.util.ResourceBundle.getBundle("ConfigurationBundle").getString("STEAM_DEV_KEY");
    private static final String METACRITIC_DEV_KEY = java.util.ResourceBundle.getBundle("ConfigurationBundle").getString("METACRITIC_DEV_KEY");

    public static JSONObject getMetacriticInfo(String gameTitle) {
        JSONObject gameInfo = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.post("https://byroredux-metacritic.p.mashape.com/find/game")
                    .header("X-Mashape-Key", METACRITIC_DEV_KEY)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .field("platform", 3)
                    .field("retry", "4")
                    .field("title", gameTitle)
                    .asJson();
                
                    if ( response.getBody().getObject().has("result") )
                        gameInfo = response.getBody().getObject().getJSONObject("result");
            } catch(UnirestException uex){
            } catch(JSONException jex){
            }
            return ( gameInfo ) ;
    }
    
    public static JSONObject getUserOwnedGames(long userId) throws IOException{
        String url;
        url = new StringBuilder()
                .append("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/")
                .append("?key=").append(STEAM_DEV_KEY)
                .append("&steamid=").append(userId)
                .append("&include_appinfo=1")
                .append("&format=json")
                .toString();
        
            return ( readJsonFromUrl(url) ) ;
    }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException {
      InputStream is = new URL(url).openStream();
      try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        JSONObject json = new JSONObject(jsonText);
        return json;
      } finally {
        is.close();
      }
    }

    public static JSONArray getSimilarMetacriticGames(String metaTitle) {
        JSONArray similarGames = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.post("https://byroredux-metacritic.p.mashape.com/search/game")
                    .header("X-Mashape-Key", METACRITIC_DEV_KEY)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .field("max_pages", 1)
                    .field("platform", "3")
                    .field("retry","4")
                    .field("title", metaTitle)
                    .asJson();
                
                    if ( response.getBody().getObject().has("results") )
                        similarGames = response.getBody().getObject().getJSONArray("results");
            } catch(UnirestException uex){
                //TODO: Controlar si no hay conexión a Internet
            } catch(JSONException jex){                
            }
            
            return ( similarGames ) ;
    }
}
