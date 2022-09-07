package edu.wis.jtlv.lib.mc.ATLstar;

import net.sf.javabdd.BDD;

import java.util.Vector;

public class NodePath {
    Vector<String> nodes; //节点路径的节点ID列表
    int loopIndex; //套索路径周期的第一个节点的索引; loopIndex=-1表示该路径为有限路径
    int pathIndex;

    BDD firstState=null; //路径中第一个节点的D||T-state

    public NodePath(int pathIndex){
        nodes = new Vector<String>();
        loopIndex = -1;
        this.pathIndex=pathIndex;
    }

    public NodePath(int pathIndex, Vector<String> nodeIdList, int loopIndex, BDD firstState){
        this.pathIndex=pathIndex;
        this.nodes = nodeIdList;
        this.loopIndex = loopIndex;
        this.firstState = firstState;
    }

    /**
     * 返回路径位置的索引
     * @param pos 路径上的逻辑位置
     * @return
     */
    int at(int pos) {
        if(pos<0) return -1;
        else if(pos<nodes.size()) return pos;
        else{
            //pos >= nodes.size()
            return loopIndex+((pos-loopIndex)%(nodes.size()-loopIndex));
        }
    }

    int size(){
        return nodes.size();
    }

    String get(int index){
        return nodes.get(index);
    }

    /**
     * <p>
     *     前提：在调用该函数之前, 位置pos已经在路径path^startPos上进行了解释<br/>
     *     当前位置pos已经被解释时返回false, 否则返回true
     * </p>
     * @param startPos
     * @param curPos
     * @return
     */
    boolean needExplainNextPosition(int startPos, int curPos){
        int startIdx=at(startPos);
        int idx=at(curPos);
        return (startIdx >= loopIndex || idx != this.size() - 1) &&
                (startIdx < loopIndex || at(curPos + 1) != startIdx);
    }

}
