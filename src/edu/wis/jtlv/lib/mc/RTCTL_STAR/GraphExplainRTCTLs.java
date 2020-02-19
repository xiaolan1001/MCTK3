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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class GraphElementAttachedSpec{
    int type; // 0--for node; 1--for edge
    String id; // the id of the element( node or edge)

    // the information attached to the element
    Spec spec;

    // for temporal spec, trunkNodePaths[pathIndex]^startPos |= spec
    NodePath path;
    int pos;

    boolean needExplained; // whether this spec should be explained
    boolean explained;  // whether this spec is explained

    public GraphElementAttachedSpec(String NodeId, Spec spec, boolean needExplained, boolean explained){
        type=0;
        id=NodeId;
        this.spec=spec;
        this.needExplained=needExplained;
        this.explained=explained;
    }

    public GraphElementAttachedSpec(String edgeId, Spec spec, NodePath path, int pos, boolean needExplained, boolean explained){
        type=1;
        id=edgeId;
        this.spec=spec;
        this.path=path;
        this.pos =pos;
        this.needExplained=needExplained;
        this.explained=explained;
    }
}

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
        n.setAttribute("additionAnnotation", ""); // addition annotations

        //attach a sprite at this node
        Sprite s = sman.addSprite("sprite-"+pathNo+"-"+stateNo);
        s.setPosition(StyleConstants.Units.PX,30,30,0);
        s.attachToNode(stateId);
        n.setAttribute("sprite", s);

        if(annotation!=null) s.setAttribute("ui.label",annotation);

        LinkedHashMap<String, GraphElementAttachedSpec> mapSpecs = new LinkedHashMap<String, GraphElementAttachedSpec>();
        n.setAttribute("mapSpecs", mapSpecs);

        return n;
    }

    public void nodeRefreshLabel(String nodeId){
        Node n = getNode(nodeId); if(n==null) return;
        String ann = n.getAttribute("additionAnnotation");
        String nodeLabel="";

        LinkedHashMap<String, GraphElementAttachedSpec> mapSpecs = n.getAttribute("mapSpecs");
        if(mapSpecs==null) return;
        Iterator iter = mapSpecs.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            String strSpec = (String) entry.getKey();
            GraphElementAttachedSpec specInfo= (GraphElementAttachedSpec) entry.getValue();

            //for nodes
            if(specInfo.type==0){
                String ss = RTCTL_STAR_ModelCheckAlg.simplifySpecString(strSpec,true);
                if(!ss.equals(""))
                    if(nodeLabel.equals("")) nodeLabel=ss; else nodeLabel=nodeLabel+", \n"+ss;
            }
            //for edges
            if(specInfo.type==1){

            }
        }


        if(nodeLabel==null) nodeLabel="";
        if(ann==null) ann="";

        String strLabel="";
        if(nodeLabel.equals("")) strLabel=ann;
        else if(ann.equals("")) strLabel=nodeLabel;
        else // strSpecs && ann are not empty
            strLabel=nodeLabel+", \n"+ann;

        Sprite s = n.getAttribute("sprite");
        if(!strLabel.equals(s.getAttribute("ui.label")))
            s.setAttribute("ui.label",strLabel);

    }

    public boolean nodeAddAnnotation(String nodeId, String annotation) {
        Node n = getNode(nodeId); if(n==null) return false;
        if(annotation==null || annotation.equals("")) return true;
        String ann = n.getAttribute("annotation");
        if(ann==null || ann.equals("")) ann=annotation;
        else ann=ann+", \n"+annotation;
        n.setAttribute("annotation");

        nodeRefreshLabel(nodeId);
        return true;
    }

    // Premises: spec is a state formula and nodeId|=spec
    // Results: spec is putted into the spec map of this node, if spec need to be explained
    public boolean nodeAddSpec(String nodeId,
                               Spec spec
    ) {
        Node n = getNode(nodeId); if(n==null) return false;
        if(spec==null) return false;

        //Sprite s = n.getAttribute("sprite");
        LinkedHashMap<String, GraphElementAttachedSpec> mapSpecs = n.getAttribute("mapSpecs");
        if(mapSpecs==null) return false;

        boolean needExplained = RTCTL_STAR_ModelCheckAlg.specNeedExplainEE(spec) || RTCTL_STAR_ModelCheckAlg.specNeedExplainTemporalOp(spec);
        mapSpecs.put(spec.toString(), new GraphElementAttachedSpec(nodeId,spec,needExplained,false));

        n.setAttribute("mapSpecs", mapSpecs);

        nodeRefreshLabel(nodeId);
        return true;
    }

    // Premises: spec is a NOT state formula and path^pos|=spec
    // Results: spec is putted into the spec map of node path[pos], if spec need to be explained
    public boolean nodeAddSpec(String nodeId,
                               Spec spec,
                               NodePath path,
                               int pos
    ) {
        Node n = getNode(nodeId); if(n==null) return false;
        if(spec==null) return false;

        //Sprite s = n.getAttribute("sprite");
        LinkedHashMap<String, GraphElementAttachedSpec> mapSpecs = n.getAttribute("mapSpecs");
        if(mapSpecs==null) return false;

        boolean needExplained = RTCTL_STAR_ModelCheckAlg.specNeedExplainEE(spec) || RTCTL_STAR_ModelCheckAlg.specNeedExplainTemporalOp(spec);
        mapSpecs.put(spec.toString(), new GraphElementAttachedSpec(nodeId,spec, path, pos, needExplained,false));

        n.setAttribute("mapSpecs", mapSpecs);
        return true;
    }

    public int nodeGetPathNo(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return -1;
        return n.getAttribute("pathNo");
    }

    public int nodeGetStateNo(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return -1;
        return n.getAttribute("stateNo");
    }

    public BDD nodeGetBDD(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return null;
        return n.getAttribute("BDD");
    }

    public boolean nodeSetBDD(String nodeId, BDD stateBDD) {
        Node n = getNode(nodeId); if(n==null) return false;
        n.setAttribute("BDD", stateBDD);
        return true;
    }

    public String nodeGetSpec(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        Sprite s = n.getAttribute("sprite");
        return s.getAttribute("ui.label");
    }

    public String nodeGetStateDetails(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        return Env.getOneBDDStateDetails(n.getAttribute("BDD"),"\n");
    }

    // add a transition from->to
    public Edge addArc(String id, String from, String to, boolean directed){
        Edge e = addEdge(id,from,to,directed);
        if(e==null) return null;
        //attach a sprite at this node
        Sprite s = sman.addSprite("sprite-edge-"+id.replace(".","+"));
        s.attachToEdge(id);
        s.setPosition(0.5);
        s.setAttribute("ui.label","");
        e.setAttribute("sprite", s);
        return e;
    }

    public boolean edgeAddAnnotation(String edgeId, String annotation) {
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


}
