/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lajuderia.steamfiltergames;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class SteamAPI {
    private static final String _key = "";

    public static JSONObject getGameInfo(String gameTitle) throws UnirestException{
        HttpResponse<JsonNode> response;
            response = Unirest.post("https://byroredux-metacritic.p.mashape.com/find/game")
                .header("X-Mashape-Key", "")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .field("platform", 3)
                .field("retry", "4")
                .field("title", gameTitle)
                .asJson();
   
            return ( response.getBody().getObject() ) ;
    }
    
    public static JSONObject getUserOwnedGames(String userId) throws UnirestException{
        HttpResponse<JsonNode> response;
            response = Unirest.post("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/")                
                .field("steamid", userId)
                .field("include_appinfo", "1")
                .field("key", _key)
                .field("format", "json")                
                .asJson();
        
            return ( response.getBody().getObject() ) ;
    }
}
