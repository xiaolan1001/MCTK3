package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecBDD;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static swing.VerifyActionListener.outputFontSize;

/**
 * <p>
 *    ViewerListener:<br/>
 *    ViewerPipe可以注册类型ViewerListener的监听器, 并在更改某些事件属性时通过调用监听器的方法来发送事件.
 * </p>
 */
public class ViewerExplainATLStar implements ViewerListener, ActionListener, MouseMotionListener, ChangeListener {
    protected boolean loop = true;

    private GraphExplainATLStar graph;
    JFrame ceFrame;
    Viewer viewer; //用于接收graph图形事件的viewer
    ViewPanel graphPanel;
    JToggleButton layoutTogBtn; //自动布局切换按钮
    JTextField viewPercentText;
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
        //使用指定模块可视化图形
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        this.graph = graph;

        this.graph.addAttribute("ui.label", this.graph.getId());

        //GraphStream 层叠样式表(CSS): 用于描述由viewer呈现/渲染的结构.
        //样式规则是由选择器(selector)和一组样式属性(style properties)组成的,
        //四个主要的选择器(selector):{graph, node, edge, sprite};
        //graph { fill-color: red; }, 改变graph的背景颜色
        this.graph.addAttribute("ui.stylesheet",
                "node { stroke-mode: plain; shape: circle; size: 55px; fill-color: green; z-index: 10; text-size: 16; text-style: bold; }" +
                        "node.initialState {fill-color: pink;} " +
                        "node.epistemicState {fill-color: gold;} " +
                        "edge { size: 3px; shape: line; fill-color: blue; arrow-size: 5px, 6px; arrow-shape: arrow; text-size: 14;" +
                        "text-background-mode: rounded-box; text-background-color: #fff7bc; text-alignment: at-left; text-padding: 2;} " +
                        "edge.epistemicEdge { fill-color: gold; shape: cubic-curve;} " +
                        "sprite {size: 0px;text-size: 14; text-alignment: at-right; }"
        );

        //为了将viewer整合进自己的Swing GUI中, 需要自行创建viewer.
        //创建一个新的viewer, 将其链接为来自graph的图形事件的接收器(a sink);
        viewer = new Viewer(this.graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        //viewer因此会接收到每一个发生在graph上的改变.
        //注意: "Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD"常量, viewer活动在另一个线程中.

        //激活自动布局, 自动布局使用了力驱动算法(a force driven algorithm)
        //如果禁用了自动布局, 则需要指定节点位置, 节点只有在定位后才会显现
        viewer.enableAutoLayout();


        graphPanel = viewer.addDefaultView(false); //false表示"非JFrame"

        initialize();

        //关闭view时的默认动作是退出程序
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        //将viewer连接回graph, graph成为viewer的接收器, 并且为viewer安装监听器以拦截图形事件.
        //仿真(simulations)
        //返回ViewerPipe管道对象, 该对象作为viewer的事件源
        ViewerPipe viewerPipe = viewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(this.graph);

        //然后我们需要一个循环来完成我们的工作并等待事件.
        //在这个循环中, 我们需要在每次使用graph之前调用pump()方法来复制回(copy back)线程内viewer线程中已经发生的事件.

        while(loop) {
            //需要定期要求管道查看viewer线程是否发送了一些事件
            viewerPipe.pump();

            //这里是仿真代码
        }
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

    private void initialize() throws SpecException {
        //创建窗口实例
        ceFrame = new JFrame(this.graph.getId());
        //设置窗体自动调节大小
        ceFrame.pack();

        //设置窗口高宽
        ceFrame.setSize(1280, 800);

        //设置窗体布局
        ceFrame.setLayout(new BorderLayout());

        //设置窗体标题
        //ceFrame.setTitle("ViewerExplainATLStar");

        //设置窗体字体, 测试失败
        //ceFrame.setFont(new Font("宋体", Font.PLAIN, 18));

        //设置窗体图标
        Image logoIcon = new ImageIcon(this.getClass().getResource("/Icons/logo.png")).getImage();
        ceFrame.setIconImage(logoIcon);

        //设置窗体默认关闭方式, 点击窗口关闭按钮, 执行销毁窗口操作(如果设置为 EXIT_ON_CLOSE, 则点击新窗口关闭按钮后, 整个进程将结束)
        ceFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //设置开关按钮的文本
        layoutTogBtn=new JToggleButton("Auto Layout");
        //设置开关按钮为选中状态
        layoutTogBtn.setSelected(true);
        //添加toggleBtn的状态监听事件
        layoutTogBtn.addChangeListener(e -> {
                if(layoutTogBtn.isSelected()){
                    //激活自动布局
                    viewer.enableAutoLayout();
                }else {
                    //禁用自动布局
                    viewer.disableAutoLayout();
                }
        });

        //创建面板
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        viewPercentLabel=new JLabel("View Percent:");
        viewPercentText = new JTextField("1",4);
        viewPercentText.addActionListener(this);
        negSpecLabel = new JLabel("The following is a witness of " + SpecUtil.simplifySpecString(this.graph.negSpec,false));
        negSpecLabel.setToolTipText(SpecUtil.simplifySpecString(this.graph.negSpec,false));

        //添加面板
        controlPanel.add(layoutTogBtn);
        controlPanel.add(viewPercentLabel);
        controlPanel.add(viewPercentText);
        controlPanel.add(negSpecLabel);

        testerTextPane = new JTextPane();
        JScrollPane testerScrollPane = new JScrollPane(testerTextPane);
        testerScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Tester Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)),
                        BorderFactory.createEmptyBorder(0,0,0,0)));
        //此处待补充输出testers信息代码
        for(Map.Entry<Spec, SMVModule> entry : this.graph.specTesterMap.entrySet()) {
            if(entry.getValue() != null) {
                this.consoleOutput(testerTextPane, "emph", entry.getValue().getName());
                this.consoleOutput(testerTextPane, "weak", " => ");
                this.consoleOutput(testerTextPane, "emph",
                        SpecUtil.simplifySpecString(entry.getKey(), false) + "\n");
            }
        }

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

        //添加面板
        ceFrame.add("North", controlPanel);
        ceFrame.add("Center", splitPane_graph_rightPane);

        //设置窗体可见
        ceFrame.setVisible(true);
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
                    this.consoleOutput(outputTextPane,"darkGray","========State "+id+"========\n"+s+"\n");

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
        //View接口定义了一个"camera"对象, 该对象具有多种方法, 使您可以导航graph渲染(相机对象是同步的, 并且允许从不同的线程命令视图).
        //默认情况下, view是在适应graph大小以始终显示整个graph的模式下, 因此view的中心位于graph的中心。
        if(e.getSource() == viewPercentText) {
            //放大缩小
            graphPanel
                    .getCamera()
                    .setViewPercent(parseDouble(viewPercentText.getText()));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}
