package edu.wis.jtlv.lib.mc.RTCTLK;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.spec.Spec;
import net.sf.javabdd.BDD;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.LinkedList;
import java.util.Queue;

public class GraphExplainRTCTLK extends MultiGraph {
    private SpriteManager sman;

    public RTCTLKModelCheckAlg getChecker() {
        return checker;
    }

    public void setChecker(RTCTLKModelCheckAlg checker) {
        this.checker = checker;
    }

    private RTCTLKModelCheckAlg checker;

    public GraphExplainRTCTLK(String id, boolean strictChecking, boolean autoCreate, int initialNodeCapacity, int initialEdgeCapacity, RTCTLKModelCheckAlg checker) {
        super(id, strictChecking, autoCreate, initialNodeCapacity, initialEdgeCapacity);
        this.checker = checker;
        sman = new SpriteManager(this);
    }

    public GraphExplainRTCTLK(String id, RTCTLKModelCheckAlg checker) {
        super(id);
        this.checker = checker;
        sman = new SpriteManager(this);
    }

    public Node addStateNode(int pathNo, int stateNo, BDD stateBDD, Spec satSpec, int layer) {
        String stateId = pathNo+"."+stateNo;
        if(stateId==null || stateId.equals("")) return null;
        Node n = addNode(stateId);
        if(n==null) return null;
        n.addAttribute("ui.label", n.getId());

        n.setAttribute("pathNo", pathNo);
        n.setAttribute("stateNo", stateNo);
        n.setAttribute("BDD", stateBDD);
        n.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD,"\n"));
        n.setAttribute("layer", layer);

        //attach a sprite at this node
        Sprite s = sman.addSprite("nodeSprite-"+pathNo+"-"+stateNo);
        s.setPosition(StyleConstants.Units.PX,30,30,-90);
        s.attachToNode(stateId);
        n.setAttribute("sprite", s);

        Queue<Spec> Q = new LinkedList<Spec>();
        if(satSpec!=null) {
            if(satSpec.hasTemporalOperators() || satSpec.hasEpistemicOperators())
                Q.offer(satSpec);
            s.setAttribute("ui.label", checker.simplifySpecString(satSpec.toString(),true));
        }else
            s.setAttribute("ui.label", "");

        n.setAttribute("mapSatSpecs", Q);

        return n;
    }



    public boolean setNodeBDD(String nodeId, BDD stateBDD) {
        Node n = getNode(nodeId); if(n==null) return false;
        n.setAttribute("BDD", stateBDD);
        n.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD,"\n"));
        return true;
    }

    public boolean addNodeSatSpec(String nodeId, Spec satSpec, boolean explainSatSpec) {
        if(nodeId.equals("")) return false;
        Node n = getNode(nodeId); if(n==null) return false;
        Sprite s = n.getAttribute("sprite");

        Queue<Spec> Q = n.getAttribute("mapSatSpecs");
        if(satSpec!=null) {
            if((satSpec.hasTemporalOperators() || satSpec.hasEpistemicOperators()) && explainSatSpec)
                Q.offer(satSpec);

            String oldSatSpec = checker.simplifySpecString(getNodeSatSpec(nodeId),true);
            s.setAttribute("ui.label",  oldSatSpec.equals("") ? checker.simplifySpecString(satSpec.toString(),true) : oldSatSpec + ", \n"+
                    checker.simplifySpecString(satSpec.toString(),true));
        }

        n.setAttribute("mapSatSpecs", Q);
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

    public int getNodeLayer(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return -1;
        return n.getAttribute("layer");
    }
}
