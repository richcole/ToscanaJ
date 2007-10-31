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
		    @Override
			public void windowClosing(WindowEvent evt) {
                preferences.storeWindowPlacement(XMLEditorDialog.this.dialog);
				setVisible(false);
			}
		});
        preferences.restoreWindowPlacement(this, new Rectangle(100,100, 250, 400));
		
        init();
    }

    public void init() {
        this.textPane.setHighlighter(this.highlighter);
		this.statusBar.setBorder(BorderFactory.createEtchedBorder());
        JScrollPane scrollPane = new JScrollPane(this.textPane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension scrollPaneSize =
            new Dimension(3 * screenSize.width / 8, 5 * screenSize.height / 8);
        scrollPane.setPreferredSize(scrollPaneSize);
		
        this.textPane.getDocument().addDocumentListener(new DocumentListener() {
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
		controlPanel.add(this.statusBar, new GridBagConstraints(
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
            this.textPane.setText(outputter.outputString(content));
        } else {
            this.textPane.setText("<html>\n  <head>\n    <title></title>\n  </head>\n  <body>\n  </body>\n</html>\n");
        }
        this.result = content;
    }

    public Element getContent() {
		return this.result;
    }

    public void checkXML() {
        SAXBuilder builder = new SAXBuilder();
        this.highlighter.removeAllHighlights();
        try {
            this.document = builder.build(new StringReader(this.textPane.getText()));
            this.statusBar.setForeground(Color.BLACK);
            this.statusBar.setText("well-formed");
			this.statusBar.setToolTipText(null);
			this.useDescriptionButton.setEnabled(true);
        } catch (JDOMException e) {
            showErrorMessage(e);
        } catch (IOException e) {
			showErrorMessage(e);
        }
    }

    private void showErrorMessage(Exception exception) {
        this.useDescriptionButton.setEnabled(false);
        this.statusBar.setForeground(Color.RED);
        String message = exception.getMessage();
        this.statusBar.setText(message);
        this.statusBar.setToolTipText(message);
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
        int endPos = this.textPane.getDocument().getLength();
        char[] text = this.textPane.getText().toCharArray();
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
            this.highlighter.addHighlight(
                startPos,
                endPos,
                new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }
    
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();

		this.useDescriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkXML();
				if (XMLEditorDialog.this.document == null) {
					XMLEditorDialog.this.result = null;
				} else {
					XMLEditorDialog.this.result = XMLEditorDialog.this.document.getRootElement();
				}
				dispose();
			}
		});
		this.useDescriptionButton.setMnemonic(KeyEvent.VK_U);
		JButton removeDescriptionButton = new JButton("Remove Description");
		removeDescriptionButton.setMnemonic(KeyEvent.VK_R);
		removeDescriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLEditorDialog.this.result = null;
                preferences.storeWindowPlacement(XMLEditorDialog.this.dialog);
				dispose();
			}
		});

		JButton cancelEditingButton = new JButton("Cancel Editing");
		cancelEditingButton.setMnemonic(KeyEvent.VK_C);
		cancelEditingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                preferences.storeWindowPlacement(XMLEditorDialog.this.dialog);
				dispose();
			}
		});

		buttonsPanel.add(this.useDescriptionButton);
		buttonsPanel.add(removeDescriptionButton);
		buttonsPanel.add(cancelEditingButton);

		return buttonsPanel;
	}
}
