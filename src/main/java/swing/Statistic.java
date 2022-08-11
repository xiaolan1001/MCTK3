package swing;

import edu.wis.jtlv.env.Env;

public class Statistic {
    Runtime runtime;
    long initBddNodeNum, initBddVarNum;
    long beginTime,beginMemory, beginBddNodeNum, beginBddVarNum;

    public Statistic() {
        runtime = Runtime.getRuntime();
        initBddNodeNum = Env.TRUE().getFactory().getNodeNum();
        initBddVarNum = Env.TRUE().getFactory().varNum();
        beginTimeMemory();
        beginBddInfo();
    }

    public void beginTimeMemory() {
        beginTime = System.currentTimeMillis();
        runtime.gc();
        beginMemory =runtime.freeMemory();// 开始时的剩余内存
    }

    public void beginBddInfo() {// BDD and Var of building model
        beginBddNodeNum =Env.TRUE().getFactory().getNodeNum();
        beginBddVarNum =Env.TRUE().getFactory().varNum();
    }

    public double getUsedTime() { // seconds
        return ((System.currentTimeMillis() - beginTime) / 1000.0);
    }

    public double getUsedMemory() {// KB -剩余内存 现在
        return (beginMemory-runtime.freeMemory()) / 1024.0;
    }

    public long getUsedBddNodeNum() {
        return (Env.TRUE().getFactory().getNodeNum() - beginBddNodeNum + initBddNodeNum);
    }

    public long getUsedBddVarNum() {
        //System.out.println("endVar------" + Env.TRUE().getFactory().varNum());
        return (Env.TRUE().getFactory().varNum()- beginBddVarNum + initBddVarNum);
    }

    public void beginStatistic(boolean beginTimeMemory, boolean beginBddInfo){
        String s="";
        if(beginTimeMemory) beginTimeMemory();
        if(beginBddInfo) beginBddInfo();
    }

    public String getUsedInfo(boolean showTime, boolean showMemory, boolean showBddVarNum, boolean showBddNodeNum){
        String s="";
        if(showTime) s+="Time used: "+getUsedTime()+"s\n";
        if(showMemory) s+="Memory used: "+getUsedMemory()+"KB\n";
        if(showBddVarNum) s+="Number of BDD variables used: "+getUsedBddVarNum()+"\n";
        if(showBddNodeNum) s+="Number of BDD nodes used: "+getUsedBddNodeNum()+"\n";
        return s;
    }
}
