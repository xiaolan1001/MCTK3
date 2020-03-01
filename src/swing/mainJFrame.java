package swing;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static jdk.nashorn.internal.runtime.Context.DEBUG;

class SpecsTableModel extends AbstractTableModel {
	private String[] columnNames = {
			"No",
			"Status",
			"RTCTL* Spec",
			"Annotation"};
	private Object[][] data = {
	};

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/
	 * editor for each cell.  If we didn't implement this method,
	 * then the last column would contain text ("true"/"false"),
	 * rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's
	 * editable.
	 */
	public boolean isCellEditable(int row, int col) {
		//Note that the data/cell address is constant,
		//no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Don't need to implement this method unless your table's
	 * data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col
					+ " to " + value
					+ " (an instance of "
					+ value.getClass() + ")");
		}

		data[row][col] = value;
		fireTableCellUpdated(row, col);

		if (DEBUG) {
			System.out.println("New value of data:");
			printDebugData();
		}
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}

public class mainJFrame extends JFrame implements MouseListener {

	final static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	final static int height = Toolkit.getDefaultToolkit().getScreenSize().height;

	JPanel backgroundPanel;
	JPanel menuControlPanel;
	JPanel editorPanelContainer; // the panel holding editorPanel
	JPanel outputPanel;

	JScrollPane buttonsSpecsScrollPane, outputScrollPane;
	public static JSplitPane editorSpecsAndOutputSplitPane;
	public static JSplitPane editorAndSpecsSplitPane;


	// backgroundPanel=(North:menuControlPanel, Center:modelSpecsPanel, East: outputPanel)

	JPanel menuToolBarPanel; //, pageMenuPanel;
//	JLabel editorLabel, verifyLabel;
	public static MenuToolBarJPanel controlPanel;
	public static EditorJPanel editorPanel;
	public static VerificationActionListener verificationListener;
	public mainJFrame()
	{
		this.setLayout(new BorderLayout());

		Image logoIcon = new ImageIcon(mainJFrame.class.getResource("/swing/Icons/logo.png")).getImage();
		this.setIconImage(logoIcon);
		initBackgroundPanel();
		this.setTitle("  MCTK 2.0  ");
		this.setSize((int) (width * 0.8f), (int) (height));
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			// ���´��ڹر�ť�¼�����
			public void windowClosing(WindowEvent e) {
				Object[] options = {"Exit", "Cancel"};
				int response = JOptionPane.showOptionDialog(null, "Do you want to exit MCTK ?",
						"Confirm Exit", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (response == 0) {
					System.exit(0);
				}
			}
		});
	}

	public void initBackgroundPanel() {

		//backgroundPanel = new JPanel(new BorderLayout());
		//backgroundPanel.setOpaque(true);

		initMenuControlPanel();
		initEditorSpecsPanel();
		this.add(menuControlPanel, BorderLayout.NORTH);

		//=====================for editor(left) and output area(right)==========================
		JLabel outputTitleLabel=new JLabel("Verification Information");
		outputTitleLabel.setFont(new Font("System",Font.BOLD,VerificationActionListener.outputFontSize));
		JTextArea outputTextArea = new JTextArea("Verification Outputs:\n");
		JScrollPane outputScrollPane=new JScrollPane(outputTextArea);

		JPanel outputPanel=new JPanel(new BorderLayout());
		outputPanel.add("North", outputTitleLabel);
		outputPanel.add("Center", outputScrollPane);

		JSplitPane upSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanelContainer, outputPanel);
		upSplitPane.setDividerLocation(900);

		//=====================for spec list area==========================
		JToolBar specToolBar=new JToolBar();
		JButton addSpecButton, delSpecButton, verifySpecButton;
		addSpecButton=new JButton("  Insert Spec  ");
		delSpecButton=new JButton("  Delete Spec  ");
		verifySpecButton=new JButton(("  Verify Spec  "));
		specToolBar.add(addSpecButton);
		specToolBar.add(delSpecButton);
		specToolBar.add(verifySpecButton);

		String[] columnNames = {"No",
				"Status",
				"RTCTL* Spec",
				"Annotation"};
		JTable specsTable = new JTable(new SpecsTableModel());
		
		JScrollPane specsTableScrollPane=new JScrollPane(specsTable);

		JPanel specsPanel = new JPanel(new BorderLayout());
		specsPanel.add("North",specToolBar);
		specsPanel.add("Center",specsTableScrollPane);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upSplitPane, specsPanel);
		mainSplitPane.setDividerLocation(600);

		this.add(mainSplitPane, BorderLayout.CENTER);

		//backgroundPanel.add(editorSpecsPanel, BorderLayout.CENTER);
		//backgroundPanel.add(outputPanel,BorderLayout.EAST);
		//this.add(backgroundPanel);
	}

	public void initMenuControlPanel() {
		menuToolBarPanel = new JPanel(new BorderLayout());
		controlPanel = new MenuToolBarJPanel(this);

		menuControlPanel = new JPanel(new BorderLayout());
		menuControlPanel.setPreferredSize(new Dimension(width, 65));
		menuControlPanel.add(menuToolBarPanel, BorderLayout.NORTH);
	}

	public void initEditorSpecsPanel() {
		editorPanelContainer = new JPanel(new BorderLayout());
		//editorLabel.setText("<html><font color='#336699' style='font-weight:bold'>" + " SMV Editor " + "</font>&nbsp;</html>");
		//centerPanel.setOpaque(false);
		editorPanel=new EditorJPanel(this);
		verificationListener =new VerificationActionListener(this);
	}

/*
	public void initPageMenu() {
		pageMenuPanel = new JPanel(new FlowLayout());
		//topMenu.setPreferredSize(new Dimension(900, 40));
		//pageMenu.setOpaque(false);
		String[] nameStrings = {" Model Editor ", " Verification "};
		editorLabel = CreateMenuLabel(editorLabel, nameStrings[0], "text", pageMenuPanel);
		editorLabel.setName("Editor");

		JLabel line = new JLabel("<html>&nbsp;<font color='#D2D2D2'>|</font>&nbsp;</html>");
		pageMenuPanel.add(line);

		verifyLabel = CreateMenuLabel(verifyLabel, nameStrings[1], "verify", pageMenuPanel);
		verifyLabel.setName("Verify");
	}
*/



	public JLabel CreateMenuLabel(JLabel jlabel, String text, String name, JPanel jpanel) {
		Icon icon = new ImageIcon(mainJFrame.class.getResource("/swing/Icons/" + name + ".png"));
		jlabel = new JLabel(icon);
		jlabel.setText("<html><font color='black'>" + text + "</font>&nbsp;</html>");
		jlabel.addMouseListener(this);
		jlabel.setFont(new Font("Default", Font.PLAIN, 14));
		jpanel.add(jlabel);
		return jlabel;
	}

	public void creatEditor() {
		editorPanelContainer.removeAll();
		editorPanelContainer.add("West",editorPanel.rowScroll);
		editorPanelContainer.add("Center",editorPanel.textScroll);
		editorPanelContainer.updateUI();
	}

	public void creatVerifyUI() {
		editorPanelContainer.removeAll();
		editorPanelContainer.add(VerificationActionListener.mainSplitPane, "Center");
		editorPanelContainer.updateUI();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
/*
		if (e.getSource() == editorLabel) {
			creatEditor();
			editorLabel.setText("<html><font color='#336699' style='font-weight:bold'>" + " SMV Editor " + "</font>&nbsp;</html>");
			verifyLabel.setText("<html><font color='black'>" + " Verification " + "</font>&nbsp;</html>");
		} else if (e.getSource() == verifyLabel) {
			creatVerifyUI();
			editorLabel.setText("<html><font color='black'>" + " SMV Editor " + "</font>&nbsp;</html>");
			verifyLabel.setText("<html><font color='#336699' style='font-weight:bold'>" + " Verification " + "</font>&nbsp;</html>");
			verificationListener.ReadSMVSpec();
		}
*/
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
