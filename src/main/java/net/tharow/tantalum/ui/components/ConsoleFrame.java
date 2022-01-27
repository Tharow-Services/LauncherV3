/*
 * This file is part of Technic UI Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic UI Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic UI Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic UI Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.ui.components;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;

/**
 * Console dialog for showing console messages.
 *
 * @author sk89q
 *         <p/>
 *         This code reused & relicensed as LGPL v 3 with permission.
 */
public class ConsoleFrame extends JFrame implements MouseListener {
    private static final long serialVersionUID = 1L;
    private static final String[] monospaceFontNames = {"Consolas", "DejaVu Sans Mono", "Bitstream Vera Sans Mono", "Lucida Console"};
    private final SimpleAttributeSet highlightedAttributes;
    private final SimpleAttributeSet errorAttributes;
    private final SimpleAttributeSet warnAttributes;
    private final SimpleAttributeSet infoAttributes;
    private final SimpleAttributeSet configAttributes;
    private final SimpleAttributeSet debugAttributes;
    private final SimpleAttributeSet fineAttributes;
    private final SimpleAttributeSet finerAttributes;
    private final SimpleAttributeSet finestAttributes;
    private final SimpleAttributeSet invertedAttributes;
    private final SimpleAttributeSet defaultAttributes = new SimpleAttributeSet();
    private JTextComponent textComponent;
    private Document document;
    private final int numLines;

    /**
     * Construct the frame.
     *
     * @param numLines     number of lines to show at a time
     */
    public ConsoleFrame(int numLines, Image frameIcon) {
        super("Tantalum Launcher Console");
        this.numLines = numLines;

        this.highlightedAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(highlightedAttributes, Color.BLACK);
        StyleConstants.setBackground(highlightedAttributes, Color.YELLOW);

        this.errorAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(errorAttributes, Color.RED);
        this.warnAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(warnAttributes, Color.YELLOW);
        this.infoAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(infoAttributes, Color.WHITE);
        this.debugAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(debugAttributes, Color.GREEN);
        this.configAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(configAttributes, Color.BLUE);
        this.fineAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(fineAttributes, Color.LIGHT_GRAY);
        this.finerAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(finerAttributes, Color.GRAY);
        this.finestAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(finestAttributes, Color.DARK_GRAY);

        this.invertedAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(invertedAttributes, Color.black);
        StyleConstants.setBackground(invertedAttributes, Color.WHITE);


        setSize(new Dimension(650, 400));
        buildUI();

        this.setIconImage(frameIcon);

        addMouseListener(this);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    public Document getDocument() { return document; }
    public AttributeSet getHighlightedAttributes() { return highlightedAttributes; }
    public AttributeSet getDefaultAttributes() { return defaultAttributes; }
    public AttributeSet getErrorAttributes() { return errorAttributes; }
    public AttributeSet getWarnAttributes() { return warnAttributes; }
    public AttributeSet getDebugAttributes() { return debugAttributes; }
    public void setCaretPosition(int position) { textComponent.setCaretPosition(position); }

    public AttributeSet getAttributeSet(Level level){
        AttributeSet result1;
        switch(level.intValue()){
            case 1000 : result1 = errorAttributes;
            case 900 : result1 = warnAttributes;
            case 800 : result1 = infoAttributes;
            case 700 : result1 = configAttributes;
            case 600 : result1 = debugAttributes;
            case 500 : result1 = fineAttributes;
            case 400 : result1 = finerAttributes;
            case 300 : result1 = finestAttributes;
            case 200 : result1 = defaultAttributes;
            case 100 : result1 = defaultAttributes;
            case -1 : result1 = highlightedAttributes;
            default : result1 = invertedAttributes;

        }
        return result1;
    }

    /**
     * Build the interface.
     */
    private void buildUI() {
        this.textComponent = new JTextPane();
        textComponent.addMouseListener(this);
        textComponent.setFont(getMonospaceFont().deriveFont(14F));
        textComponent.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textComponent.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        document = textComponent.getDocument();
        document.addDocumentListener(new LimitLinesDocumentListener(numLines, true));
        textComponent.setBackground(Color.BLACK);
        textComponent.setForeground(Color.WHITE);

        JScrollPane scrollText = new JScrollPane(textComponent);
        scrollText.setBorder(null);
        scrollText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollText, BorderLayout.CENTER);
    }

    /**
     * Get a supported monospace font.
     *
     * @return font
     */
    public static Font getMonospaceFont() {
        for (String fontName : monospaceFontNames) {
            Font font = Font.decode(fontName + "-11");
            if (!font.getFamily().equalsIgnoreCase("Dialog")) {
                return font;
            }
        }
        return new Font("Monospace", Font.PLAIN, 11);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    private void doPop(MouseEvent e) {
        ContextMenu menu = new ContextMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private class ContextMenu extends JPopupMenu {
        private static final long serialVersionUID = 1L;
        final JMenuItem copy;
        final JMenuItem clear;

        public ContextMenu() {
            copy = new JMenuItem("Copy");
            add(copy);
            copy.addActionListener(e -> textComponent.copy());

            clear = new JMenuItem("Clear");
            add(clear);
            clear.addActionListener(e -> textComponent.setText(""));
        }
    }
}