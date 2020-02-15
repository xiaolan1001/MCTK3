package edu.wis.jtlv.lib.mc.RTCTL_STAR;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.spec.Spec;
import net.sf.javabdd.BDD;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.HashMap;

public class GraphExplainRTCTLs extends MultiGraph {
    private SpriteManager sman;

    public RTCTL_STAR_ModelCheckAlg getChecker() {
        return checker;
    }

    public void setChecker(RTCTL_STAR_ModelCheckAlg checker) {
        this.checker = checker;
    }

    private RTCTL_STAR_ModelCheckAlg checker;

    public GraphExplainRTCTLs(String id, RTCTL_STAR_ModelCheckAlg checker) {
        super(id);
        this.checker = checker;
        sman = new SpriteManager(this);
    }

    public Node addNode(int pathNo, int stateNo, BDD stateBDD, String annotation) {
        String stateId = pathNo+"."+stateNo;
        if(stateId==null || stateId.equals("")) return null;
        Node n = addNode(stateId);
        if(n==null) return null;
        n.addAttribute("ui.label", n.getId());

        n.setAttribute("pathNo", pathNo);
        n.setAttribute("stateNo", stateNo);
        n.setAttribute("BDD", stateBDD);
        n.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD,"\n"));

        //attach a sprite at this node
        Sprite s = sman.addSprite("sprite-"+pathNo+"-"+stateNo);
        s.setPosition(StyleConstants.Units.PX,30,30,-90);
        s.attachToNode(stateId);
        n.setAttribute("sprite", s);

        if(annotation!=null) s.setAttribute("ui.label",annotation);

        HashMap<String, Spec> mapSpecsForExplain = new HashMap<String, Spec>();
        n.setAttribute("mapSpecsForExplain", mapSpecsForExplain);

        /*
        HashMap<String, Spec> StrSpecMap = new HashMap<String, Spec>(); //satSpec.toString() <-> satSpec
        if(satSpec!=null){
            if(satSpec.hasTemporalOperators() || satSpec.hasEpistemicOperators())
                StrSpecMap.put(satSpec.toString(), satSpec);
            s.setAttribute("ui.label", checker.simplifySpecString(satSpec.toString(),true));
        }else{
            s.setAttribute("ui.label", "");
        }
        n.setAttribute("mapSatSpecs", StrSpecMap);
         */

        return n;
    }

    // add a transition from->to
    public Edge addArc(String id, String from, String to, boolean directed){
        Edge e = addEdge(id,from,to,directed);
        if(e==null) return null;
        //attach a sprite at this node
        Sprite s = sman.addSprite("sprite-edge-"+id.replace(".","-"));
        s.attachToEdge(id);
        s.setPosition(0.5);
        s.setAttribute("ui.label","");
        e.setAttribute("sprite", s);
        return e;
    }

    public boolean setNodeBDD(String nodeId, BDD stateBDD) {
        Node n = getNode(nodeId); if(n==null) return false;
        n.setAttribute("BDD", stateBDD);
        n.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD,"\n"));
        return true;
    }

    public boolean addNodeAnnotation(String nodeId, String annotation) {
        Node n = getNode(nodeId); if(n==null) return false;
        Sprite s = n.getAttribute("sprite");

        if(annotation!=null) {
            String old_ann = s.getAttribute("ui.label");
            if(old_ann==null || old_ann.equals(""))
                s.setAttribute("ui.label",annotation);
            else
                s.setAttribute("ui.label", s.getAttribute("ui.label") + ", \n" + annotation);
        }
        return true;
    }

    public boolean addEdgeAnnotation(String edgeId, String annotation) {
        Edge e = getEdge(edgeId); if(e==null) return false;
        Sprite s = e.getAttribute("sprite");

        if(annotation!=null) {
            String old_ann = s.getAttribute("ui.label");
            if(old_ann==null || old_ann.equals(""))
                s.setAttribute("ui.label",annotation);
            else
                s.setAttribute("ui.label", s.getAttribute("ui.label") + ", \n" + annotation);
        }
        return true;
    }

    /*
    if spec is the subformula preceded by E, then it will be added to the spec. map of the node
     */
    public boolean addNodeSpecForExplain(String nodeId,
                                         Spec spec
    ) {
        Node n = getNode(nodeId); if(n==null) return false;
        if(spec==null) return false;
        /*
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        if(se.getOperator()!= Operator.EE) return false;

         */

        //Sprite s = n.getAttribute("sprite");
        HashMap<String, Spec> mapSpecsForExplain = n.getAttribute("mapSpecsForExplain");
        if(mapSpecsForExplain==null) return false;
        mapSpecsForExplain.put(spec.toString(), spec);
        n.setAttribute("mapSpecsForExplain", mapSpecsForExplain);
/*
        if(spec.hasTemporalOperators() ||
                spec.hasPathOperators() ||
                spec.hasEpistemicOperators())
           // Q.offer(satSpec);
            if (mapSpecsForExplain.put(spec.toString(), spec)==null){
                String oldSatSpec = getNodeSatSpec(nodeId);
                s.setAttribute("ui.label",
                        oldSatSpec.equals("") ?
                                checker.simplifySpecString(spec.toString(),true) :
                                oldSatSpec + ", \n"+ checker.simplifySpecString(spec.toString(),true));
                n.setAttribute("mapSatSpecs", mapSpecsForExplain);
            }
 */
        return true;
    }

    public int getNodePathNo(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return -1;
        return n.getAttribute("pathNo");
    }

    public int getNodeStateNo(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return -1;
        return n.getAttribute("stateNo");
    }

    public BDD getNodeBDD(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return null;
        return n.getAttribute("BDD");
    }

    public String getNodeSatSpec(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        Sprite s = n.getAttribute("sprite");
        return s.getAttribute("ui.label");
    }

    public String getNodeStateDetails(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        return n.getAttribute("stateDetails");
    }

}
