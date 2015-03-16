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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR DEFAULT_PLATFORM
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR DEFAULT_PLATFORM DEALINGS IN
 * THE SOFTWARE.
 */
package org.lajuderia.beans;

/**
 *
 * @author Sergio
 */
public class Game {
    private String _name;
    private String _genre;
    private AbstractPlatformGame _platformGame;
    private MetaInformation _metaInformation;
    private boolean _completed;
    
    public Game(){
        
    }
    
    public Game(AbstractPlatformGame platformGame) {
        this._name = platformGame.getName();
        this._platformGame = platformGame ;
    }
  
    /**
     * @return the _id
     */
    public String getId() {
        return ( new StringBuilder()
                    .append(_platformGame.getPlatform().getPrefix())
                    .append(_platformGame.getId()).toString() );
    }

    /**
     * @return the _name
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name the _name to set
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * @return the _genre
     */
    public String getGenre() {
        return _genre;
    }

    /**
     * @param genre the _genre to set
     */
    public void setGenre(String genre) {
        this._genre = genre;
    }

    /**
     * @return the _platformGame
     */
    public AbstractPlatformGame getAssociatedGame() {
        return _platformGame;
    }

    /**
     * @param associatedGame the _platformGame to set
     */
    public void setAssociatedGame(AbstractPlatformGame associatedGame) {
        this._platformGame = associatedGame;
    }

    /**
     * @return the _metaInformation
     */
    public MetaInformation getMetaInformation() {
        return _metaInformation;
    }

    /**
     * @param metaInformation the _metaInformation to set
     */
    public void setMetaInformation(MetaInformation metaInformation) {
        this._metaInformation = metaInformation;
    }

    /**
     * @return the _completed
     */
    public boolean isCompleted() {
        return _completed;
    }

    /**
     * @param _completed the _completed to set
     */
    public void setCompleted(boolean _completed) {
        this._completed = _completed;
    }
    
    public boolean hasMetaInformation(){
        return ( this._metaInformation != null ) ;
    }    

    public boolean hasAssociatedPlatformGame() {
        return ( this._platformGame != null );
    }
}
