/*
 * Copyright Peter Becker (http://www.peterbecker.de).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import javax.swing.*;

import org.tockit.swing.preferences.ExtendedPreferences;

import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateScaleDialog extends JDialog {
	
	private final class ScaleListModel extends AbstractListModel {
        public int getSize() {
            return scale.getCriteria().size();
        }
        public Object getElementAt(int index) {
            return scale.getCriteria().get(index);
        }
        public void fireUpdate() {
            fireContentsChanged(this, 0, scale.getCriteria().size());
        }
    }

    boolean result;
	private JTextField titleEditor = new JTextField();
	private JButton createButton;
    private JList mvAttributesList;
    private JList svAttributesList;

	private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(CreateScaleDialog.class);
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 400;
	private static final Rectangle DEFAULT_PLACEMENT = new Rectangle(10, 10, MINIMUM_WIDTH, MINIMUM_HEIGHT);
    private ManyValuedContext context;
    private View scale;
    private ScaleListModel svAttributeModel;

	public CreateScaleDialog(Frame owner, ManyValuedContext context) {
		super(owner, true);
        this.context = context;
        this.scale = new View("");
        
		preferences.restoreWindowPlacement(this, DEFAULT_PLACEMENT); 
		//	to enforce the minimum size during resizing of the JDialog
		 addComponentListener( new ComponentAdapter() {
			 public void componentResized(ComponentEvent e) {
				 int width = getWidth();
				 int height = getHeight();
				 if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				 if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				 setSize(width, height);
			 }
			 public void componentShown(ComponentEvent e) {
				 componentResized(e);
			 }
		 });
		
		layoutDialog();
	}

    public View execute() {
		result = false;
		show();
        if(result) {
            this.scale.setName(titleEditor.getText());
            return scale;
        } else {
            return null;
        }
	}

	private void layoutDialog() {
		setTitle("Create diagram");
		JPanel mainPane = new JPanel(new GridBagLayout());
        
		mainPane.add(makeTitlePane(),new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
				
		mainPane.add(makeSelectionPane(), new GridBagConstraints(
					0,1,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,2),
					2,2
		));
		
		mainPane.add(makeButtonsPane(), new GridBagConstraints(
					0,2,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints .HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		setContentPane(mainPane);

	}

	private JPanel makeTitlePane() {
		this.titleEditor.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
				setCreateButtonState();
			}
			public void keyReleased(KeyEvent e) {
				setCreateButtonState();
			}
			public void keyPressed(KeyEvent e) {}		
		});
		return new LabeledPanel("Title:", this.titleEditor, false);
	}
	
	protected void setCreateButtonState() {
		createButton.setEnabled(true);
	}
  
	private JPanel makeSelectionPane() {
		JPanel selectionPane = new JPanel();
		selectionPane.setLayout(new GridBagLayout());

		this.mvAttributesList = new JList(new AbstractListModel() {
            public int getSize() {
                return context.getAttributes().size();
            }

            public Object getElementAt(int index) {
                return context.getAttributes().get(index);
            }
        });
        this.mvAttributesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = mvAttributesList.locationToIndex(e.getPoint());
                    scaleAttribute((ManyValuedAttribute) context.getAttributes().get(index));
                 }
            }
        });
		this.svAttributeModel = new ScaleListModel();
        this.svAttributesList = new JList(svAttributeModel);
		
        selectionPane.add(new JLabel("Available Attributes:"),new GridBagConstraints(
                    0,0,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(2,2,2,2),
                    2,2
        ));
                
        selectionPane.add(new JLabel("Selected Attributes:"), new GridBagConstraints(
                    1,0,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(2,2,2,2),
                    2,2
        ));
        
        selectionPane.add(new JScrollPane(mvAttributesList),new GridBagConstraints(
                    0,1,1,1,1,1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(2,2,2,2),
                    2,2
        ));
                
        selectionPane.add(new JScrollPane(svAttributesList), new GridBagConstraints(
                    1,1,1,1,1,1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(2,2,2,2),
                    2,2
        ));
        
		return selectionPane;
		
	}

	protected void scaleAttribute(ManyValuedAttribute attribute) {
        new AddCriterionDialog(attribute, JOptionPane.getFrameForComponent(this), this.scale);
        this.svAttributeModel.fireUpdate();
    }

    private JPanel makeButtonsPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(true);
			}
		});

		buttonPane.add(createButton);

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(false);
			}
		});
		buttonPane.add(cancelButton);
		return buttonPane;
	}
	
	private void closeDialog(boolean result) {
		preferences.storeWindowPlacement(this);
		dispose();
		this.result = result;
	}
}
