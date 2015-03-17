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
import org.lajuderia.communication.SteamAPI;

/**
 *
 * @author Sergio
 */
public class MetaInformationDAO {
    public static List<MetaInformation> getSimilarGamesFromMetacritic(String title){
        List<MetaInformation> metaInformation = null ;
        JSONArray jsonMetacritic;
            jsonMetacritic = SteamAPI.getSimilarMetacriticGames(title);
            if ( jsonMetacritic != null ){
                metaInformation = new ArrayList<MetaInformation>();
                for ( int i = 0 ; i < jsonMetacritic.length() ; i++ )
                    metaInformation.add(readGameFromMetacriticJSon(jsonMetacritic.getJSONObject(i)));
            }
            
            return ( metaInformation );
    }

    private static MetaInformation readGameFromMetacriticJSon(JSONObject gameInfo) {
        String title = !gameInfo.isNull("name") && gameInfo.get("name") instanceof String
            ? gameInfo.getString("name")
            : "";
        String summary = !gameInfo.isNull("summary") && gameInfo.get("summary") instanceof String 
            ? gameInfo.getString("summary") 
            : "";
        String genre = !gameInfo.isNull("genre") && gameInfo.get("genre") instanceof String 
            ? gameInfo.getString("genre") 
            : "";
        int metascore = !gameInfo.isNull("score") && gameInfo.get("score") instanceof String && gameInfo.getString("score").matches("[+-]?\\d*(\\.\\d+)?") && !gameInfo.getString("score").equals("")
            ? gameInfo.getInt("score") 
            : 0 ;
        int userscore = !gameInfo.isNull("userscore") && ( gameInfo.get("userscore") instanceof Double || gameInfo.get("userscore") instanceof Integer )
            ? (int) (gameInfo.getDouble("userscore")*10) 
            : 0;

        return ( new MetaInformation(title, summary, genre, metascore, userscore) ) ;
    }

    public static MetaInformation getMetaInfoByTitle(String title) {
        MetaInformation metaInformation = null;
        JSONObject json;
            json = SteamAPI.getMetacriticInfo(title);
            if ( json != null ) {
                metaInformation = readGameFromMetacriticJSon(json);
            }
        
        return ( metaInformation );
    }
}
