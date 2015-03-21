/*
 * The MIT License
 *
 * Copyright 2015 Sergio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
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
public abstract class AbstractPlatformGame {
    private int _id;
    private String _name;
    protected PlatformGame _platform ;
    
    protected AbstractPlatformGame(PlatformGame platform){
        this._platform = platform;
    }
    
    protected AbstractPlatformGame(int id, String name, PlatformGame platform){
        this._id = id;
        this._name = name;
        this._platform = platform;
    }
    
    public int getId(){
        return ( this._id ) ;
    }
    
    public void setId(int id){
        this._id = id ;
    }
    
    public String getTitle(){
        return ( this._name ) ;
    }
    
    public void setName(String name){
        this._name = name;
    }
    
    public PlatformGame getPlatform(){
        return (_platform);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (
                (obj != null)
                && (obj instanceof AbstractPlatformGame)
                && (this._id == ((AbstractPlatformGame) obj)._id)
                && (this._name.equals(((AbstractPlatformGame) obj)._name))
                && (this._platform == ((AbstractPlatformGame) obj)._platform)
                );
    }
        
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this._id;
        hash = 97 * hash + (this._name != null ? this._name.hashCode() : 0);
        hash = 97 * hash + (this._platform != null ? this._platform.hashCode() : 0);
        return hash;
    }
    
    public enum PlatformGame{
        STEAM('S', "Steam") ,
        ORIGIN('O', "Origin") ,
        DESURA('D', "Desura") ,
        UPLAY('U', "UPlay") ,
        GOG('G', "GOG"),
        DEFAULT_PLATFORM('?',java.util.ResourceBundle.getBundle("TextsBundle").getString("DEFAULT_PLATFORM"));
        
        private final char _prefix;
        private final String _name;
        
        private PlatformGame(char prefix, String value){
            this._prefix = prefix;
            this._name = value;
        }
        
        public char getPrefix(){
            return(_prefix);
        }
        
        public String toPrettyString(){
            return ( _name );
        }
    }
}
