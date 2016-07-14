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
public class IGDBAPI {
    private static final String MASHAPE_DEV_KEY = java.util.ResourceBundle.getBundle("ConfigurationBundle").getString("IGDB_DEV_KEY");
    private static final int HTTP_OK_STATUS_CODE = 200;

    /**
     * Gets the Metacritic information related to the game
     * @param gameId The game id
     * @return JSONObject
     */
    public static JSONObject getIGDBInfoById(int gameId) {
        JSONObject gameInfo = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.get("https://igdbcom-internet-game-database-v1.p.mashape.com/games/" + gameId +
                        "?fields=id,name,summary,storyline,genres,rating,aggregated_rating,time_to_beat,cover.cloudinary_id")
                        .header("X-Mashape-Key", MASHAPE_DEV_KEY)
                        .header("Accept", "application/json")
                        .asJson();

                if ( response.getCode() == HTTP_OK_STATUS_CODE )
                    gameInfo = response.getBody().getArray().getJSONObject(0);
            } catch (UnirestException | JSONException ignored){}

        return ( gameInfo ) ;
    }

    /**
     * Gets the Metacritic information related to the game
     * @param gameTitle The title game
     * @return JSONObject
     */
    public static JSONObject getIGDBInfoByTitle(String gameTitle) {
        JSONObject gameInfo = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.get("https://igdbcom-internet-game-database-v1.p.mashape.com/games/?" +
                        "fields=id,name,summary,storyline,genres,rating,aggregated_rating,time_to_beat,cover.cloudinary_id&limit=2&filter[name][eq]=" + gameTitle)
                        .header("X-Mashape-Key", MASHAPE_DEV_KEY)
                        .header("Accept", "application/json")
                        .asJson();

                    if (
                            response.getCode() == HTTP_OK_STATUS_CODE &&
                            response.getBody().getArray().length() == 1
                        )
                        gameInfo = response.getBody().getArray().getJSONObject(0);
            } catch(UnirestException | JSONException ignored) {}
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
        JSONObject json ;
            InputStream is = new URL(url).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    json = new JSONObject(readAll(rd));
                    is.close();

        return json;
    }

    /**
     * Gets the game list with similar title from Metacritic
     * @param metaTitle The title game
     * @return JSONArray
     */

    public static JSONArray getSimilarIGDBGames(String metaTitle) {
        JSONArray similarGames = null;
        HttpResponse<JsonNode> response;
            try{
                response = Unirest.get("https://igdbcom-internet-game-database-v1.p.mashape.com/games/?" +
                        "fields=id,name,summary,storyline,genres,rating,aggregated_rating,time_to_beat,cover.cloudinary_id&limit=20&search=" + metaTitle)
                        .header("X-Mashape-Key", MASHAPE_DEV_KEY)
                        .header("Accept", "application/json")
                        .asJson();

                    if (
                        response.getCode() == HTTP_OK_STATUS_CODE
                            && response.getBody().getArray().length() > 0
                        )
                        similarGames = response.getBody().getArray();
            } catch(UnirestException | JSONException ignored){
                //TODO: Controlar si no hay conexión a Internet
            }

        return ( similarGames ) ;
    }
}
