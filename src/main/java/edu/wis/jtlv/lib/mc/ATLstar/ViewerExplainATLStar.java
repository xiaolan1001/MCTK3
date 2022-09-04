package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class ViewerExplainATLStar implements ViewerListener {
    protected boolean loop = true;

    private final GraphExplainATLStar graph;

    public ViewerExplainATLStar(GraphExplainATLStar graph) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        this.graph = graph;

        graph.addAttribute("ui.label", graph.getId());

        for (Node node: graph) {
            node.addAttribute("ui.label", node.getId());
        }

        graph.addAttribute("ui.stylesheet",
                "node { stroke-mode: plain; shape: circle; size: 40px; fill-color: green; z-index: 10; text-size: 11; }" +
                        "node.initialState {fill-color: pink;} " +
                        "node.epistemicState {fill-color: gold;} " +
                        "edge { size: 2px; shape: line; fill-color: green; arrow-size: 8px, 6px; arrow-shape: arrow; }" +
                        "edge.epistemicEdge { fill-color: gold; shape: cubic-curve;} " +
                        "sprite {size: 0px;}"
        );

        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        while (loop) {
            fromViewer.pump();
        }
    }

    @Override
    public void viewClosed(String s) {
        loop = false;
    }

    @Override
    public void buttonPushed(String s) {
        System.out.println("-------- State "+s+" --------");
        String str = graph.getNodeSatSpec(s);
        if(str!=null && !str.equals(""))
            System.out.println("[satisfies " + str + "]");
        System.out.println( graph.getNodeStateDetails(s));
        try {
            try {
                graph.getChecker().explainOneGraphNode(graph,s);
            } catch (ModelCheckException | SpecException | SMVParseException | ModuleException e) {
                e.printStackTrace();
            }
        } catch (ModelCheckAlgException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buttonReleased(String s) {

    }
}
