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

package org.lajuderia.daos;

import org.lajuderia.communication.SteamAPI;
import org.lajuderia.beans.SteamGame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.lajuderia.beans.MetaInformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Sergio
 */
public class SteamGameDAO {
    public static List<SteamGame> getUserOwnedGames(long userId) {
        List<SteamGame> gameList = new ArrayList<SteamGame>();
        JSONObject jsonGames = null ;
            try {
                jsonGames = SteamAPI.getUserOwnedGames(userId);
            }
            catch(IOException iex){
            }
            if ( jsonGames != null ) {
                for ( int i = 0 ; i < jsonGames.getJSONObject("response").getJSONArray("games").length() ; i++)
                {
                    gameList.add(readGameFromSteamJson(jsonGames.getJSONObject("response").getJSONArray("games").getJSONObject(i)));
                }
            }
            
        return(gameList);
    }    
    /*
    public static List<MetaInformation> searchSimilarMetacriticGames(SteamGame game){
        List<MetaInformation> metaGames = null ;
        JSONArray jsonMetacritic;
            jsonMetacritic = SteamAPI.getSimilarMetacriticGames(game.getName());
            if ( jsonMetacritic != null ){
                metaGames = new ArrayList<MetaInformation>();
                for ( int i = 0 ; i < jsonMetacritic.length() ; i++ )
                    metaGames.add(readGameFromMetacriticJSon(jsonMetacritic.getJSONObject(i)));
            }
            
            return ( metaGames );
    }
    */
    private static SteamGame readGameFromSteamJson(JSONObject json){
        return ( new SteamGame(json.getInt("appid"),json.getString("name")) );
    }    

    private static MetaInformation readGameFromMetacriticJSon(JSONObject gameInfo) {
        MetaInformation game = new MetaInformation();
            try{
                game.setName(gameInfo.getString("name"));
            }catch(JSONException ex){}
            try{
                game.setSummary(gameInfo.getString("summary"));
            }catch(JSONException ex){}
            try{
                game.setGenre(gameInfo.getString("genre"));
            }catch(JSONException ex){}
            try{
                game.setMetascore(gameInfo.getInt("score"));
            }catch(JSONException ex){}
            try{
                game.setUserscore((int) (gameInfo.getDouble("userscore")*10));
            }catch(JSONException ex)
            {}
            return ( game ) ;
    }
}
