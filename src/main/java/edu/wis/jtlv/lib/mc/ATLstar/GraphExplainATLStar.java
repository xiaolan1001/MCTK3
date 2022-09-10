package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import net.sf.javabdd.BDD;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.util.HashMap;

/**
 * <p>
 *     继承自MultiGraph:支持两个节点之间有多个边的图实现
 * </p>
 * @see org.graphstream.graph.implementations.MultiGraph
 */
public class GraphExplainATLStar extends MultiGraph {
    private final SpriteManager spriteManager; //精灵管理器的唯一角色是允许创建、销毁和枚举图形中的精灵.
    private ATLStarModelCheckAlg checker;

    public Spec spec=null, negSpec=null;

    public HashMap<Spec, SMVModule> specTesterMap;

    //有参构造
    public GraphExplainATLStar(Spec spec, Spec negSpec, HashMap<Spec, SMVModule> specTesterMap, ATLStarModelCheckAlg checker) throws SpecException {
        //Creates an empty graph with strict checking and without auto-creation.
        super("A counterexample of " + SpecUtil.simplifySpecString(spec, false));

        this.checker = checker;
        this.spec = spec;
        this.negSpec = negSpec;
        this.specTesterMap = specTesterMap;
        //为精灵创建一个新的管理者(器), 并且将其绑定至给定的图.
        spriteManager = new SpriteManager(this);
    }

    //Getter和Setter方法
    public ATLStarModelCheckAlg getChecker() {
        return checker;
    }

    public void setChecker(ATLStarModelCheckAlg checker) {
        this.checker = checker;
    }

    //成员方法

    /**
     * 添加状态节点
     * @param pathNo 路径编号(>=1)
     * @param stateNo 状态(节点)编号(>=0)
     * @param stateBDD 该状态的BDD表示
     * @return 返回添加好的节点
     */
    public Node addStateNode(int pathNo, int stateNo, BDD stateBDD) {
        String stateID = pathNo + "." + stateNo;

        Node node = addNode(stateID);
        if(node==null) return null;

        //添加或取代一个属性的值
        node.addAttribute("ui.label", node.getId());

        //与addAttributes作用类似
        node.setAttribute("pathNo", pathNo);
        node.setAttribute("stateNo", stateNo);
        node.setAttribute("BDD", stateBDD);

        //规约数量
        node.setAttribute("spriteSpecNumber",0);
        //额外注解数量
        node.setAttribute("spriteAnnotationNumber",0);

        return node;
    }

    public void nodeLayoutSprites(String nodeID){
        Node node = getNode(nodeID);
        if(node == null) return;

        int specNum = node.getAttribute("spriteSpecNumber");
        int annotationNum = node.getAttribute("spriteAnnotationNumber");
        if((specNum+annotationNum) <= 0) return;

        int labelHeight = 40;
        int z = (int)(labelHeight * (specNum + annotationNum) / 2.0) - 20;

        for(int i=1; i <= specNum; i++){
            //精灵对象允许在图的图形显示中添加数据表示.
            Sprite sprite = node.getAttribute("spriteSpec" + i);
            if(sprite == null) return;

            //使用PX作为数值单位
            sprite.setPosition(StyleConstants.Units.PX, 30, 30, z);
            z -= labelHeight;
        }

        for(int i=1; i <= annotationNum; i++){
            Sprite sprite = node.getAttribute("spriteAnnotation"+i);
            if(sprite == null) return;

            //使用PX作为数值单位
            sprite.setPosition(StyleConstants.Units.PX, 30, 30, z);
            z -= labelHeight;
        }
    }

    public boolean nodeAddAnnotation(String nodeID, String annotation) {
        Node node = getNode(nodeID);
        if(node == null) return false;
        if(annotation==null || annotation.equals("")) return true;

        int annotationNum=node.getAttribute("spriteAnnotationNumber");
        //检查注解是否已经在该节点中
        for(int i=1; i <= annotationNum; i++){
            Sprite sprite = node.getAttribute("spriteAnnotation"+i);
            String label = sprite.getAttribute("ui.label");
            if(label.equals(annotation))
                return false;
        }

        //注解不在该节点中
        Sprite sprite = spriteManager
                .addSprite(nodeID.replace(".","+")+"-spriteNodeAnnotation-"+(++annotationNum));
        sprite.setAttribute("ui.label", annotation);
        sprite.addAttribute("ui.style", "text-color:gray;");
        sprite.attachToNode(nodeID);
        node.setAttribute("spriteAnnotation"+annotationNum, sprite);
        node.setAttribute("spriteAnnotationNumber", annotationNum);

        nodeLayoutSprites(nodeID);

        return true;
    }

    /**
     * <p>
     *     前提：规约是状态公式且nodeID满足规约<br/>
     *     结果：如果需要解释spec, 则将spec放入该节点的spec map中
     * </p>
     * @param nodeID 节点ID
     * @param spec 规约
     * @return 布尔值
     * @throws SpecException 异常处理
     */
    public boolean nodeAddSpec(String nodeID, Spec spec) throws SpecException {
        Node node = getNode(nodeID);
        if(node == null) return false;
        if(spec == null) return false;

        int specNum = node.getAttribute("spriteSpecNumber");
        //检查注解是否已经在该节点中
        for(int i=1; i <= specNum; i++){
            Sprite sprite = node.getAttribute("spriteSpec"+i);
            Spec nspec = sprite.getAttribute("spec");
            if(nspec.toString().equals(spec.toString())) return false;
        }

        //注解不在该节点中
        Sprite sprite = spriteManager
                .addSprite(nodeID.replace(".","+")+"-spriteNodeSpec-"+(++specNum));
        sprite.setAttribute("spec",spec);

        //无需解释TRUE
        if(spec.isPropSpec() && spec.toBDD().isOne()) return true;

        boolean needExplained = ATLStarModelCheckAlg.needExpE(spec) || ATLStarModelCheckAlg.needExpT(spec);
        sprite.setAttribute("needExplained", needExplained);

        sprite.setAttribute("explained", false);
        sprite.setAttribute("ui.label","⊨"+ SpecUtil.simplifySpecString(spec,false));
        sprite.attachToNode(nodeID);

        node.setAttribute("spriteSpec"+specNum, sprite);
        node.setAttribute("spriteSpecNumber",specNum);

        nodeLayoutSprites(nodeID);

        return true;
    }

    public String nodeGetSpecsInfo(String nodeID) throws SpecException {
        Node node = getNode(nodeID);
        if(node==null) return "";

        int specNum = node.getAttribute("spriteSpecNumber");
        StringBuilder ret= new StringBuilder();

        for(int i=1; i <= specNum; i++){
            Sprite sprite = node.getAttribute("spriteSpec"+i);
            Spec spec = sprite.getAttribute("spec");
            String d = ""; if(ret.toString().equals("")) d=""; else d="\n";
            ret.append(d).append(SpecUtil.simplifySpecString(spec, false));
        }
        return ret.toString();
    }

    public String nodeGetAnnotations(String nodeID) {
        Node node = getNode(nodeID);
        if(node==null) return "";

        int annotationNum = node.getAttribute("spriteAnnotationNumber");
        StringBuilder ret = new StringBuilder();

        for(int i=1; i <= annotationNum; i++){
            Sprite sprite=node.getAttribute("spriteAnnotation"+i);
            String ann=sprite.getAttribute("ui.label");
            String d=""; if(ret.toString().equals("")) d=""; else d="\n";
            ret.append(d).append(ann);
        }
        return ret.toString();
    }

    public String nodeGetInfo(String nodeID, boolean withStateDetails) throws SpecException {
        Node n=getNode(nodeID); if(n==null) return null;

        String ret="";

        String s1 = nodeGetSpecsInfo(nodeID);
        String s2 = nodeGetAnnotations(nodeID);
        if(s1==null || s1.equals("")) {
            if(s2==null || s2.equals(""))
                ret="";
            else ret=s2;
        }
        else {
            if(s2==null || s2.equals(""))
                ret=s1;
            else ret=s1+"\n"+s2;
        }

        if(withStateDetails) {
            String stateDetails=Env.getOneBDDStateDetails(n.getAttribute("BDD"),"\n");
            if(!ret.equals(""))
                return "----annotations----\n"+ret
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

    /**
     * 添加迁移关系from->to
     * @param id edgeID
     * @param from 起始节点
     * @param to 目标节点
     * @param directed 有无方向
     * @return edge
     */
    public Edge addArc(String id, String from, String to, boolean directed){
        Edge edge = getEdge(id);
        if(edge==null) {
            edge = addEdge(id, from, to, directed);
            if(edge==null) return null;
            edge.setAttribute("spriteSpecNumber",0);
            edge.setAttribute("spriteAnnotationNumber",0);
        }
        return edge;
    }

    /**
     * <p>
     *     前提：path^pos|=spec, edgeID是路径的第一条边<br/>
     *     结果：如果spec需要解释, spec放入该节点的spec map中
     * </p>
     * @param edgeID edgeID
     * @param spec spec
     * @param path path
     * @param pos position
     * @param onlyShow 如果onlyShow为true, 那么spec仅用作展示
     * @return boolean
     * @throws SpecException 异常处理
     */
    public boolean edgeAddSpec(String edgeID, Spec spec, NodePath path, int pos, boolean onlyShow)
            throws SpecException {
        Edge edge = getEdge(edgeID); if(edge==null) return false;
        if(spec==null) return false;

        int specNum = edge.getAttribute("spriteSpecNumber");
        //检查规约是否已经在这条边
        for(int i=1; i <= specNum; i++){
            Sprite sprite = edge.getAttribute("spriteSpec"+i);
            Spec espec = sprite.getAttribute("spec");
            if(espec.toString().equals(spec.toString())) return false;
        }

        //规约不在该边中
        Sprite sprite = spriteManager.addSprite(edgeID.replace(".","+")+"-spriteEdgeSpec-"+(++specNum));
        sprite.setAttribute("spec",spec);
        sprite.addAttribute("ui.style", "text-color:blue;");

        if(spec.isPropSpec() && spec.toBDD().isOne()) return true; //无需解释TRUE
        boolean needExplained = !onlyShow &&
                (ATLStarModelCheckAlg.needExpE(spec) || ATLStarModelCheckAlg.needExpT(spec));
        sprite.setAttribute("needExplained", needExplained);

        sprite.setAttribute("path",path);
        sprite.setAttribute("pos",pos);

        sprite.setAttribute("explained", false);
        sprite.setAttribute("ui.label",
                "path"+(path.pathIndex+1)+((pos==0)?"":","+pos)
                        +"⊨"+ SpecUtil.simplifySpecString(spec,false));
        sprite.attachToEdge(edgeID);

        edge.setAttribute("spriteSpec"+specNum, sprite);
        edge.setAttribute("spriteSpecNumber",specNum);

        edgeLayoutSprites(edgeID);
        return true;
    }

    public void edgeLayoutSprites(String edgeId){
        Edge edge = getEdge(edgeId); if(edge==null) return;
        int specNum = edge.getAttribute("spriteSpecNumber");
        int annNum = edge.getAttribute("spriteAnnotationNumber");
        if((specNum+annNum) <= 0) return;

        double interval = 1.0/(specNum+annNum+1);
        int i;
        for(i=1; i <= specNum; i++){
            Sprite sprite = edge.getAttribute("spriteSpec"+i); if(sprite==null) return;
            sprite.setPosition(interval*i);
        }
        for(int j=i; j <= specNum+annNum; j++){
            Sprite sprite = edge.getAttribute("spriteAnnotation"+i); if(sprite==null) return;
            sprite.setPosition(interval*j);
        }
    }

    public BDD nodeGetBDD(String nodeID) {
        Node node = getNode(nodeID); if(node==null) return null;

        return node.getAttribute("BDD");
    }
}