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
import org.tockit.swing.preferences.ExtendedPreferences;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;

public class XMLEditorDialog extends JDialog {
	private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(XMLEditorDialog.class);
    
	private JTextArea textPane = new JTextArea();
    private JLabel statusBar = new JLabel();
    private JButton useDescriptionButton = new JButton("Use Description");
    private Document document;
    private Element result;
    private DefaultHighlighter highlighter = new DefaultHighlighter();
	final XMLEditorDialog dialog = this;

    public XMLEditorDialog(Frame aFrame, String title) {
        super(aFrame, true);
        setTitle(title);
		this.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent evt) {
                preferences.storeWindowPlacement(dialog);
				setVisible(false);
			}
		});
        preferences.restoreWindowPlacement(this, new Rectangle(100,100, 250, 400));
		
        init();
    }

    public void init() {
        textPane.setHighlighter(highlighter);
		statusBar.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scrollPane = new JScrollPane(textPane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension scrollPaneSize =
            new Dimension(3 * screenSize.width / 8, 5 * screenSize.height / 8);
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
		JPanel controlPanel = new JPanel(new GridBagLayout());
		controlPanel.add(createButtonsPanel(), new GridBagConstraints(
						0,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5, 0, 0, 0),
						2,2));
		controlPanel.add(statusBar, new GridBagConstraints(
						0,1,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5, 0, 0, 0),
						2,2));
						
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);	
        pack();
    }

    public void setContent(Element content) {
        if (content != null) {
            XMLOutputter outputter = new XMLOutputter("  ", false);
            textPane.setText(outputter.outputString(content));
        } else {
            textPane.setText("<description><html>\n  <head>\n    <title></title>\n  </head>\n  <body>\n  </body>\n</html>\n</description>\n");
        }
        this.result = content;
    }

    public Element getContent() {
		return result;
    }

    public void checkXML() {
        SAXBuilder builder = new SAXBuilder();
        highlighter.removeAllHighlights();
        try {
            document = builder.build(new StringReader(textPane.getText()));
            statusBar.setForeground(Color.BLACK);
            statusBar.setText("well-formed");
			statusBar.setToolTipText(null);
			useDescriptionButton.setEnabled(true);
        } catch (JDOMException e) {
            showErrorMessage(e);
        } catch (IOException e) {
			showErrorMessage(e);
        }
    }

    private void showErrorMessage(Exception exception) {
        useDescriptionButton.setEnabled(false);
        statusBar.setForeground(Color.RED);
        String message = exception.getMessage();
        statusBar.setText(message);
        statusBar.setToolTipText(message);
        int posLine = message.indexOf("line");
        if (posLine > 0) {
            String rest = message.substring(posLine + 5);
            int errorLine =
                Integer.parseInt(rest.substring(0, rest.indexOf(":")));
            addErrorHighlight(errorLine);
            posLine = rest.lastIndexOf(" ");
            if (posLine > 0) {
                rest = rest.substring(posLine + 1);
                try {
                    int dotIndex = rest.indexOf(".");
                    if (dotIndex != -1) {
                        int openTagLine =
                            Integer.parseInt(rest.substring(0, dotIndex));
                        addErrorHighlight(openTagLine);
                    }
                } catch (NumberFormatException e1) {
                    // ignore, we don't understand the message, just highlight one line
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
            if (c == '\n') {
                lineCount++;
                if (lineCount == errorLine) {
                    startPos = i;
                }
                if (lineCount == errorLine + 1) {
                    endPos = i;
                }
            }
        }
        try {
            highlighter.addHighlight(
                startPos,
                endPos,
                new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }
    
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		useDescriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkXML();
				if (document == null) {
					result = null;
				} else {
					result = document.getRootElement();
				}
				dispose();
			}
		});
		useDescriptionButton.setMnemonic(KeyEvent.VK_U);
		JButton removeDescriptionButton = new JButton("Remove Description");;
		removeDescriptionButton.setMnemonic(KeyEvent.VK_R);
		removeDescriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = null;
                preferences.storeWindowPlacement(dialog);
				dispose();
			}
		});

		JButton cancelEditingButton = new JButton("Cancel Editing");
		cancelEditingButton.setMnemonic(KeyEvent.VK_C);
		cancelEditingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                preferences.storeWindowPlacement(dialog);
				dispose();
			}
		});

		buttonsPanel.add(useDescriptionButton);
		buttonsPanel.add(removeDescriptionButton);
		buttonsPanel.add(cancelEditingButton);

		return buttonsPanel;
	}
}
