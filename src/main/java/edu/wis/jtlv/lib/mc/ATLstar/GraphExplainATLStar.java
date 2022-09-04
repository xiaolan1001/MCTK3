package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import net.sf.javabdd.BDD;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.LinkedList;
import java.util.Queue;

public class GraphExplainATLStar extends MultiGraph {
    private SpriteManager spriteManager;
    private ATLStarModelCheckAlg checker;

    //有参构造
    public GraphExplainATLStar(String id, ATLStarModelCheckAlg checker) {
        super(id);
        this.checker = checker;
        spriteManager = new SpriteManager(this);
    }

    //Getter和Setter方法
    public ATLStarModelCheckAlg getChecker() {
        return checker;
    }

    public void setChecker(ATLStarModelCheckAlg checker) {
        this.checker = checker;
    }

    public Node addStateNode(int pathNo, int stateNo, BDD stateBDD, Spec satSpec, int layer) throws SpecException {
        String stateID = pathNo + "." + stateNo;

        Node node = addNode(stateID);
        if(node==null) return null;

        node.addAttribute("ui.label", node.getId());

        node.setAttribute("pathNo", pathNo);
        node.setAttribute("stateNo", stateNo);
        node.setAttribute("BDD", stateBDD);
        node.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD, "\n"));
        node.setAttribute("layer", layer);

        Sprite sprite = spriteManager.addSprite("nodeSprite-"+pathNo+"-"+stateNo);
        sprite.setPosition(StyleConstants.Units.PX, 30, 30, -90);
        sprite.attachToNode(stateID);

        node.setAttribute("sprite", sprite);

        Queue<Spec> queue = new LinkedList<>();
        if(satSpec != null) {
            if(satSpec.hasTemporalOperators() || satSpec.hasEpistemicOperators())
                queue.offer(satSpec);
            sprite.setAttribute("ui.label", SpecUtil.simplifySpecString(satSpec.toString(), false));
        } else sprite.setAttribute("ui.label", "");

        node.setAttribute("mapSatSpecs", queue);

        return node;
    }

    public boolean setNodeBDD(String nodeID, BDD stateBDD) {
        Node node = getNode(nodeID);
        if(node == null) return false;
        node.setAttribute("BDD", stateBDD);
        node.setAttribute("stateDetails", Env.getOneBDDStateDetails(stateBDD, "\n"));

        return true;
    }

    public boolean addNodeSatSpec(String nodeID, Spec satSpec, boolean explainSatSpec) throws SpecException {
        if(nodeID.equals("")) return false;
        Node node = getNode(nodeID);
        if(node == null) return false;

        Sprite sprite = node.getAttribute("sprite");
        Queue<Spec> queue = node.getAttribute("mapSatSecs");

        if(satSpec != null) {
            if((satSpec.hasTemporalOperators() || satSpec.hasEpistemicOperators()
            && explainSatSpec)) {
                queue.offer(satSpec);
            }
            String oldSatSpec = SpecUtil.simplifySpecString(getNodeSatSpec(nodeID), false);
            sprite.setAttribute("ui.label", oldSatSpec.equals("")?
                    SpecUtil.simplifySpecString(satSpec.toString(), false):
                    oldSatSpec+", \n"+SpecUtil.simplifySpecString(satSpec.toString(), true));
        }

        node.setAttribute("mapSatSpecs", queue);

        return true;
    }

    public int getNodePathNo(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return -1;

        return node.getAttribute("pathNo");
    }

    public int getNodeStateNo(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return -1;

        return node.getAttribute("stateNo");
    }

    public BDD getNodeBDD(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return null;
        return node.getAttribute("BDD");
    }

    public String getNodeSatSpec(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return "";
        Sprite sprite = node.getAttribute("sprite");

        return sprite.getAttribute("ui.label");
    }

    public String getNodeStateDetails(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return "";

        return node.getAttribute("stateDetails");
    }

    public int getNodeLayer(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return -1;

        return node.getAttribute("layer");
    }
}
