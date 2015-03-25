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
package org.lajuderia.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.lajuderia.components.JCheckBoxList;

/**
 *
 * @author Sergio
 */
public class GameListView extends JFrame {
    private JButton _btAddGame;
    private JButton _btRemoveGame;
    private JButton _btImportFromSteam;
    private JButton _btLoadXML;
    private JButton _btMetaSimilar;
    private JButton _btMetacritic;
    private JButton _btSaveXML;
    private JButton _btViewGame;
    private JButton _btRefreshGenres;
    private JTable _jtGames;
    private JLabel _lbStatus;
    private JCheckBoxList _cblGenreFilter;
    
    public GameListView(){
        super();
        initComponents();
    }

    private void initComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        //setResizable(false);
        
        //North        
        _btAddGame = new JButton(bundle.getString("ADD_GAME"));
        _btRemoveGame = new JButton(bundle.getString("REMOVE_GAME"));
        _btViewGame = new JButton(bundle.getString("VIEW_GAME"));
        _btImportFromSteam = new JButton(bundle.getString("IMPORT_GAMES_FROM_STEAM"));
        _btLoadXML = new JButton(bundle.getString("LOAD_GAMES"));
        _btSaveXML = new JButton(bundle.getString("SAVE_GAMES"));
        _btMetacritic = new JButton(bundle.getString("IMPORT_METACRITIC"));
        _btMetaSimilar = new JButton(bundle.getString("IMPORT_METACRITIC_MANUAL"));
        
        JPanel pnlMenu = new JPanel(new FlowLayout());
            pnlMenu.add(_btAddGame);
            pnlMenu.add(_btRemoveGame);
            pnlMenu.add(_btViewGame);
            pnlMenu.add(_btLoadXML);
            pnlMenu.add(_btSaveXML);
            pnlMenu.add(_btImportFromSteam);
            pnlMenu.add(_btMetacritic);
            pnlMenu.add(_btMetaSimilar);            
        
            this.add(pnlMenu, BorderLayout.NORTH);
        
        //Center
        JScrollPane scrGames = new JScrollPane();
            _jtGames = new JTable();
            _jtGames.setAutoCreateRowSorter(true);
            scrGames.setViewportView(_jtGames);
            this.add(scrGames, BorderLayout.CENTER);
                
        //South
        _lbStatus = new JLabel();
        _lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.add(_lbStatus, BorderLayout.SOUTH);
        
        //West
        _btRefreshGenres = new JButton(bundle.getString("REFRESH"));
        _cblGenreFilter = new JCheckBoxList();
            
        JPanel pnlGenres = new JPanel(new BorderLayout());
            pnlGenres.setBorder(BorderFactory.createEtchedBorder());
            JScrollPane scrGenres = new JScrollPane();
                scrGenres.setViewportView(_cblGenreFilter);
                pnlGenres.add(scrGenres, BorderLayout.CENTER);

                pnlGenres.add(_btRefreshGenres, BorderLayout.SOUTH);
            
            this.add(pnlGenres, BorderLayout.WEST);        
            
        pack();
        
        setLocationRelativeTo(null);
    }
    
    public void setStatusBarMessage(String message){
        this._lbStatus.setText(message);
    }
    
    public void registerActionListener(ActionListener listener){
        this._btLoadXML.addActionListener(listener);
        this._btImportFromSteam.addActionListener(listener);
        this._btSaveXML.addActionListener(listener);
        this._btMetacritic.addActionListener(listener);
        this._btMetaSimilar.addActionListener(listener);
        this._btAddGame.addActionListener(listener);
        this._btRemoveGame.addActionListener(listener);
        this._btViewGame.addActionListener(listener);
        this._btRefreshGenres.addActionListener(listener);
    }
    
    public void registerListModelListener(ListSelectionListener listener) {
        this._cblGenreFilter.addListSelectionListener(listener);
    }
    
    public void setTableModel(TableModel tableModel) {
       _jtGames.setModel(tableModel);
    }
    
    public int[] getSelectedModelRows() {
        int[] selectedRows;
        int[] selectedModelRows;
            selectedRows = _jtGames.getSelectedRows();
            selectedModelRows = new int[selectedRows.length];
                for ( int i = 0 ; i < selectedRows.length ; i++ )
                    selectedModelRows[i] = _jtGames.convertRowIndexToModel(selectedRows[i]);
                
        return ( selectedModelRows );
    }

    public void updateGenreCheckBoxList(String[] genres) {
        //TODO: Controlar que cuando se actualice, también se registre el listener
        DefaultListModel<JCheckBox> genreModel = new DefaultListModel<JCheckBox>();
         
        for ( String genre : genres ) {
            genreModel.addElement(new JCheckBox(genre));
        }
        
        this._cblGenreFilter.setModel(genreModel);
        _cblGenreFilter.updateUI();
    }
    
    public List<String> getSelectedGenres() {
        List<String> genres = new ArrayList<String>();
            for ( int i = 0 ; i < _cblGenreFilter.getModel().getSize() ; i++ ) {
                if ( _cblGenreFilter.getModel().getElementAt(i).isSelected() ) {
                    genres.add(_cblGenreFilter.getModel().getElementAt(i).getText());
                }
            }
        return ( genres );
    }

    public void setRowSorter(RowSorter sortedModel) {
        this._jtGames.setRowSorter(sortedModel);
    }

    public RowSorter getTableRowSorter() {
        return ( this._jtGames.getRowSorter() );
    }
    
}
