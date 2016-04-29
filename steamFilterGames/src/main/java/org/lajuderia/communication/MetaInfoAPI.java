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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class MetaInfoAPI {
    private static final String MASHAPE_DEV_KEY = java.util.ResourceBundle.getBundle("ConfigurationBundle").getString("MASHAPE_DEV_KEY");

    /**
     * Gets the Metacritic information related to the game
     * @param gameTitle The title game
     * @return JSONObject
     */
    public static JSONObject getMetacriticInfo(String gameTitle) {
        JSONObject gameInfo = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.get("https://ahmedakhan-game-review-information-v1.p.mashape.com/api/v1/information?game_name=" + gameTitle)
                        .header("X-Mashape-Key", MASHAPE_DEV_KEY)
                        .header("Accept", "application/json")
                        .asJson();

                    if ( response.getBody().getObject().has("result") )
                        gameInfo = response.getBody().getObject().getJSONObject("result");
            } catch(UnirestException uex){
            } catch(JSONException jex){
            }
            return ( gameInfo ) ;
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
        JSONObject json = new JSONObject(readAll(rd));
            return json;
      } finally {
        is.close();
      }
    }

    /**
     * Gets the game list with similar title from Metacritic
     * @param metaTitle The title game
     * @return JSONArray
     */
    public static JSONArray getSimilarMetacriticGames(String metaTitle) {
        JSONArray similarGames = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.get("https://ahmedakhan-game-review-information-v1.p.mashape.com/api/v1/search?game_name=" + metaTitle)
                        .header("X-Mashape-Key", MASHAPE_DEV_KEY)
                        .header("Accept", "application/json")
                        .asJson();

                    if ( response.getBody().getObject().has("result") )
                        similarGames = response.getBody().getObject().getJSONArray("result");
            } catch(UnirestException uex){
                //TODO: Controlar si no hay conexión a Internet
            } catch(JSONException jex){                
            }
            
            return ( similarGames ) ;
    }
}