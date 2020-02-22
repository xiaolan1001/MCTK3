package edu.wis.jtlv.lib.mc.RTCTL_STAR;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import net.sf.javabdd.BDD;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

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

class GraphNodeSpecInfo{
    Spec spec;
    boolean needExplained; // whether this spec should be explained
    boolean explained;  // whether this spec is explained

    public GraphNodeSpecInfo(Spec spec, boolean needExplained, boolean explained){
        this.spec=spec;
        this.needExplained=needExplained;
        this.explained=explained;
    }
}

class GraphEdgeSpecInfo{
    Spec spec;
    NodePath path;
    int pos;
    boolean needExplained; // whether this spec should be explained
    boolean explained;  // whether this spec is explained

    public GraphEdgeSpecInfo(Spec spec, NodePath path, int pos, boolean needExplained, boolean explained){
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

    public Node addNode(int pathNo, int stateNo, BDD stateBDD) {
        String stateId = pathNo+"."+stateNo;
        if(stateId==null || stateId.equals("")) return null;
        Node n = addNode(stateId);
        if(n==null) return null;
        n.addAttribute("ui.label", n.getId());

        n.setAttribute("pathNo", pathNo);
        n.setAttribute("stateNo", stateNo);
        n.setAttribute("BDD", stateBDD);

        n.setAttribute("spriteSpecNumber",0); // the number of specs
        n.setAttribute("spriteAnnotationNumber",0); // the number of additional annotations

/*
        //attach a sprite at this node
        Sprite s = sman.addSprite("sprite-"+pathNo+"-"+stateNo);
        s.setPosition(StyleConstants.Units.PX,30,30,0);
        s.attachToNode(stateId);
        n.setAttribute("sprite", s);

        LinkedHashMap<String, GraphElementAttachedSpec> mapSpecs = new LinkedHashMap<String, GraphElementAttachedSpec>();
        n.setAttribute("mapSpecs", mapSpecs);
*/

        return n;
    }



    public void nodeLayoutSprites(String nodeId){
        Node n = getNode(nodeId); if(n==null) return;
        int specNum=n.getAttribute("spriteSpecNumber");
        int annNum=n.getAttribute("spriteAnnotationNumber");
        if(specNum+annNum<=0) return;

        int labelHeight=40;
        int z=(int)(labelHeight*(specNum+annNum)/2.0)-20;
        for(int i=1;i<=specNum;i++){
            Sprite s = n.getAttribute("spriteSpec"+i); if(s==null) return;
            s.setPosition(StyleConstants.Units.PX,30,30,z);
            z-=labelHeight;
        }
        for(int i=1;i<=annNum;i++){
            Sprite s = n.getAttribute("spriteAnnotation"+i); if(s==null) return;
            s.setPosition(StyleConstants.Units.PX,30,30,z);
            z-=labelHeight;
        }
    }

    public boolean nodeAddAnnotation(String nodeId, String annotation) {
        Node n = getNode(nodeId); if(n==null) return false;
        if(annotation==null || annotation.equals("")) return true;

        int annNum=n.getAttribute("spriteAnnotationNumber");
        //check if annotation is already in this node
        for(int i=1;i<=annNum;i++){
            Sprite s=n.getAttribute("spriteAnnotation"+i);
            String label=s.getAttribute("ui.label");
            if(label.toString().equals(annotation)) return false;
        }
        // now annotation is not yet in this node

        Sprite s = sman.addSprite(nodeId.replace(".","+")+"-spriteNodeAnnotation-"+(++annNum));
        s.setAttribute("ui.label",annotation);
        s.attachToNode(nodeId);
        n.setAttribute("spriteAnnotation"+annNum, s);
        n.setAttribute("spriteAnnotationNumber",annNum);

        nodeLayoutSprites(nodeId);
        return true;
    }

    // Premises: spec is a state formula and nodeId|=spec
    // Results: spec is putted into the spec map of this node, if spec need to be explained
    public boolean nodeAddSpec(String nodeId,
                               Spec spec
    ) throws SpecException {
        Node n = getNode(nodeId); if(n==null) return false;
        if(spec==null) return false;

        int specNum=n.getAttribute("spriteSpecNumber");
        //check if spec is already in this node
        for(int i=1;i<=specNum;i++){
            Sprite s=n.getAttribute("spriteSpec"+i);
            Spec nspec=s.getAttribute("spec");
            if(nspec.toString().equals(spec.toString())) return false;
        }
        // now spec is not yet in this node

        Sprite s = sman.addSprite(nodeId.replace(".","+")+"-spriteNodeSpec-"+(++specNum));
        s.setAttribute("spec",spec);

        if(spec.isPropSpec() && spec.toBDD().isOne()) return true;  // do not explain TRUE
        boolean needExplained = RTCTL_STAR_ModelCheckAlg.specNeedExplainEE(spec) || RTCTL_STAR_ModelCheckAlg.specNeedExplainTemporalOp(spec);
        s.setAttribute("needExplained", needExplained);

        s.setAttribute("explained", false);
        s.setAttribute("ui.label","⊨"+RTCTL_STAR_ModelCheckAlg.simplifySpecString(spec,false));
        s.attachToNode(nodeId);

        n.setAttribute("spriteSpec"+specNum, s);
        n.setAttribute("spriteSpecNumber",specNum);

        nodeLayoutSprites(nodeId);
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

    public String nodeGetSpecsInfo(String nodeID) throws SpecException {
        Node n = getNode(nodeID); if(n==null) return "";
        int specNum = n.getAttribute("spriteSpecNumber");
        String ret="";
        for(int i=1;i<=specNum;i++){
            Sprite s=n.getAttribute("spriteSpec"+i);
            Spec spec=s.getAttribute("spec");
            String d=""; if(ret.equals("")) d=""; else d="\n";
            ret+=d+RTCTL_STAR_ModelCheckAlg.simplifySpecString(spec,false);
        }
        return ret;
    }

    public String nodeGetAnnotations(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        int annNum = n.getAttribute("spriteAnnotationNumber");
        String ret="";
        for(int i=1;i<=annNum;i++){
            Sprite s=n.getAttribute("spriteAnnotation"+i);
            String ann=s.getAttribute("ui.label");
            String d=""; if(ret.equals("")) d=""; else d="\n";
            ret+=d+ann;
        }
        return ret;
    }

    public String nodeGetInfo(String nodeID, boolean withStateDetails) throws SpecException {
        Node n=getNode(nodeID); if(n==null) return null;
        BDD state=nodeGetBDD(nodeID);

        String s1=nodeGetSpecsInfo(nodeID);
        String s2=nodeGetAnnotations(nodeID);
        String ret="";
        if(s1==null || s1.equals(""))
            {if(s2==null || s2.equals("")) ret=""; else ret=s2;}
        else
            {if(s2==null || s2.equals("")) ret=s1; else ret=s1+"\n"+s2;}

        if(withStateDetails){
            String stateDetails=Env.getOneBDDStateDetails(n.getAttribute("BDD"),"\n");
            if(!ret.equals(""))
                return "----satisfied formulas and annotations----\n"+ret
                        +"\n----state details----\n"+stateDetails;
            else
                return stateDetails;
        }else
            return ret;
    }

    public String nodeGetStateDetails(String nodeID) {
        Node n = getNode(nodeID); if(n==null) return "";
        return Env.getOneBDDStateDetails(n.getAttribute("BDD"),"\n");
    }

    // add a transition from->to
    public Edge addArc(String id, String from, String to, boolean directed){
        Edge e = addEdge(id,from,to,directed);
        if(e==null) return null;

        e.setAttribute("spriteSpecNumber",0);
        e.setAttribute("spriteAnnotationNumber",0);

        return e;
    }

    // Premises: path^pos|=spec, edgeId is the first edge of path
    // Results: spec is putted into the spec map of this node, if spec need to be explained
    public boolean edgeAddSpec(String edgeId,
                               Spec spec,
                               NodePath path,
                               int pos,
                               boolean onlyShow   // if onlyShow=true, then spec is only for displaying
    ) throws SpecException {
        Edge e = getEdge(edgeId); if(e==null) return false;
        if(spec==null) return false;

        int specNum=e.getAttribute("spriteSpecNumber");
        //check if spec is already in this edge
        for(int i=1;i<=specNum;i++){
            Sprite s=e.getAttribute("spriteSpec"+i);
            Spec espec=s.getAttribute("spec");
            if(espec.toString().equals(spec.toString())) return false;
        }
        // now spec is not yet in this edge

        Sprite s = sman.addSprite(edgeId.replace(".","+")+"-spriteEdgeSpec-"+(++specNum));
        s.setAttribute("spec",spec);
        s.addAttribute("ui.style", "text-color:blue;");

        if(spec.isPropSpec() && spec.toBDD().isOne()) return true;  // do not explain TRUE
        boolean needExplained = !onlyShow &&
                (RTCTL_STAR_ModelCheckAlg.specNeedExplainEE(spec) || RTCTL_STAR_ModelCheckAlg.specNeedExplainTemporalOp(spec));
        s.setAttribute("needExplained", needExplained);

        s.setAttribute("path",path);
        s.setAttribute("pos",pos);

        s.setAttribute("explained", false);
        s.setAttribute("ui.label",
                "path"+(path.pathIndex+1)+((pos==0)?"":","+pos)
                        +"⊨"+RTCTL_STAR_ModelCheckAlg.simplifySpecString(spec,false));
        s.attachToEdge(edgeId);

        e.setAttribute("spriteSpec"+specNum, s);
        e.setAttribute("spriteSpecNumber",specNum);

        edgeLayoutSprites(edgeId);
        return true;
    }

    public boolean edgeAddAnnotation(String edgeId, String annotation) {
        Node n = getNode(edgeId); if(n==null) return false;
        if(annotation==null || annotation.equals("")) return true;

        int annNum=n.getAttribute("spriteAnnotationNumber");
        //check if annotation is already in this node
        for(int i=1;i<=annNum;i++){
            Sprite s=n.getAttribute("spriteAnnotation"+i);
            String label=s.getAttribute("ui.label");
            if(label.toString().equals(annotation)) return false;
        }
        // now annotation is not yet in this node

        Sprite s = sman.addSprite(edgeId.replace(".","+")+"-spriteEdgeAnnotation-"+(++annNum));
        s.setAttribute("ui.label",annotation);
        s.attachToNode(edgeId);
        n.setAttribute("spriteAnnotation"+annNum, s);
        n.setAttribute("spriteAnnotationNumber",annNum);

        edgeLayoutSprites(edgeId);
        return true;
    }

    public void edgeLayoutSprites(String edgeId){
        Edge e = getEdge(edgeId); if(e==null) return;
        int specNum=e.getAttribute("spriteSpecNumber");
        int annNum=e.getAttribute("spriteAnnotationNumber");
        if(specNum+annNum<=0) return;

        double interval=1.0/(specNum+annNum+1);
        int i;
        for(i=1;i<=specNum;i++){
            Sprite s = e.getAttribute("spriteSpec"+i); if(s==null) return;
            s.setPosition(interval*i);
        }
        for(int j=i;j<=specNum+annNum;j++){
            Sprite s = e.getAttribute("spriteAnnotation"+i); if(s==null) return;
            s.setPosition(interval*j);
        }
    }



}
