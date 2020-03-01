/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package test;

/*
 * ToolBarDemo.java requires the following addditional files:
 * images/Back24.gif
 * images/Forward24.gif
 * images/Up24.gif
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MCTKframework extends JPanel
                         implements ActionListener {
    private JSplitPane upDownSplitPane;  // placing UpLeftRightSplitPane (up) and spec panel (down)
    private JSplitPane upLeftRightSplitPane; // placing model editor (left) and output panel (right)
    private JToolBar mainToolBar;
    private JEditorPane editorPane; // model editor

    private JScrollPane specsScrollPane;
    private JToolBar specsToolBar;
    private JTable specsTable;

    protected JTextArea textArea;
    protected String newline = "\n";
    static final private String NEWFILE = "New File";
    static final private String OPENFILE = "Open File";
    static final private String CLOSEFILE = "Close File";
    static final private String SAVEFILE = "Save File";
    static final private String SAVEASFILE = "Save As";

    public MCTKframework() {
        super(new BorderLayout());

        //Create the toolbar.
        mainToolBar = new JToolBar("Still draggable");
        addButtonsToMainToolBar();

        //Create the text area used for output.  Request
        //enough space for 5 rows and 30 columns.
        textArea = new JTextArea(5, 30);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        //editorPane = new JEditorPane()
        //upDownSplitPane = new JSplitPane()

        //Lay out the main panel.
        setPreferredSize(new Dimension(1024, 768));
        add(mainToolBar, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
    }

    protected void addButtonsToMainToolBar() {
        JButton button = null;

        //first button
        button = makeNavigationButton("Back24", NEWFILE,
                                      "Create a new file",
                NEWFILE);
        mainToolBar.add(button);
        mainToolBar.add(Box.createRigidArea(new Dimension(2, getHeight())));

        //second button
        button = makeNavigationButton("Up24", OPENFILE,
                                      "Open a file",
                                      OPENFILE);
        mainToolBar.add(button);
        mainToolBar.add(Box.createRigidArea(new Dimension(2, getHeight())));

        //third button
        button = makeNavigationButton("Forward24", CLOSEFILE,
                                      "Close the file",
                                      CLOSEFILE);
        mainToolBar.add(button);

        mainToolBar.add(Box.createRigidArea(new Dimension(2, getHeight())));

        //third button
        button = makeNavigationButton("Forward24", SAVEFILE,
                "Save the file",
                SAVEFILE);
        mainToolBar.add(button);

        mainToolBar.add(Box.createRigidArea(new Dimension(2, getHeight())));

        //third button
        button = makeNavigationButton("Forward24", SAVEASFILE,
                "Save as another file",
                SAVEASFILE);
        mainToolBar.add(button);
    }

    protected JButton makeNavigationButton(String imageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText) {
/*
        //Look for the image.
        String imgLocation = "images/"
                             + imageName
                             + ".gif";
        URL imageURL = MCTKframework.class.getResource(imgLocation);
*/

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.setContentAreaFilled(true);
        //button.setForeground(Color.BLUE);
        //button.setUI(new AquaButtonUI());
        button.addActionListener(this);

        button.setText(altText);
/*
        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                                     //no image found
            button.setText(altText);
            System.err.println("Resource not found: "
                               + imgLocation);
        }
*/
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        String description = null;

        // Handle each button.
        if (NEWFILE.equals(cmd)) { //first button clicked
            description = "taken you to the previous <something>.";
        } else if (OPENFILE.equals(cmd)) { // second button clicked
            description = "taken you up one level to <something>.";
        } else if (CLOSEFILE.equals(cmd)) { // third button clicked
            description = "taken you to the next <something>.";
        }

        displayResult("If this were a real app, it would have "
                        + description);
    }

    protected void displayResult(String actionDescription) {
        textArea.append(actionDescription + newline);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MCTK 2.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new MCTKframework());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
	        createAndShowGUI();
            }
        });
    }
}
