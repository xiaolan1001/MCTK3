package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static swing.mainJFrame.editorPanel;


public class SetFont implements ActionListener {
	JDialog setFontDialog;
	String[] fontValues={"Regular","Arial","Times New Roman","Arial Italic","Courier","Microsoft YaHei"};
	String[] styleValues={"Regular","Italic","Bold"};
	int[] styleValues2={Font.PLAIN,Font.ITALIC,Font.BOLD};
	int[] sizeValues={5,6,7,8,9,10,12,14,16,18,20,22,24,26,28,36};
	JComboBox fontValue;
	JComboBox styleValue;
	JComboBox sizeValue;
	mainJFrame indexJFrame;
	public SetFont(mainJFrame indexJFrame)
	{
		this.indexJFrame=indexJFrame;
		setFontDialog=new JDialog(this.indexJFrame);
		setFontDialog.setTitle("Font Settings");//��������
		fontValue=new JComboBox();
		styleValue=new JComboBox();
		sizeValue=new JComboBox();
		setFontDialog.setLayout(new BorderLayout());
		init();
	}
	
	public void init()
	{
		JLabel font=new JLabel("Font");//����
		JLabel style=new JLabel("Type");//����
		JLabel size=new JLabel("Size");//��С
		
		for(int i=0;i<fontValues.length;i++)
		{
			fontValue.addItem(fontValues[i]);
		}
		for(int i=0;i<styleValues.length;i++)
		{
			styleValue.addItem(styleValues[i]);
		}
		for(int i=0;i<sizeValues.length;i++)
		{
			sizeValue.addItem(sizeValues[i]);
		}
		
		sizeValue.setSelectedIndex(8);
		
		JButton ok=new JButton("OK");
		JButton no=new JButton("Cancel");
		
		Panel names=new Panel(new GridLayout(3,1));
		names.add(font);
		names.add(style);
		names.add(size);

		Panel values=new Panel(new GridLayout(3, 1));
		values.add(fontValue);
		values.add(styleValue);
		values.add(sizeValue);
		Panel contral=new Panel();
		contral.add(ok);
		contral.add(no);
		ok.addActionListener(this);
		no.addActionListener(this);
		setFontDialog.add("West",names);
		setFontDialog.add("Center", values);
		setFontDialog.add("South", contral);
		setFontDialog.setLocation(mainJFrame.width/3, mainJFrame.height/3);
		setFontDialog.setSize(240, 210);
		setFontDialog.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK"))
		{
			int fontIndex=fontValue.getSelectedIndex();
			int styleIndex=styleValue.getSelectedIndex();
			int sizeIndex=sizeValue.getSelectedIndex(); 
			Font f=new Font(fontValues[fontIndex],styleValues2[styleIndex],sizeValues[sizeIndex]);
			editorPanel.setFontStyle(f);
			setFontDialog.dispose();
		} else if(e.getActionCommand().equals("Cancel"))
		{
			setFontDialog.dispose();
		}
	}
	
}
