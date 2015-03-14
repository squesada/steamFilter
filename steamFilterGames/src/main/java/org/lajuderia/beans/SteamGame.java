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

package org.lajuderia.beans;

/**
 *
 * @author Sergio Quesada <squesada.dev@gmail.com>
 */
public class SteamGame {
    private int _id;
    private String _name;
    private boolean _completed;
    private String _genre;
    private MetacriticGame _metagame;
    
    public SteamGame(){
        this._metagame = new MetacriticGame();
    }
     
    public SteamGame(int id , String name, String genre, Boolean completed){
        this();
        this._id = id;
        this._name = name;
        this._genre = genre;
        this._completed = completed;
    }   
    
    public int getId(){
        return ( this._id ) ;
    }
    
    public void setId(int id){
        this._id = id ;
    }
    
    public String getName(){
        return ( this._name ) ;
    }
    
    public void setName(String name){
        this._name = name;
    }

    /**
     * @return the _completed
     */
    public boolean isCompleted() {
        return _completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this._completed = completed;
    }

    /**
     * @return the _metagame
     */
    public MetacriticGame getMetagame() {
        return _metagame;
    }

    /**
     * @param metagame the metagame to set
     */
    public void setMetagame(MetacriticGame metagame) {
        this._metagame = metagame;
    }

    /**
     * @return the _genre
     */
    public String getGenre() {
        String genre ;
            if ( _genre != null && !_genre.isEmpty() )
                genre = _genre;
            else
                genre = _metagame.getGenre();
            
            return ( genre );
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this._genre = genre;
    }
}
