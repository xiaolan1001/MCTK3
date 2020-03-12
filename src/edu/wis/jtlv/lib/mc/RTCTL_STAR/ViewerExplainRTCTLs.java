package edu.wis.jtlv.lib.mc.RTCTL_STAR;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import swing.MCTK2Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Double.parseDouble;
import static swing.MCTK2Frame.consoleOutput;

public class ViewerExplainRTCTLs implements ViewerListener, ActionListener, MouseMotionListener {
    protected boolean loop = true;
    JFrame ceFrame;
    Viewer viewer;
    ViewPanel graphPanel;
    JButton layoutButton, viewPercentButton,viewCenterButton;
    JTextField viewPercentTextField, mouseXTextField, mouseYTextField;

    public GraphExplainRTCTLs getGraph() {
        return graph;
    }

    public void setGraph(GraphExplainRTCTLs graph) {
        this.graph = graph;
    }

    private GraphExplainRTCTLs graph;

    public ViewerExplainRTCTLs(GraphExplainRTCTLs G) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = G;
        graph.addAttribute("ui.label", graph.getId());
/*
        for (Node n: graph) {
            n.addAttribute("ui.label", n.getId());
        }
*/

//        for (Edge e: graph.getEachEdge()) {
//            e.addAttribute("ui.label", e.getId());
//        }


        graph.addAttribute("ui.stylesheet",
                "node { stroke-mode: plain; shape: circle; size: 55px; fill-color: green; z-index: 10; text-size: 16; text-style: bold; }" +
                "node.initialState {fill-color: pink;} " +
                "node.epistemicState {fill-color: gold;} " +
                "edge { size: 3px; shape: line; fill-color: blue; arrow-size: 5px, 6px; arrow-shape: arrow; text-size: 14;" +
                        "text-background-mode: rounded-box; text-background-color: #fff7bc; text-alignment: at-left; text-padding: 2;} " +
                "edge.epistemicEdge { fill-color: gold; shape: cubic-curve;} " +
                        "sprite {size: 0px;text-size: 14; text-alignment: at-right; }"
        );

        //Viewer viewer = graph.display(true);

        ceFrame=new JFrame(graph.getId());
        ceFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                MCTK2Frame.isOpeningCounterexampleWindow=false;
                ceFrame.dispose();
            }
        });

        ceFrame.setSize(800, 600);
        // 把新窗口的位置设置到 relativeWindow 窗口的中心
        //ceFrame.setLocationRelativeTo(this.indexJFrame);
        Image logoIcon = new ImageIcon(MCTK2Frame.class.getResource("/swing/Icons/logo.png")).getImage();
        ceFrame.setIconImage(logoIcon);
        // 点击窗口关闭按钮, 执行销毁窗口操作（如果设置为 EXIT_ON_CLOSE, 则点击新窗口关闭按钮后, 整个进程将结束）
        ceFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // 窗口设置为不可改变大小 newJFrame.setResizable(false);

        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        layoutButton =new JButton("Auto Layout is working");
        layoutButton.addActionListener(this);

        viewPercentButton=new JButton("View Percent:");
        viewPercentButton.addActionListener(this);
        viewPercentTextField = new JTextField("1",4);

        viewCenterButton = new JButton("View Center:");
        viewCenterButton.addActionListener(this);
        mouseXTextField = new JTextField("0",4);
        mouseYTextField = new JTextField("0",4);

        JPanel controlPanel = new JPanel();
        controlPanel.add(layoutButton);
        controlPanel.add(viewPercentButton);
        controlPanel.add(viewPercentTextField);
        controlPanel.add(viewCenterButton);
        controlPanel.add(mouseXTextField);
        controlPanel.add(mouseYTextField);

        graphPanel = (ViewPanel) viewer.addDefaultView(false);

        ceFrame.setLayout(new BorderLayout());
        ceFrame.add("North", controlPanel);
        ceFrame.add("Center",graphPanel);

        //ceFrame.setContentPane(viewPanel);
        ceFrame.setVisible(true);


        // The default action when closing the view is to quit
        // the program.
        //viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        // Then we need a loop to do our work and to wait for events.
        // In this loop we will need to call the
        // pump() method before each use of the graph to copy back events
        // that have already occurred in the viewer thread inside
        // our thread.

        while(loop) {
            fromViewer.pump(); // or fromViewer.blockingPump(); in the nightly builds

            // here your simulation code.

            // You do not necessarily need to use a loop, this is only an example.
            // as long as you call pump() before using the graph. pump() is non
            // blocking.  If you only use the loop to look at event, use blockingPump()
            // to avoid 100% CPU usage. The blockingPump() method is only available from
            // the nightly builds.
        }
    }

    public void viewClosed(String id) {
        MCTK2Frame.isOpeningCounterexampleWindow=false;
        loop = false;
    }

    public void buttonPushed(String id) {
        Node n = graph.getNode(id);

        try {
            try {
                graph.getChecker().explainOneNode(id);

                String s=graph.nodeGetInfo(id,true);
                if(s!=null)
                    consoleOutput("darkGray","==========State "+id+"==========\n"+s+"\n");

                // NOTE: it is easy to cause error when invoking two consoleOutput() one by one

            } catch (ModelCheckException e) {
                e.printStackTrace();
            } catch (SpecException e) {
                e.printStackTrace();
            } catch (SMVParseException e) {
                e.printStackTrace();
            } catch (ModuleException e) {
                e.printStackTrace();
            }
        } catch (ModelCheckAlgException e) {
            e.printStackTrace();
        }

    }

    public void buttonReleased(String id) {
        //System.out.println("Button released on node "+id);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== layoutButton){
            if(layoutButton.getText().equals("Auto Layout is working")){
                viewer.disableAutoLayout();
                layoutButton.setText("Auto Layout is closed");
            }else{
                viewer.enableAutoLayout();
                layoutButton.setText("Auto Layout is working");
            }
        }else if(e.getSource()==viewPercentButton){
            graphPanel.getCamera().setViewPercent(parseDouble(viewPercentTextField.getText()));
        }else if(e.getSource()==viewCenterButton){
            graphPanel.getCamera().setViewCenter(parseDouble(mouseXTextField.getText()), parseDouble(mouseYTextField.getText()), 0);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
/*
        mouseXTextField.setText(String.valueOf(e.getX()));
        mouseYTextField.setText(String.valueOf(e.getY()));
*/
    }
}