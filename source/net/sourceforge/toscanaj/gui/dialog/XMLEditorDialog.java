/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.StringReader;

public class XMLEditorDialog extends JDialog {
    private JTextArea textPane = new JTextArea();
    private JLabel statusBar = new JLabel();
    private Document document;
    private DefaultHighlighter highlighter = new DefaultHighlighter();

    public XMLEditorDialog(Frame aFrame, String title) {
        super(aFrame, true);
        setTitle(title);
        init();
    }

	public void init(){
        textPane.setHighlighter(highlighter);

		JScrollPane scrollPane = new JScrollPane(textPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension scrollPaneSize = new Dimension(5*screenSize.width/8,5*screenSize.height/8);
		scrollPane.setPreferredSize(scrollPaneSize);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkXML();
            }

            public void removeUpdate(DocumentEvent e) {
                checkXML();
            }

            public void changedUpdate(DocumentEvent e) {
                checkXML();
            }
        });
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
		pack();
	}

    public void setContent(Element content) {
        XMLOutputter outputter = new XMLOutputter("  ", false);
        textPane.setText(outputter.outputString(content));
    }

    public Element getContent() {
        checkXML();
        if(document == null) {
            return null;
        }
        return document.getRootElement();
    }

    public void checkXML() {
        SAXBuilder builder = new SAXBuilder();
        highlighter.removeAllHighlights();
        try {
            document = builder.build(new StringReader(textPane.getText()));
            statusBar.setForeground(Color.BLACK);
            statusBar.setText("well-formed");
        } catch (JDOMException e) {
            statusBar.setForeground(Color.RED);
            String message = e.getMessage();
            statusBar.setText(message);
            int posLine = message.indexOf("line");
            if(posLine > 0) {
                String rest = message.substring(posLine + 5);
                int errorLine = Integer.parseInt(rest.substring(0, rest.indexOf(":")));
                addErrorHighlight(errorLine);
                posLine = rest.lastIndexOf(" ");
                if(posLine > 0) {
                    rest = rest.substring(posLine + 1);
                    try {
                        int dotIndex = rest.indexOf(".");
                        if (dotIndex != -1) {
                            int openTagLine = Integer.parseInt(rest.substring(0, dotIndex));
                            addErrorHighlight(openTagLine);
                        }
                    } catch (NumberFormatException e1) {
                        // ignore, we don't understand the message, just highlight one line
                    }
                }
            }
        }
    }

    private void addErrorHighlight(int errorLine) {
        int startPos = 0;
        int endPos = textPane.getDocument().getLength();
        char[] text = textPane.getText().toCharArray();
        int lineCount = 1;
        for (int i = 0; i < text.length; i++) {
            char c = text[i];
            if(c == '\n') {
                lineCount++;
                if(lineCount == errorLine) {
                    startPos = i;
                }
                if(lineCount == errorLine + 1) {
                    endPos = i;
                }
            }
        }
        try {
            highlighter.addHighlight(startPos, endPos, new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }
}
