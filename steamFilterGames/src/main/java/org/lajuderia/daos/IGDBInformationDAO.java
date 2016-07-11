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
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lajuderia.beans.IGDBInformation;
import org.lajuderia.communication.IGDBAPI;

/**
 * Data access object related to Metacritic information
 * @author Sergio
 */
public class IGDBInformationDAO {
    /**
     * Gets the list of games which title is similar to another from Metacritic
     * @param title the game title
     * @return List of IGDBInformation
     */
    public static List<IGDBInformation> getSimilarGamesFromIGDB(String title){
        List<IGDBInformation> metaInformation = new ArrayList<IGDBInformation>();
        JSONArray jsonIGDB;
            jsonIGDB = IGDBAPI.getSimilarIGDBGames(title);
            if ( jsonIGDB != null ){
                for ( int i = 0 ; i < jsonIGDB.length() ; i++ )
                    metaInformation.add(readGameFromIGDBJSon(jsonIGDB.getJSONObject(i)));
            }
            
            return ( metaInformation );
    }

    private static IGDBInformation readGameFromIGDBJSon(JSONObject gameInfo) {
        ResourceBundle tagsBundle = ResourceBundle.getBundle("JsonTagsBundle");

        int id = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_ID"))
            ? gameInfo.getInt(tagsBundle.getString("IGDB_TAG_ID"))
            : Integer.MIN_VALUE ;

        String name = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_NAME"))
            ? gameInfo.getString(tagsBundle.getString("IGDB_TAG_NAME"))
            : "" ;

        String summary = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_SUMMARY"))
                ? gameInfo.getString(tagsBundle.getString("IGDB_TAG_SUMMARY"))
                : "" ;

        String storyLine = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_STORYLINE"))
                ? gameInfo.getString(tagsBundle.getString("IGDB_TAG_STORYLINE"))
                : "" ;

        String genre = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_GENRE"))
                ? parseGenreIdToDescription(gameInfo.getJSONArray(tagsBundle.getString("IGDB_TAG_GENRE")).getInt(0))
                : "" ;

        float rating = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_RATING"))
                ? (float) gameInfo.getDouble(tagsBundle.getString("IGDB_TAG_RATING"))
                : 0 ;

        float aggregatedRating = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_AGGREGATED_RATING"))
                ? (float) gameInfo.getDouble(tagsBundle.getString("IGDB_TAG_AGGREGATED_RATING"))
                : 0 ;

        int t2bHastily = 0, t2bNormally = 0, t2bCompletely = 0;
            if ( !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_TIME_TO_BEAT")) ) {
                JSONObject jsonT2b = gameInfo.getJSONObject(tagsBundle.getString("IGDB_TAG_TIME_TO_BEAT")) ;
                    t2bHastily = !jsonT2b.isNull(tagsBundle.getString("IGDB_TAG_T2B_HASTILY"))
                            ? jsonT2b.getInt(tagsBundle.getString("IGDB_TAG_T2B_HASTILY"))
                            : 0 ;

                     t2bNormally = !jsonT2b.isNull(tagsBundle.getString("IGDB_TAG_T2B_NORMALLY"))
                            ? jsonT2b.getInt(tagsBundle.getString("IGDB_TAG_T2B_NORMALLY"))
                            : 0 ;

                     t2bCompletely = !jsonT2b.isNull(tagsBundle.getString("IGDB_TAG_T2B_COMPLETELY"))
                            ? jsonT2b.getInt(tagsBundle.getString("IGDB_TAG_T2B_COMPLETELY"))
                            : 0 ;
            }

        String coverCloudinaryId = !gameInfo.isNull(tagsBundle.getString("IGDB_TAG_COVER"))
                ? gameInfo.getJSONObject(tagsBundle.getString("IGDB_TAG_COVER"))
                    .getString(tagsBundle.getString("IGDB_TAG_COVER_CLOUD_ID"))
                : "" ;

        return ( new IGDBInformation(id, name, summary, storyLine, genre, rating, aggregatedRating , t2bHastily/3600, t2bNormally/3600, t2bCompletely/3600, coverCloudinaryId) ) ;
    }

    private static String parseGenreIdToDescription(int igdbGenreId) {
        //TODO: Fix this function
        String result = null;
            switch(igdbGenreId) {
                case 33: result = "Arcade"; break;
                case 32: result = "Indie"; break;
                case 31: result = "Adventure"; break;
                case 30: result = "Pinball"; break;
                case 26: result = "Quiz/Trivia"; break;
                case 25: result = "Hack and slash/Beat 'em up"; break;
                case 24: result = "Tactical"; break;
                case 16: result = "Turn-based strategy (TBS)"; break;
                case 15: result = "Strategy"; break;
                case 14: result = "Sport"; break;
                case 13: result = "Simulator"; break;
                case 12: result = "Role-playing (RPG)"; break;
                case 11: result = "Real Time Strategy (RTS)"; break;
                case 10: result = "Racing"; break;
                case 9:  result = "Puzzle"; break;
                case 8:  result = "Platform"; break;
                case 7:  result = "Music"; break;
                case 5:  result = "Shooter"; break;
                case 4:  result = "Fighting"; break;
                case 2:  result = "Point-and-click"; break;
                default: result = "Other";
            }
            return ( result );        
    }

    /**
     * Gets the IGDB information related to a game
     * @param title the game title
     * @return IGDBInformation
     */
    public static IGDBInformation findInfoByTitle(String title) {
        IGDBInformation igdbInformation = null;
        JSONObject json;
            json = IGDBAPI.getIGDBInfo(title);
            if ( json != null ) {
                igdbInformation = readGameFromIGDBJSon(json);
            }
        
        return (igdbInformation);
    }
}
