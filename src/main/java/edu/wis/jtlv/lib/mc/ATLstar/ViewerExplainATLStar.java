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
import java.text.DecimalFormat;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static swing.VerifyActionListener.outputFontSize;

/**
 * <p>
 *    ViewerListener:<br/>
 *    ViewerPipe可以注册类型ViewerListener的监听器, 并在更改某些事件属性时通过调用监听器的方法来发送事件.
 * </p>
 */
public class ViewerExplainATLStar implements ViewerListener, ActionListener, MouseMotionListener, MouseWheelListener {
    protected boolean loop = true;

    private GraphExplainATLStar graph;
    JFrame ceJFrame; //counterexample JFrame
    Viewer viewer; //用于接收graph图形事件的viewer
    ViewPanel graphJPanel;
    JToggleButton layoutTogBtn; //自动布局切换按钮
    JTextField viewPercentText;
    JLabel viewPercentLabel;
    JLabel negSpecLabel;
    JSplitPane centerJSplitPane, infoJSplitPane;

    public static JTextPane testerTextPane, outputTextPane;

    private Toast toast;

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


        graphJPanel = viewer.addDefaultView(false); //false表示"非JFrame"
        graphJPanel.addMouseWheelListener(this);

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
        //创建窗口实例, 同时设置标题
        ceJFrame = new JFrame(this.graph.getId());
        //设置窗体自动调节大小
        //ceJFrame.pack();

        //设置窗口高宽
        ceJFrame.setSize(1280, 800);

        //设置界面居中
        ceJFrame.setLocationRelativeTo(null);

        //设置窗体布局为边界布局(JFrame和JDialog默认布局为BorderLayout, JPanel和Applet默认布局为FlowLayout)
        ceJFrame.setLayout(new BorderLayout()); //默认为水平间距0, 垂直间距0

        //设置窗体标题
        //ceFrame.setTitle("ViewerExplainATLStar");

        //设置窗体字体, 测试失败
        //ceFrame.setFont(new Font("宋体", Font.PLAIN, 18));

        //设置窗体图标
        Image logoIcon = new ImageIcon(this.getClass().getResource("/Icons/logo.png")).getImage();
        ceJFrame.setIconImage(logoIcon);

        //设置窗体默认关闭方式, 点击窗口关闭按钮, 执行销毁窗口操作(如果设置为 EXIT_ON_CLOSE, 则点击新窗口关闭按钮后, 整个进程将结束)
        ceJFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        UIManager.put("Label.font", new Font("Default", Font.PLAIN, 18));
        UIManager.put("ToggleButton.font", new Font("Default", Font.PLAIN, 18));
        UIManager.put("TitledBorder.font", new Font("Default", Font.PLAIN, 18));

        //用于测试Ctrl+滚轮
        //ceJFrame.addMouseWheelListener(this);

        //********************北方向的控制面板********************/
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

        //创建上方控制面板
        JPanel ctrlJPanel = new JPanel();
        //设置控制面板布局为流式布局, 默认组件是居中对齐
        ctrlJPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); //指定对齐方式为左对齐

        //创建一个标签并制定文本内容, 默认左对齐
        viewPercentLabel=new JLabel("View Percent:");
        //JTextField文本框, 用来编辑单行的文本
        //参数说明-text: 默认显示的文本; columns: 用来计算首选宽度的列数, 如果列设置为0, 则首选宽度将是组件实现的自然结果.
        viewPercentText = new JTextField("1",4);
        //为百分比文本框绑定监听
        viewPercentText.addActionListener(this);
        //创建一个标签用于展示规约的否定范式
        negSpecLabel = new JLabel("The following is a witness of " + SpecUtil.simplifySpecString(this.graph.negSpec,false));
        //注册要在工具提示中显示的文本, 当光标停留在组件上时显示文本.
        negSpecLabel.setToolTipText(SpecUtil.simplifySpecString(this.graph.negSpec,false));

        //面板添加组件, 流式布局, 左对齐
        ctrlJPanel.add(layoutTogBtn);
        ctrlJPanel.add(viewPercentLabel);
        ctrlJPanel.add(viewPercentText);
        ctrlJPanel.add(negSpecLabel);
        //********************************************************/

        //*************************中间面板*************************/
        testerTextPane = new JTextPane();
        //创建滚动面板, 设置滚动显示的视图内容组件为testerTextPane
        JScrollPane testerScrollPane = new JScrollPane(testerTextPane);
        testerScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Tester Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)
                        ),
                        BorderFactory.createEmptyBorder(0,0,0,0)
                )
        );
        //此处待完善输出testers信息代码
        for(Map.Entry<Spec, SMVModule> entry : this.graph.specTesterMap.entrySet()) {
            if(entry.getValue() != null) {
                this.consoleOutput(testerTextPane, "emph", entry.getValue().getName());
                this.consoleOutput(testerTextPane, "weak", " => ");
                this.consoleOutput(testerTextPane, "emph",
                        SpecUtil.simplifySpecString(entry.getKey(), false) + "\n");
            }
        }

        outputTextPane = new JTextPane();
        //创建滚动面板, 设置滚动显示的视图内容组件为outputTextPane
        JScrollPane outputScrollPane = new JScrollPane(outputTextPane);
        outputScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Counterexample Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)
                        ),
                        BorderFactory.createEmptyBorder(0,0,0,0)
                )
        );


        //创建分割面板, JSplitPane.VERTICAL_SPLIT垂直分割: 使两个组件从上到下排列
        //将"Tester Information"和"Counterexample Information"分隔开来
        infoJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,testerScrollPane,outputScrollPane);
        //设置分隔条的位置
        infoJSplitPane.setDividerLocation(150);
        infoJSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        //创建分割面板, JSplitPane.VERTICAL_SPLIT水平分割: 使两个组件从左到右排列
        //将information面板与graph分隔开来
        centerJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphJPanel, infoJSplitPane);
        centerJSplitPane.setDividerLocation(ceJFrame.getWidth()-350);
        centerJSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        //**********************************************************/

        //添加面板
        //ceJFrame.add("North", ctrlJPanel);
        //ceJFrame.add("Center", splitPane_graph_rightPane);
        ceJFrame.add(ctrlJPanel, BorderLayout.NORTH);
        ceJFrame.add(centerJSplitPane, BorderLayout.CENTER);

        //设置窗体可见, 建议写在最后
        ceJFrame.setVisible(true);

        toast  = new Toast(ceJFrame, "Toast是很好用的", 5000, Toast.success);
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
            graphJPanel
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
    public void mouseWheelMoved(MouseWheelEvent e) {
        double percent = parseDouble(viewPercentText.getText());
        //如果鼠标滚轮向上/远离用户旋转, 则为负值; 如果鼠标滚轮朝向用户向下旋转, 则为正值
        if(e.getWheelRotation() == 1 && e.isControlDown()) {
            //LoggerUtil.info("缩小{}", e.getWheelRotation());
            percent += 0.02;
            graphJPanel
                    .getCamera()
                    .setViewPercent(percent);
        } else if(e.getWheelRotation() == -1 && e.isControlDown()) {
            //LoggerUtil.info("放大{}", e.getWheelRotation());
            percent -= 0.02;
            graphJPanel
                    .getCamera()
                    .setViewPercent(percent);
        } else {
            //Toast使用
            toast.setMessage("Ctrl键+鼠标滚轮缩放");
        }
        //viewPercentText.setText(String.valueOf(percent)); //需要保留两位小数
        DecimalFormat format = new DecimalFormat("#0.00");
        viewPercentText.setText(format.format(percent));
    }
}
