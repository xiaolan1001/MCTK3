package swing;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static swing.EditorJPanel.modelTextPane;
import static swing.EditorJPanel.setRowContent;
import static swing.FileOperation.currentPathFileName;
import static swing.MCTKFrame.modelTextPaneChanged;
import static swing.MCTKFrame.specsTableChanged;

public class MenuToolBarJPanel implements ActionListener{

	MCTKFrame mainFrame;
	FileOperation fileOperation;
	JMenuBar menuBar;
	public static UndoManager uo;
	Document doc;

	//创建工具栏
	//JPanel toolBar=new JPanel();
	JToolBar toolBar=new JToolBar();

	private JButton // newButton,
			openButton,saveAsButton,saveButton,
			 undoButton,redoButton,cutButton,copyButton,
			 pasteButton,frontButton,
			//compileButton,
			helpButton;

	public MenuToolBarJPanel(MCTKFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		fileOperation=new FileOperation(mainFrame);
		creatMenu();
	}

	public void creatMenu()
	{
		menuBar =new JMenuBar();
		JMenu file=new JMenu("File");
		JMenu edit=new JMenu("Edit");
		JMenu style=new JMenu("Style");
//		JMenu run=new JMenu("Run");
		JMenu about=new JMenu("Help");

		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(style);
//		menuBar.add(run);
		menuBar.add(about);

		JMenuItem newFile=new JMenuItem("New");
		setShortcut(newFile, KeyEvent.VK_N);
		JMenuItem open=new JMenuItem("Open File");
		setShortcut(open, KeyEvent.VK_O);
		JMenuItem save=new JMenuItem("Save");
		setShortcut(save, KeyEvent.VK_S);
		JMenuItem saveas=new JMenuItem("Save As");
		JMenuItem quit=new JMenuItem("Quit MCTK");
		setShortcut(quit, KeyEvent.VK_Q);


		JMenuItem redo=new JMenuItem("Undo");
		setShortcut(redo, KeyEvent.VK_Z);
		JMenuItem undo=new JMenuItem("Redo");
		setShortcut(undo, KeyEvent.VK_Y);


		JMenuItem copy=new JMenuItem("Copy");
		setShortcut(copy, KeyEvent.VK_C);
		JMenuItem paste=new JMenuItem("Paste");
		setShortcut(paste, KeyEvent.VK_V);
		JMenuItem cut=new JMenuItem("Cut");
		setShortcut(cut, KeyEvent.VK_X);
		JMenuItem selall=new JMenuItem("Select All");
		setShortcut(cut, KeyEvent.VK_A);
		JMenuItem setFont=new JMenuItem("Set Front");
		JMenuItem compile=new JMenuItem("Compile Model");
		JMenuItem verify=new JMenuItem("Verify Model");
		//JMenuItem sourcecode=new JMenuItem("Source Code");
		Icon codeIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/code.png"));
		//sourcecode.setIcon(codeIcon);

		JMenuItem about1=new JMenuItem("About");
		Icon aboutIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/helppng.png"));
		about1.setIcon(aboutIcon);


		file.add(newFile);file.addSeparator();
		file.add(open);file.addSeparator();
		file.add(save);
		file.add(saveas);file.addSeparator();
		file.add(quit);
		edit.add(undo);
		edit.add(redo);edit.addSeparator();
		edit.add(copy);edit.add(paste);
		edit.add(cut);edit.addSeparator();
		edit.add(selall);
		style.add(setFont);
//		run.add(compile);run.add(verify);
		//about.add(sourcecode);
		about.add(about1);

		newFile.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		saveas.addActionListener(this);
		quit.addActionListener(this);
		undo.addActionListener(this);
		redo.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
		cut.addActionListener(this);
		selall.addActionListener(this);
		compile.addActionListener(this);
		verify.addActionListener(this);
		//sourcecode.addActionListener(this);
		about.addActionListener(this);

//		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		Icon newIcon = new ImageIcon(MCTKFrame.class.getResource("/swing/Icons/new.png"));
		Icon openIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/open.png"));
		Icon saveIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/save.png"));
		Icon saveAsIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/saveas.png"));
		Icon undoIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/Undo.gif"));
		Icon redoIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/Redo.gif"));
		Icon cutIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/cut.png"));
		Icon copyIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/copy.png"));
		Icon pasteIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/paste.png"));
		Icon frontIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/font.png"));
		Icon compileIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/debug.png"));
		Icon helpIcon=new ImageIcon(MenuToolBarJPanel.class.getResource("/swing/Icons/help.png"));

/*
		newButton= new JButton(newIcon);
		newButton.setBorderPainted(false);
*/
		openButton=new JButton(openIcon);
		openButton.setBorderPainted(false);
		saveButton= new JButton(saveIcon);
		saveButton.setBorderPainted(false);
		saveAsButton=new JButton(saveAsIcon);
		saveAsButton.setBorderPainted(false);
		undoButton=new JButton(undoIcon);
		undoButton.setBorderPainted(false);
		redoButton=new JButton(redoIcon);
		redoButton.setBorderPainted(false);
		cutButton=new JButton(cutIcon);
		cutButton.setBorderPainted(false);
		copyButton=new JButton(copyIcon);
		copyButton.setBorderPainted(false);
		pasteButton=new JButton(pasteIcon);
		pasteButton.setBorderPainted(false);

		frontButton=new JButton(frontIcon);
		frontButton.setBorderPainted(false);
//		compileButton=new JButton(compileIcon);
//		compileButton.setBorderPainted(false);

		helpButton=new JButton(helpIcon);
		helpButton.setBorderPainted(false);

//		newButton.setPreferredSize(new Dimension(22,22));
		openButton.setPreferredSize(new Dimension(22,22));
		saveButton.setPreferredSize(new Dimension(22,22));
		saveAsButton.setPreferredSize(new Dimension(22,22));
		undoButton.setPreferredSize(new Dimension(22,22));
		redoButton.setPreferredSize(new Dimension(22,22));
		cutButton.setPreferredSize(new Dimension(22,22));
		copyButton.setPreferredSize(new Dimension(22,22));
		pasteButton.setPreferredSize(new Dimension(22,22));
		frontButton.setPreferredSize(new Dimension(22,22));
//		compileButton.setPreferredSize(new Dimension(22,22));
		helpButton.setPreferredSize(new Dimension(22,22));
		//注册工具栏按钮事件
//		newButton.addActionListener(this);
		openButton.addActionListener(this);
		saveButton.addActionListener(this);
		saveAsButton.addActionListener(this);
		undoButton.addActionListener(this);
		redoButton.addActionListener(this);
		cutButton.addActionListener(this);
		copyButton.addActionListener(this);
		pasteButton.addActionListener(this);
		frontButton.addActionListener(this);
//		compileButton.addActionListener(this);
		helpButton.addActionListener(this);
		//设置按钮提示文字
//		newButton.setToolTipText("New File");
		openButton.setToolTipText("Open");
		saveButton.setToolTipText("Save");
		saveAsButton.setToolTipText("Save As");
		undoButton.setToolTipText("Undo");
		redoButton.setToolTipText("Redo");
		cutButton.setToolTipText("Cut");
		copyButton.setToolTipText("Copy");
		pasteButton.setToolTipText("Paste");
		frontButton.setToolTipText("Front Style");
//		compileButton.setToolTipText("Compile This Model");
		helpButton.setToolTipText("About MCTK 3.0.0");

		//向工具栏添加按钮
		toolBar.setBorderPainted(false);
		toolBar.setBorder(new EmptyBorder(8,8,0,0));
//		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.add(saveAsButton);

		toolBar.addSeparator();

		toolBar.add(undoButton);
		toolBar.add(redoButton);

		toolBar.addSeparator();

		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);

		toolBar.addSeparator();

		toolBar.add(frontButton);
//		toolBar.add(compileButton);
		toolBar.add(helpButton);

		uo = new UndoManager();
		doc= modelTextPane.getDocument();

		doc.addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(UndoableEditEvent e)
			{
				uo.addEdit(e.getEdit());
			}
		});
		setFont.addActionListener(this);
		about1.addActionListener(this);

		mainFrame.menuToolBarPanel.add("North", menuBar);
		mainFrame.menuToolBarPanel.add("South", toolBar);
	}

	public void setShortcut(JMenuItem jmi,int key)
	{
		KeyStroke ms1 = KeyStroke.getKeyStroke(key,InputEvent.CTRL_MASK);
		jmi.setMnemonic(key);
		jmi.setAccelerator(ms1);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Save")||e.getSource()==saveButton) {
			try {
				fileOperation.saveFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if(e.getActionCommand().equals("Save As")||e.getSource()==saveAsButton) {
			try {
				fileOperation.SaveAs();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
//		} else if(e.getActionCommand().equals("New")||e.getSource()==newButton) {
//			fileOperation.creat();
		} else if(e.getActionCommand().equals("Open File")||e.getSource()==openButton) {
			if(fileOperation.open());
			setRowContent();
		} else if(e.getActionCommand().equals("Quit MCTK")) {
			try {
				quitMCTK();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if(e.getActionCommand().equals("Set Front")||e.getSource()==frontButton) {
			new SetFont(mainFrame);
		} else if(e.getActionCommand().equals("Undo")||e.getSource()==undoButton) {
			if(uo.canUndo())
				uo.undo();
			//System.out.println("Undo");
		} else if(e.getActionCommand().equals("Redo")||e.getSource()==redoButton) {
			if(uo.canRedo())
			uo.redo();
			//System.out.println("Redo");
		} else if(e.getActionCommand().equals("Copy")||e.getSource()==copyButton) {
			modelTextPane.copy();// 复制
			//System.out.println("Copy");
		} else if(e.getActionCommand().equals("Paste")||e.getSource()==pasteButton) {
			modelTextPane.paste();
			setRowContent();
			//System.out.println("Paste");
		} else if(e.getActionCommand().equals("Cut")||e.getSource()==cutButton) {
			modelTextPane.cut();
			setRowContent();
			//System.out.println("Cut");
		} else if(e.getActionCommand().equals("Select All")) {
			modelTextPane.selectAll();
/*
		}  else if(e.getActionCommand().equals("Compile Model")||e.getSource()==compileButton) {
			try {
				mainFrame.readSMVFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
*/
		} else if(e.getActionCommand().equals("Verify Model")) {
			mainFrame.creatVerifyUI();
/*
			indexJFrame.editorLabel.setText("<html><font color='black'>" + " SMV Editor " + "</font>&nbsp;</html>");
			indexJFrame.verifyLabel.setText("<html><font color='#336699' style='font-weight:bold'>" + " Verification " + "</font>&nbsp;</html>");
*/
			try {
				mainFrame.readSMVmodulesFromFile();//读取SMV中文本属性
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}else if(e.getActionCommand().equals("Source Code")){
			new Thread() {
				public void run() {
					String cmd = "cmd.exe /c start ";
					String URL = "https://github.com/hovertiger/MCTK2-under-development-backup-20180123";
					try {
						Runtime.getRuntime().exec(cmd + URL);
					} catch (IOException ignore) {
						ignore.printStackTrace();
					}
				}
			}.start();//start this thread
		} else if(e.getActionCommand().equals("About")||e.getSource()==helpButton){
			Object[] options = {"OK"};
			JOptionPane.showOptionDialog(mainFrame, "MCTK 3.0.0 : A Symbolic Model Checker for Real-Time Branching Time Dynamic Logic RTCDL* and Real-Time Branching Time Logic RTCTL*\n" +
							"Developers:\n" +
							"    Xiangyu Luo, Huaqiao University, luoxy@hqu.edu.cn\n" +
							"    Sen Liang, Huaqiao University, liangsen@hqu.edu.cn\n" +
					"MCTK3 Website: https://gitlab.com/hovertiger/mctk3",
					"About",JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		}
	}

	public void quitMCTK() throws IOException {
		if (currentPathFileName.equals("")){
			if(!modelTextPaneChanged && !specsTableChanged()){
				Object[] options = {"Quit", "Cancel"};
				int response = JOptionPane.showOptionDialog(null, "Do you want to quit MCTK?",
						"Quit MCTK", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if(response==0) System.exit(0); else return;
			}else{ // there is change occurred
				Object[] options = {"Save", "Don't Save and Quit", "Cancel"};
				int response = JOptionPane.showOptionDialog(null, "Do you want to save the editing model?",
						"Save As", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(response==0) {
					fileOperation.saveFile();
				}else if (response == 1) {
					System.exit(0);
				}else if(response==2){
					return;
				}
			}
		}else{ // a file opened
			if(!modelTextPaneChanged && !specsTableChanged()){
				Object[] options = {"Quit", "Cancel"};
				int response = JOptionPane.showOptionDialog(null, "Do you want to quit MCTK?",
						"Quit MCTK", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if(response==0) System.exit(0); else return;
			}else{ // there is change occurred
				Object[] options = {"Save and Quit", "Don't Save and Quit", "Cancel"};
				int response = JOptionPane.showOptionDialog(null, "Do you want to save the editing model?",
						"Save File", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(response==0) {
					fileOperation.saveFile();
					System.exit(0);
				}else if (response == 1) {
					System.exit(0);
				}else if(response==2){
					return;
				}
			}
		}
	}
}
