package swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;



public class EditorJPanel implements KeyListener, ActionListener, ChangeListener{
    MCTK2Frame indexJFrame;
    static JTextPane rowTextPane;
    static JTextPane modelTextPane =new JTextPane();
    JScrollPane rowScrollPane, textScrollPane;
    ColorKeyWords cKeyWord;

    JPopupMenu jPopMenu;
    JPopupMenu cPopMenu;

    Clipboard clipboard;
    static StringBuffer rowContent;
    BoundedRangeModel model;

    //Font  DeFont=new Font("TimesRoman",0,14);
    Font  DeFont=new Font("System",0, VerifyActionListener.inputFontSize);
    public EditorJPanel(MCTK2Frame indexJFrame)
    {
        this.indexJFrame=indexJFrame;
        clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
        modelTextPane.setBorder(null);
        textScrollPane =new JScrollPane(modelTextPane);
        rowTextPane =new JTextPane(); rowTextPane.setBorder(null);
        rowScrollPane =new JScrollPane(rowTextPane);
        rowScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        jPopMenu=new JPopupMenu();
        cPopMenu=new JPopupMenu();
        cKeyWord=new ColorKeyWords(modelTextPane);
        setFontStyle(DeFont);
        initEditor();
    }


    public void initEditor()
    {
        JMenuItem cut=new JMenuItem("Cut(X)");
        JMenuItem copy=new JMenuItem("Copy(C)");
        JMenuItem paste=new JMenuItem("Paste(V)");
        jPopMenu.add(cut);
        jPopMenu.add(copy);
        jPopMenu.add(paste);
        JMenuItem clear=new JMenuItem("Clear");
        cPopMenu.add(clear);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        clear.addActionListener(this);

        modelTextPane.add(jPopMenu);
        modelTextPane.addMouseListener(new MyMouseListener());
        //+++++++++++++++++++++++++++++++++++++++++++++++++++
        modelTextPane.getDocument().addDocumentListener(cKeyWord);

        rowTextPane.setForeground(Color.lightGray);
        rowTextPane.setText("1");
        rowTextPane.setPreferredSize(new Dimension(45,  Toolkit.getDefaultToolkit().getScreenSize().height));
        rowTextPane.setEnabled(false);
        rowTextPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        indexJFrame.editorPanelContainer.add("West", rowScrollPane);
        indexJFrame.editorPanelContainer.add("Center", textScrollPane);

        //((BorderLayout)indexJFrame.editorPanelContainer.getLayout()).setHgap(0);
        //((BorderLayout)indexJFrame.editorPanelContainer.getLayout()).setVgap(0);

        modelTextPane.addKeyListener(this);
        model= textScrollPane.getVerticalScrollBar().getModel();
        model.addChangeListener(this);
    }
    public void setFontStyle(Font font)
    {
        modelTextPane.setFont(font);
        rowTextPane.setFont(font);
        setRowContent();
    }
    @Override
    public void keyPressed(KeyEvent e) { }
    @Override
    public void keyReleased(KeyEvent e) {
/*
        if ((e.isControlDown() == true) && ((e.getKeyCode() == KeyEvent.VK_V)|(e.getKeyCode() == KeyEvent.VK_X)))
            setRowContent();//检测到键盘输入，更新行号
        StringBuffer s=new StringBuffer(modelTextPane.getText());//获取当前文本内容
        if(e.getKeyCode()==9)//修改tab缩进值
        {
            int pos= modelTextPane.getCaretPosition();
            StyledDocument doc = modelTextPane.getStyledDocument();
            Style style = doc.addStyle("normalstyle", null);
            try {
                doc.remove(pos-1, 1);
                doc.insertString(pos-1, "   ", style);
            } catch (BadLocationException be) {
                be.printStackTrace();
            }
        }
*/

    }
    @Override
    public void keyTyped(KeyEvent e) {
/*        char x=e.getKeyChar();//获取当前键盘输入符号
        String text= modelTextPane.getText().replaceAll("\n", "");//获取当前文本内容
        int pos= modelTextPane.getCaretPosition();
        if(x=='\n')
        {
            setRowContent();
            tabTime();
            if(pos<text.length()&&text.charAt(pos)=='}')
            {
                StyledDocument doc = modelTextPane.getStyledDocument();
                try {
                    doc.remove(pos+3, 3);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
            return ;
        }

        if(e.getKeyChar()=='}'&&text.charAt(pos-1)==' ')//当未回车插入}时不进行此操作
        {
            StyledDocument doc = modelTextPane.getStyledDocument();
            try {
                doc.remove(pos-3, 3);
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
*/
        setRowContent();

    }

    public static void setRowContent()
    {
        rowContent=new StringBuffer();
        int modelElementCount=modelTextPane.getText().split("\\n").length;
        int rowElementCount=rowTextPane.getText().split("\\n").length;
        
//        if(rowElementCount==modelElementCount) return;
        if(rowTextPane.getDocument().getDefaultRootElement().getElementCount() ==
                modelTextPane.getDocument().getDefaultRootElement().getElementCount())
            return;

        rowTextPane.setText("");
        for(int i=0;i<modelElementCount;i++)
        {
            rowContent.append((i+1)+"\n");
        }
        rowTextPane.setText(rowContent.toString());
    }

    public void tabTime() //缩进处理
    {
        StyledDocument doc = modelTextPane.getStyledDocument();
        Style style = doc.addStyle("normalstyle", null);
        int tabNum=0;
        String text= modelTextPane.getText().replaceAll("\n", "");
        int pos= modelTextPane.getCaretPosition();
        for(int i=0;i<pos;i++)
        {
            if(text.charAt(i)=='{')
            {
                tabNum++;
            }
            if(text.charAt(i)=='}')
            {
                tabNum--;
            }
        }
        for(int i=0;i<tabNum;i++)
        {
            try {
                doc.insertString(modelTextPane.getCaretPosition(), "   ", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name= e.getActionCommand();
        if(name.equals("Cut(X)"))
        {
            String temp= modelTextPane.getSelectedText();
            StringSelection content = new StringSelection(temp);
            clipboard.setContents(content,null);
            int x= modelTextPane.getSelectionStart();
            int y= modelTextPane.getSelectionEnd();
            StyledDocument doc = modelTextPane.getStyledDocument();
            try {
                doc.remove(x,y-x);
            } catch (BadLocationException be) {
                be.printStackTrace();
            }
            setRowContent();//更新行号
        }
        if(name.equals("Paste(V)"))
        {
            Transferable contents = clipboard.getContents(this);
            DataFlavor flavor = DataFlavor.stringFlavor;
            String str="";
            if(contents.isDataFlavorSupported(flavor))
                try{
                    str = (String)contents.getTransferData(flavor);
                }catch(Exception ee){}

            int pos= modelTextPane.getCaretPosition();
            StyledDocument doc = modelTextPane.getStyledDocument();
            Style style = doc.addStyle("normalstyle", null);
            try {
                doc.insertString(pos, str, style);
            } catch (BadLocationException be) {
                be.printStackTrace();
            }
            setRowContent();//更新行号
        }
        if(name.equals("Copy(C)"))
        {
            String temp= modelTextPane.getSelectedText();
            StringSelection content = new StringSelection(temp);
            clipboard.setContents(content,null);
        }
    }

    private class MyMouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopMenu.show(modelTextPane, e.getX(), e.getY());
            }
        }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==model)
        {
            JScrollBar sBar = textScrollPane.getVerticalScrollBar();
            int x=sBar.getValue();
            JScrollBar sBar2 = rowScrollPane.getVerticalScrollBar();
            sBar2.setValue(x);
            rowScrollPane.setVerticalScrollBar(sBar2);
        }
    }
}
