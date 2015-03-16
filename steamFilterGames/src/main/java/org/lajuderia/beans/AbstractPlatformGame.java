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
    protected Platform _platform ;
    
    protected AbstractPlatformGame(Platform platform){
        this._platform = platform;
    }
    
    protected AbstractPlatformGame(int id, String name, Platform platform){
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
    
    public String getName(){
        return ( this._name ) ;
    }
    
    public void setName(String name){
        this._name = name;
    }
    
    public Platform getPlatform(){
        return (_platform);
    }
    
    public enum Platform{
        STEAM('S', "Steam") , ORIGIN('O', "Origin") , DESURA('D', "Desura") , UPLAY('U', "Uplay") , GOG('G', "GOG"), DEFAULT_PLATFORM('?',"Default");
        private char _prefix;
        private String _name;
        
        private Platform(char prefix, String value){
            this._prefix = prefix;
            this._name = value;
        }
        
        public char getPrefix(){
            return(_prefix);
        }
        
        /*
        @Override
        public String toString() {
            return(_name);
        }
        */
    }
}
