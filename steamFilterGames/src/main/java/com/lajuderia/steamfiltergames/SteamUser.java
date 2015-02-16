/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lajuderia.steamfiltergames;

import java.util.List;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class SteamUser {
    private long _userId;
    private String _userName;
    private List<SteamGame> _ownedGames ;
    
    public SteamUser(){
        
    }
    
    public long getUserId(){
        return ( this._userId );
    }
    
    public void setUserId(long userId){
        this._userId = userId;
    }
    
    public String getUserName(){
        return ( this._userName );
    }
    
    public void setUserId(String userName){
        this._userName = userName;
    }
    
    public List<SteamGame> getOwnedGames(){
        return ( this._ownedGames );
    }
    
    public void addOwnedGame(SteamGame game){
        this._ownedGames.add(game);
    }
}
