package edu.wis.jtlv.env.spec;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.lib.mc.RTCDLs.RTCDLs_ModelCheckAlg;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;

/**
 * <p>
 * Specification expression.
 * </p>
 * 
 * @version {@value edu.wis.jtlv.env.Env#version}
 * @author yaniv sa'ar.
 * 
 */
public class SpecExp implements Spec {
	private Operator theOp;
	private Spec[] elements;
	private InternalSpecLanguage language=InternalSpecLanguage.UNDEF;

	/**
	 * <p>
	 * A general purpose constructor.
	 * </p>
	 * 
	 * @param op
	 *            The operator.
	 * @param el
	 *            An array of children specification
	 */
	public SpecExp(Operator op, Spec[] el) throws SpecException {
		if(op == Operator.CAN_ENFORCE || op == Operator.CANNOT_AVOID) {
			if(el.length<1)
				throw new SpecException("Cannot instantiate operator " + op + " without operand.");
		} else if (op.numOfOperands() != el.length)
			throw new SpecException("Cannot instantiate operator " + op
					+ " with " + el.length + " operands.");
		this.theOp = op;
		this.elements = el;
	}

	/**
	 * <p>
	 * Constructor for an unary specification.
	 * </p>
	 * 
	 * @param op
	 *            The operator.
	 * @param e1
	 *            The sub specification.
	 */
	public SpecExp(Operator op, Spec e1) {
		this.theOp = op;
		this.elements = new Spec[] { e1 };
	}

	/**
	 * <p>
	 * Constructor for a binary specification.
	 * </p>
	 * 
	 * @param op
	 *            The operator.
	 * @param e1
	 *            The first sub specification.
	 * @param e2
	 *            The second sub specification.
	 */
	public SpecExp(Operator op, Spec e1, Spec e2) {
		this.theOp = op;
		this.elements = new Spec[] { e1, e2 };
	}

	/**
	 * <p>
	 * Constructor for a triplet specification.
	 * </p>
	 * 
	 * @param op
	 *            The operator.
	 * @param e1
	 *            The first sub specification.
	 * @param e2
	 *            The second sub specification.
	 * @param e3
	 *            The third sub specification.
	 */
	public SpecExp(Operator op, Spec e1, Spec e2, Spec e3) {
		this.theOp = op;
		this.elements = new Spec[] { e1, e2, e3  };
	}

	/**
	 * <p>
	 * The operator representing this node.
	 * </p>
	 * 
	 * @return An operator representing this node.
	 */
	public Operator getOperator() {
		return theOp;
	};

	/**
	 * <p>
	 * Get the children specification of this node.
	 * <p>
	 * 
	 * @return The children specification.
	 */
	public Spec[] getChildren() {
		return this.elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isCTLSpec()
	 */
	public boolean isCTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isCTLSpec())
				return false;
		// checking that I'm prop or CTL
		return this.getOperator().isPropOp()
				| this.getOperator().isCTLOp();
	}

	public boolean isCTLKSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isCTLKSpec())
				return false;
		// checking that I'm prop or CTL
		return this.getOperator().isPropOp()
				| this.getOperator().isCTLOp()
				| this.getOperator().isEpistemicOp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isRTCTLSpec()
	 */
	public boolean isRTCTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isRTCTLSpec())
				return false;
		// checking that I'm prop, CTL or RealTimeCTL
		return this.getOperator().isPropOp()
				| this.getOperator().isCTLOp()
				| this.getOperator().isRTCTLOp();
	}

	public boolean isRTCTLKSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isRTCTLKSpec())
				return false;
		// checking that I'm prop, CTL or RealTimeCTL
		return this.getOperator().isPropOp()
				| this.getOperator().isCTLOp()
				| this.getOperator().isRTCTLOp()
				| this.getOperator().isEpistemicOp();
	}

	/*
 * (non-Javadoc)
 *
 * @see edu.wis.jtlv.env.spec.Spec#isRTLTLSpec()
 */
	public boolean isRTLTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isRTLTLSpec())
				return false;
		// checking that I'm prop, LTL or RealTimeLTL
		return this.getOperator().isPropOp()
				| this.getOperator().isLTLOp()
				| this.getOperator().isRTLTLOp();
	}

	@Override
	public boolean isRTLTLKSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isRTLTLKSpec())
				return false;
		// checking that I'm prop, LTL, RealTimeLTL or epistemic logic
		return this.getOperator().isPropOp()
				| this.getOperator().isLTLOp()
				| this.getOperator().isRTLTLOp()
				| this.getOperator().isEpistemicOp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isLTLSpec()
	 */
	public boolean isLTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isLTLSpec())
				return false;
		// checking that I'm prop, FutureLTL or PastLTL
		return this.getOperator().isPropOp()
				| this.getOperator().isLTLOp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isFutureLTLSpec()
	 */
	public boolean isFutureLTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isFutureLTLSpec())
				return false;
		// checking that I'm prop or FutureLTL
		return this.getOperator().isPropOp()
				| this.getOperator().isFutureLTLOp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isPastLTLSpec()
	 */
	public boolean isPastLTLSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isPastLTLSpec())
				return false;
		// checking that I'm prop or pastLTL
		return this.getOperator().isPropOp()
				| this.getOperator().isPastLTLOp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isCTLStarSpec()
	 */
	public boolean isCTLStarSpec() {
		// this is a bit redundant... it suppose to always return true...
		// return true;
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isCTLStarSpec())
				return false;
		// checking that I'm prop, LTL, CTL or RealTimeCTL
		return this.getOperator().isPropOp()
				| this.getOperator().isLTLOp()
				| this.getOperator().isRTLTLOp()
				| this.getOperator().isCTLsPathOp();
	}

	public boolean isATLsKSpec() { // RTATL*K
		// this is a bit redundant... it suppose to always return true...
		// return true;
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isATLsKSpec())
				return false;
		Operator op = this.getOperator();
		return op.isPropOp()
				| op.isLTLOp()
				| op.isRTLTLOp()
				| op.isCTLsPathOp()
				| op.isATLsPathOp()
				| op.isEpistemicOp();
	}

	// return true if this is a state formula
	// return false if there exists temporal operators that are not restricted by path quantifiers
	public boolean isStateSpec() {
		Operator op = this.getOperator();
		if(op.isPropOp()){
			for (Spec s : this.getChildren())
				if(!s.isStateSpec()) return false;
			return true;
		}else if (
				op.isLTLOp() || op.isRTLTLOp() ||
				op.isLDLSereOp() || op.isLDLPathOp()
			) // temporal operators
			return false;
		else { // other operators, including path quantifiers and epistemic modalities
			return true;
		}
	}

	@Override
	public InternalSpecLanguage getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(InternalSpecLanguage language) {
		this.language = language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#isPropSpec()
	 */
	public boolean isPropSpec() {
		// checking that all children are prop.
		for (Spec s : this.getChildren())
			if (!s.isPropSpec())
				return false;
		// checking that I'm prop
		return this.getOperator().isPropOp();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see edu.wis.jtlv.env.spec.Spec#isCTLStarSpec()
	 */
	public boolean isLDLSpec(StringBuilder syntaxMsg) throws SpecException {
		Operator op = this.getOperator();
		Spec[] ch = this.getChildren();
		boolean b=false;

		if(this.isPropSpec()){
			syntaxMsg.delete(0,syntaxMsg.length());
			return true;
		}else if(op.isPropOp()){
			b=ch[0].isLDLSpec(syntaxMsg);
			if(!b) return false;
			else if(op.numOfOperands()>1){
				return ch[1].isLDLSpec(syntaxMsg);
			}else{
				syntaxMsg.delete(0,syntaxMsg.length());
				return true;
			}
		}else if(op==Operator.LDL_SERE_IMP || op==Operator.LDL_SERE_SAT){
			b=ch[0].isSereSpec(syntaxMsg);
			if(!b) return false;
			return ch[1].isLDLSpec(syntaxMsg);
		}else{
			syntaxMsg.replace(0,syntaxMsg.length(), RTCDLs_ModelCheckAlg.simplifySpecString(this,false) + " is not LDL sub-formula.");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * syntaxMsg -- the outputted message to show why this spec is NOT SERE formula
	 * @see edu.wis.jtlv.env.spec.Spec#isCTLStarSpec()
	 */
	public boolean isSereSpec(StringBuilder syntaxMsg) throws SpecException {
		Operator op = this.getOperator();
		Spec[] ch = this.getChildren();
		boolean b=false;
		syntaxMsg.delete(0,syntaxMsg.length());

		if(this.isPropSpec()) {
			return true;
		}else if(op==Operator.LDL_TEST) {
			return ch[0].isCDLstarSpec(syntaxMsg);
		}else if(op==Operator.LDL_OR || op==Operator.LDL_AND || op==Operator.LDL_CONC) {
			StringBuilder s0=new StringBuilder(""),s1=new StringBuilder("");
			b = ch[0].isSereSpec(s0) && ch[1].isSereSpec(s1);
			if(!b){
				if(!s0.equals(""))
					syntaxMsg.append(s0.toString());
				else
					syntaxMsg.append(s1.toString());
				return false;
			}else
				return true;
		}else if(op==Operator.LDL_REPEAT)
			return ch[0].isSereSpec(syntaxMsg);
		else if(op==Operator.LDL_BOUNDED_REPEAT)
			return ch[0].isSereSpec(syntaxMsg);
		else {
			syntaxMsg.append(RTCDLs_ModelCheckAlg.simplifySpecString(this,false) + " is not SERE sub-formula.");
			return false;
		}
	}

	@Override
	public boolean isCDLstarSpec(StringBuilder syntaxMsg) throws SpecException { // a CDL* formula is a STATE formula
		Operator op = this.getOperator();
		Spec[] ch = this.getChildren();
		boolean b=false;
		syntaxMsg.delete(0,syntaxMsg.length());

		if(this.isStateSpec()){ // state formula
			return true;
		}else if(op.isPropOp()){
			b=ch[0].isCDLstarSpec(syntaxMsg);
			if(!b)
				return false;
			else if(op.numOfOperands()>1)
				return ch[1].isCDLstarSpec(syntaxMsg);
			else
				return true;
		}else if(op.isLTLOp() || op.isRTLTLOp()) { // LTL
			return true;
		}else if(op==Operator.LDL_SERE_IMP || op==Operator.LDL_SERE_SAT){ // <r>f or [r]f
			b=ch[0].isSereSpec(syntaxMsg);
			if(!b) return false;
			else return ch[1].isCDLstarSpec(syntaxMsg);
		}else{
			syntaxMsg.append(RTCDLs_ModelCheckAlg.simplifySpecString(this,false) + " is not CDL* sub-formula.");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#hasTemporalOperators()
	 */
	public boolean hasTemporalOperators() {
		// if one of my elements is temporal.
		for (Spec s : this.getChildren())
			if (s.hasTemporalOperators())
				return true;
		// or I'm temporal.
		return this.theOp.isTemporalOp();
	}


	public boolean hasEpistemicOperators() {
		// if one of my elements is epistemic.
		for (Spec s : this.getChildren())
			if (s.hasEpistemicOperators())
				return true;
		// or I'm epistemic.
		return this.theOp.isEpistemicOp();
	}

	public boolean hasObsEpistemicOperators() {
		// if one of my elements is syn epistemic.
		for (Spec s : this.getChildren())
			if (s.hasObsEpistemicOperators())
				return true;
		// or I'm syn epistemic.
		return this.theOp.isObsEpistemicOp();
	}

	public boolean hasSynEpistemicOperators() {
		// if one of my elements is syn epistemic.
		for (Spec s : this.getChildren())
			if (s.hasSynEpistemicOperators())
				return true;
		// or I'm syn epistemic.
		return this.theOp.isSynEpistemicOp();
	}

	@Override
	public boolean hasPathOperators() {
		return hasCTLsPathOperators() | hasATLsPathOperators();
	}

	@Override
	public boolean hasCTLOperators() {
		for (Spec s : this.getChildren())
			if (s.hasCTLOperators())
				return true;
		return this.theOp.isCTLOp()|this.theOp.isRTCTLOp();
	}

	@Override
	public boolean hasCTLsPathOperators() {
		for (Spec s : this.getChildren())
			if (s.hasCTLsPathOperators())
				return true;
		return this.theOp.isCTLsPathOp();
	}
/*
	@Override
	public boolean hasPathOperatorE() {
		for (Spec s : this.getChildren())
			if (s.hasPathOperatorE())
				return true;
		return this.theOp==Operator.EE;
	}
	@Override
	public boolean hasPathOperatorA() {
		for (Spec s : this.getChildren())
			if (s.hasPathOperatorA())
				return true;
		return this.theOp==Operator.AA;
	}
*/
	@Override
	public boolean hasLTLOperators() {
		for (Spec s : this.getChildren())
			if (s.hasLTLOperators())
				return true;
		return this.theOp.isLTLOp()|this.theOp.isRTLTLOp();
	}
	@Override
	public boolean hasATLsPathOperators() {
		for (Spec s : this.getChildren())
			if (s.hasATLsPathOperators())
				return true;
		return this.theOp.isATLsPathOp();
	}

	@Override
	public String toStringBracketed(String lBracket, String rBracket) {
		String s = this.toString();
		if( (s.startsWith("(") && s.endsWith(")")) ||
				(s.startsWith("[") && s.endsWith("]")) ||
				(s.startsWith("{") && s.endsWith("}")) ||
				(s.startsWith("<") && s.endsWith(">"))
		) // spec has outermost bracket
			return s;
		else // spec has NOT outermost bracket
			return lBracket+s+rBracket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String toString() {
		if(this.isPropSpec()){
			try {
				return this.toBDD().toString();
			} catch (SpecException e) {
				e.printStackTrace();
			}
		}

		Operator op = this.getOperator();
		Spec[] ch = this.getChildren();

		// special cases
		if (op == Operator.AU)
			return "A[" + ch[0] + " U " + ch[1] + "]";
		if (op == Operator.EU)
			return "E[" + ch[0] + " U " + ch[1] + "]";
		if (op == Operator.ABF)
			return "ABF[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.EBF)
			return "EBF[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.ABG)
			return "ABG[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.EBG)
			return "EBG[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.ABU)
			return "A[" + ch[0] + " BU " + ch[1] + " " + ch[2] + "]";
		if (op == Operator.EBU)
			return "E[" + ch[0] + " BU " + ch[1] + " " + ch[2] + "]";

		//special cases of path quantifiers of ATL* and CTL*
		if (op == Operator.EE)
			return "E[" + ch[0] + "]";
		if (op == Operator.AA)
			return "A[" + ch[0] + "]";
		if (op == Operator.CAN_ENFORCE) {
			String agt_list = "";
			if(ch.length>1) {
				agt_list+=ch[0];
				for (int i = 1; i < ch.length-1; i++)
					agt_list+=","+ch[i];
			}
			return "<" + agt_list + "> " + ch[ch.length - 1];
		}
		if (op == Operator.CANNOT_AVOID) {
			String agt_list = "";
			if(ch.length>1) {
				agt_list+=ch[0];
				for (int i = 1; i < ch.length-1; i++)
					agt_list+=","+ch[i];
			}
			return "[" + agt_list + "] " + ch[ch.length - 1];
		}

		// epistemic
		if (op == Operator.KNOW)
			return "(" + ch[0] + " KNOW " + ch[1] + ")";
		if (op == Operator.NKNOW)
			return "(" + ch[0] + " NKNOW " + ch[1] + ")";
		if (op == Operator.SKNOW)
			return "(" + ch[0] + " SKNOW " + ch[1] + ")";
		if (op == Operator.NSKNOW)
			return "(" + ch[0] + " NSKNOW " + ch[1] + ")";

		//special cases of RTLTL
		if (op==Operator.B_FINALLY || op==Operator.B_GLOBALLY) {
			String o="";
			if(op==Operator.B_FINALLY) o="BF"; else o="BG";
			boolean b=false; if(ch[1] instanceof SpecExp){ SpecExp se=(SpecExp)ch[1]; if(!se.getOperator().isUnary()) b=true;}
			return o+" " + ch[0] + " " + (b?"(":"") + ch[1] + (b?")":"");
		}
		if (op == Operator.B_UNTIL || op == Operator.B_RELEASES) {
			String o="";
			if(op == Operator.B_UNTIL) o="BU"; else o="BR";

			boolean b1=false; if(ch[0] instanceof SpecExp){ SpecExp se=(SpecExp)ch[0]; if(!se.getOperator().isUnary()) b1=true;}
			boolean b2=false; if(ch[2] instanceof SpecExp){ SpecExp se=(SpecExp)ch[2]; if(!se.getOperator().isUnary()) b2=true;}
			return (b1?"(":"") + ch[0] + (b1?") ":" ") + o + " " + ch[1] + (b2?" (":" ") + ch[2] + (b2?")":"");
		}

		// LDL
		String ch0Str=ch[0].toStringBracketed("(",")");
		String ch1Str="";
		if(ch.length>1) ch1Str=ch[1].toStringBracketed("(",")");

		if (op == Operator.LDL_TEST)
			return ch0Str + "?";
		if (op == Operator.LDL_AND)
			return ch0Str + "&&" + ch1Str;
		if (op == Operator.LDL_OR)
			return ch0Str + "||" + ch1Str;
		if (op == Operator.LDL_CONC)
			return ch0Str + "," + ch1Str;
		if (op == Operator.LDL_REPEAT)
			return ch0Str + "[*]";
		if (op == Operator.LDL_BOUNDED_REPEAT)
			return ch0Str + "[*" + ch[1] + "]";
		if (op == Operator.LDL_SERE_SAT)
			return ch0Str + ":-" + ch1Str;
		if (op == Operator.LDL_SERE_IMP)
			return ch0Str + ":=" + ch1Str;
/*
		if (op == Operator.LDL_TEST)
			return "(" + ch[0] + ")?";
		if (op == Operator.LDL_AND)
			return ch[0] + "&&" + ch[1];
		if (op == Operator.LDL_OR)
			return ch[0] + "||" + ch[1];
		if (op == Operator.LDL_CONC)
			return ch[0] + "," + ch[1];
		if (op == Operator.LDL_REPEAT)
			return "(" + ch[0] + ")[*]";
		if (op == Operator.LDL_BOUNDED_REPEAT)
			return "(" + ch[0] + ")[*" + ch[1] + "]";
		if (op == Operator.LDL_SOMEPATH)
			return "(" + ch[0] + ")#(" + ch[1] + ")";
		if (op == Operator.LDL_ALLPATH)
			return "(" + ch[0] + ")@(" + ch[1] + ")";
*/

		// simple unary
		if (op.isUnary()) {
			String o="";
			switch (op){
				case NOT: o="!"; break;
				case FINALLY: o="F"; break;
				case GLOBALLY: o="G"; break;
				case HISTORICALLY: o="H"; break;
				case NEXT: o="X"; break;
				case NOT_PREV_NOT: o="Z"; break;
				case ONCE: o="O"; break;
				case PREV: o="Y"; break;
				default: o="";
			}

			boolean b=false; if(ch[0] instanceof SpecExp){ SpecExp se=(SpecExp)ch[0]; if(!se.getOperator().isUnary()) b=true;}
			return o + (b?"(":" ") + ch[0] + (b?")":"");
		}
		// simple binary AND, OR, XOR, XNOR, IFF,
		//			IMPLIES, RELEASES, SINCE, TRIGGERED, UNTIL, ABF, ABG, EBF, EBG, AU,
		//			EU, B_FINALLY, B_GLOBALLY, KNOW, NKNOW, SKNOW, NSKNOW
		if (op.isBinary()) {
			String o="";
			switch (op){
				case AND: o="&"; break;
				case OR: o="|"; break;
				case XOR: o="xor"; break;
				case XNOR: o="xnor"; break;
				case IFF: o="<->"; break;
				case IMPLIES: o="->"; break;
				case RELEASES: o="R"; break;
				case SINCE: o="S"; break;
				case TRIGGERED: o="T"; break;
				case UNTIL: o="U"; break;
				default: o="";
			}
			//return "(" + ch[0] + " " + o + " " + ch[1] + ")";

			boolean b1=false; if(ch[0] instanceof SpecExp){ SpecExp se=(SpecExp)ch[0]; if(!se.getOperator().isUnary()) b1=true;}
			boolean b2=false; if(ch[1] instanceof SpecExp){ SpecExp se=(SpecExp)ch[1]; if(!se.getOperator().isUnary()) b2=true;}
			return (b1?"(":"") + ch[0] + (b1?") ":" ") + o + (b2?" (":" ") + ch[1] + (b2?")":"");
		}

		return "[!#$! Cannot Identify Expression]";
	}


/*
	public String toString() {
		Operator op = this.getOperator();
		Spec[] ch = this.getChildren();

		// special cases
		if (op == Operator.AU)
			return "A[" + ch[0] + " UNTIL " + ch[1] + "]";
		if (op == Operator.EU)
			return "E[" + ch[0] + " UNTIL " + ch[1] + "]";
		if (op == Operator.ABF)
			return "ABF[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.EBF)
			return "EBF[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.ABG)
			return "ABG[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.EBG)
			return "EBG[" + ch[0] + " " + ch[1] + "]";
		if (op == Operator.ABU)
			return "A[" + ch[0] + " BUNTIL " + ch[1] + " " + ch[2] + "]";
		if (op == Operator.EBU)
			return "E[" + ch[0] + " BUNTIL " + ch[1] + " " + ch[2] + "]";

		//special cases of path quantifiers of ATL* and CTL*
		if (op == Operator.EE)
			return "E[" + ch[0] + "]";
		if (op == Operator.AA)
			return "A[" + ch[0] + "]";
		if (op == Operator.CAN_ENFORCE) {
			String agt_list = "";
			if(ch.length>1) {
				agt_list+=ch[0];
				for (int i = 1; i < ch.length-1; i++)
					agt_list+=","+ch[i];
			}
			return "<" + agt_list + "> " + ch[ch.length - 1];
		}
		if (op == Operator.CANNOT_AVOID) {
			String agt_list = "";
			if(ch.length>1) {
				agt_list+=ch[0];
				for (int i = 1; i < ch.length-1; i++)
					agt_list+=","+ch[i];
			}
			return "[" + agt_list + "] " + ch[ch.length - 1];
		}

		// epistemic
		if (op == Operator.KNOW)
			return "(" + ch[0] + " KNOW " + ch[1] + ")";
		if (op == Operator.NKNOW)
			return "(" + ch[0] + " NKNOW " + ch[1] + ")";
		if (op == Operator.SKNOW)
			return "(" + ch[0] + " SKNOW " + ch[1] + ")";
		if (op == Operator.NSKNOW)
			return "(" + ch[0] + " NSKNOW " + ch[1] + ")";

		//special cases of RTLTL
		if (op == Operator.B_FINALLY)
//			return "(BF " + ch[0] + " " + ((SpecRange)ch[0]).getOriginSpec() + ")";
			return "(BF " + ch[0] + " " + ch[1] + ")";
		if (op == Operator.B_GLOBALLY)
//			return "(BG " + ch[0] + " " + ((SpecRange)ch[0]).getOriginSpec() + ")";
			return "(BG " + ch[0] + " " + ch[1] + ")";
		if (op == Operator.B_UNTIL)
//			return "("+((SpecRange)ch[0]).getOriginLeftSpec()+" BU " +ch[0]+ " " + ((SpecRange)ch[0]).getOriginSpec() + ")";
			return "(" + ch[0] + " BUNTIL " + ch[1] + " " + ch[2] + ")";
		if (op == Operator.B_RELEASES)
			return "(" + ch[0] + " BRELEASE " + ch[1] + " " + ch[2] + ")";

		// simple unary
		if (op.isUnary())
			return "(" + op + " " + ch[0] + ")";  // original code
//			return op + " " + ch[0];  //added by LXY
		// simple binary
		if (op.isBinary())
			return "(" + ch[0] + " " + op + " " + ch[1] + ")";  //original code
//			return ch[0] + " " + op + " " + ch[1];  //added by LXY


		return "[!#$! Cannot Identify Expression]";
	}
*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof SpecExp))
			return false;
		SpecExp otherExp = (SpecExp) other;
		if (this.getOperator() != otherExp.getOperator())
			return false;

		Spec[] this_children = this.getChildren();
		Spec[] other_children = otherExp.getChildren();
		if (this_children.length != other_children.length)
			return false;

		for (int i = 0; i < this_children.length; i++)
			if (!this_children[i].equals(other_children[i]))
				return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#releventVars()
	 */
	public BDDVarSet releventVars() {
		BDDVarSet res = Env.getEmptySet();
		for (Spec s : this.getChildren()) {
			res = res.id().union(s.releventVars());
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.wis.jtlv.env.spec.Spec#toBDD()
	 */
	public BDD toBDD() throws SpecException {
		if (!this.getOperator().isPropOp())
			throw new SpecException("Cannot convert temporal expression into"
					+ " BDD in specification: " + this.toString());
		// else building the BDD.
		Spec[] child = this.getChildren();
		Operator op = this.getOperator();

		if (op == Operator.NOT)
			return child[0].toBDD().not();
		if (op == Operator.AND)
			return child[0].toBDD().and(child[1].toBDD());
		if (op == Operator.OR)
			return child[0].toBDD().or(child[1].toBDD());
		if (op == Operator.XOR)
			return child[0].toBDD().xor(child[1].toBDD());
		if (op == Operator.XNOR)
			return child[0].toBDD().xor(child[1].toBDD()).not();
		if (op == Operator.IFF)
			return child[0].toBDD().biimp(child[1].toBDD());
		if (op == Operator.IMPLIES)
			return child[0].toBDD().imp(child[1].toBDD());

		throw new SpecException("Cannot evaluate operator " + op
				+ " in specification: " + this.toString());
	}
}
