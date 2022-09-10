package swing;

import org.graphstream.graph.implementations.MultiGraph;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import static swing.MCTKFrame.controlPanel;
import static swing.MCTKFrame.editorPanel;


public class VerifyActionListener implements ActionListener {
    public static int buttonFontSize = 18;
    public static int inputFontSize = 18;
    public static int outputFontSize = 18;

    public static int specInputLineHeight =40;

    MCTKFrame indexJFrame;
    MultiGraph graph;
    JPanel buttonsSpecsPanel, witnessPanel;
    JPopupMenu jPopMenu;
    JMenuItem menuItemCopy, menuItemClear;
    Clipboard clipboard;
    JButton readButton, VerifyButton, ClearButton, SaveButton;
    Vector<JPanel> specPanelsVector = new Vector<JPanel>();
    Vector<JTextArea> specTextAreasVector = new Vector<JTextArea>();

    JPanel specsPanel;
    JTextArea specTextArea = new JTextArea();// 文本窗格
    JButton addButton, delButton, verifyButton;

    public JTextPane outputTextPane;// Console of verification
    JScrollPane buttonsSpecsScrollPane, outputScrollPane;
    public static JSplitPane mainSplitPane;
    Icon plusIcon = new ImageIcon(MenuToolBarJPanel.class.getResource("/Icons/plusm.gif"));
    Icon minIcon = new ImageIcon(MenuToolBarJPanel.class.getResource("/Icons/minusm.gif"));
    Icon witIcon = new ImageIcon(MenuToolBarJPanel.class.getResource("/Icons/witm.gif"));
    Icon verIcon = new ImageIcon(MenuToolBarJPanel.class.getResource("/Icons/verm.gif"));
    final String atltips = "Please input a RTCDL*SPEC...";


    public VerifyActionListener(MCTKFrame indexJFrame) {
        this.indexJFrame = indexJFrame;
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        buttonsSpecsPanel = new JPanel(new BorderLayout());// (new GridLayout(3,1));
        buttonsSpecsPanel.setLayout(new BoxLayout(buttonsSpecsPanel, BoxLayout.Y_AXIS));

        // the structure of verification form
        // mainSplitPane = (LEFT:buttonsSpecsScrollPane, RIGHT:outputScrollPane)
        // buttonsSpecsScrollPanel = (buttonsSpecsPanel)
        // buttonsSpecsPanel = (NORTH:buttonsPanel, specsPanel)
        // outputScrollPane = (outputText)

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        readButton = createBtn("Read Specs", "/Icons/extract.png", 120, specInputLineHeight);
        VerifyButton = createBtn("Verify All", "/Icons/verifyAll.png", 120, specInputLineHeight);
        ClearButton = createBtn("Delete All", "/Icons/deleteAll.png", 120, specInputLineHeight);
        SaveButton = createBtn("Save Specs", "/Icons/saveAll.png", 120, specInputLineHeight);

        buttonsPanel.add(readButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(50, specInputLineHeight)));
        buttonsPanel.add(VerifyButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(50, specInputLineHeight)));
        buttonsPanel.add(ClearButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(50, specInputLineHeight)));
        buttonsPanel.add(SaveButton);
        buttonsSpecsPanel.add(buttonsPanel, BorderLayout.NORTH);

        specsPanel = new JPanel();
        specsPanel.setLayout(new BoxLayout(specsPanel, BoxLayout.Y_AXIS));

        JTextArea specTextArea = insertSpecLine("",0);
        specTextArea.requestFocus(true);

        buttonsSpecsPanel.add(specsPanel);
        buttonsSpecsScrollPane = new JScrollPane(buttonsSpecsPanel);

        outputTextPane = new JTextPane();
        outputScrollPane = new JScrollPane(outputTextPane);
        addDocument(outputTextPane, "Verification information...", outputFontSize, Color.BLUE, 1);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        menuItemCopy = new JMenuItem("Copy(C)");
        menuItemClear = new JMenuItem("Clear");
        jPopMenu = new JPopupMenu();
        jPopMenu.add(menuItemCopy);
        jPopMenu.add(menuItemClear);
        menuItemCopy.addActionListener(this);
        menuItemClear.addActionListener(this);
        outputTextPane.add(jPopMenu);
        outputTextPane.setBorder(null);
        outputTextPane.addMouseListener(new MyMouseListener());

        witnessPanel = new JPanel(new BorderLayout());//初始化
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(buttonsSpecsScrollPane);
        mainSplitPane.setRightComponent(outputScrollPane);
        mainSplitPane.setDividerSize(5);//设置分隔条大小，以像素为单位
        mainSplitPane.setDividerLocation(950);

        buttonsSpecsPanel.setOpaque(true);
        outputTextPane.setOpaque(true);
        buttonsSpecsScrollPane.setOpaque(true);
        outputScrollPane.setOpaque(true);
        mainSplitPane.setOpaque(true);
    }
    /**
     * 创建工具栏按钮
     * @param text 按钮名称
     * @param icon 按钮图标所在路径
     * @return 返回添加样式和监听器后的按钮
     * @author LiangSen
     */
    public JButton createBtn(String text, String icon, int width, int height) {
        JButton btn = new JButton(text, new ImageIcon(MCTKFrame.class.getResource(icon)));
        //btn.setUI(new BasicButtonUI());// 恢复基本视觉效果
        btn.setPreferredSize(new Dimension(width, height));// 设置按钮大小
        btn.setForeground(Color.BLUE);
        btn.setContentAreaFilled(true);// 设置按钮透明
        btn.setFont(new Font("粗体", Font.PLAIN, buttonFontSize));// 按钮文本样式
        btn.setMargin(new Insets(0, 0, 0, 0));// 按钮内容与边框距离
        btn.addActionListener(this);
        return btn;
    }

    // the source can be JButton or JTextArea
    int getClickedSourceIndex(ActionEvent e){
        //if(e.getSource()!=addButton) return -1;
        JPanel specLinePanel;
        if(e.getSource() instanceof JButton){
            specLinePanel=(JPanel) ((JButton)e.getSource()).getParent();
        }else if(e.getSource() instanceof JTextArea){
            specLinePanel=(JPanel) ((JTextArea)e.getSource()).getParent();
        }else return -1;
        return specPanelsVector.indexOf(specLinePanel);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemCopy) {
            String temp = outputTextPane.getSelectedText();
            StringSelection content = new StringSelection(temp);
            clipboard.setContents(content, null);
        } else if (e.getSource() == menuItemClear) {
            outputTextPane.setText("");
            addDocument(outputTextPane, "Verification information...", outputFontSize, Color.BLUE, 1);
        } else if (e.getSource() == addButton || e.getActionCommand().equals("atlADD")) {
            JTextArea specTextArea=insertSpecLine("",getClickedSourceIndex(e));
            specTextArea.requestFocus(true);
        } else if (e.getSource() == delButton) {
            specTextArea.setText("");
        } else if (e.getSource() == verifyButton) {
            String specific = specTextArea.getText();
            if (atltips.equalsIgnoreCase(specific) || specific.equals("") || specific.trim().startsWith("--"))
                addDocument(outputTextPane, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
            else if (specific.endsWith(";"))
                runModelChecking("RTCDL*SPEC ".concat(specific), false);
            else
                runModelChecking("RTCDL*SPEC ".concat(specific) + ";", false);
        } else if (((JButton) e.getSource()).getText().equals("Extract Spec")) {
            if (ExtractSpec()) {
                Object[] options = {"OK"};
                int response = JOptionPane.showOptionDialog(indexJFrame, "Read all specificatons from SMV successfully!",
                        "Tips", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }else{
                Object[] options = {"OK"};
                int response = JOptionPane.showOptionDialog(indexJFrame, "Unable to read specifications, please check the model.",
                        "Error", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }

        } else if (((JButton) e.getSource()).getText().equals("Verify All")) {
            String parse = GetAllSpec();
            if ("".equals(parse))
                addDocument(outputTextPane, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
            else
                runModelChecking(parse, false);
        } else if (((JButton) e.getSource()).getText().equals("Delete All")) {
            while (specPanelsVector.size() > 1) {
                specsPanel.remove(specPanelsVector.lastElement());
                specPanelsVector.remove(specPanelsVector.lastElement());
                specTextAreasVector.remove(specTextAreasVector.lastElement());
                specsPanel.revalidate();
            }
            specTextArea.setText("");
        } else if (((JButton) e.getSource()).getText().equals("Save All")) {
            String parse = GetAllSpec(); //Read all the Specifications from SMV
            editorPanel.modelTextPane.setText(editorPanel.modelTextPane.getText() + parse);
            try {
                controlPanel.fileOperation.saveFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (parse.equals("")) {
                Object[] options = {"OK"};
                int response = JOptionPane.showOptionDialog(indexJFrame, "Please input a specificaton firstly!",
                        "Error", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }else{
                Object[] options = {"OK"};
                int response = JOptionPane.showOptionDialog(indexJFrame, "Save all specificatons to SMV successfully!",
                        "Tip", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }
        }
    }

    private class MyMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                jPopMenu.show(outputTextPane, e.getX(), e.getY());
            }

        }
    }

    // In the spec list, insert a new spec line before the index
    protected JTextArea insertSpecLine(String property, int insertPos) {
        JPanel specLinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("+"); //plusIcon);
        addButton.setActionCommand("atlADD");
        JButton delButton = new JButton("-"); //minIcon);

        String tips = "Please input a RTCDL*SPEC...";
        JTextArea specTextArea = new JTextArea();// 文本窗格
        specTextArea.setPreferredSize(new Dimension(650, specInputLineHeight));
        specTextArea.setText("");
        specTextArea.setLineWrap(true);
        specTextArea.setFont(new Font("Default", Font.PLAIN, inputFontSize));
        specTextArea.setAutoscrolls(true);
        //specTextArea.setRows(2);

        JButton verifyButton = createBtn("Verify", "/Icons/verifyAll.png", 100, specInputLineHeight); //new JButton(verIcon);
        JLabel resultLabel = new JLabel(""); // used to show the truth of the spec
        resultLabel.setForeground(Color.RED);
        resultLabel.setFont(new Font("Default", Font.BOLD, 14));

        if (property.equals(""))
            specTextArea.setText(tips);
        else
            specTextArea.setText(property);
        specTextArea.setFont(new Font("标楷体", Font.TRUETYPE_FONT, inputFontSize));
        specLinePanel.add(addButton);
        specLinePanel.add(delButton);
        specLinePanel.add(specTextArea);
        specLinePanel.add(verifyButton);
        specLinePanel.add(resultLabel);
        specLinePanel.setPreferredSize(new Dimension(150, specInputLineHeight));  // 设置容器的大小

        specsPanel.add(specLinePanel); // add a new spec line at the end of specsPanel
        specsPanel.revalidate();

        specPanelsVector.add(specLinePanel);
        specTextAreasVector.add(specTextArea);
        // move all spec lines with index insertPos or greater to the next position
        for(int i=specTextAreasVector.size()-2; i>=insertPos; i--){
            String s=specTextAreasVector.get(i).getText();
            specTextAreasVector.get(i+1).setText(s);
        }
        specTextAreasVector.get(insertPos).setText("");

        specTextArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tips.equalsIgnoreCase(specTextArea.getText())) {
                    specTextArea.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(specTextArea.getText())) {
                    specTextArea.setText(tips);
                }
            }
        });
        addButton.addActionListener(this);
        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == delButton) {
                    specsPanel.remove(specLinePanel);
                    int r = specPanelsVector.indexOf(specLinePanel);
                    specPanelsVector.removeElementAt(r);
                    specTextAreasVector.removeElementAt(r);
                    specsPanel.revalidate();
                }
            }
        });

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == verifyButton) {
                    String specific = specTextArea.getText();
                    if (tips.equalsIgnoreCase(specific) || specific.equals("") || specific.trim().startsWith("--"))
                        addDocument(outputTextPane, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
                    else if (specific.endsWith(";"))
                        runModelChecking("RTCDL*SPEC ".concat(specific), false);
                    else
                        runModelChecking("RTCDL*SPEC ".concat(specific) + ";", false);
                }
            }
        });
        return specTextAreasVector.get(insertPos);
    }

    protected String GetAllSpec() {
        // TODO Auto-generated method stub

        String atlstr = "";
        Iterator<JTextArea> itatll = specTextAreasVector.iterator();
        while (itatll.hasNext()) {
            String s = itatll.next().getText();
            if (!"".equals(s) && !s.equals("Please input a ATL*SPEC...") && !s.trim().startsWith("--"))
                if (!s.endsWith(";"))
                    atlstr += "\nRTCDL*SPEC " + s + ";";
                else
                    atlstr += "\nRTCDL*SPEC " + s;
        }
        return atlstr;
    }

    public boolean ExtractSpec() {
/*
        String[][] all_specs = Env.getAllSpecsString();
        if (all_specs == null || all_specs.length == 0) {
            addDocument(outputTextPane, "\n =========No Specs loaded=========", outputFontSize, Color.GREEN, 1);
            return false;
        } else
            addDocument(outputTextPane, "\n =====Automatic Loading Specs=======", outputFontSize, Color.GREEN, 1);
        for (int i = 0; i < all_specs.length; i++) {
            if (all_specs[i].startsWith("RTCDL*SPEC")) {
                if (atltips.equals(specTextArea.getText()))
                    specTextArea.setText(all_specs[i].replaceAll("RTCTL\\*SPEC", "").toString());
                else
                    insertSpecLine(all_specs[i].replaceAll("RTCTL\\*SPEC", "").toString(), i);
            }
        }
*/
        return true;
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





    public void runModelChecking(String parse, Boolean isgraph) {
/*        //System.out.println("GRun----------"+parse);
        Spec[] all_specs = Env.loadSpecString(parse);
        if (all_specs == null||all_specs[0]==null) {
            addDocument(outputTextPane, "\n Sorry, please input correct specifications.", outputFontSize, Color.RED, 1);
            return;
        }
        String[] SpecStr = parse.split(";");
        addDocument(outputTextPane, "\n ======DONE Loading Specs=========", outputFontSize, Color.ORANGE, 1);
        AlgRunnerThread runner;
        for (int i = 0; i < all_specs.length; i++) {
            getStat.startBDDVar();
            getStat.startTimeMemory();
            addDocument(outputTextPane, "\nModel checking " + SpecStr[i], outputFontSize, Color.BLACK, 1);
            if (all_specs[i].getLanguage() == InternalSpecLanguage.CTL) {
                RTCTLKModelCheckAlg algorithm = new RTCTLKModelCheckAlg(smvModule, all_specs[i]);
                if (isgraph) {//return result & graph
                    algorithm.SetShowGraph(true);
                    runner = new AlgRunnerThread(algorithm);
                    runner.runSequential();
                    if (runner.getDoResult().getResultStat() == failed) {//结果false，否则无图形反例
                        this.graph = algorithm.GetGraph();
                        SetGraphThread x = new SetGraphThread(SpecStr[i], this.graph, this.indexJFrame);
                        Thread y = new Thread(x);
                        y.start();
                    }
                } else {//return result
                    runner = new AlgRunnerThread(algorithm);
                    runner.runSequential();
                }
                if (runner.getDoResult() != null)
                    addDocument(outputTextPane, "\n" + runner.getDoResult().resultString() +
                            "\n" + getStat.endTime() + getStat.endBDD() +getStat.endVar()+ getStat.endMemory(), outputFontSize, Color.BLACK, 1);
                else if (runner.getDoException() != null)
                    addDocument(outputTextPane, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);

            } else if (all_specs[i].getLanguage() == InternalSpecLanguage.RTCTLs || all_specs[i].getLanguage() == InternalSpecLanguage.LTL) {
                RTCTL_STAR_ModelCheckAlg algorithm = new RTCTL_STAR_ModelCheckAlg(smvModule, all_specs[i]);
                if (isgraph) {//带图的反例
                    runner = new AlgRunnerThread(algorithm);
                    runner.runSequential();
                    if (runner.getDoResult().getResultStat() == failed) {//结果false，否则无图形反例
                        this.graph = algorithm.getGraph();
                        SetGraphThread x = new SetGraphThread(SpecStr[i], this.graph, this.indexJFrame);
                        Thread y = new Thread(x);
                        y.start();
                    }
                } else {
                    runner = new AlgRunnerThread(algorithm);
                    runner.runSequential();
                }


                if (runner.getDoResult() != null)
                    addDocument(outputTextPane, "\n" + runner.getDoResult().resultString() +
                            "\n" + getStat.endTime() + getStat.endBDD() +getStat.endVar()+ getStat.endMemory(), outputFontSize, Color.BLACK, 1);
                else if (runner.getDoException() != null)
                    addDocument(outputTextPane, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);
            }
        }*/
    }
}

