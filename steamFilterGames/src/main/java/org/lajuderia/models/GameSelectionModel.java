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
package org.lajuderia.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;

import org.lajuderia.beans.IGDBInformation;
import org.lajuderia.daos.IGDBInformationDAO;

/**
 * Model class related to the game selection functionality
 * @author Sergio
 */
public class GameSelectionModel extends AbstractListModel {
    private List<IGDBInformation> _gameList = new ArrayList<IGDBInformation>();
    
    /**
     * Updates the model with IGDBInformation games with title similar to another
     * @param title the game title
     * @return Integer (Number of similar games)
     */
    public int updateModelWithSimilarMetainfoTo(String title) {
        _gameList = IGDBInformationDAO.getSimilarGamesFromIGDB(title);
        fireContentsChanged(this, 0, _gameList.size()-1);
                
        return ( _gameList.size() );
    }
    
    /**
     * Gets the IGDBInformation iterator
     * @return Iterator of IGDBInformation
     */
    public Iterator<IGDBInformation> getMetaInformationIterator() {
        Iterator it = null ;
            if ( _gameList != null ) {
                it = _gameList.iterator();
            }
            
        return ( it );
    }

    public int getSize() {
        return (_gameList.size());
    }

    public Object getElementAt(int i) {
        return ( _gameList.get(i) );
    }
}
