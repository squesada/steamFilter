/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lajuderia.steamfiltergames;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class SteamGame {
    private final int _id;
    private final String _name;
    private final String _description;
    private final String _genre;
    private final int _metascore ;
    private final int _userscore ;    
    
    public SteamGame(int id , String name, String description, String genre,
            int metascore, int userscore){
        this._id = id;
        this._name = name;
        this._description = description;
        this._genre = genre;
        this._metascore = metascore;
        this._userscore = userscore;
    }
    
    public int getId(){
        return ( this._id ) ;
    }
    
    public String getName(){
        return ( this._name ) ;
    }
    
    public String getDescription(){
        return ( this._description ) ;
    }
    
    public String getGenre(){
        return ( this._genre ) ;
    }
    
    public int getMetascore(){
        return ( this._metascore ) ;
    }
    
    public int getUserscore(){
        return ( this._userscore ) ;
    }
}
