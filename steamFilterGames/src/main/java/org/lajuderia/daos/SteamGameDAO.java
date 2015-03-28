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
import org.json.JSONObject;

/**
 * Data Access Object related to Steam games
 * @author Sergio
 */
public class SteamGameDAO {
    
    /**
     * Gets the game list the user has in his account
     * @param userId
     * @return 
     */
    public static List<SteamGame> getUserOwnedGames(long userId) {
        List<SteamGame> gameList = new ArrayList<SteamGame>();
        JSONObject jsonGames = null ;
            try {
                jsonGames = SteamAPI.getUserOwnedGames(userId);
            }
            catch(IOException iex){
                //TODO: Manage exception
            }
            if ( jsonGames != null ) {
                for ( int i = 0 ; i < jsonGames.getJSONObject("response").getJSONArray("games").length() ; i++)
                {
                    gameList.add(readGameFromSteamJson(jsonGames.getJSONObject("response").getJSONArray("games").getJSONObject(i)));
                }
            }
            
        return(gameList);
    }
    
    private static SteamGame readGameFromSteamJson(JSONObject json){
        return ( new SteamGame(json.getInt("appid"),json.getString("name")) );
    }
}
