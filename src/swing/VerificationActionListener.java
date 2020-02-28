package swing;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.AlgRunnerThread;
import edu.wis.jtlv.lib.mc.RTCTLK.RTCTLKModelCheckAlg;
import edu.wis.jtlv.lib.mc.RTCTL_STAR.RTCTL_STAR_ModelCheckAlg;
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
import java.util.Iterator;
import java.util.Vector;

import static edu.wis.jtlv.lib.AlgResultI.ResultStatus.failed;
import static swing.mainJFrame.controlPanel;
import static swing.mainJFrame.editorPanel;


public class VerificationActionListener implements ActionListener {

    public static int buttonFontSize = 12;
    public static int inputFontSize = 12;
    public static int outputFontSize = 12;

    public static int specInputLineHeight =40;

    mainJFrame indexJFrame;
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
    JCheckBox selectCheckBox;

    public JTextPane outputText;// Console of verificatin
    JScrollPane buttonsSpecsScrollPane, outputScrollPane;
    public static JSplitPane mainSplitPane;
    Icon plusIcon = new ImageIcon(CtrlJPanel.class.getResource("/swing/Icons/plusm.gif"));
    Icon minIcon = new ImageIcon(CtrlJPanel.class.getResource("/swing/Icons/minusm.gif"));
    Icon witIcon = new ImageIcon(CtrlJPanel.class.getResource("/swing/Icons/witm.gif"));
    Icon verIcon = new ImageIcon(CtrlJPanel.class.getResource("/swing/Icons/verm.gif"));
    final String atltips = "Please input a RTCTL*SPEC...";
    SMVModule main;//read the smv model only once.
    private Statistic getStat; //get the time consuming, memory, etc.

    public VerificationActionListener(mainJFrame indexJFrame) {
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
        readButton = createBtn("Read Specs", "/swing/Icons/extract.png", 120, specInputLineHeight);
        VerifyButton = createBtn("Verify All", "/swing/Icons/verifyAll.png", 120, specInputLineHeight);
        ClearButton = createBtn("Delete All", "/swing/Icons/deleteAll.png", 120, specInputLineHeight);
        SaveButton = createBtn("Save Specs", "/swing/Icons/saveAll.png", 120, specInputLineHeight);

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

        outputText = new JTextPane();
        outputScrollPane = new JScrollPane(outputText);
        insertDocument(outputText, "Verification information...", outputFontSize, Color.BLUE, 1);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        menuItemCopy = new JMenuItem("Copy(C)");
        menuItemClear = new JMenuItem("Clear");
        jPopMenu = new JPopupMenu();
        jPopMenu.add(menuItemCopy);
        jPopMenu.add(menuItemClear);
        menuItemCopy.addActionListener(this);
        menuItemClear.addActionListener(this);
        outputText.add(jPopMenu);
        outputText.setBorder(null);
        outputText.addMouseListener(new MyMouseListener());

        witnessPanel = new JPanel(new BorderLayout());//初始化
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(buttonsSpecsScrollPane);
        mainSplitPane.setRightComponent(outputScrollPane);
        mainSplitPane.setDividerSize(5);//设置分隔条大小，以像素为单位
        mainSplitPane.setDividerLocation(950);

        buttonsSpecsPanel.setOpaque(true);
        outputText.setOpaque(true);
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
        JButton btn = new JButton(text, new ImageIcon(mainJFrame.class.getResource(icon)));
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
            String temp = outputText.getSelectedText();
            StringSelection content = new StringSelection(temp);
            clipboard.setContents(content, null);
        } else if (e.getSource() == menuItemClear) {
            outputText.setText("");
            insertDocument(outputText, "Verification information...", outputFontSize, Color.BLUE, 1);
        } else if (e.getSource() == addButton || e.getActionCommand().equals("atlADD")) {
            JTextArea specTextArea=insertSpecLine("",getClickedSourceIndex(e));
            specTextArea.requestFocus(true);
        } else if (e.getSource() == delButton) {
            specTextArea.setText("");
        } else if (e.getSource() == verifyButton) {
            String specific = specTextArea.getText();
            if (atltips.equalsIgnoreCase(specific) || specific.equals("") || specific.trim().startsWith("--"))
                insertDocument(outputText, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
            else if (specific.endsWith(";"))
                GRun("RTCTL*SPEC ".concat(specific), false);
            else
                GRun("RTCTL*SPEC ".concat(specific) + ";", false);
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
                insertDocument(outputText, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
            else
                GRun(parse, false);
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
            editorPanel.textModel.setText(editorPanel.textModel.getText() + parse);
            controlPanel.fileOperation.save();
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
                jPopMenu.show(outputText, e.getX(), e.getY());
            }

        }
    }

    // In the spec list, insert a new spec line before the index
    protected JTextArea insertSpecLine(String property, int insertPos) {
        JPanel specLinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("+"); //plusIcon);
        addButton.setActionCommand("atlADD");
        JButton delButton = new JButton("-"); //minIcon);

        String tips = "Please input a RTCTL*SPEC...";
        JTextArea specTextArea = new JTextArea();// 文本窗格
        specTextArea.setPreferredSize(new Dimension(650, specInputLineHeight));
        specTextArea.setText("");
        specTextArea.setLineWrap(true);
        specTextArea.setFont(new Font("Default", Font.PLAIN, inputFontSize));
        specTextArea.setAutoscrolls(true);
        //specTextArea.setRows(2);

        JButton verifyButton = createBtn("Verify", "/swing/Icons/verifyAll.png", 100, specInputLineHeight); //new JButton(verIcon);
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
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == verifyButton) {
                    String specific = specTextArea.getText();
                    if (tips.equalsIgnoreCase(specific) || specific.equals("") || specific.trim().startsWith("--"))
                        insertDocument(outputText, "\n Sorry,please input a specification !", outputFontSize, Color.red, 2);
                    else if (specific.endsWith(";"))
                        GRun("RTCTL*SPEC ".concat(specific), false);
                    else
                        GRun("RTCTL*SPEC ".concat(specific) + ";", false);
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
                    atlstr += "\nRTCTL*SPEC " + s + ";";
                else
                    atlstr += "\nRTCTL*SPEC " + s;
        }
        return atlstr;
    }

    public void ReadSMVSpec() {
        String name = controlPanel.fileOperation.getFileName();
        if (name.equals("")) {
            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(null, "Sorry,please input the model file first!",
                    "Warm Tips", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        } else {
            String src = controlPanel.fileOperation.getPath();
            String url = src + name + ".smv";
            controlPanel.fileOperation.save();    //Save the file you are editing first.
            Env.resetEnv();
            try {

                Env.loadModule(url);
                main = (SMVModule) Env.getModule("main");
                main.setFullPrintingMode(true);
                insertDocument(outputText, "\n =======Done Loading Modules========", outputFontSize, Color.GREEN, 1);

            } catch (Exception ie) {
                ie.printStackTrace();

                Object[] options = {"OK"};
                JOptionPane.showOptionDialog(null, "Syntax error,please check the model file first!",
                        "Warm Tips", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            }finally {
                getStat=new Statistic();
                insertDocument(outputText, "\nInitial occupation of building model...\nNumber of BDD nodes:" + getStat.modelBDD +
                        "\nNumber of BDD variables:"+getStat.modelVar, outputFontSize, Color.GRAY, 1);
            }

        }
    }

    public boolean ExtractSpec() {
        String[] all_specs = Env.getAllSpecsString();
        if (all_specs == null || all_specs.length == 0) {
            insertDocument(outputText, "\n =========No Specs loaded=========", outputFontSize, Color.GREEN, 1);
            return false;
        } else
            insertDocument(outputText, "\n =====Automatic Loading Specs=======", outputFontSize, Color.GREEN, 1);
        for (int i = 0; i < all_specs.length; i++) {
            if (all_specs[i].startsWith("RTCTL*SPEC")) {
                if (atltips.equals(specTextArea.getText()))
                    specTextArea.setText(all_specs[i].replaceAll("RTCTL\\*SPEC", "").toString());
                else
                    insertSpecLine(all_specs[i].replaceAll("RTCTL\\*SPEC", "").toString(), i);
            }
        }
        return true;
    }


    public static void insertDocument(JTextPane JTP, String str, int textSize, Color textColor, int setFont)// 根据传入的颜色及文字，将文字插入控制台
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
        Document doc = JTP.getDocument();
        try {
            doc.insertString(doc.getLength(), str, set);// 插入文字
        } catch (BadLocationException e) {
        }
    }

    public void GRun(String parse, Boolean isgraph) {
        System.out.println("GRun----------"+parse);
        Spec[] all_specs = Env.loadSpecString(parse);
        if (all_specs == null||all_specs[0]==null) {
            insertDocument(outputText, "\n Sorry,please input correct specifications...", outputFontSize, Color.RED, 1);
            return;
        }
        String[] SpecStr = parse.split(";");
        insertDocument(outputText, "\n ======DONE Loading Specs=========", outputFontSize, Color.ORANGE, 1);
        AlgRunnerThread runner;
        for (int i = 0; i < all_specs.length; i++) {
            getStat.startBDDVar();
            getStat.startTimeMemory();
            insertDocument(outputText, "\n model checking " + SpecStr[i], outputFontSize, Color.BLACK, 1);
            if (all_specs[i].getLanguage() == InternalSpecLanguage.CTL) {
                RTCTLKModelCheckAlg algorithm = new RTCTLKModelCheckAlg(main, all_specs[i]);
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
                    insertDocument(outputText, "\n" + runner.getDoResult().resultString() +
                            "\n" + getStat.endTime() + getStat.endBDD() +getStat.endVar()+ getStat.endMemory(), outputFontSize, Color.BLACK, 1);
                else if (runner.getDoException() != null)
                    insertDocument(outputText, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);

            } else if (all_specs[i].getLanguage() == InternalSpecLanguage.RTCTLs || all_specs[i].getLanguage() == InternalSpecLanguage.LTL) {
                RTCTL_STAR_ModelCheckAlg algorithm = new RTCTL_STAR_ModelCheckAlg(main, all_specs[i]);
                if (isgraph) {//带图的反例
                    algorithm.setShowGraph(true);
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
                    insertDocument(outputText, "\n" + runner.getDoResult().resultString() +
                            "\n" + getStat.endTime() + getStat.endBDD() +getStat.endVar()+ getStat.endMemory(), outputFontSize, Color.BLACK, 1);
                else if (runner.getDoException() != null)
                    insertDocument(outputText, "\n" + runner.getDoException().getMessage(), outputFontSize, Color.RED, 1);
            }
        }
    }
}

