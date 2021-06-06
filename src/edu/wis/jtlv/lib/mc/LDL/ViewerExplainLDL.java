package edu.wis.jtlv.lib.mc.LDL;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import swing.MCTK2Frame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

import static edu.wis.jtlv.lib.mc.RTCTL_STAR.RTCTL_STAR_ModelCheckAlg.simplifySpecString;
import static java.lang.Double.parseDouble;
import static swing.MCTK2Frame.consoleOutput;

public class ViewerExplainLDL implements ViewerListener, ActionListener, MouseMotionListener {
    protected boolean loop = true;
    JFrame ceFrame;
    Viewer viewer;
    ViewPanel graphPanel;
    JButton viewCenterButton;
    JToggleButton layoutToggleButton;
    JLabel viewPercentLabel;
    JTextField viewPercentTextField, mouseXTextField, mouseYTextField;
    JLabel negSpecLabel;

    JSplitPane splitPane;
    public static JTextPane outputTextPane;

    public GraphExplainLDL getGraph() {
        return graph;
    }

    public void setGraph(GraphExplainLDL graph) {
        this.graph = graph;
    }

    private GraphExplainLDL graph;

    public ViewerExplainLDL(GraphExplainLDL G) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = G;
        graph.addAttribute("ui.label", graph.getId());

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

        ceFrame.setSize(1280, 800);
        // 把新窗口的位置设置到 relativeWindow 窗口的中心
        //ceFrame.setLocationRelativeTo(this.indexJFrame);
        Image logoIcon = new ImageIcon(MCTK2Frame.class.getResource("/swing/Icons/logo.png")).getImage();
        ceFrame.setIconImage(logoIcon);
        // 点击窗口关闭按钮, 执行销毁窗口操作（如果设置为 EXIT_ON_CLOSE, 则点击新窗口关闭按钮后, 整个进程将结束）
        ceFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // 窗口设置为不可改变大小 newJFrame.setResizable(false);

        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        layoutToggleButton=new JToggleButton("Auto Layout");
        layoutToggleButton.setSelected(true);
        layoutToggleButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(layoutToggleButton.isSelected()){
                    viewer.enableAutoLayout();
                }else viewer.disableAutoLayout();
            }
        });

        viewPercentLabel=new JLabel("View Percent:");
        viewPercentTextField = new JTextField("1",4);
        viewPercentTextField.addActionListener(this);

/*
        viewCenterButton = new JButton("View Center:");
        viewCenterButton.addActionListener(this);
        mouseXTextField = new JTextField("0",4);
        mouseYTextField = new JTextField("0",4);
*/

        negSpecLabel = new JLabel("The following is a witness of "+graph.negSpecStr);
        negSpecLabel.setToolTipText(graph.negSpecStr);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(layoutToggleButton);
        controlPanel.add(viewPercentLabel);
        controlPanel.add(viewPercentTextField);
        controlPanel.add(negSpecLabel);
/*
        controlPanel.add(viewCenterButton);
        controlPanel.add(mouseXTextField);
        controlPanel.add(mouseYTextField);
*/
        graphPanel = (ViewPanel) viewer.addDefaultView(false);

        outputTextPane = new JTextPane();
        JScrollPane outputScrollPane = new JScrollPane(outputTextPane);
        outputScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Counterexample Information"),
                                BorderFactory.createEmptyBorder(0,0,0,0)),
                        BorderFactory.createEmptyBorder(0,0,0,0)));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, outputScrollPane);
        splitPane.setDividerLocation(ceFrame.getWidth()-350);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        ceFrame.setLayout(new BorderLayout());
        ceFrame.add("North", controlPanel);
        ceFrame.add("Center",splitPane);

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

                String s=simplifySpecString(graph.nodeGetInfo(id,true),false);
                if(s!=null)
                    consoleOutput(1,"darkGray","========State "+id+"========\n"+s+"\n");

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
/*        if(e.getSource()== layoutToggleButton){
            if(layoutButton.getText().equals("Auto Layout is working")){
                viewer.disableAutoLayout();
                layoutButton.setText("Auto Layout is closed");
            }else{
                viewer.enableAutoLayout();
                layoutButton.setText("Auto Layout is working");
            }
        }else */if(e.getSource()==viewPercentTextField){
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
        // selectEdge(e.getX(),e.getY());
    }

    public Edge selectEdge(double px, double py) {
        double ld = 5; // Max distance mouse click can be from line to be a click
        GraphicEdge se = null; // Current closest edge to mouse click that is withing max distance
        //GraphicGraph gg = viewer.getGraphicGraph();
        Iterable<GraphicEdge> ie = (Iterable<GraphicEdge>) graph.getEachEdge();
        for(GraphicEdge ge : ie) {
            // Nodes of current edge
            GraphicNode gn0 = ge.getNode0();
            GraphicNode gn1 = ge.getNode1();
            // Coordinates of node 0 and node 1
            Point3 gn0p = graphPanel.getCamera().transformGuToPx(gn0.getX(), gn0.getY(), gn0.getZ());
            Point3 gn1p = graphPanel.getCamera().transformGuToPx(gn1.getX(), gn1.getY(), gn1.getZ());
            // Values for equation of the line
            double m = (gn1p.y-gn0p.y)/(gn1p.x-gn0p.x); // slope
            double b = gn1p.y-m*gn1p.x; // y intercept
            // Distance of mouse click from the line
            double d = Math.abs(m*px-py+b)/Math.sqrt(Math.pow(m,2)+1);

            System.out.println("Mouse Point: "+px+","+py+", GN0Point: "+gn0p.toString()+", GN1Point: "+gn1p.toString()+". Distance: "+d);

            // Determine lowest x (lnx), hishest x (hnx), lowest y (lny), highest y (hny)
            double lnx = gn0p.x;
            double lny = gn0p.y;
            double hnx = gn1p.x;
            double hny = gn1p.y;
            if(hnx < lnx) {
                lnx = gn1p.x;
                hnx = gn0p.x;
            }
            if(hny < lny) {
                lny = gn1p.y;
                hny = gn0p.y;
            }
            // Determine if click is close enough to line (d < ld), and click is within edge bounds (lnx <= px && lny <= py && hnx >= px && hny >= py)
            if(d<ld && lnx <= px && lny <= py && hnx >= px && hny >= py) {
                se = ge; // store edge
                ld = d; // update max distance to get the closest edge to the mouse click
            }
        }
        // Determine if edge clicked and return the edge
        if(se!=null) {
            System.out.println("Selected edge: "+se.getId());
            return graph.getEdge(se.getId());
        }
        return null;
    }
}