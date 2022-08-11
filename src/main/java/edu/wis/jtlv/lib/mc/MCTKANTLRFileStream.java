package edu.wis.jtlv.lib.mc;

import edu.wis.jtlv.env.Env;
import org.antlr.runtime.ANTLRFileStream;

import java.io.IOException;
import java.util.Vector;

public class MCTKANTLRFileStream extends ANTLRFileStream{
    //public String dataStr;
    public MCTKANTLRFileStream(String s) throws IOException {
        super(s);
    }

    public String removeSpecs(){
        Vector<String[]> moduleSpecAnns=new Vector<String[]>();
        String dataStr=new String(this.data);
        Env.seperateSpecsFromSMVfile(dataStr, moduleSpecAnns);
        if(dataStr.length()!=moduleSpecAnns.get(0)[0].length()) {
            this.data = moduleSpecAnns.get(0)[0].toCharArray();
            this.n = this.data.length;
            this.reset();
        }
        return moduleSpecAnns.get(0)[0];
    }

    public String removeComments() {
        String dataStr = new String(data);
        int oldLength=dataStr.length();

        // delete all comments
        String newStr = Env.removeCommentsFromSMVfile(dataStr);

        if(newStr.length()!=oldLength) {
            this.data = newStr.toCharArray();
            this.n = this.data.length;
            this.reset();
        }
        return newStr;
    }
}
