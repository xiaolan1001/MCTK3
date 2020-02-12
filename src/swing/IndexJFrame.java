package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class IndexJFrame extends JFrame implements MouseListener {
	// �����Ļ�Ĵ�С
	final static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	final static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	// ����ȫ�����
	JPanel backgroundPanel;
	JPanel topPanel;
	JPanel topMenu,pageMenu;
	JPanel centerPanel;
	JLabel modelTexter, verifyUI;
	public static CtrlJPanel controlPanel;
	public static EditorJPanel editorPanel;
	public static VerifyJPanel verifyPanel;
	public IndexJFrame()
	{
		Image logoIcon = new ImageIcon(IndexJFrame.class.getResource("/swing/Icons/logo.png")).getImage();
		this.setIconImage(logoIcon);
		initBackgroundPanel();
		this.setTitle("  MCTK 2.0  ");
		this.setSize((int) (width * 0.8f), (int) (height * 0.8f));
		this.setVisible(true);
		this.setLocationRelativeTo(null);    // �˴��ڽ�������Ļ�����롣
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

	// ��ʼ���������
	public void initBackgroundPanel() {
		backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.setOpaque(true);//���ò�͸��

		initTop();
		initCenterPanel();
		backgroundPanel.add(topPanel, "North");
		backgroundPanel.add(centerPanel, "Center");
		this.add(backgroundPanel);
	}

	// ��ʼ�������������
	public void initTop() {
		initTopMenu();
		initPageMenu();
		topPanel = new JPanel(new BorderLayout());
		topPanel.setPreferredSize(new Dimension(width, 90));
		topPanel.add(topMenu, "North");
		topPanel.add(pageMenu, "Center");
	}
	// ��ʼ�������˵�
	public void initTopMenu() {
		topMenu = new JPanel(new BorderLayout());
		controlPanel=new CtrlJPanel(this);
	}

	// ��ʼ�������˵�
	public void initPageMenu() {
		pageMenu = new JPanel(new FlowLayout());
		//topMenu.setPreferredSize(new Dimension(900, 40));
		//pageMenu.setOpaque(false);
		String[] nameStrings = {" SMV Editor ", " Verification "};
		modelTexter = CreateMenuLabel(modelTexter, nameStrings[0], "text", pageMenu);
		modelTexter.setName("Editor");
		//���һ����
		JLabel line = new JLabel("<html>&nbsp;<font color='#D2D2D2'>|</font>&nbsp;</html>");
		pageMenu.add(line);

		verifyUI = CreateMenuLabel(verifyUI, nameStrings[1], "verify", pageMenu);
		verifyUI.setName("Verify");
	}
	// ��ʼ���������
	public void initCenterPanel() {
		centerPanel = new JPanel(new BorderLayout());
		modelTexter.setText("<html><font color='#336699' style='font-weight:bold'>" + " SMV Editor " + "</font>&nbsp;</html>");
		//centerPanel.setOpaque(false);// ���ÿؼ�͸��
		editorPanel=new EditorJPanel(this);
		verifyPanel=new VerifyJPanel(this);
	}

	// ���������˵�Label
	public JLabel CreateMenuLabel(JLabel jlabel, String text, String name, JPanel jpanel) {
		Icon icon = new ImageIcon(IndexJFrame.class.getResource("/swing/Icons/" + name + ".png"));
		jlabel = new JLabel(icon);
		jlabel.setText("<html><font color='black'>" + text + "</font>&nbsp;</html>");
		jlabel.addMouseListener(this);
		jlabel.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		jpanel.add(jlabel);
		return jlabel;
	}
	// �����༭�����
	public void creatEditor() {
		centerPanel.removeAll();
		centerPanel.add("West",editorPanel.rowScroll);
		centerPanel.add("Center",editorPanel.textScroll);
		centerPanel.updateUI();
	}
	// ������֤���
	public void creatVerifyUI() {
		centerPanel.removeAll();
		centerPanel.add(VerifyJPanel.HJPanel, "Center");
		centerPanel.updateUI();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == modelTexter) {
			creatEditor();
			modelTexter.setText("<html><font color='#336699' style='font-weight:bold'>" + " SMV Editor " + "</font>&nbsp;</html>");
			verifyUI.setText("<html><font color='black'>" + " Verification " + "</font>&nbsp;</html>");
		} else if (e.getSource() == verifyUI) {
			creatVerifyUI();
			modelTexter.setText("<html><font color='black'>" + " SMV Editor " + "</font>&nbsp;</html>");
			verifyUI.setText("<html><font color='#336699' style='font-weight:bold'>" + " Verification " + "</font>&nbsp;</html>");
			verifyPanel.ReadSMVSpec();//��ȡSMV���ı�����
		}
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
