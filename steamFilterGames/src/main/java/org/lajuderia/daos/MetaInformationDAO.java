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

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lajuderia.beans.MetaInformation;
import org.lajuderia.communication.MetaInfoAPI;

/**
 * Data access object related to Metacritic information
 * @author Sergio
 */
public class MetaInformationDAO {
    
    /**
     * Gets the list of games which title is similar to another from Metacritic
     * @param title the game title
     * @return List of MetaInformation
     */
    public static List<String> getSimilarGamesFromMetacritic(String title){
        List<String> metaInformation = new ArrayList<String>();
        JSONArray jsonMetacritic;
            jsonMetacritic = MetaInfoAPI.getSimilarMetacriticGames(title);
            if ( jsonMetacritic != null ){                
                for ( int i = 0 ; i < jsonMetacritic.length() ; i++ )
                    metaInformation.add(jsonMetacritic.getString(i));
                    //metaInformation.add(readGameFromMetacriticJSon(jsonMetacritic.getJSONObject(i)));
            }
            
            return ( metaInformation );
    }

    private static MetaInformation readGameFromMetacriticJSon(JSONObject gameInfo) {
        String title = !gameInfo.isNull("name") && gameInfo.get("name") instanceof String
            ? gameInfo.getString("name")
            : "";

        String summary = "";

        String genre = !gameInfo.isNull("genre") && gameInfo.get("genre") instanceof JSONArray
                && gameInfo.getJSONArray("genre").length() > 0
            ? gameInfo.getJSONArray("genre").getString(0)
            : "";

        int metascore;
        int userscore;

        if (!gameInfo.isNull("metacritic") && gameInfo.get("metacritic") instanceof JSONObject) {
            metascore = !gameInfo.getJSONObject("metacritic").isNull("criticScore")
                    && gameInfo.getJSONObject("metacritic").getString("criticScore").matches("[+-]?\\d*(\\.\\d+)?")
                    && !gameInfo.getJSONObject("metacritic").getString("criticScore").equals("")
                    ? gameInfo.getJSONObject("metacritic").getInt("criticScore")
                    : 0 ;

            userscore = !gameInfo.getJSONObject("metacritic").isNull("userScore")
                    && gameInfo.getJSONObject("metacritic").getString("userScore").matches("[+-]?\\d*(\\.\\d+)?")
                    ? (int) (gameInfo.getJSONObject("metacritic").getDouble("userScore")*10)
                    : 0;
        }
        else
            metascore = userscore = 0 ;

        return ( new MetaInformation(title, summary, genre, metascore, userscore) ) ;
    }

    /**
     * Gets the Metacritic information related to a game
     * @param title the game title
     * @return MetaInformation
     */
    public static MetaInformation findMetaInfoByTitle(String title) {
        MetaInformation metaInformation = null;
        JSONObject json;
            json = MetaInfoAPI.getMetacriticInfo(title);
            if ( json != null ) {
                metaInformation = readGameFromMetacriticJSon(json);
            }
        
        return ( metaInformation );
    }
}
