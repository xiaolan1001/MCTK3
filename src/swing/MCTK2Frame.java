package swing;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.AlgRunnerThread;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.lib.mc.RTCTLK.RTCTLKModelCheckAlg;
import edu.wis.jtlv.lib.mc.RTCTL_STAR.RTCTL_STAR_ModelCheckAlg;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import net.sf.javabdd.BDD;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import static edu.wis.jtlv.lib.AlgResultI.ResultStatus.failed;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static jdk.nashorn.internal.runtime.Context.DEBUG;
import static swing.VerifyActionListener.outputFontSize;

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

public class MCTK2Frame extends JFrame implements MouseListener, ActionListener, FocusListener {
	//=================The model information after loading a SMV file=================
	static SMVModule smvModule;//read the smv model only once.
	BDD original_feasibleStates=null;
	static Vector<BDD> original_AllIniRestrictions=null;
	static Vector<BDD> original_AllTransRestrictions=null;
	static int original_AllInstancesCount = 0;
	static int original_AllJusticesCount = 0;
	static int original_AllCompassionsCount = 0;

	public static Statistic statistic=null; //get the time consuming, memory, etc.

	//=================For GUI=====================
	public static boolean isOpeningCounterexampleWindow=false;

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
	public static JTextPane outputTextPane;
	JSplitPane upSplitPane;
	JToolBar specToolBar;
	JButton addSpecButton, delSpecButton, verifySpecButton, saveSpecsButton;

	JTable specsTable;
	DefaultTableModel specsTableModel;
	Vector specsData;
	static int colNo=0, colStatus=1, colSpec=2, colAnnotation=3;

	JScrollPane specsTableScrollPane;
	JPanel specsPanel;
	JSplitPane mainSplitPane;

	public void initializeAfterModuleLoaded(){
		original_AllIniRestrictions = smvModule.getAllIniRestrictions();
		original_AllTransRestrictions = smvModule.getAllTransRestrictions();
		original_AllInstancesCount = smvModule.getAllInstances().length;
		original_AllJusticesCount = smvModule.allJustice().length;
		original_AllCompassionsCount = smvModule.allPCompassion().length;
		original_AllInstancesCount = smvModule.getAllInstancesVector().size();
	}

	// invoked to restore the original model data after a model checking algorithm finished
	public static boolean restoreOriginalModuleData(){
		boolean modelChangedAfterLoaded=false;

		if(original_AllIniRestrictions.size()!=smvModule.getAllIniRestrictions().size()) {
			modelChangedAfterLoaded=true;
			smvModule.setAllTransRestrictions(original_AllIniRestrictions);
		}

		if(original_AllTransRestrictions.size()!=smvModule.getAllTransRestrictions().size()) {
			modelChangedAfterLoaded=true;
			smvModule.setAllTransRestrictions(original_AllTransRestrictions);
		}

		for(int i=smvModule.allJustice().length;i>original_AllJusticesCount;i--) {
			modelChangedAfterLoaded=true;
			smvModule.popLastJustice();
		}

		for(int i=smvModule.allPCompassion().length;i>original_AllCompassionsCount;i--) {
			modelChangedAfterLoaded=true;
			smvModule.popLastCompassion();
		}

		for(int i=smvModule.getAllInstancesVector().size(); i>original_AllInstancesCount; i--) {
			modelChangedAfterLoaded=true;
			smvModule.decompose(smvModule.getAllInstancesVector().lastElement());
		}

/*
        if(modelChangedAfterLoaded){
            // recalculate the set of feasible states
            original_feasibleStates=smvModule.feasible();
        }
*/
		return modelChangedAfterLoaded;
	}

	public void readSMVFile(){
		String fileName = controlPanel.fileOperation.getFileName();
		if (fileName.equals("")) {
			Object[] options = {"OK"};
			JOptionPane.showOptionDialog(null, "Sorry, please create a SMV file first!",
					"Warm Tips", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		} else {
			String src = controlPanel.fileOperation.getPath();
			String url = src + fileName + ".smv";
			controlPanel.fileOperation.save();    //Save the file you are editing first.

			Env.resetEnv();
			try {
				statistic =new Statistic();
				consoleOutput("normal", "Loading the Modules of "+fileName + ".smv ...\n");
				Env.loadModule(url);
				smvModule = (SMVModule) Env.getModule("main");
				initializeAfterModuleLoaded();
				smvModule.setFullPrintingMode(true);

				consoleOutput("weak", statistic.getUsedInfo(true,true,true,true));
			} catch (Exception ie) {
				ie.printStackTrace();
				Object[] options = {"OK"};
				JOptionPane.showOptionDialog(null, "Syntax error, please check the SMV file!",
						"Warm Tips", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			}finally {
			}

		}
	}

	//public static VerifyActionListener verificationListener;
	public MCTK2Frame()
	{
		this.setLayout(new BorderLayout());

		Image logoIcon = new ImageIcon(MCTK2Frame.class.getResource("/swing/Icons/logo.png")).getImage();
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

	public void verifyOneSpec(String specStr) {
		Spec[] specs = Env.loadSpecString(specStr);
		if (specs == null||specs[0]==null) {
			addDocument(outputTextPane, "\n Sorry, please input correct specification.", outputFontSize, Color.RED, 1);
			return;
		}
//        String[] SpecStr = specStr.split(";");
//        insertDocument(outputTextPane, "\n ======DONE Loading Specs=========", outputFontSize, Color.ORANGE, 1);
		AlgRunnerThread runner;
		statistic.beginBddInfo();
		statistic.beginTimeMemory();
		addDocument(outputTextPane, "\nModel checking " + specStr, outputFontSize, Color.BLACK, 1);
		if (specs[0].getLanguage() == InternalSpecLanguage.RTCTLs || specs[0].getLanguage() == InternalSpecLanguage.LTL) {
			RTCTL_STAR_ModelCheckAlg algorithm = new RTCTL_STAR_ModelCheckAlg(smvModule, specs[0]);
			runner = new AlgRunnerThread(algorithm);
			runner.runSequential();
			if (runner.getDoResult().getResultStat() == failed) {//结果false，否则无图形反例
				SetGraphThread x = new SetGraphThread(specStr, algorithm.getGraph(), this);
				Thread y = new Thread(x);
				y.start();
			}
			if (runner.getDoResult() != null)
				addDocument(outputTextPane, "\n" + runner.getDoResult().resultString() +
						"\n" + statistic.getUsedTime() + statistic.getUsedBddNodeNum() + statistic.getUsedBddVarNum()+ statistic.getUsedMemory(), outputFontSize, Color.BLACK, 1);
			else if (runner.getDoException() != null)
				addDocument(outputTextPane, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);
		}
		else if (specs[0].getLanguage() == InternalSpecLanguage.CTL) {
			RTCTLKModelCheckAlg algorithm = new RTCTLKModelCheckAlg(smvModule, specs[0]);
			algorithm.SetShowGraph(true);
			runner = new AlgRunnerThread(algorithm);
			runner.runSequential();
			if (runner.getDoResult().getResultStat() == failed) {//结果false，否则无图形反例
				//SetGraphThread x = new SetGraphThread(specStr, algorithm.getGraph(), this);
				//Thread y = new Thread(x);
				//y.start();
			}
			if (runner.getDoResult() != null)
				addDocument(outputTextPane, "\n" + runner.getDoResult().resultString() +
						"\n" + statistic.getUsedTime() + statistic.getUsedBddNodeNum() + statistic.getUsedBddVarNum()+ statistic.getUsedMemory(), outputFontSize, Color.BLACK, 1);
			else if (runner.getDoException() != null)
				addDocument(outputTextPane, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);
		}
	}

	public void initBackgroundPanel() {

		initMenuControlPanel();
		initEditorPanel();
		this.add(menuControlPanel, BorderLayout.NORTH);

		//=====================for editor(left) and output area(right)==========================
		outputTitleLabel=new JLabel("Verification Information");
		outputTitleLabel.setFont(new Font("System",Font.BOLD, outputFontSize));
		outputTextPane = new JTextPane();
		outputScrollPane=new JScrollPane(outputTextPane);

		outputTextPane.setBorder(
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

		saveSpecsButton=new JButton(("  Save Specs  "));
		saveSpecsButton.addActionListener(this);

		specToolBar.add(addSpecButton);
		specToolBar.add(delSpecButton);
		specToolBar.add(verifySpecButton);
		specToolBar.add(saveSpecsButton);

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
		editorPanel=new EditorJPanel(this);
		//verificationListener =new VerifyActionListener(this);
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
		Icon icon = new ImageIcon(MCTK2Frame.class.getResource("/swing/Icons/" + name + ".png"));
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
		editorPanelContainer.add(VerifyActionListener.mainSplitPane, "Center");
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

	public static void addDocument(JTextPane toTextPane, String str, int textSize, Color textColor, int setFont)// 根据传入的颜色及文字，将文字插入控制台
	{
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, textColor);// 设置文字颜色
		StyleConstants.setFontSize(set, textSize);// 设置字体大小
		switch (setFont) {
			case 1://正常输出
				StyleConstants.setFontFamily(set, "新宋体");
			case 2://提示，警告，异常
				StyleConstants.setFontFamily(set, "标楷体");
			case 3://错误提示
				StyleConstants.setFontFamily(set, "华文行楷");
			default:
				StyleConstants.setFontFamily(set, "微软雅黑");
		}
		Document doc = toTextPane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, set);// 插入文字
		} catch (BadLocationException e) {
		}
	}

	public static void consoleOutput(String type, String str)
	{
		SimpleAttributeSet attribureSet = new SimpleAttributeSet();
		Color textColor=Color.BLACK;
		if(type.equals("warning") || type.equals("magenta")){
			textColor=Color.MAGENTA;
		}else if(type.equals("error") || type.equals("red")){
			textColor=Color.RED;
		}else if(type.equals("emph") || type.equals("blue")){
			textColor=Color.BLUE;
		}else if(type.equals("weak") || type.equals("gray")){
			textColor=Color.GRAY;
		}else if(type.equals("green")){
			textColor=Color.GREEN;
		}else if(type.equals("darkGray")){
			textColor=Color.darkGray;
		}

		int textSize=outputFontSize;
		StyleConstants.setForeground(attribureSet, textColor);// 设置文字颜色
		StyleConstants.setFontSize(attribureSet, textSize);// 设置字体大小
		Document doc = outputTextPane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attribureSet);// 插入文字
			outputTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
		}
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
		if(e.getSource()==verifySpecButton){
			if(isOpeningCounterexampleWindow){
				consoleOutput("warning", "Please close the counterexample window before verification.\n");
				return;
			}
			readSMVFile();

			int row;
			if(specsTable.getRowCount()<=0) { consoleOutput("warning", "There is not any specification inputted.\n");return; }
			row=specsTable.getSelectedRow();
			if(row==-1) { consoleOutput("warning", "Select one specification please.\n"); return; }

			String specStr=(String)specsTableModel.getValueAt(row,colSpec);
			Spec spec=generateSpec(row);
			if(spec==null) return;

			RTCTL_STAR_ModelCheckAlg alg = new RTCTL_STAR_ModelCheckAlg(this,smvModule);
			try {
				alg.modelCheckingOneSpec(spec);
			} catch (SpecException ex) {
				ex.printStackTrace();
			} catch (ModelCheckException ex) {
				ex.printStackTrace();
			} catch (ModuleException ex) {
				ex.printStackTrace();
			} catch (ModelCheckAlgException ex) {
				ex.printStackTrace();
			} catch (SMVParseException ex) {
				ex.printStackTrace();
			}

		}
		if(e.getSource()==saveSpecsButton){
			Object[] options = {"Save","Cancel"};
			int response = JOptionPane.showOptionDialog(this,
					"Do you want to save these specifications to the opened SMV file?",
					"Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if(response==0){
				//save
				String specs = generateSpecsString();
			}

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

	public Spec generateSpec(int row){
		if(specsTableModel.getRowCount()<=0) return null;
		if(row<0 || row>specsTableModel.getRowCount()-1) return null;
		String s="";
		String inputSpec = ((String) specsTableModel.getValueAt(row, colSpec)).trim();

		if(inputSpec!=null && inputSpec!=""){
			String aLine="RTCTL*SPEC "+inputSpec.trim();
			Spec[] specs = Env.loadSpecString(aLine);
			if(specs==null || specs[0]==null) return null;
			else return specs[0];
		}
		return null;
	}

	public String generateSpecsString(){
		String s="";
		for(int row=0; row<specsTableModel.getRowCount();row++){
			String inputSpec = ((String) specsTableModel.getValueAt(row, colSpec)).trim();
			String inputAnn = ((String) specsTableModel.getValueAt(row, colAnnotation)).trim();

			if(inputSpec!=null && inputSpec!=""){
				String aLine="RTCTL*SPEC "+inputSpec.trim()+";";
				Spec[] specs = Env.loadSpecString(aLine);
/*
				if(specs==null || specs[0]==null) {
					System.out.println("The following specification is illegal: "+aLine);
					return "";
				}else{
*/
					if(!inputAnn.equals("")) aLine+=" -- "+inputAnn+"\n"; else aLine+="\n";
					s+=aLine;
//				}
			}
		}
		return s;
	}


}