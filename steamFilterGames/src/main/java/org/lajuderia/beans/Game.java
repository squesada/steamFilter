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
    private String _title;
    private String _genre;
    private AbstractPlatformGame _platformGame;
    private IGDBInformation _igdbInformation;
    private boolean _completed;
    private boolean _favourite;   
    
    public Game(){
        _platformGame = new DefaultPlatformGame();
    }
        
    public Game(AbstractPlatformGame platformGame) {
        this._title = platformGame.getTitle();
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
     * @return the _title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this._title = title;
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
     * @return the _igdbInformation
     */
    public IGDBInformation getIGDBInformation() {
        return _igdbInformation;
    }

    /**
     * @param igdbInformation the _igdbInformation to set
     */
    public void setMetaInformation(IGDBInformation igdbInformation) {
        this._igdbInformation = igdbInformation;

        if ( ( igdbInformation != null) &&
                ((_genre == null) || (_genre.isEmpty()))
                )
            _genre = igdbInformation.getGenre();
    }

    /**
     * @return the _completed
     */
    public boolean isCompleted() {
        return _completed;
    }

    /**
     * @param completed the _completed to set
     */
    public void setCompleted(boolean completed) {
        this._completed = completed;
    }
    
    public boolean hasIGDBInformation(){
        return ( this._igdbInformation != null ) ;
    }    

    public boolean hasAssociatedPlatformGame() {
        return ( this._platformGame != null );
    }
    
     public boolean isFavourite() {
        return _favourite;
    }

    public void setFavourite(boolean favourite) {
        this._favourite = favourite;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (
                (obj != null)
                && (obj instanceof Game)
                && (_title.equals(((Game) obj)._title))
                && (_genre.equals(((Game) obj)._genre))
                && (_completed == ((Game) obj)._completed)
                && (_platformGame.equals(((Game) obj)._platformGame))
                && (_igdbInformation.equals(((Game) obj)._igdbInformation))
                );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this._title != null ? this._title.hashCode() : 0);
        hash = 23 * hash + (this._genre != null ? this._genre.hashCode() : 0);
        hash = 23 * hash + (this._platformGame != null ? this._platformGame.hashCode() : 0);
        hash = 23 * hash + (this._igdbInformation != null ? this._igdbInformation.hashCode() : 0);
        hash = 23 * hash + (this._completed ? 1 : 0);
        return hash;
    }
}
