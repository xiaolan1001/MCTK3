package swing;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
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

public class mainJFrame extends JFrame implements MouseListener, ActionListener, FocusListener {

	final static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	final static int height = Toolkit.getDefaultToolkit().getScreenSize().height;

	JPanel menuControlPanel;
	JPanel editorPanelContainer; // the panel holding editorPanel
	JPanel outputPanel;

	JScrollPane buttonsSpecsScrollPane, outputScrollPane;
	public static JSplitPane editorSpecsAndOutputSplitPane;
	public static JSplitPane editorAndSpecsSplitPane;


	JPanel menuToolBarPanel; //, pageMenuPanel;
	public static MenuToolBarJPanel controlPanel;
	public static EditorJPanel editorPanel;

	JLabel outputTitleLabel;
	JTextArea outputTextArea;
	JSplitPane upSplitPane;
	JToolBar specToolBar;
	JButton addSpecButton, delSpecButton, verifySpecButton;

	JTable specsTable;
	DefaultTableModel specsTableModel;
	Vector specsData;
	static int colNo=0, colStatus=1, colSpec=2, colAnnotation=3;

	JScrollPane specsTableScrollPane;
	JPanel specsPanel;
	JSplitPane mainSplitPane;

	//=====================for spec list area==========================

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
				int response = JOptionPane.showOptionDialog(null, "Do you want to exit MCTK2?",
						"Confirm Exit", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (response == 0) {
					System.exit(0);
				}
			}
		});
	}

	public void initBackgroundPanel() {

		initMenuControlPanel();
		initEditorPanel();
		this.add(menuControlPanel, BorderLayout.NORTH);

		//=====================for editor(left) and output area(right)==========================
		outputTitleLabel=new JLabel("Verification Information");
		outputTitleLabel.setFont(new Font("System",Font.BOLD,VerificationActionListener.outputFontSize));
		outputTextArea = new JTextArea();
		outputScrollPane=new JScrollPane(outputTextArea);

		outputTextArea.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Verification Information"),
								BorderFactory.createEmptyBorder(0,0,0,0)),
						BorderFactory.createEmptyBorder(0,0,0,0)));


		outputPanel=new JPanel(new BorderLayout());
		outputPanel.add("North", outputTitleLabel);
		outputPanel.add("Center", outputScrollPane);

		upSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanelContainer, outputScrollPane);
		upSplitPane.setDividerLocation(900);
		upSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		//=====================for spec list area==========================
		specToolBar=new JToolBar();
		addSpecButton=new JButton("  Insert Spec  ");
		addSpecButton.addActionListener(this);

		delSpecButton=new JButton("  Delete Spec  ");
		delSpecButton.addActionListener(this);

		verifySpecButton=new JButton(("  Verify Spec  "));
		verifySpecButton.addActionListener(this);

		specToolBar.add(addSpecButton);
		specToolBar.add(delSpecButton);
		specToolBar.add(verifySpecButton);

		//specsTable = new JTable(new SpecsTableModel());
		initSpecsTable();

		specsTableScrollPane=new JScrollPane(specsTable);

		specsPanel = new JPanel(new BorderLayout());
		specsPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		specsPanel.add("North",specToolBar);
		specsPanel.add("Center",specsTableScrollPane);

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upSplitPane, specsPanel);
		mainSplitPane.setDividerLocation(600);
		mainSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		this.add(mainSplitPane, BorderLayout.CENTER);

	}

	public void initMenuControlPanel() {
		menuToolBarPanel = new JPanel(new BorderLayout());
		controlPanel = new MenuToolBarJPanel(this);

		menuControlPanel = new JPanel(new BorderLayout());
		menuControlPanel.setPreferredSize(new Dimension(width, 65));
		menuControlPanel.add(menuToolBarPanel, BorderLayout.NORTH);
	}

	public void initEditorPanel() {
		editorPanelContainer = new JPanel(new BorderLayout());
		editorPanelContainer.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		//editorLabel.setText("<html><font color='#336699' style='font-weight:bold'>" + " SMV Editor " + "</font>&nbsp;</html>");
		//centerPanel.setOpaque(false);
		editorPanel=new EditorJPanel(this);
		verificationListener =new VerificationActionListener(this);
	}

	public static void setColumnSize(JTable table, int i, int preferedWidth, int minWidth, int maxWidth){
		//表格的列模型
		TableColumnModel cm = table.getColumnModel();
		//得到第i个列对象
		TableColumn column = cm.getColumn(i);
		column.setPreferredWidth(preferedWidth);
		column.setMaxWidth(maxWidth);
		column.setMinWidth(minWidth);
	}

	public void initSpecsTable(){
		Vector columnNames=new Vector();
		columnNames.add("No");
		columnNames.add("Status");
		columnNames.add("RTCTL* Specification");
		columnNames.add("Annotation");
		specsData=new Vector();
		specsTableModel = new DefaultTableModel(specsData, columnNames);

		specsTable=new JTable(specsTableModel){

			@Override
			public boolean isCellEditable(int row, int column) {
				if(column<colSpec) return false;
				return true;
			}

		};


		DefaultTableCellRenderer  r  =  new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JTextField.CENTER);
		r.setFont(new Font("Default",Font.PLAIN,12));

		setColumnSize(specsTable,colNo,30,30,30);
		TableColumn c=specsTable.getColumn("No");
		c.setCellRenderer(r);

		setColumnSize(specsTable,colStatus,100,100,100);
		specsTable.getColumn("Status").setCellRenderer(r);
		c=specsTable.getColumn("Status");
		c.setCellRenderer(r);

		setColumnSize(specsTable,colSpec,1000,30,3000);

		specsTable.setShowGrid(true);
		specsTable.setSelectionMode(SINGLE_SELECTION);
		specsTable.setRowSelectionAllowed(true);

		specsTable.addFocusListener(this);

	}

	public void refreshSpecsTable(){
		for(int row=0; row<specsTableModel.getRowCount();row++)
			specsTableModel.setValueAt(row+1,row,0);
	}

	public int insertSpec(int row, String status, String spec, String annotation){

/*
		int row = specsTable.getSelectedRow();
		int col = specsTable.getSelectedColumn();
*/
		CellEditor ce=specsTable.getCellEditor(row, colSpec);
		if(specsTable.isEditing()) ce.stopCellEditing();

		Object[] rowData={1,status,spec,annotation};
		specsTableModel.insertRow(row, rowData);
		refreshSpecsTable();
		setEditing(row,colSpec);
		return 1;
	}

	public void setEditing(int row, int col) {
		if (!specsTable.isCellEditable(row,col))
			return;
		specsTable.editCellAt(row, col);
		JTextField jText = (JTextField) ( (DefaultCellEditor) specsTable.getCellEditor(row,col)).getComponent();
		jText.requestFocus();
		jText.selectAll();
	}

	public int removeSpec(int row) {
		CellEditor ce=specsTable.getCellEditor(row, colSpec);
		if(specsTable.isEditing()) ce.stopCellEditing();

		int num=specsTableModel.getRowCount();
		if (row >= 0 && row<num) specsTableModel.removeRow(row);
		if (row+1<num)
			specsTable.setRowSelectionInterval(row, row);
		else if(row+1==num && row>0)
			specsTable.setRowSelectionInterval(row-1,row-1);

		refreshSpecsTable();
		return 0;
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
		editorPanelContainer.add("West",editorPanel.rowScrollPane);
		editorPanelContainer.add("Center",editorPanel.textScrollPane);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==addSpecButton) {
			//System.out.println("add spec clicked");
			int row;
			if(specsTable.getRowCount()<=0 || specsTable.getSelectedRow()==-1) row=0;
			else row=specsTable.getSelectedRow();

			insertSpec(row, "","", "");

			specsTable.setEditingRow(row);
			specsTable.setEditingColumn(2); // spec column
			specsTable.setRowSelectionInterval(row,row);
		}
		if(e.getSource()==delSpecButton){
			removeSpec(specsTable.getSelectedRow());
		}

	}

	@Override
	public void focusGained(FocusEvent e) {

		//System.out.println(e.getSource().getClass().getName()+" focus gained");
	}

	@Override
	public void focusLost(FocusEvent e) {

		//System.out.println(e.getSource().getClass().getName()+" focus lost");
	}
}
