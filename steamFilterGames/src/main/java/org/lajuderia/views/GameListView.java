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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

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
    private JTable _jtGames;
    private JLabel _lbStatus;
    
    public GameListView(){
        super();
        initComponents();
    }

    private void initComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("LabelsBundle");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        
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
        JScrollPane jScrollPane = new JScrollPane();
            _jtGames = new JTable();
            _jtGames.setAutoCreateRowSorter(true);
            jScrollPane.setViewportView(_jtGames);
            this.add(jScrollPane, BorderLayout.CENTER);
                
        //South
        _lbStatus = new JLabel();
        _lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        this.add(_lbStatus, BorderLayout.SOUTH);
        
            
        pack();
        
        setLocationRelativeTo(null);
    }
    
    public void setStatusBarMessage(String message){
        this._lbStatus.setText(message);
    }
    
    public void registerListener(ActionListener listener){
        this._btLoadXML.addActionListener(listener);
        this._btImportFromSteam.addActionListener(listener);
        this._btSaveXML.addActionListener(listener);
        this._btMetacritic.addActionListener(listener);
        this._btMetaSimilar.addActionListener(listener);
        this._btAddGame.addActionListener(listener);
        this._btRemoveGame.addActionListener(listener);
        this._btViewGame.addActionListener(listener);
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
    
}
