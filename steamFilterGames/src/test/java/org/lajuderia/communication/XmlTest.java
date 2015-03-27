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
package org.lajuderia.communication;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.lajuderia.beans.Game;
import org.lajuderia.exceptions.CustomException;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.xml.sax.SAXException;

/**
 *
 * @author Sergio
 */
public class XmlTest {
    
    private class XmlMock extends Xml{
        @Override
            protected DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException {
                DocumentBuilder documentBuilder;
                documentBuilder = Mockito.mock(DocumentBuilder.class);
                try {
                    when(documentBuilder.parse(Mockito.any(java.io.File.class))).thenThrow(IOException.class);
                } catch (SAXException ex) {
                } catch (IOException ex) {
                }
                
                return ( documentBuilder );
            }
    }
    
    public XmlTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of loadGamesFromDisk method, of class Xml.
     * @throws org.lajuderia.exceptions.CustomException
     */
    @Test(expected = CustomException.class)
    public void testLoadGamesFromDiskFileNotFoundThrowsException() throws CustomException {
        new XmlMock().loadGamesFromDisk();
        fail();
    }
    
}
