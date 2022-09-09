package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;

import static edu.wis.jtlv.lib.mc.RTCTLs.RTCTLs_ModelCheckAlg.simplifySpecString;
import static java.lang.Double.parseDouble;
import static swing.MCTKFrame.consoleOutput;
import static swing.VerifyActionListener.outputFontSize;

public class ViewerExplainATLStar implements ViewerListener, ActionListener, MouseMotionListener {
    protected boolean loop = true;

    private GraphExplainATLStar graph;
    JFrame ceFrame;
    Viewer viewer;
    ViewPanel graphPanel;
    JToggleButton layoutToggleButton;
    JTextField viewPercentTextField;
    JLabel viewPercentLabel;
    JLabel negSpecLabel;
    JSplitPane splitPane_graph_rightPane, splitPane_testers_output;

    public static JTextPane testerTextPane, outputTextPane;

    //Getter和Setter方法
    public GraphExplainATLStar getGraph() {
        return graph;
    }

    public void setGraph(GraphExplainATLStar graph) {
        this.graph = graph;
    }

    public ViewerExplainATLStar(GraphExplainATLStar graph) throws SpecException {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        this.graph = graph;

        this.graph.addAttribute("ui.label", this.graph.getId());

        this.graph.addAttribute("ui.stylesheet",
                "node { stroke-mode: plain; shape: circle; size: 55px; fill-color: green; z-index: 10; text-size: 16; text-style: bold; }" +
                        "node.initialState {fill-color: pink;} " +
                        "node.epistemicState {fill-color: gold;} " +
                        "edge { size: 3px; shape: line; fill-color: blue; arrow-size: 5px, 6px; arrow-shape: arrow; text-size: 14;" +
                        "text-background-mode: rounded-box; text-background-color: #fff7bc; text-alignment: at-left; text-padding: 2;} " +
                        "edge.epistemicEdge { fill-color: gold; shape: cubic-curve;} " +
                        "sprite {size: 0px;text-size: 14; text-alignment: at-right; }"
        );

        ceFrame = new JFrame(this.graph.getId());
        ceFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ceFrame.dispose();
            }
        });

        ceFrame.setSize(1280, 800);
        Image logoIcon = new ImageIcon(this.getClass().getResource("/Icons/logo.png")).getImage();

        ceFrame.setIconImage(logoIcon);
        // 点击窗口关闭按钮, 执行销毁窗口操作(如果设置为 EXIT_ON_CLOSE, 则点击新窗口关闭按钮后, 整个进程将结束)
        ceFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        viewer = new Viewer(this.graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        layoutToggleButton=new JToggleButton("Auto Layout");
        layoutToggleButton.setSelected(true);
        layoutToggleButton.addChangeListener(e -> {
            if(layoutToggleButton.isSelected()){
                viewer.enableAutoLayout();
            }else viewer.disableAutoLayout();
        });

        viewPercentLabel=new JLabel("View Percent:");
        viewPercentTextField = new JTextField("1",4);
        viewPercentTextField.addActionListener(this);

        negSpecLabel = new JLabel("The following is a witness of " + SpecUtil.simplifySpecString(this.graph.negSpec,false));
        negSpecLabel.setToolTipText(SpecUtil.simplifySpecString(this.graph.negSpec,false));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(layoutToggleButton);
        controlPanel.add(viewPercentLabel);
        controlPanel.add(viewPercentTextField);
        controlPanel.add(negSpecLabel);

        graphPanel = viewer.addDefaultView(false);

        testerTextPane = new JTextPane();

        JScrollPane testerScrollPane = new JScrollPane(testerTextPane);
        testerScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Tester Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)),
                        BorderFactory.createEmptyBorder(0,0,0,0)));
        //此处待补充输出testers信息代码

        outputTextPane = new JTextPane();
        JScrollPane outputScrollPane = new JScrollPane(outputTextPane);
        outputScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Counterexample Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)),
                        BorderFactory.createEmptyBorder(0,0,0,0)));


        splitPane_testers_output = new JSplitPane(JSplitPane.VERTICAL_SPLIT,testerScrollPane,outputScrollPane);
        splitPane_testers_output.setDividerLocation(150);
        splitPane_testers_output.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        splitPane_graph_rightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, splitPane_testers_output);
        splitPane_graph_rightPane.setDividerLocation(ceFrame.getWidth()-350);
        splitPane_graph_rightPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        ceFrame.setLayout(new BorderLayout());
        ceFrame.add("North", controlPanel);
        ceFrame.add("Center", splitPane_graph_rightPane);

        ceFrame.setVisible(true);


        //关闭窗口时应退出程序
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(this.graph);

        //然后我们需要一个循环来完成我们的工作并等待事件.
        //在这个循环中, 我们需要在每次使用graph之前调用pump()方法来复制回(copy back)线程内viewer线程中已经发生的事件.

        while(loop) {
            fromViewer.pump();
        }
    }

    @Override
    public void viewClosed(String s) {
        loop = false;
    }

    @Override
    public void buttonPushed(String id) {
        try {
            try {
                this.graph.getChecker().explainOneNode(id);

                String s=SpecUtil.simplifySpecString(this.graph.nodeGetInfo(id,true),false);
                if(s!=null)
                    consoleOutput(outputTextPane,"darkGray","========State "+id+"========\n"+s+"\n");

                // NOTE: it is easy to cause error when invoking two consoleOutput() one by one
                // Re: 确实如此

            } catch (ModelCheckException | ModuleException | SMVParseException | SpecException e) {
                e.printStackTrace();
            }
        } catch (ModelCheckAlgException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buttonReleased(String s) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == viewPercentTextField) {
            graphPanel
                    .getCamera()
                    .setViewPercent(parseDouble(viewPercentTextField.getText()));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void consoleOutput(JTextPane textPane, String type, String str) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        Color textColor=Color.BLACK;
        switch (type) {
            case "warning":
            case "magenta":
                textColor = Color.MAGENTA;
                break;
            case "error":
            case "red":
                textColor = Color.RED;
                break;
            case "emph":
            case "blue":
                textColor = Color.BLUE;
                break;
            case "weak":
            case "gray":
                textColor = Color.GRAY;
                break;
            case "green":
                textColor = Color.GREEN;
                break;
            case "darkGray":
                textColor = Color.darkGray;
                break;
        }

        int textSize=outputFontSize;
        StyleConstants.setForeground(attributeSet, textColor);// 设置文字颜色
        StyleConstants.setFontSize(attributeSet, textSize);// 设置字体大小
        Document doc = textPane.getDocument();
        try {
            doc.insertString(doc.getLength(), str, attributeSet);// 插入文字
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
