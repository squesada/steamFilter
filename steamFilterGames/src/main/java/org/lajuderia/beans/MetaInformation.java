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
 * @author Sergio
 */
public class MetaInformation {
    private String _name;
    private String _genre;
    private String _summary;
    private int _metascore;
    private int _userscore;
    
    public MetaInformation(){
    }
    
    public MetaInformation(String name, String summary, String genre, 
            int metascore, int userscore){
        this._name = name;
        this._summary = summary;
        this._genre = genre;
        this._metascore = metascore;
        this._userscore = userscore;
    }

    /**
     * @return the _name
     */
    public String getTitle() {
        return _name;
    }

    /**
     * @param _name the _name to set
     */
    public void setName(String _name) {
        this._name = _name;
    }

    /**
     * @return the _genre
     */
    public String getGenre() {
        return _genre;
    }

    /**
     * @param _genre the _genre to set
     */
    public void setGenre(String _genre) {
        this._genre = _genre;
    }

    /**
     * @return the _summary
     */
    public String getSummary() {
        return _summary;
    }

    /**
     * @param _summary the _summary to set
     */
    public void setSummary(String _summary) {
        this._summary = _summary;
    }

    /**
     * @return the _metascore
     */
    public int getMetascore() {
        return _metascore;
    }

    /**
     * @param _metascore the _metascore to set
     */
    public void setMetascore(int _metascore) {
        this._metascore = _metascore;
    }

    /**
     * @return the _userscore
     */
    public int getUserscore() {
        return _userscore;
    }

    /**
     * @param _userscore the _userscore to set
     */
    public void setUserscore(int _userscore) {
        this._userscore = _userscore;
    }
    
    @Override
    public String toString(){
        return ( new StringBuilder()
                .append(_name)
                .append(" (MSC:")
                .append(Integer.toString(_metascore))
                .append(")")
                .toString() );
    }
    
    @Override
    public boolean equals(Object obj) {
        return (
                (obj != null)
                && (obj instanceof MetaInformation)
                && (this._name.equals(((MetaInformation) obj)._name))
                && (this._genre.equals(((MetaInformation) obj)._genre))
                && (this._summary.equals(((MetaInformation) obj)._summary))
                && (this._metascore == ((MetaInformation) obj)._metascore)
                && (this._userscore == ((MetaInformation) obj)._userscore)
                );        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this._name != null ? this._name.hashCode() : 0);
        hash = 89 * hash + (this._genre != null ? this._genre.hashCode() : 0);
        hash = 89 * hash + (this._summary != null ? this._summary.hashCode() : 0);
        hash = 89 * hash + this._metascore;
        hash = 89 * hash + this._userscore;
        return hash;
    }
    
}