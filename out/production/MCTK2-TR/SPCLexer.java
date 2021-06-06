// $ANTLR 3.0.1 /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g 2021-05-01 10:56:19

package edu.wis.jtlv.env.core.spec;
import edu.wis.jtlv.env.Env;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class SPCLexer extends Lexer {
    public static final int TOK_PLUS=55;
    public static final int TOK_RCB=50;
    public static final int CTL_KNOW_T=21;
    public static final int TOK_UNARY_MINUS_T=14;
    public static final int TOK_XOR=38;
    public static final int ARRAY_INDEX_T=13;
    public static final int TOK_ABG=79;
    public static final int TOK_ABF=77;
    public static final int TOK_TIMES=57;
    public static final int AGENT_SET_LIST_T=29;
    public static final int TOK_EBG=78;
    public static final int TOK_EBF=76;
    public static final int Tokens=116;
    public static final int TOK_LP=61;
    public static final int TOK_LT=43;
    public static final int CTLS_KNOW_T=25;
    public static final int TOK_COLON=104;
    public static final int JTOK_MULTI_COMMENT=114;
    public static final int PURE_RE_T=18;
    public static final int TOK_SETIN=47;
    public static final int SPEC_LIST_T=5;
    public static final int TOK_BRELEASE=99;
    public static final int TOK_EQUAL=41;
    public static final int TOK_LB=81;
    public static final int TOK_TRUEEXP=109;
    public static final int TOK_BUNTIL=85;
    public static final int TOK_LE=45;
    public static final int TOK_OP_BGLOBALLY=101;
    public static final int TOK_UNTIL=82;
    public static final int TOK_DOT=102;
    public static final int SUBRANGE_T=6;
    public static final int TOK_RTCTL_STAR_SPEC=33;
    public static final int CASE_ELEMENT_EXPR_T=11;
    public static final int TOK_CTL_SKNOW_T=24;
    public static final int TOK_ATOM=88;
    public static final int TOK_XNOR=39;
    public static final int TOK_AG=75;
    public static final int TOK_AF=73;
    public static final int TOK_WAWRITE=69;
    public static final int TOK_RB=83;
    public static final int TOK_OP_ONCE=98;
    public static final int TOK_CTL_KNOW_T=22;
    public static final int TOK_SEMI=105;
    public static final int TOK_OP_BFINALLY=100;
    public static final int TOK_AA=80;
    public static final int TOK_NUMBER_FRAC=112;
    public static final int TOK_MINUS=56;
    public static final int TOK_NUMBER_WORD=106;
    public static final int PURE_RTCTL_STAR_T=28;
    public static final int TOK_IMPLIES=35;
    public static final int TOK_KNOW=86;
    public static final int TOK_AX=71;
    public static final int JTOK_WS=113;
    public static final int TOK_OP_GLOBALLY=95;
    public static final int TOK_FALSEEXP=108;
    public static final int TOK_RP=62;
    public static final int VALUE_T=7;
    public static final int TOK_RSHIFT=53;
    public static final int TOK_SINCE=89;
    public static final int TOK_WAREAD=68;
    public static final int TOK_OR=37;
    public static final int TOK_OP_NOTPREVNOT=94;
    public static final int TOK_NOT=60;
    public static final int TOK_OP_PREV=93;
    public static final int BLOCK_T=9;
    public static final int TOK_LSHIFT=52;
    public static final int CTL_SKNOW_T=23;
    public static final int TOK_AND=40;
    public static final int TOK_GT=44;
    public static final int PURE_RTCTLS_T=27;
    public static final int TOK_TRIGGERED=91;
    public static final int TOK_AGENT_NAME_T=26;
    public static final int TOK_CASE=66;
    public static final int TOK_IFF=36;
    public static final int TOK_CTL_STAR_SPEC=110;
    public static final int TOK_GE=46;
    public static final int SET_LIST_EXP_T=8;
    public static final int CASE_LIST_EXPR_T=10;
    public static final int TOK_LDL_SPEC=34;
    public static final int TOK_TWODOTS=107;
    public static final int NOP=4;
    public static final int TOK_COMMA=51;
    public static final int TOK_UNION=48;
    public static final int TOK_NUMBER=103;
    public static final int TOK_LTL_SPEC=32;
    public static final int TOK_ESAC=67;
    public static final int TOK_SKNOW=87;
    public static final int TOK_LCB=49;
    public static final int TOK_DIVIDE=58;
    public static final int TOK_OP_NEXT=92;
    public static final int PURE_CTL_EPISTEMIC_T=20;
    public static final int TOK_EG=74;
    public static final int TOK_EF=72;
    public static final int TOK_RELEASE=90;
    public static final int PURE_CTL_T=15;
    public static final int TOK_NOTEQUAL=42;
    public static final int RTCTLS_PURE_CTL_T=19;
    public static final int BIT_SELECT_T=12;
    public static final int TOK_INVAR_SPEC=30;
    public static final int TOK_EE=84;
    public static final int EOF=-1;
    public static final int TOK_BOOL=63;
    public static final int PURE_LTL_T=16;
    public static final int TOK_OP_FINALLY=97;
    public static final int JTOK_LINE_COMMENT=115;
    public static final int TOK_OP_HISTORICALLY=96;
    public static final int PURE_LDL_T=17;
    public static final int TOK_WORD1=64;
    public static final int TOK_CONCATENATION=59;
    public static final int TOK_NEXT=65;
    public static final int TOK_EX=70;
    public static final int TOK_MOD=54;
    public static final int TOK_WORD=111;
    public static final int TOK_CTL_SPEC=31;

    public String getErrorMessage(RecognitionException e, String[] tokenNames) {
    	String msg = null;
    	if (e instanceof SpecParseException) {
    		msg = e.toString();
    		Env.doError(e, msg);
    	} else {
    		msg = super.getErrorMessage(e, tokenNames);
    		Env.doError(e, msg);
    	}
    	return msg;
    }
    public void emitErrorMessage(String msg) {
    	// System.err.println(msg);
    	// do nothing.
    }

    public SPCLexer() {;} 
    public SPCLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g"; }

    // $ANTLR start TOK_CTL_SPEC
    public final void mTOK_CTL_SPEC() throws RecognitionException {
        try {
            int _type = TOK_CTL_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1980:17: ( 'CTLSPEC' | 'SPEC' )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0=='C') ) {
                alt1=1;
            }
            else if ( (LA1_0=='S') ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1980:1: TOK_CTL_SPEC : ( 'CTLSPEC' | 'SPEC' );", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1980:19: 'CTLSPEC'
                    {
                    match("CTLSPEC"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1980:31: 'SPEC'
                    {
                    match("SPEC"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_CTL_SPEC

    // $ANTLR start TOK_CTL_STAR_SPEC
    public final void mTOK_CTL_STAR_SPEC() throws RecognitionException {
        try {
            int _type = TOK_CTL_STAR_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1981:21: ( 'CTL*SPEC' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1981:23: 'CTL*SPEC'
            {
            match("CTL*SPEC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_CTL_STAR_SPEC

    // $ANTLR start TOK_LTL_SPEC
    public final void mTOK_LTL_SPEC() throws RecognitionException {
        try {
            int _type = TOK_LTL_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1982:17: ( 'LTLSPEC' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1982:19: 'LTLSPEC'
            {
            match("LTLSPEC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LTL_SPEC

    // $ANTLR start TOK_INVAR_SPEC
    public final void mTOK_INVAR_SPEC() throws RecognitionException {
        try {
            int _type = TOK_INVAR_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1983:19: ( 'INVARSPEC' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1983:21: 'INVARSPEC'
            {
            match("INVARSPEC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_INVAR_SPEC

    // $ANTLR start TOK_RTCTL_STAR_SPEC
    public final void mTOK_RTCTL_STAR_SPEC() throws RecognitionException {
        try {
            int _type = TOK_RTCTL_STAR_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1984:23: ( 'RTCTL*SPEC' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1984:25: 'RTCTL*SPEC'
            {
            match("RTCTL*SPEC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RTCTL_STAR_SPEC

    // $ANTLR start TOK_LDL_SPEC
    public final void mTOK_LDL_SPEC() throws RecognitionException {
        try {
            int _type = TOK_LDL_SPEC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1985:17: ( 'LDLSPEC' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1985:19: 'LDLSPEC'
            {
            match("LDLSPEC"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LDL_SPEC

    // $ANTLR start TOK_EX
    public final void mTOK_EX() throws RecognitionException {
        try {
            int _type = TOK_EX;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1987:13: ( 'EX' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1987:15: 'EX'
            {
            match("EX"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EX

    // $ANTLR start TOK_AX
    public final void mTOK_AX() throws RecognitionException {
        try {
            int _type = TOK_AX;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1988:13: ( 'AX' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1988:15: 'AX'
            {
            match("AX"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_AX

    // $ANTLR start TOK_EF
    public final void mTOK_EF() throws RecognitionException {
        try {
            int _type = TOK_EF;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1989:13: ( 'EF' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1989:15: 'EF'
            {
            match("EF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EF

    // $ANTLR start TOK_AF
    public final void mTOK_AF() throws RecognitionException {
        try {
            int _type = TOK_AF;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1990:13: ( 'AF' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1990:15: 'AF'
            {
            match("AF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_AF

    // $ANTLR start TOK_EG
    public final void mTOK_EG() throws RecognitionException {
        try {
            int _type = TOK_EG;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1991:13: ( 'EG' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1991:15: 'EG'
            {
            match("EG"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EG

    // $ANTLR start TOK_AG
    public final void mTOK_AG() throws RecognitionException {
        try {
            int _type = TOK_AG;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1992:13: ( 'AG' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1992:15: 'AG'
            {
            match("AG"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_AG

    // $ANTLR start TOK_EE
    public final void mTOK_EE() throws RecognitionException {
        try {
            int _type = TOK_EE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1993:13: ( 'E' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1993:15: 'E'
            {
            match('E'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EE

    // $ANTLR start TOK_AA
    public final void mTOK_AA() throws RecognitionException {
        try {
            int _type = TOK_AA;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1994:13: ( 'A' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1994:15: 'A'
            {
            match('A'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_AA

    // $ANTLR start TOK_BUNTIL
    public final void mTOK_BUNTIL() throws RecognitionException {
        try {
            int _type = TOK_BUNTIL;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1995:16: ( 'BU' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1995:18: 'BU'
            {
            match("BU"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_BUNTIL

    // $ANTLR start TOK_EBF
    public final void mTOK_EBF() throws RecognitionException {
        try {
            int _type = TOK_EBF;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1996:14: ( 'EBF' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1996:16: 'EBF'
            {
            match("EBF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EBF

    // $ANTLR start TOK_ABF
    public final void mTOK_ABF() throws RecognitionException {
        try {
            int _type = TOK_ABF;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1997:14: ( 'ABF' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1997:16: 'ABF'
            {
            match("ABF"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_ABF

    // $ANTLR start TOK_EBG
    public final void mTOK_EBG() throws RecognitionException {
        try {
            int _type = TOK_EBG;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1998:14: ( 'EBG' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1998:16: 'EBG'
            {
            match("EBG"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EBG

    // $ANTLR start TOK_ABG
    public final void mTOK_ABG() throws RecognitionException {
        try {
            int _type = TOK_ABG;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1999:14: ( 'ABG' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1999:16: 'ABG'
            {
            match("ABG"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_ABG

    // $ANTLR start TOK_OP_FINALLY
    public final void mTOK_OP_FINALLY() throws RecognitionException {
        try {
            int _type = TOK_OP_FINALLY;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2001:19: ( 'F' | 'FINALLY' | 'EVENTUALLY' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='F') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='I') ) {
                    alt2=2;
                }
                else {
                    alt2=1;}
            }
            else if ( (LA2_0=='E') ) {
                alt2=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2001:1: TOK_OP_FINALLY : ( 'F' | 'FINALLY' | 'EVENTUALLY' );", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2001:21: 'F'
                    {
                    match('F'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2001:27: 'FINALLY'
                    {
                    match("FINALLY"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2001:39: 'EVENTUALLY'
                    {
                    match("EVENTUALLY"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_FINALLY

    // $ANTLR start TOK_OP_ONCE
    public final void mTOK_OP_ONCE() throws RecognitionException {
        try {
            int _type = TOK_OP_ONCE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2002:16: ( 'O' | 'ONCE' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='O') ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1=='N') ) {
                    alt3=2;
                }
                else {
                    alt3=1;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2002:1: TOK_OP_ONCE : ( 'O' | 'ONCE' );", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2002:18: 'O'
                    {
                    match('O'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2002:24: 'ONCE'
                    {
                    match("ONCE"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_ONCE

    // $ANTLR start TOK_OP_GLOBALLY
    public final void mTOK_OP_GLOBALLY() throws RecognitionException {
        try {
            int _type = TOK_OP_GLOBALLY;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2003:20: ( 'G' | 'GLOBALLY' | 'ALWAYS' )
            int alt4=3;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='G') ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1=='L') ) {
                    alt4=2;
                }
                else {
                    alt4=1;}
            }
            else if ( (LA4_0=='A') ) {
                alt4=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2003:1: TOK_OP_GLOBALLY : ( 'G' | 'GLOBALLY' | 'ALWAYS' );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2003:22: 'G'
                    {
                    match('G'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2003:28: 'GLOBALLY'
                    {
                    match("GLOBALLY"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2003:41: 'ALWAYS'
                    {
                    match("ALWAYS"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_GLOBALLY

    // $ANTLR start TOK_OP_HISTORICALLY
    public final void mTOK_OP_HISTORICALLY() throws RecognitionException {
        try {
            int _type = TOK_OP_HISTORICALLY;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2004:23: ( 'H' | 'HISTORICALLY' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='H') ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1=='I') ) {
                    alt5=2;
                }
                else {
                    alt5=1;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2004:1: TOK_OP_HISTORICALLY : ( 'H' | 'HISTORICALLY' );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2004:25: 'H'
                    {
                    match('H'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2004:31: 'HISTORICALLY'
                    {
                    match("HISTORICALLY"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_HISTORICALLY

    // $ANTLR start TOK_OP_NEXT
    public final void mTOK_OP_NEXT() throws RecognitionException {
        try {
            int _type = TOK_OP_NEXT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2005:17: ( 'X' | 'NEXT' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='X') ) {
                alt6=1;
            }
            else if ( (LA6_0=='N') ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2005:1: TOK_OP_NEXT : ( 'X' | 'NEXT' );", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2005:19: 'X'
                    {
                    match('X'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2005:25: 'NEXT'
                    {
                    match("NEXT"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_NEXT

    // $ANTLR start TOK_OP_PREV
    public final void mTOK_OP_PREV() throws RecognitionException {
        try {
            int _type = TOK_OP_PREV;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2006:17: ( 'Y' | 'PREV' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='Y') ) {
                alt7=1;
            }
            else if ( (LA7_0=='P') ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2006:1: TOK_OP_PREV : ( 'Y' | 'PREV' );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2006:19: 'Y'
                    {
                    match('Y'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2006:25: 'PREV'
                    {
                    match("PREV"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_PREV

    // $ANTLR start TOK_UNTIL
    public final void mTOK_UNTIL() throws RecognitionException {
        try {
            int _type = TOK_UNTIL;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2007:15: ( 'Until' | 'U' | 'UNTIL' )
            int alt8=3;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='U') ) {
                switch ( input.LA(2) ) {
                case 'N':
                    {
                    alt8=3;
                    }
                    break;
                case 'n':
                    {
                    alt8=1;
                    }
                    break;
                default:
                    alt8=2;}

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2007:1: TOK_UNTIL : ( 'Until' | 'U' | 'UNTIL' );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2007:17: 'Until'
                    {
                    match("Until"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2007:27: 'U'
                    {
                    match('U'); 

                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2007:33: 'UNTIL'
                    {
                    match("UNTIL"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_UNTIL

    // $ANTLR start TOK_SINCE
    public final void mTOK_SINCE() throws RecognitionException {
        try {
            int _type = TOK_SINCE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2008:15: ( 'Since' | 'S' | 'SINCE' )
            int alt9=3;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='S') ) {
                switch ( input.LA(2) ) {
                case 'I':
                    {
                    alt9=3;
                    }
                    break;
                case 'i':
                    {
                    alt9=1;
                    }
                    break;
                default:
                    alt9=2;}

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2008:1: TOK_SINCE : ( 'Since' | 'S' | 'SINCE' );", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2008:17: 'Since'
                    {
                    match("Since"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2008:27: 'S'
                    {
                    match('S'); 

                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2008:33: 'SINCE'
                    {
                    match("SINCE"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_SINCE

    // $ANTLR start TOK_RELEASE
    public final void mTOK_RELEASE() throws RecognitionException {
        try {
            int _type = TOK_RELEASE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2009:16: ( 'Awaits' | 'R' | 'RELEASE' )
            int alt10=3;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='A') ) {
                alt10=1;
            }
            else if ( (LA10_0=='R') ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2=='E') ) {
                    alt10=3;
                }
                else {
                    alt10=2;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2009:1: TOK_RELEASE : ( 'Awaits' | 'R' | 'RELEASE' );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2009:18: 'Awaits'
                    {
                    match("Awaits"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2009:29: 'R'
                    {
                    match('R'); 

                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2009:35: 'RELEASE'
                    {
                    match("RELEASE"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RELEASE

    // $ANTLR start TOK_TRIGGERED
    public final void mTOK_TRIGGERED() throws RecognitionException {
        try {
            int _type = TOK_TRIGGERED;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2010:18: ( 'Backto' | 'T' | 'TRIGGERED' )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='B') ) {
                alt11=1;
            }
            else if ( (LA11_0=='T') ) {
                int LA11_2 = input.LA(2);

                if ( (LA11_2=='R') ) {
                    alt11=3;
                }
                else {
                    alt11=2;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2010:1: TOK_TRIGGERED : ( 'Backto' | 'T' | 'TRIGGERED' );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2010:20: 'Backto'
                    {
                    match("Backto"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2010:31: 'T'
                    {
                    match('T'); 

                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2010:37: 'TRIGGERED'
                    {
                    match("TRIGGERED"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_TRIGGERED

    // $ANTLR start TOK_OP_NOTPREVNOT
    public final void mTOK_OP_NOTPREVNOT() throws RecognitionException {
        try {
            int _type = TOK_OP_NOTPREVNOT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2011:21: ( 'Z' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2011:23: 'Z'
            {
            match('Z'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_NOTPREVNOT

    // $ANTLR start TOK_OP_BFINALLY
    public final void mTOK_OP_BFINALLY() throws RecognitionException {
        try {
            int _type = TOK_OP_BFINALLY;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2014:20: ( 'BF' | 'BFINALLY' | 'BEVENTUALLY' )
            int alt12=3;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='B') ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1=='E') ) {
                    alt12=3;
                }
                else if ( (LA12_1=='F') ) {
                    int LA12_3 = input.LA(3);

                    if ( (LA12_3=='I') ) {
                        alt12=2;
                    }
                    else {
                        alt12=1;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2014:1: TOK_OP_BFINALLY : ( 'BF' | 'BFINALLY' | 'BEVENTUALLY' );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2014:1: TOK_OP_BFINALLY : ( 'BF' | 'BFINALLY' | 'BEVENTUALLY' );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2014:22: 'BF'
                    {
                    match("BF"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2014:29: 'BFINALLY'
                    {
                    match("BFINALLY"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2014:42: 'BEVENTUALLY'
                    {
                    match("BEVENTUALLY"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_BFINALLY

    // $ANTLR start TOK_OP_BGLOBALLY
    public final void mTOK_OP_BGLOBALLY() throws RecognitionException {
        try {
            int _type = TOK_OP_BGLOBALLY;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2015:20: ( 'BG' | 'BGLOBALLY' | 'BALWAYS' )
            int alt13=3;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='B') ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1=='A') ) {
                    alt13=3;
                }
                else if ( (LA13_1=='G') ) {
                    int LA13_3 = input.LA(3);

                    if ( (LA13_3=='L') ) {
                        alt13=2;
                    }
                    else {
                        alt13=1;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2015:1: TOK_OP_BGLOBALLY : ( 'BG' | 'BGLOBALLY' | 'BALWAYS' );", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2015:1: TOK_OP_BGLOBALLY : ( 'BG' | 'BGLOBALLY' | 'BALWAYS' );", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2015:22: 'BG'
                    {
                    match("BG"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2015:29: 'BGLOBALLY'
                    {
                    match("BGLOBALLY"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2015:43: 'BALWAYS'
                    {
                    match("BALWAYS"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OP_BGLOBALLY

    // $ANTLR start TOK_BRELEASE
    public final void mTOK_BRELEASE() throws RecognitionException {
        try {
            int _type = TOK_BRELEASE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2016:17: ( 'BR' | 'BRELEASE' )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='B') ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1=='R') ) {
                    int LA14_2 = input.LA(3);

                    if ( (LA14_2=='E') ) {
                        alt14=2;
                    }
                    else {
                        alt14=1;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2016:1: TOK_BRELEASE : ( 'BR' | 'BRELEASE' );", 14, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2016:1: TOK_BRELEASE : ( 'BR' | 'BRELEASE' );", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2016:19: 'BR'
                    {
                    match("BR"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2016:26: 'BRELEASE'
                    {
                    match("BRELEASE"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_BRELEASE

    // $ANTLR start TOK_KNOW
    public final void mTOK_KNOW() throws RecognitionException {
        try {
            int _type = TOK_KNOW;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2020:13: ( 'K' | 'KNOW' | 'Know' )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='K') ) {
                switch ( input.LA(2) ) {
                case 'N':
                    {
                    alt15=2;
                    }
                    break;
                case 'n':
                    {
                    alt15=3;
                    }
                    break;
                default:
                    alt15=1;}

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2020:1: TOK_KNOW : ( 'K' | 'KNOW' | 'Know' );", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2020:15: 'K'
                    {
                    match('K'); 

                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2020:21: 'KNOW'
                    {
                    match("KNOW"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2020:30: 'Know'
                    {
                    match("Know"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_KNOW

    // $ANTLR start TOK_SKNOW
    public final void mTOK_SKNOW() throws RecognitionException {
        try {
            int _type = TOK_SKNOW;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2021:14: ( 'SK' | 'SKNOW' | 'Sknow' )
            int alt16=3;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='S') ) {
                int LA16_1 = input.LA(2);

                if ( (LA16_1=='K') ) {
                    int LA16_2 = input.LA(3);

                    if ( (LA16_2=='N') ) {
                        alt16=2;
                    }
                    else {
                        alt16=1;}
                }
                else if ( (LA16_1=='k') ) {
                    alt16=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("2021:1: TOK_SKNOW : ( 'SK' | 'SKNOW' | 'Sknow' );", 16, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2021:1: TOK_SKNOW : ( 'SK' | 'SKNOW' | 'Sknow' );", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2021:16: 'SK'
                    {
                    match("SK"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2021:23: 'SKNOW'
                    {
                    match("SKNOW"); 


                    }
                    break;
                case 3 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2021:33: 'Sknow'
                    {
                    match("Sknow"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_SKNOW

    // $ANTLR start TOK_LP
    public final void mTOK_LP() throws RecognitionException {
        try {
            int _type = TOK_LP;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2026:13: ( '(' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2026:15: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LP

    // $ANTLR start TOK_RP
    public final void mTOK_RP() throws RecognitionException {
        try {
            int _type = TOK_RP;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2027:13: ( ')' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2027:15: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RP

    // $ANTLR start TOK_LB
    public final void mTOK_LB() throws RecognitionException {
        try {
            int _type = TOK_LB;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2028:13: ( '[' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2028:15: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LB

    // $ANTLR start TOK_RB
    public final void mTOK_RB() throws RecognitionException {
        try {
            int _type = TOK_RB;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2029:13: ( ']' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2029:15: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RB

    // $ANTLR start TOK_LCB
    public final void mTOK_LCB() throws RecognitionException {
        try {
            int _type = TOK_LCB;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2030:14: ( '{' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2030:16: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LCB

    // $ANTLR start TOK_RCB
    public final void mTOK_RCB() throws RecognitionException {
        try {
            int _type = TOK_RCB;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2031:14: ( '}' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2031:16: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RCB

    // $ANTLR start TOK_FALSEEXP
    public final void mTOK_FALSEEXP() throws RecognitionException {
        try {
            int _type = TOK_FALSEEXP;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2032:17: ( 'FALSE' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2032:19: 'FALSE'
            {
            match("FALSE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_FALSEEXP

    // $ANTLR start TOK_TRUEEXP
    public final void mTOK_TRUEEXP() throws RecognitionException {
        try {
            int _type = TOK_TRUEEXP;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2033:17: ( 'TRUE' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2033:19: 'TRUE'
            {
            match("TRUE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_TRUEEXP

    // $ANTLR start TOK_WORD1
    public final void mTOK_WORD1() throws RecognitionException {
        try {
            int _type = TOK_WORD1;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2037:15: ( 'word1' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2037:17: 'word1'
            {
            match("word1"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_WORD1

    // $ANTLR start TOK_WORD
    public final void mTOK_WORD() throws RecognitionException {
        try {
            int _type = TOK_WORD;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2038:14: ( 'word' | 'Word' )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='w') ) {
                alt17=1;
            }
            else if ( (LA17_0=='W') ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2038:1: TOK_WORD : ( 'word' | 'Word' );", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2038:16: 'word'
                    {
                    match("word"); 


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2038:25: 'Word'
                    {
                    match("Word"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_WORD

    // $ANTLR start TOK_BOOL
    public final void mTOK_BOOL() throws RecognitionException {
        try {
            int _type = TOK_BOOL;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2039:14: ( 'bool' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2039:16: 'bool'
            {
            match("bool"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_BOOL

    // $ANTLR start TOK_WAREAD
    public final void mTOK_WAREAD() throws RecognitionException {
        try {
            int _type = TOK_WAREAD;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2040:16: ( 'READ' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2040:18: 'READ'
            {
            match("READ"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_WAREAD

    // $ANTLR start TOK_WAWRITE
    public final void mTOK_WAWRITE() throws RecognitionException {
        try {
            int _type = TOK_WAWRITE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2041:17: ( 'WRITE' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2041:19: 'WRITE'
            {
            match("WRITE"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_WAWRITE

    // $ANTLR start TOK_CASE
    public final void mTOK_CASE() throws RecognitionException {
        try {
            int _type = TOK_CASE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2043:14: ( 'case' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2043:16: 'case'
            {
            match("case"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_CASE

    // $ANTLR start TOK_ESAC
    public final void mTOK_ESAC() throws RecognitionException {
        try {
            int _type = TOK_ESAC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2044:14: ( 'esac' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2044:16: 'esac'
            {
            match("esac"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_ESAC

    // $ANTLR start TOK_PLUS
    public final void mTOK_PLUS() throws RecognitionException {
        try {
            int _type = TOK_PLUS;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2045:14: ( '+' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2045:16: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_PLUS

    // $ANTLR start TOK_MINUS
    public final void mTOK_MINUS() throws RecognitionException {
        try {
            int _type = TOK_MINUS;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2046:15: ( '-' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2046:17: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_MINUS

    // $ANTLR start TOK_TIMES
    public final void mTOK_TIMES() throws RecognitionException {
        try {
            int _type = TOK_TIMES;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2047:15: ( '*' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2047:17: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_TIMES

    // $ANTLR start TOK_DIVIDE
    public final void mTOK_DIVIDE() throws RecognitionException {
        try {
            int _type = TOK_DIVIDE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2048:16: ( '/' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2048:18: '/'
            {
            match('/'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_DIVIDE

    // $ANTLR start TOK_MOD
    public final void mTOK_MOD() throws RecognitionException {
        try {
            int _type = TOK_MOD;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2049:14: ( 'mod' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2049:16: 'mod'
            {
            match("mod"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_MOD

    // $ANTLR start TOK_LSHIFT
    public final void mTOK_LSHIFT() throws RecognitionException {
        try {
            int _type = TOK_LSHIFT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2050:16: ( '<<' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2050:18: '<<'
            {
            match("<<"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LSHIFT

    // $ANTLR start TOK_RSHIFT
    public final void mTOK_RSHIFT() throws RecognitionException {
        try {
            int _type = TOK_RSHIFT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2051:16: ( '>>' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2051:18: '>>'
            {
            match(">>"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_RSHIFT

    // $ANTLR start TOK_EQUAL
    public final void mTOK_EQUAL() throws RecognitionException {
        try {
            int _type = TOK_EQUAL;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2054:15: ( '=' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2054:17: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_EQUAL

    // $ANTLR start TOK_NOTEQUAL
    public final void mTOK_NOTEQUAL() throws RecognitionException {
        try {
            int _type = TOK_NOTEQUAL;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2055:17: ( '!=' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2055:19: '!='
            {
            match("!="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NOTEQUAL

    // $ANTLR start TOK_LE
    public final void mTOK_LE() throws RecognitionException {
        try {
            int _type = TOK_LE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2056:13: ( '<=' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2056:15: '<='
            {
            match("<="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LE

    // $ANTLR start TOK_GE
    public final void mTOK_GE() throws RecognitionException {
        try {
            int _type = TOK_GE;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2057:13: ( '>=' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2057:15: '>='
            {
            match(">="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_GE

    // $ANTLR start TOK_LT
    public final void mTOK_LT() throws RecognitionException {
        try {
            int _type = TOK_LT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2058:13: ( '<' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2058:15: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_LT

    // $ANTLR start TOK_GT
    public final void mTOK_GT() throws RecognitionException {
        try {
            int _type = TOK_GT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2059:13: ( '>' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2059:15: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_GT

    // $ANTLR start TOK_NEXT
    public final void mTOK_NEXT() throws RecognitionException {
        try {
            int _type = TOK_NEXT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2060:14: ( 'next' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2060:16: 'next'
            {
            match("next"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NEXT

    // $ANTLR start TOK_UNION
    public final void mTOK_UNION() throws RecognitionException {
        try {
            int _type = TOK_UNION;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2062:15: ( 'union' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2062:17: 'union'
            {
            match("union"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_UNION

    // $ANTLR start TOK_SETIN
    public final void mTOK_SETIN() throws RecognitionException {
        try {
            int _type = TOK_SETIN;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2063:15: ( 'in' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2063:17: 'in'
            {
            match("in"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_SETIN

    // $ANTLR start TOK_TWODOTS
    public final void mTOK_TWODOTS() throws RecognitionException {
        try {
            int _type = TOK_TWODOTS;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2064:17: ( '..' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2064:19: '..'
            {
            match(".."); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_TWODOTS

    // $ANTLR start TOK_DOT
    public final void mTOK_DOT() throws RecognitionException {
        try {
            int _type = TOK_DOT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2065:14: ( '.' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2065:16: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_DOT

    // $ANTLR start TOK_IMPLIES
    public final void mTOK_IMPLIES() throws RecognitionException {
        try {
            int _type = TOK_IMPLIES;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2068:17: ( '->' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2068:19: '->'
            {
            match("->"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_IMPLIES

    // $ANTLR start TOK_IFF
    public final void mTOK_IFF() throws RecognitionException {
        try {
            int _type = TOK_IFF;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2069:14: ( '<->' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2069:16: '<->'
            {
            match("<->"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_IFF

    // $ANTLR start TOK_OR
    public final void mTOK_OR() throws RecognitionException {
        try {
            int _type = TOK_OR;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2070:13: ( '|' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2070:15: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_OR

    // $ANTLR start TOK_AND
    public final void mTOK_AND() throws RecognitionException {
        try {
            int _type = TOK_AND;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2071:14: ( '&' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2071:16: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_AND

    // $ANTLR start TOK_XOR
    public final void mTOK_XOR() throws RecognitionException {
        try {
            int _type = TOK_XOR;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2072:14: ( 'xor' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2072:16: 'xor'
            {
            match("xor"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_XOR

    // $ANTLR start TOK_XNOR
    public final void mTOK_XNOR() throws RecognitionException {
        try {
            int _type = TOK_XNOR;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2073:14: ( 'xnor' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2073:16: 'xnor'
            {
            match("xnor"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_XNOR

    // $ANTLR start TOK_NOT
    public final void mTOK_NOT() throws RecognitionException {
        try {
            int _type = TOK_NOT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2074:14: ( '!' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2074:16: '!'
            {
            match('!'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NOT

    // $ANTLR start TOK_COMMA
    public final void mTOK_COMMA() throws RecognitionException {
        try {
            int _type = TOK_COMMA;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2076:15: ( ',' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2076:17: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_COMMA

    // $ANTLR start TOK_COLON
    public final void mTOK_COLON() throws RecognitionException {
        try {
            int _type = TOK_COLON;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2077:15: ( ':' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2077:17: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_COLON

    // $ANTLR start TOK_SEMI
    public final void mTOK_SEMI() throws RecognitionException {
        try {
            int _type = TOK_SEMI;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2078:14: ( ';' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2078:16: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_SEMI

    // $ANTLR start TOK_CONCATENATION
    public final void mTOK_CONCATENATION() throws RecognitionException {
        try {
            int _type = TOK_CONCATENATION;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2079:21: ( '::' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2079:23: '::'
            {
            match("::"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_CONCATENATION

    // $ANTLR start TOK_NUMBER_WORD
    public final void mTOK_NUMBER_WORD() throws RecognitionException {
        try {
            int _type = TOK_NUMBER_WORD;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2088:21: ( '0' ( 'b' | 'B' | 'o' | 'O' | 'd' | 'D' | 'h' | 'H' ) ( '0' .. '9' )* '_' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' | '_' )* )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2088:23: '0' ( 'b' | 'B' | 'o' | 'O' | 'd' | 'D' | 'h' | 'H' ) ( '0' .. '9' )* '_' ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' | '_' )*
            {
            match('0'); 
            if ( input.LA(1)=='B'||input.LA(1)=='D'||input.LA(1)=='H'||input.LA(1)=='O'||input.LA(1)=='b'||input.LA(1)=='d'||input.LA(1)=='h'||input.LA(1)=='o' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2088:75: ( '0' .. '9' )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='0' && LA18_0<='9')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2088:76: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match('_'); 
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2088:124: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' | '_' )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='0' && LA19_0<='9')||(LA19_0>='A' && LA19_0<='F')||LA19_0=='_'||(LA19_0>='a' && LA19_0<='f')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='f') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NUMBER_WORD

    // $ANTLR start TOK_NUMBER_FRAC
    public final void mTOK_NUMBER_FRAC() throws RecognitionException {
        try {
            int _type = TOK_NUMBER_FRAC;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:21: ( ( 'f' | 'F' ) '\\'' ( '0' .. '9' )+ '/' ( '0' .. '9' )+ )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:23: ( 'f' | 'F' ) '\\'' ( '0' .. '9' )+ '/' ( '0' .. '9' )+
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            match('\''); 
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:40: ( '0' .. '9' )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:41: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);

            match('/'); 
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:56: ( '0' .. '9' )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2091:57: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NUMBER_FRAC

    // $ANTLR start TOK_NUMBER
    public final void mTOK_NUMBER() throws RecognitionException {
        try {
            int _type = TOK_NUMBER;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2094:17: ( ( '0' .. '9' )+ )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2094:19: ( '0' .. '9' )+
            {
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2094:19: ( '0' .. '9' )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( ((LA22_0>='0' && LA22_0<='9')) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2094:20: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_NUMBER

    // $ANTLR start TOK_ATOM
    public final void mTOK_ATOM() throws RecognitionException {
        try {
            int _type = TOK_ATOM;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2097:15: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\\\' | '$' | '#' | '-' )* )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2097:17: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\\\' | '$' | '#' | '-' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2097:45: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '\\\\' | '$' | '#' | '-' )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='#' && LA23_0<='$')||LA23_0=='-'||(LA23_0>='0' && LA23_0<='9')||(LA23_0>='A' && LA23_0<='Z')||LA23_0=='\\'||LA23_0=='_'||(LA23_0>='a' && LA23_0<='z')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:
            	    {
            	    if ( (input.LA(1)>='#' && input.LA(1)<='$')||input.LA(1)=='-'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='\\'||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TOK_ATOM

    // $ANTLR start JTOK_WS
    public final void mJTOK_WS() throws RecognitionException {
        try {
            int _type = JTOK_WS;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2100:15: ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2100:19: ( ' ' | '\\t' | '\\r' | '\\n' )+
            {
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2100:19: ( ' ' | '\\t' | '\\r' | '\\n' )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>='\t' && LA24_0<='\n')||LA24_0=='\r'||LA24_0==' ') ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);

             channel=HIDDEN; 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JTOK_WS

    // $ANTLR start JTOK_MULTI_COMMENT
    public final void mJTOK_MULTI_COMMENT() throws RecognitionException {
        try {
            int _type = JTOK_MULTI_COMMENT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2106:23: ( ( '/*' ( options {greedy=false; } : '\\r' | '\\n' | ~ ( '\\n' | '\\r' ) )* '*/' ) )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2106:25: ( '/*' ( options {greedy=false; } : '\\r' | '\\n' | ~ ( '\\n' | '\\r' ) )* '*/' )
            {
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2106:25: ( '/*' ( options {greedy=false; } : '\\r' | '\\n' | ~ ( '\\n' | '\\r' ) )* '*/' )
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2106:26: '/*' ( options {greedy=false; } : '\\r' | '\\n' | ~ ( '\\n' | '\\r' ) )* '*/'
            {
            match("/*"); 

            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2106:31: ( options {greedy=false; } : '\\r' | '\\n' | ~ ( '\\n' | '\\r' ) )*
            loop25:
            do {
                int alt25=4;
                int LA25_0 = input.LA(1);

                if ( (LA25_0=='*') ) {
                    int LA25_1 = input.LA(2);

                    if ( (LA25_1=='/') ) {
                        alt25=4;
                    }
                    else if ( ((LA25_1>='\u0000' && LA25_1<='.')||(LA25_1>='0' && LA25_1<='\uFFFE')) ) {
                        alt25=3;
                    }


                }
                else if ( (LA25_0=='\r') ) {
                    alt25=1;
                }
                else if ( (LA25_0=='\n') ) {
                    alt25=2;
                }
                else if ( ((LA25_0>='\u0000' && LA25_0<='\t')||(LA25_0>='\u000B' && LA25_0<='\f')||(LA25_0>='\u000E' && LA25_0<=')')||(LA25_0>='+' && LA25_0<='\uFFFE')) ) {
                    alt25=3;
                }


                switch (alt25) {
            	case 1 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2109:9: '\\r'
            	    {
            	    match('\r'); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2110:13: '\\n'
            	    {
            	    match('\n'); 

            	    }
            	    break;
            	case 3 :
            	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2111:13: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            match("*/"); 

            channel=HIDDEN;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JTOK_MULTI_COMMENT

    // $ANTLR start JTOK_LINE_COMMENT
    public final void mJTOK_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = JTOK_LINE_COMMENT;
            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:22: ( ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? ) | ( '//' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? ) )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='-') ) {
                alt34=1;
            }
            else if ( (LA34_0=='/') ) {
                alt34=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("2115:1: JTOK_LINE_COMMENT : ( ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? ) | ( '//' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? ) );", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:24: ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? )
                    {
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:24: ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? )
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:25: '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )?
                    {
                    match("--"); 

                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:30: (~ ( '\\n' | '\\r' ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>='\u0000' && LA26_0<='\t')||(LA26_0>='\u000B' && LA26_0<='\f')||(LA26_0>='\u000E' && LA26_0<='\uFFFE')) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:31: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:46: ( ( '\\n' | '\\r' ( '\\n' )? ) )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0=='\n'||LA29_0=='\r') ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:47: ( '\\n' | '\\r' ( '\\n' )? )
                            {
                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:47: ( '\\n' | '\\r' ( '\\n' )? )
                            int alt28=2;
                            int LA28_0 = input.LA(1);

                            if ( (LA28_0=='\n') ) {
                                alt28=1;
                            }
                            else if ( (LA28_0=='\r') ) {
                                alt28=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("2115:47: ( '\\n' | '\\r' ( '\\n' )? )", 28, 0, input);

                                throw nvae;
                            }
                            switch (alt28) {
                                case 1 :
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:48: '\\n'
                                    {
                                    match('\n'); 

                                    }
                                    break;
                                case 2 :
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:53: '\\r' ( '\\n' )?
                                    {
                                    match('\r'); 
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:57: ( '\\n' )?
                                    int alt27=2;
                                    int LA27_0 = input.LA(1);

                                    if ( (LA27_0=='\n') ) {
                                        alt27=1;
                                    }
                                    switch (alt27) {
                                        case 1 :
                                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2115:58: '\\n'
                                            {
                                            match('\n'); 

                                            }
                                            break;

                                    }


                                    }
                                    break;

                            }


                            }
                            break;

                    }

                    channel=HIDDEN;

                    }


                    }
                    break;
                case 2 :
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:11: ( '//' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? )
                    {
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:11: ( '//' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )? )
                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:12: '//' (~ ( '\\n' | '\\r' ) )* ( ( '\\n' | '\\r' ( '\\n' )? ) )?
                    {
                    match("//"); 

                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:17: (~ ( '\\n' | '\\r' ) )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( ((LA30_0>='\u0000' && LA30_0<='\t')||(LA30_0>='\u000B' && LA30_0<='\f')||(LA30_0>='\u000E' && LA30_0<='\uFFFE')) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:18: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);

                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:33: ( ( '\\n' | '\\r' ( '\\n' )? ) )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0=='\n'||LA33_0=='\r') ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:34: ( '\\n' | '\\r' ( '\\n' )? )
                            {
                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:34: ( '\\n' | '\\r' ( '\\n' )? )
                            int alt32=2;
                            int LA32_0 = input.LA(1);

                            if ( (LA32_0=='\n') ) {
                                alt32=1;
                            }
                            else if ( (LA32_0=='\r') ) {
                                alt32=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("2116:34: ( '\\n' | '\\r' ( '\\n' )? )", 32, 0, input);

                                throw nvae;
                            }
                            switch (alt32) {
                                case 1 :
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:35: '\\n'
                                    {
                                    match('\n'); 

                                    }
                                    break;
                                case 2 :
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:40: '\\r' ( '\\n' )?
                                    {
                                    match('\r'); 
                                    // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:44: ( '\\n' )?
                                    int alt31=2;
                                    int LA31_0 = input.LA(1);

                                    if ( (LA31_0=='\n') ) {
                                        alt31=1;
                                    }
                                    switch (alt31) {
                                        case 1 :
                                            // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:2116:45: '\\n'
                                            {
                                            match('\n'); 

                                            }
                                            break;

                                    }


                                    }
                                    break;

                            }


                            }
                            break;

                    }

                    channel=HIDDEN;

                    }


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JTOK_LINE_COMMENT

    public void mTokens() throws RecognitionException {
        // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:8: ( TOK_CTL_SPEC | TOK_CTL_STAR_SPEC | TOK_LTL_SPEC | TOK_INVAR_SPEC | TOK_RTCTL_STAR_SPEC | TOK_LDL_SPEC | TOK_EX | TOK_AX | TOK_EF | TOK_AF | TOK_EG | TOK_AG | TOK_EE | TOK_AA | TOK_BUNTIL | TOK_EBF | TOK_ABF | TOK_EBG | TOK_ABG | TOK_OP_FINALLY | TOK_OP_ONCE | TOK_OP_GLOBALLY | TOK_OP_HISTORICALLY | TOK_OP_NEXT | TOK_OP_PREV | TOK_UNTIL | TOK_SINCE | TOK_RELEASE | TOK_TRIGGERED | TOK_OP_NOTPREVNOT | TOK_OP_BFINALLY | TOK_OP_BGLOBALLY | TOK_BRELEASE | TOK_KNOW | TOK_SKNOW | TOK_LP | TOK_RP | TOK_LB | TOK_RB | TOK_LCB | TOK_RCB | TOK_FALSEEXP | TOK_TRUEEXP | TOK_WORD1 | TOK_WORD | TOK_BOOL | TOK_WAREAD | TOK_WAWRITE | TOK_CASE | TOK_ESAC | TOK_PLUS | TOK_MINUS | TOK_TIMES | TOK_DIVIDE | TOK_MOD | TOK_LSHIFT | TOK_RSHIFT | TOK_EQUAL | TOK_NOTEQUAL | TOK_LE | TOK_GE | TOK_LT | TOK_GT | TOK_NEXT | TOK_UNION | TOK_SETIN | TOK_TWODOTS | TOK_DOT | TOK_IMPLIES | TOK_IFF | TOK_OR | TOK_AND | TOK_XOR | TOK_XNOR | TOK_NOT | TOK_COMMA | TOK_COLON | TOK_SEMI | TOK_CONCATENATION | TOK_NUMBER_WORD | TOK_NUMBER_FRAC | TOK_NUMBER | TOK_ATOM | JTOK_WS | JTOK_MULTI_COMMENT | JTOK_LINE_COMMENT )
        int alt35=86;
        switch ( input.LA(1) ) {
        case 'C':
            {
            int LA35_1 = input.LA(2);

            if ( (LA35_1=='T') ) {
                int LA35_56 = input.LA(3);

                if ( (LA35_56=='L') ) {
                    switch ( input.LA(4) ) {
                    case '*':
                        {
                        alt35=2;
                        }
                        break;
                    case 'S':
                        {
                        int LA35_205 = input.LA(5);

                        if ( (LA35_205=='P') ) {
                            int LA35_254 = input.LA(6);

                            if ( (LA35_254=='E') ) {
                                int LA35_292 = input.LA(7);

                                if ( (LA35_292=='C') ) {
                                    int LA35_315 = input.LA(8);

                                    if ( ((LA35_315>='#' && LA35_315<='$')||LA35_315=='-'||(LA35_315>='0' && LA35_315<='9')||(LA35_315>='A' && LA35_315<='Z')||LA35_315=='\\'||LA35_315=='_'||(LA35_315>='a' && LA35_315<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=1;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                        }
                        break;
                    default:
                        alt35=83;}

                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'S':
            {
            switch ( input.LA(2) ) {
            case 'P':
                {
                int LA35_57 = input.LA(3);

                if ( (LA35_57=='E') ) {
                    int LA35_144 = input.LA(4);

                    if ( (LA35_144=='C') ) {
                        int LA35_206 = input.LA(5);

                        if ( ((LA35_206>='#' && LA35_206<='$')||LA35_206=='-'||(LA35_206>='0' && LA35_206<='9')||(LA35_206>='A' && LA35_206<='Z')||LA35_206=='\\'||LA35_206=='_'||(LA35_206>='a' && LA35_206<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=1;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'i':
                {
                int LA35_58 = input.LA(3);

                if ( (LA35_58=='n') ) {
                    int LA35_145 = input.LA(4);

                    if ( (LA35_145=='c') ) {
                        int LA35_207 = input.LA(5);

                        if ( (LA35_207=='e') ) {
                            int LA35_256 = input.LA(6);

                            if ( ((LA35_256>='#' && LA35_256<='$')||LA35_256=='-'||(LA35_256>='0' && LA35_256<='9')||(LA35_256>='A' && LA35_256<='Z')||LA35_256=='\\'||LA35_256=='_'||(LA35_256>='a' && LA35_256<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=27;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'I':
                {
                int LA35_59 = input.LA(3);

                if ( (LA35_59=='N') ) {
                    int LA35_146 = input.LA(4);

                    if ( (LA35_146=='C') ) {
                        int LA35_208 = input.LA(5);

                        if ( (LA35_208=='E') ) {
                            int LA35_257 = input.LA(6);

                            if ( ((LA35_257>='#' && LA35_257<='$')||LA35_257=='-'||(LA35_257>='0' && LA35_257<='9')||(LA35_257>='A' && LA35_257<='Z')||LA35_257=='\\'||LA35_257=='_'||(LA35_257>='a' && LA35_257<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=27;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'K':
                {
                switch ( input.LA(3) ) {
                case 'N':
                    {
                    int LA35_147 = input.LA(4);

                    if ( (LA35_147=='O') ) {
                        int LA35_209 = input.LA(5);

                        if ( (LA35_209=='W') ) {
                            int LA35_258 = input.LA(6);

                            if ( ((LA35_258>='#' && LA35_258<='$')||LA35_258=='-'||(LA35_258>='0' && LA35_258<='9')||(LA35_258>='A' && LA35_258<='Z')||LA35_258=='\\'||LA35_258=='_'||(LA35_258>='a' && LA35_258<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=35;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case '#':
                case '$':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt35=83;
                    }
                    break;
                default:
                    alt35=35;}

                }
                break;
            case 'k':
                {
                int LA35_61 = input.LA(3);

                if ( (LA35_61=='n') ) {
                    int LA35_149 = input.LA(4);

                    if ( (LA35_149=='o') ) {
                        int LA35_210 = input.LA(5);

                        if ( (LA35_210=='w') ) {
                            int LA35_259 = input.LA(6);

                            if ( ((LA35_259>='#' && LA35_259<='$')||LA35_259=='-'||(LA35_259>='0' && LA35_259<='9')||(LA35_259>='A' && LA35_259<='Z')||LA35_259=='\\'||LA35_259=='_'||(LA35_259>='a' && LA35_259<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=35;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'j':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=27;}

            }
            break;
        case 'L':
            {
            switch ( input.LA(2) ) {
            case 'T':
                {
                int LA35_63 = input.LA(3);

                if ( (LA35_63=='L') ) {
                    int LA35_150 = input.LA(4);

                    if ( (LA35_150=='S') ) {
                        int LA35_211 = input.LA(5);

                        if ( (LA35_211=='P') ) {
                            int LA35_260 = input.LA(6);

                            if ( (LA35_260=='E') ) {
                                int LA35_293 = input.LA(7);

                                if ( (LA35_293=='C') ) {
                                    int LA35_316 = input.LA(8);

                                    if ( ((LA35_316>='#' && LA35_316<='$')||LA35_316=='-'||(LA35_316>='0' && LA35_316<='9')||(LA35_316>='A' && LA35_316<='Z')||LA35_316=='\\'||LA35_316=='_'||(LA35_316>='a' && LA35_316<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=3;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'D':
                {
                int LA35_64 = input.LA(3);

                if ( (LA35_64=='L') ) {
                    int LA35_151 = input.LA(4);

                    if ( (LA35_151=='S') ) {
                        int LA35_212 = input.LA(5);

                        if ( (LA35_212=='P') ) {
                            int LA35_261 = input.LA(6);

                            if ( (LA35_261=='E') ) {
                                int LA35_294 = input.LA(7);

                                if ( (LA35_294=='C') ) {
                                    int LA35_317 = input.LA(8);

                                    if ( ((LA35_317>='#' && LA35_317<='$')||LA35_317=='-'||(LA35_317>='0' && LA35_317<='9')||(LA35_317>='A' && LA35_317<='Z')||LA35_317=='\\'||LA35_317=='_'||(LA35_317>='a' && LA35_317<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=6;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            default:
                alt35=83;}

            }
            break;
        case 'I':
            {
            int LA35_4 = input.LA(2);

            if ( (LA35_4=='N') ) {
                int LA35_65 = input.LA(3);

                if ( (LA35_65=='V') ) {
                    int LA35_152 = input.LA(4);

                    if ( (LA35_152=='A') ) {
                        int LA35_213 = input.LA(5);

                        if ( (LA35_213=='R') ) {
                            int LA35_262 = input.LA(6);

                            if ( (LA35_262=='S') ) {
                                int LA35_295 = input.LA(7);

                                if ( (LA35_295=='P') ) {
                                    int LA35_318 = input.LA(8);

                                    if ( (LA35_318=='E') ) {
                                        int LA35_332 = input.LA(9);

                                        if ( (LA35_332=='C') ) {
                                            int LA35_341 = input.LA(10);

                                            if ( ((LA35_341>='#' && LA35_341<='$')||LA35_341=='-'||(LA35_341>='0' && LA35_341<='9')||(LA35_341>='A' && LA35_341<='Z')||LA35_341=='\\'||LA35_341=='_'||(LA35_341>='a' && LA35_341<='z')) ) {
                                                alt35=83;
                                            }
                                            else {
                                                alt35=4;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'R':
            {
            switch ( input.LA(2) ) {
            case 'T':
                {
                int LA35_66 = input.LA(3);

                if ( (LA35_66=='C') ) {
                    int LA35_153 = input.LA(4);

                    if ( (LA35_153=='T') ) {
                        int LA35_214 = input.LA(5);

                        if ( (LA35_214=='L') ) {
                            int LA35_263 = input.LA(6);

                            if ( (LA35_263=='*') ) {
                                alt35=5;
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'E':
                {
                switch ( input.LA(3) ) {
                case 'A':
                    {
                    int LA35_154 = input.LA(4);

                    if ( (LA35_154=='D') ) {
                        int LA35_215 = input.LA(5);

                        if ( ((LA35_215>='#' && LA35_215<='$')||LA35_215=='-'||(LA35_215>='0' && LA35_215<='9')||(LA35_215>='A' && LA35_215<='Z')||LA35_215=='\\'||LA35_215=='_'||(LA35_215>='a' && LA35_215<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=47;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case 'L':
                    {
                    int LA35_155 = input.LA(4);

                    if ( (LA35_155=='E') ) {
                        int LA35_216 = input.LA(5);

                        if ( (LA35_216=='A') ) {
                            int LA35_265 = input.LA(6);

                            if ( (LA35_265=='S') ) {
                                int LA35_297 = input.LA(7);

                                if ( (LA35_297=='E') ) {
                                    int LA35_319 = input.LA(8);

                                    if ( ((LA35_319>='#' && LA35_319<='$')||LA35_319=='-'||(LA35_319>='0' && LA35_319<='9')||(LA35_319>='A' && LA35_319<='Z')||LA35_319=='\\'||LA35_319=='_'||(LA35_319>='a' && LA35_319<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=28;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                default:
                    alt35=83;}

                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=28;}

            }
            break;
        case 'E':
            {
            switch ( input.LA(2) ) {
            case 'X':
                {
                int LA35_69 = input.LA(3);

                if ( ((LA35_69>='#' && LA35_69<='$')||LA35_69=='-'||(LA35_69>='0' && LA35_69<='9')||(LA35_69>='A' && LA35_69<='Z')||LA35_69=='\\'||LA35_69=='_'||(LA35_69>='a' && LA35_69<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=7;}
                }
                break;
            case 'F':
                {
                int LA35_70 = input.LA(3);

                if ( ((LA35_70>='#' && LA35_70<='$')||LA35_70=='-'||(LA35_70>='0' && LA35_70<='9')||(LA35_70>='A' && LA35_70<='Z')||LA35_70=='\\'||LA35_70=='_'||(LA35_70>='a' && LA35_70<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=9;}
                }
                break;
            case 'G':
                {
                int LA35_71 = input.LA(3);

                if ( ((LA35_71>='#' && LA35_71<='$')||LA35_71=='-'||(LA35_71>='0' && LA35_71<='9')||(LA35_71>='A' && LA35_71<='Z')||LA35_71=='\\'||LA35_71=='_'||(LA35_71>='a' && LA35_71<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=11;}
                }
                break;
            case 'B':
                {
                switch ( input.LA(3) ) {
                case 'F':
                    {
                    int LA35_159 = input.LA(4);

                    if ( ((LA35_159>='#' && LA35_159<='$')||LA35_159=='-'||(LA35_159>='0' && LA35_159<='9')||(LA35_159>='A' && LA35_159<='Z')||LA35_159=='\\'||LA35_159=='_'||(LA35_159>='a' && LA35_159<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=16;}
                    }
                    break;
                case 'G':
                    {
                    int LA35_160 = input.LA(4);

                    if ( ((LA35_160>='#' && LA35_160<='$')||LA35_160=='-'||(LA35_160>='0' && LA35_160<='9')||(LA35_160>='A' && LA35_160<='Z')||LA35_160=='\\'||LA35_160=='_'||(LA35_160>='a' && LA35_160<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=18;}
                    }
                    break;
                default:
                    alt35=83;}

                }
                break;
            case 'V':
                {
                int LA35_73 = input.LA(3);

                if ( (LA35_73=='E') ) {
                    int LA35_161 = input.LA(4);

                    if ( (LA35_161=='N') ) {
                        int LA35_219 = input.LA(5);

                        if ( (LA35_219=='T') ) {
                            int LA35_266 = input.LA(6);

                            if ( (LA35_266=='U') ) {
                                int LA35_298 = input.LA(7);

                                if ( (LA35_298=='A') ) {
                                    int LA35_320 = input.LA(8);

                                    if ( (LA35_320=='L') ) {
                                        int LA35_333 = input.LA(9);

                                        if ( (LA35_333=='L') ) {
                                            int LA35_342 = input.LA(10);

                                            if ( (LA35_342=='Y') ) {
                                                int LA35_348 = input.LA(11);

                                                if ( ((LA35_348>='#' && LA35_348<='$')||LA35_348=='-'||(LA35_348>='0' && LA35_348<='9')||(LA35_348>='A' && LA35_348<='Z')||LA35_348=='\\'||LA35_348=='_'||(LA35_348>='a' && LA35_348<='z')) ) {
                                                    alt35=83;
                                                }
                                                else {
                                                    alt35=20;}
                                            }
                                            else {
                                                alt35=83;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'C':
            case 'D':
            case 'E':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'W':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=13;}

            }
            break;
        case 'A':
            {
            switch ( input.LA(2) ) {
            case 'X':
                {
                int LA35_75 = input.LA(3);

                if ( ((LA35_75>='#' && LA35_75<='$')||LA35_75=='-'||(LA35_75>='0' && LA35_75<='9')||(LA35_75>='A' && LA35_75<='Z')||LA35_75=='\\'||LA35_75=='_'||(LA35_75>='a' && LA35_75<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=8;}
                }
                break;
            case 'F':
                {
                int LA35_76 = input.LA(3);

                if ( ((LA35_76>='#' && LA35_76<='$')||LA35_76=='-'||(LA35_76>='0' && LA35_76<='9')||(LA35_76>='A' && LA35_76<='Z')||LA35_76=='\\'||LA35_76=='_'||(LA35_76>='a' && LA35_76<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=10;}
                }
                break;
            case 'G':
                {
                int LA35_77 = input.LA(3);

                if ( ((LA35_77>='#' && LA35_77<='$')||LA35_77=='-'||(LA35_77>='0' && LA35_77<='9')||(LA35_77>='A' && LA35_77<='Z')||LA35_77=='\\'||LA35_77=='_'||(LA35_77>='a' && LA35_77<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=12;}
                }
                break;
            case 'B':
                {
                switch ( input.LA(3) ) {
                case 'G':
                    {
                    int LA35_165 = input.LA(4);

                    if ( ((LA35_165>='#' && LA35_165<='$')||LA35_165=='-'||(LA35_165>='0' && LA35_165<='9')||(LA35_165>='A' && LA35_165<='Z')||LA35_165=='\\'||LA35_165=='_'||(LA35_165>='a' && LA35_165<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=19;}
                    }
                    break;
                case 'F':
                    {
                    int LA35_166 = input.LA(4);

                    if ( ((LA35_166>='#' && LA35_166<='$')||LA35_166=='-'||(LA35_166>='0' && LA35_166<='9')||(LA35_166>='A' && LA35_166<='Z')||LA35_166=='\\'||LA35_166=='_'||(LA35_166>='a' && LA35_166<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=17;}
                    }
                    break;
                default:
                    alt35=83;}

                }
                break;
            case 'L':
                {
                int LA35_79 = input.LA(3);

                if ( (LA35_79=='W') ) {
                    int LA35_167 = input.LA(4);

                    if ( (LA35_167=='A') ) {
                        int LA35_222 = input.LA(5);

                        if ( (LA35_222=='Y') ) {
                            int LA35_267 = input.LA(6);

                            if ( (LA35_267=='S') ) {
                                int LA35_299 = input.LA(7);

                                if ( ((LA35_299>='#' && LA35_299<='$')||LA35_299=='-'||(LA35_299>='0' && LA35_299<='9')||(LA35_299>='A' && LA35_299<='Z')||LA35_299=='\\'||LA35_299=='_'||(LA35_299>='a' && LA35_299<='z')) ) {
                                    alt35=83;
                                }
                                else {
                                    alt35=22;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'w':
                {
                int LA35_80 = input.LA(3);

                if ( (LA35_80=='a') ) {
                    int LA35_168 = input.LA(4);

                    if ( (LA35_168=='i') ) {
                        int LA35_223 = input.LA(5);

                        if ( (LA35_223=='t') ) {
                            int LA35_268 = input.LA(6);

                            if ( (LA35_268=='s') ) {
                                int LA35_300 = input.LA(7);

                                if ( ((LA35_300>='#' && LA35_300<='$')||LA35_300=='-'||(LA35_300>='0' && LA35_300<='9')||(LA35_300>='A' && LA35_300<='Z')||LA35_300=='\\'||LA35_300=='_'||(LA35_300>='a' && LA35_300<='z')) ) {
                                    alt35=83;
                                }
                                else {
                                    alt35=28;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'C':
            case 'D':
            case 'E':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=14;}

            }
            break;
        case 'B':
            {
            switch ( input.LA(2) ) {
            case 'U':
                {
                int LA35_82 = input.LA(3);

                if ( ((LA35_82>='#' && LA35_82<='$')||LA35_82=='-'||(LA35_82>='0' && LA35_82<='9')||(LA35_82>='A' && LA35_82<='Z')||LA35_82=='\\'||LA35_82=='_'||(LA35_82>='a' && LA35_82<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=15;}
                }
                break;
            case 'a':
                {
                int LA35_83 = input.LA(3);

                if ( (LA35_83=='c') ) {
                    int LA35_170 = input.LA(4);

                    if ( (LA35_170=='k') ) {
                        int LA35_224 = input.LA(5);

                        if ( (LA35_224=='t') ) {
                            int LA35_269 = input.LA(6);

                            if ( (LA35_269=='o') ) {
                                int LA35_301 = input.LA(7);

                                if ( ((LA35_301>='#' && LA35_301<='$')||LA35_301=='-'||(LA35_301>='0' && LA35_301<='9')||(LA35_301>='A' && LA35_301<='Z')||LA35_301=='\\'||LA35_301=='_'||(LA35_301>='a' && LA35_301<='z')) ) {
                                    alt35=83;
                                }
                                else {
                                    alt35=29;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'F':
                {
                switch ( input.LA(3) ) {
                case 'I':
                    {
                    int LA35_171 = input.LA(4);

                    if ( (LA35_171=='N') ) {
                        int LA35_225 = input.LA(5);

                        if ( (LA35_225=='A') ) {
                            int LA35_270 = input.LA(6);

                            if ( (LA35_270=='L') ) {
                                int LA35_302 = input.LA(7);

                                if ( (LA35_302=='L') ) {
                                    int LA35_321 = input.LA(8);

                                    if ( (LA35_321=='Y') ) {
                                        int LA35_334 = input.LA(9);

                                        if ( ((LA35_334>='#' && LA35_334<='$')||LA35_334=='-'||(LA35_334>='0' && LA35_334<='9')||(LA35_334>='A' && LA35_334<='Z')||LA35_334=='\\'||LA35_334=='_'||(LA35_334>='a' && LA35_334<='z')) ) {
                                            alt35=83;
                                        }
                                        else {
                                            alt35=31;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case '#':
                case '$':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt35=83;
                    }
                    break;
                default:
                    alt35=31;}

                }
                break;
            case 'E':
                {
                int LA35_85 = input.LA(3);

                if ( (LA35_85=='V') ) {
                    int LA35_173 = input.LA(4);

                    if ( (LA35_173=='E') ) {
                        int LA35_226 = input.LA(5);

                        if ( (LA35_226=='N') ) {
                            int LA35_271 = input.LA(6);

                            if ( (LA35_271=='T') ) {
                                int LA35_303 = input.LA(7);

                                if ( (LA35_303=='U') ) {
                                    int LA35_322 = input.LA(8);

                                    if ( (LA35_322=='A') ) {
                                        int LA35_335 = input.LA(9);

                                        if ( (LA35_335=='L') ) {
                                            int LA35_343 = input.LA(10);

                                            if ( (LA35_343=='L') ) {
                                                int LA35_349 = input.LA(11);

                                                if ( (LA35_349=='Y') ) {
                                                    int LA35_351 = input.LA(12);

                                                    if ( ((LA35_351>='#' && LA35_351<='$')||LA35_351=='-'||(LA35_351>='0' && LA35_351<='9')||(LA35_351>='A' && LA35_351<='Z')||LA35_351=='\\'||LA35_351=='_'||(LA35_351>='a' && LA35_351<='z')) ) {
                                                        alt35=83;
                                                    }
                                                    else {
                                                        alt35=31;}
                                                }
                                                else {
                                                    alt35=83;}
                                            }
                                            else {
                                                alt35=83;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'G':
                {
                switch ( input.LA(3) ) {
                case 'L':
                    {
                    int LA35_174 = input.LA(4);

                    if ( (LA35_174=='O') ) {
                        int LA35_227 = input.LA(5);

                        if ( (LA35_227=='B') ) {
                            int LA35_272 = input.LA(6);

                            if ( (LA35_272=='A') ) {
                                int LA35_304 = input.LA(7);

                                if ( (LA35_304=='L') ) {
                                    int LA35_323 = input.LA(8);

                                    if ( (LA35_323=='L') ) {
                                        int LA35_336 = input.LA(9);

                                        if ( (LA35_336=='Y') ) {
                                            int LA35_344 = input.LA(10);

                                            if ( ((LA35_344>='#' && LA35_344<='$')||LA35_344=='-'||(LA35_344>='0' && LA35_344<='9')||(LA35_344>='A' && LA35_344<='Z')||LA35_344=='\\'||LA35_344=='_'||(LA35_344>='a' && LA35_344<='z')) ) {
                                                alt35=83;
                                            }
                                            else {
                                                alt35=32;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case '#':
                case '$':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt35=83;
                    }
                    break;
                default:
                    alt35=32;}

                }
                break;
            case 'A':
                {
                int LA35_87 = input.LA(3);

                if ( (LA35_87=='L') ) {
                    int LA35_176 = input.LA(4);

                    if ( (LA35_176=='W') ) {
                        int LA35_228 = input.LA(5);

                        if ( (LA35_228=='A') ) {
                            int LA35_273 = input.LA(6);

                            if ( (LA35_273=='Y') ) {
                                int LA35_305 = input.LA(7);

                                if ( (LA35_305=='S') ) {
                                    int LA35_324 = input.LA(8);

                                    if ( ((LA35_324>='#' && LA35_324<='$')||LA35_324=='-'||(LA35_324>='0' && LA35_324<='9')||(LA35_324>='A' && LA35_324<='Z')||LA35_324=='\\'||LA35_324=='_'||(LA35_324>='a' && LA35_324<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=32;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'R':
                {
                switch ( input.LA(3) ) {
                case 'E':
                    {
                    int LA35_177 = input.LA(4);

                    if ( (LA35_177=='L') ) {
                        int LA35_229 = input.LA(5);

                        if ( (LA35_229=='E') ) {
                            int LA35_274 = input.LA(6);

                            if ( (LA35_274=='A') ) {
                                int LA35_306 = input.LA(7);

                                if ( (LA35_306=='S') ) {
                                    int LA35_325 = input.LA(8);

                                    if ( (LA35_325=='E') ) {
                                        int LA35_337 = input.LA(9);

                                        if ( ((LA35_337>='#' && LA35_337<='$')||LA35_337=='-'||(LA35_337>='0' && LA35_337<='9')||(LA35_337>='A' && LA35_337<='Z')||LA35_337=='\\'||LA35_337=='_'||(LA35_337>='a' && LA35_337<='z')) ) {
                                            alt35=83;
                                        }
                                        else {
                                            alt35=33;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case '#':
                case '$':
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt35=83;
                    }
                    break;
                default:
                    alt35=33;}

                }
                break;
            default:
                alt35=83;}

            }
            break;
        case 'F':
            {
            switch ( input.LA(2) ) {
            case 'I':
                {
                int LA35_89 = input.LA(3);

                if ( (LA35_89=='N') ) {
                    int LA35_179 = input.LA(4);

                    if ( (LA35_179=='A') ) {
                        int LA35_230 = input.LA(5);

                        if ( (LA35_230=='L') ) {
                            int LA35_275 = input.LA(6);

                            if ( (LA35_275=='L') ) {
                                int LA35_307 = input.LA(7);

                                if ( (LA35_307=='Y') ) {
                                    int LA35_326 = input.LA(8);

                                    if ( ((LA35_326>='#' && LA35_326<='$')||LA35_326=='-'||(LA35_326>='0' && LA35_326<='9')||(LA35_326>='A' && LA35_326<='Z')||LA35_326=='\\'||LA35_326=='_'||(LA35_326>='a' && LA35_326<='z')) ) {
                                        alt35=83;
                                    }
                                    else {
                                        alt35=20;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'A':
                {
                int LA35_90 = input.LA(3);

                if ( (LA35_90=='L') ) {
                    int LA35_180 = input.LA(4);

                    if ( (LA35_180=='S') ) {
                        int LA35_231 = input.LA(5);

                        if ( (LA35_231=='E') ) {
                            int LA35_276 = input.LA(6);

                            if ( ((LA35_276>='#' && LA35_276<='$')||LA35_276=='-'||(LA35_276>='0' && LA35_276<='9')||(LA35_276>='A' && LA35_276<='Z')||LA35_276=='\\'||LA35_276=='_'||(LA35_276>='a' && LA35_276<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=42;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '\'':
                {
                alt35=81;
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=20;}

            }
            break;
        case 'O':
            {
            switch ( input.LA(2) ) {
            case 'N':
                {
                int LA35_93 = input.LA(3);

                if ( (LA35_93=='C') ) {
                    int LA35_181 = input.LA(4);

                    if ( (LA35_181=='E') ) {
                        int LA35_232 = input.LA(5);

                        if ( ((LA35_232>='#' && LA35_232<='$')||LA35_232=='-'||(LA35_232>='0' && LA35_232<='9')||(LA35_232>='A' && LA35_232<='Z')||LA35_232=='\\'||LA35_232=='_'||(LA35_232>='a' && LA35_232<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=21;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=21;}

            }
            break;
        case 'G':
            {
            switch ( input.LA(2) ) {
            case 'L':
                {
                int LA35_95 = input.LA(3);

                if ( (LA35_95=='O') ) {
                    int LA35_182 = input.LA(4);

                    if ( (LA35_182=='B') ) {
                        int LA35_233 = input.LA(5);

                        if ( (LA35_233=='A') ) {
                            int LA35_277 = input.LA(6);

                            if ( (LA35_277=='L') ) {
                                int LA35_309 = input.LA(7);

                                if ( (LA35_309=='L') ) {
                                    int LA35_327 = input.LA(8);

                                    if ( (LA35_327=='Y') ) {
                                        int LA35_338 = input.LA(9);

                                        if ( ((LA35_338>='#' && LA35_338<='$')||LA35_338=='-'||(LA35_338>='0' && LA35_338<='9')||(LA35_338>='A' && LA35_338<='Z')||LA35_338=='\\'||LA35_338=='_'||(LA35_338>='a' && LA35_338<='z')) ) {
                                            alt35=83;
                                        }
                                        else {
                                            alt35=22;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=22;}

            }
            break;
        case 'H':
            {
            switch ( input.LA(2) ) {
            case 'I':
                {
                int LA35_97 = input.LA(3);

                if ( (LA35_97=='S') ) {
                    int LA35_183 = input.LA(4);

                    if ( (LA35_183=='T') ) {
                        int LA35_234 = input.LA(5);

                        if ( (LA35_234=='O') ) {
                            int LA35_278 = input.LA(6);

                            if ( (LA35_278=='R') ) {
                                int LA35_310 = input.LA(7);

                                if ( (LA35_310=='I') ) {
                                    int LA35_328 = input.LA(8);

                                    if ( (LA35_328=='C') ) {
                                        int LA35_339 = input.LA(9);

                                        if ( (LA35_339=='A') ) {
                                            int LA35_345 = input.LA(10);

                                            if ( (LA35_345=='L') ) {
                                                int LA35_350 = input.LA(11);

                                                if ( (LA35_350=='L') ) {
                                                    int LA35_352 = input.LA(12);

                                                    if ( (LA35_352=='Y') ) {
                                                        int LA35_353 = input.LA(13);

                                                        if ( ((LA35_353>='#' && LA35_353<='$')||LA35_353=='-'||(LA35_353>='0' && LA35_353<='9')||(LA35_353>='A' && LA35_353<='Z')||LA35_353=='\\'||LA35_353=='_'||(LA35_353>='a' && LA35_353<='z')) ) {
                                                            alt35=83;
                                                        }
                                                        else {
                                                            alt35=23;}
                                                    }
                                                    else {
                                                        alt35=83;}
                                                }
                                                else {
                                                    alt35=83;}
                                            }
                                            else {
                                                alt35=83;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=23;}

            }
            break;
        case 'X':
            {
            int LA35_13 = input.LA(2);

            if ( ((LA35_13>='#' && LA35_13<='$')||LA35_13=='-'||(LA35_13>='0' && LA35_13<='9')||(LA35_13>='A' && LA35_13<='Z')||LA35_13=='\\'||LA35_13=='_'||(LA35_13>='a' && LA35_13<='z')) ) {
                alt35=83;
            }
            else {
                alt35=24;}
            }
            break;
        case 'N':
            {
            int LA35_14 = input.LA(2);

            if ( (LA35_14=='E') ) {
                int LA35_100 = input.LA(3);

                if ( (LA35_100=='X') ) {
                    int LA35_184 = input.LA(4);

                    if ( (LA35_184=='T') ) {
                        int LA35_235 = input.LA(5);

                        if ( ((LA35_235>='#' && LA35_235<='$')||LA35_235=='-'||(LA35_235>='0' && LA35_235<='9')||(LA35_235>='A' && LA35_235<='Z')||LA35_235=='\\'||LA35_235=='_'||(LA35_235>='a' && LA35_235<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=24;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'Y':
            {
            int LA35_15 = input.LA(2);

            if ( ((LA35_15>='#' && LA35_15<='$')||LA35_15=='-'||(LA35_15>='0' && LA35_15<='9')||(LA35_15>='A' && LA35_15<='Z')||LA35_15=='\\'||LA35_15=='_'||(LA35_15>='a' && LA35_15<='z')) ) {
                alt35=83;
            }
            else {
                alt35=25;}
            }
            break;
        case 'P':
            {
            int LA35_16 = input.LA(2);

            if ( (LA35_16=='R') ) {
                int LA35_102 = input.LA(3);

                if ( (LA35_102=='E') ) {
                    int LA35_185 = input.LA(4);

                    if ( (LA35_185=='V') ) {
                        int LA35_236 = input.LA(5);

                        if ( ((LA35_236>='#' && LA35_236<='$')||LA35_236=='-'||(LA35_236>='0' && LA35_236<='9')||(LA35_236>='A' && LA35_236<='Z')||LA35_236=='\\'||LA35_236=='_'||(LA35_236>='a' && LA35_236<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=25;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'U':
            {
            switch ( input.LA(2) ) {
            case 'n':
                {
                int LA35_103 = input.LA(3);

                if ( (LA35_103=='t') ) {
                    int LA35_186 = input.LA(4);

                    if ( (LA35_186=='i') ) {
                        int LA35_237 = input.LA(5);

                        if ( (LA35_237=='l') ) {
                            int LA35_279 = input.LA(6);

                            if ( ((LA35_279>='#' && LA35_279<='$')||LA35_279=='-'||(LA35_279>='0' && LA35_279<='9')||(LA35_279>='A' && LA35_279<='Z')||LA35_279=='\\'||LA35_279=='_'||(LA35_279>='a' && LA35_279<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=26;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'N':
                {
                int LA35_104 = input.LA(3);

                if ( (LA35_104=='T') ) {
                    int LA35_187 = input.LA(4);

                    if ( (LA35_187=='I') ) {
                        int LA35_238 = input.LA(5);

                        if ( (LA35_238=='L') ) {
                            int LA35_280 = input.LA(6);

                            if ( ((LA35_280>='#' && LA35_280<='$')||LA35_280=='-'||(LA35_280>='0' && LA35_280<='9')||(LA35_280>='A' && LA35_280<='Z')||LA35_280=='\\'||LA35_280=='_'||(LA35_280>='a' && LA35_280<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=26;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=26;}

            }
            break;
        case 'T':
            {
            switch ( input.LA(2) ) {
            case 'R':
                {
                switch ( input.LA(3) ) {
                case 'U':
                    {
                    int LA35_188 = input.LA(4);

                    if ( (LA35_188=='E') ) {
                        int LA35_239 = input.LA(5);

                        if ( ((LA35_239>='#' && LA35_239<='$')||LA35_239=='-'||(LA35_239>='0' && LA35_239<='9')||(LA35_239>='A' && LA35_239<='Z')||LA35_239=='\\'||LA35_239=='_'||(LA35_239>='a' && LA35_239<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=43;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                case 'I':
                    {
                    int LA35_189 = input.LA(4);

                    if ( (LA35_189=='G') ) {
                        int LA35_240 = input.LA(5);

                        if ( (LA35_240=='G') ) {
                            int LA35_282 = input.LA(6);

                            if ( (LA35_282=='E') ) {
                                int LA35_311 = input.LA(7);

                                if ( (LA35_311=='R') ) {
                                    int LA35_329 = input.LA(8);

                                    if ( (LA35_329=='E') ) {
                                        int LA35_340 = input.LA(9);

                                        if ( (LA35_340=='D') ) {
                                            int LA35_346 = input.LA(10);

                                            if ( ((LA35_346>='#' && LA35_346<='$')||LA35_346=='-'||(LA35_346>='0' && LA35_346<='9')||(LA35_346>='A' && LA35_346<='Z')||LA35_346=='\\'||LA35_346=='_'||(LA35_346>='a' && LA35_346<='z')) ) {
                                                alt35=83;
                                            }
                                            else {
                                                alt35=29;}
                                        }
                                        else {
                                            alt35=83;}
                                    }
                                    else {
                                        alt35=83;}
                                }
                                else {
                                    alt35=83;}
                            }
                            else {
                                alt35=83;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                    }
                    break;
                default:
                    alt35=83;}

                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=29;}

            }
            break;
        case 'Z':
            {
            int LA35_19 = input.LA(2);

            if ( ((LA35_19>='#' && LA35_19<='$')||LA35_19=='-'||(LA35_19>='0' && LA35_19<='9')||(LA35_19>='A' && LA35_19<='Z')||LA35_19=='\\'||LA35_19=='_'||(LA35_19>='a' && LA35_19<='z')) ) {
                alt35=83;
            }
            else {
                alt35=30;}
            }
            break;
        case 'K':
            {
            switch ( input.LA(2) ) {
            case 'N':
                {
                int LA35_109 = input.LA(3);

                if ( (LA35_109=='O') ) {
                    int LA35_190 = input.LA(4);

                    if ( (LA35_190=='W') ) {
                        int LA35_241 = input.LA(5);

                        if ( ((LA35_241>='#' && LA35_241<='$')||LA35_241=='-'||(LA35_241>='0' && LA35_241<='9')||(LA35_241>='A' && LA35_241<='Z')||LA35_241=='\\'||LA35_241=='_'||(LA35_241>='a' && LA35_241<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=34;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'n':
                {
                int LA35_110 = input.LA(3);

                if ( (LA35_110=='o') ) {
                    int LA35_191 = input.LA(4);

                    if ( (LA35_191=='w') ) {
                        int LA35_242 = input.LA(5);

                        if ( ((LA35_242>='#' && LA35_242<='$')||LA35_242=='-'||(LA35_242>='0' && LA35_242<='9')||(LA35_242>='A' && LA35_242<='Z')||LA35_242=='\\'||LA35_242=='_'||(LA35_242>='a' && LA35_242<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=34;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case '#':
            case '$':
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt35=83;
                }
                break;
            default:
                alt35=34;}

            }
            break;
        case '(':
            {
            alt35=36;
            }
            break;
        case ')':
            {
            alt35=37;
            }
            break;
        case '[':
            {
            alt35=38;
            }
            break;
        case ']':
            {
            alt35=39;
            }
            break;
        case '{':
            {
            alt35=40;
            }
            break;
        case '}':
            {
            alt35=41;
            }
            break;
        case 'w':
            {
            int LA35_27 = input.LA(2);

            if ( (LA35_27=='o') ) {
                int LA35_112 = input.LA(3);

                if ( (LA35_112=='r') ) {
                    int LA35_192 = input.LA(4);

                    if ( (LA35_192=='d') ) {
                        switch ( input.LA(5) ) {
                        case '1':
                            {
                            int LA35_283 = input.LA(6);

                            if ( ((LA35_283>='#' && LA35_283<='$')||LA35_283=='-'||(LA35_283>='0' && LA35_283<='9')||(LA35_283>='A' && LA35_283<='Z')||LA35_283=='\\'||LA35_283=='_'||(LA35_283>='a' && LA35_283<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=44;}
                            }
                            break;
                        case '#':
                        case '$':
                        case '-':
                        case '0':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case '\\':
                        case '_':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z':
                            {
                            alt35=83;
                            }
                            break;
                        default:
                            alt35=45;}

                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'W':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA35_113 = input.LA(3);

                if ( (LA35_113=='r') ) {
                    int LA35_193 = input.LA(4);

                    if ( (LA35_193=='d') ) {
                        int LA35_244 = input.LA(5);

                        if ( ((LA35_244>='#' && LA35_244<='$')||LA35_244=='-'||(LA35_244>='0' && LA35_244<='9')||(LA35_244>='A' && LA35_244<='Z')||LA35_244=='\\'||LA35_244=='_'||(LA35_244>='a' && LA35_244<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=45;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'R':
                {
                int LA35_114 = input.LA(3);

                if ( (LA35_114=='I') ) {
                    int LA35_194 = input.LA(4);

                    if ( (LA35_194=='T') ) {
                        int LA35_245 = input.LA(5);

                        if ( (LA35_245=='E') ) {
                            int LA35_285 = input.LA(6);

                            if ( ((LA35_285>='#' && LA35_285<='$')||LA35_285=='-'||(LA35_285>='0' && LA35_285<='9')||(LA35_285>='A' && LA35_285<='Z')||LA35_285=='\\'||LA35_285=='_'||(LA35_285>='a' && LA35_285<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=48;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            default:
                alt35=83;}

            }
            break;
        case 'b':
            {
            int LA35_29 = input.LA(2);

            if ( (LA35_29=='o') ) {
                int LA35_115 = input.LA(3);

                if ( (LA35_115=='o') ) {
                    int LA35_195 = input.LA(4);

                    if ( (LA35_195=='l') ) {
                        int LA35_246 = input.LA(5);

                        if ( ((LA35_246>='#' && LA35_246<='$')||LA35_246=='-'||(LA35_246>='0' && LA35_246<='9')||(LA35_246>='A' && LA35_246<='Z')||LA35_246=='\\'||LA35_246=='_'||(LA35_246>='a' && LA35_246<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=46;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'c':
            {
            int LA35_30 = input.LA(2);

            if ( (LA35_30=='a') ) {
                int LA35_116 = input.LA(3);

                if ( (LA35_116=='s') ) {
                    int LA35_196 = input.LA(4);

                    if ( (LA35_196=='e') ) {
                        int LA35_247 = input.LA(5);

                        if ( ((LA35_247>='#' && LA35_247<='$')||LA35_247=='-'||(LA35_247>='0' && LA35_247<='9')||(LA35_247>='A' && LA35_247<='Z')||LA35_247=='\\'||LA35_247=='_'||(LA35_247>='a' && LA35_247<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=49;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'e':
            {
            int LA35_31 = input.LA(2);

            if ( (LA35_31=='s') ) {
                int LA35_117 = input.LA(3);

                if ( (LA35_117=='a') ) {
                    int LA35_197 = input.LA(4);

                    if ( (LA35_197=='c') ) {
                        int LA35_248 = input.LA(5);

                        if ( ((LA35_248>='#' && LA35_248<='$')||LA35_248=='-'||(LA35_248>='0' && LA35_248<='9')||(LA35_248>='A' && LA35_248<='Z')||LA35_248=='\\'||LA35_248=='_'||(LA35_248>='a' && LA35_248<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=50;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case '+':
            {
            alt35=51;
            }
            break;
        case '-':
            {
            switch ( input.LA(2) ) {
            case '>':
                {
                alt35=69;
                }
                break;
            case '-':
                {
                alt35=86;
                }
                break;
            default:
                alt35=52;}

            }
            break;
        case '*':
            {
            alt35=53;
            }
            break;
        case '/':
            {
            switch ( input.LA(2) ) {
            case '/':
                {
                alt35=86;
                }
                break;
            case '*':
                {
                alt35=85;
                }
                break;
            default:
                alt35=54;}

            }
            break;
        case 'm':
            {
            int LA35_36 = input.LA(2);

            if ( (LA35_36=='o') ) {
                int LA35_123 = input.LA(3);

                if ( (LA35_123=='d') ) {
                    int LA35_198 = input.LA(4);

                    if ( ((LA35_198>='#' && LA35_198<='$')||LA35_198=='-'||(LA35_198>='0' && LA35_198<='9')||(LA35_198>='A' && LA35_198<='Z')||LA35_198=='\\'||LA35_198=='_'||(LA35_198>='a' && LA35_198<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=55;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case '<':
            {
            switch ( input.LA(2) ) {
            case '<':
                {
                alt35=56;
                }
                break;
            case '=':
                {
                alt35=60;
                }
                break;
            case '-':
                {
                alt35=70;
                }
                break;
            default:
                alt35=62;}

            }
            break;
        case '>':
            {
            switch ( input.LA(2) ) {
            case '>':
                {
                alt35=57;
                }
                break;
            case '=':
                {
                alt35=61;
                }
                break;
            default:
                alt35=63;}

            }
            break;
        case '=':
            {
            alt35=58;
            }
            break;
        case '!':
            {
            int LA35_40 = input.LA(2);

            if ( (LA35_40=='=') ) {
                alt35=59;
            }
            else {
                alt35=75;}
            }
            break;
        case 'n':
            {
            int LA35_41 = input.LA(2);

            if ( (LA35_41=='e') ) {
                int LA35_133 = input.LA(3);

                if ( (LA35_133=='x') ) {
                    int LA35_199 = input.LA(4);

                    if ( (LA35_199=='t') ) {
                        int LA35_250 = input.LA(5);

                        if ( ((LA35_250>='#' && LA35_250<='$')||LA35_250=='-'||(LA35_250>='0' && LA35_250<='9')||(LA35_250>='A' && LA35_250<='Z')||LA35_250=='\\'||LA35_250=='_'||(LA35_250>='a' && LA35_250<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=64;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'u':
            {
            int LA35_42 = input.LA(2);

            if ( (LA35_42=='n') ) {
                int LA35_134 = input.LA(3);

                if ( (LA35_134=='i') ) {
                    int LA35_200 = input.LA(4);

                    if ( (LA35_200=='o') ) {
                        int LA35_251 = input.LA(5);

                        if ( (LA35_251=='n') ) {
                            int LA35_290 = input.LA(6);

                            if ( ((LA35_290>='#' && LA35_290<='$')||LA35_290=='-'||(LA35_290>='0' && LA35_290<='9')||(LA35_290>='A' && LA35_290<='Z')||LA35_290=='\\'||LA35_290=='_'||(LA35_290>='a' && LA35_290<='z')) ) {
                                alt35=83;
                            }
                            else {
                                alt35=65;}
                        }
                        else {
                            alt35=83;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
            }
            else {
                alt35=83;}
            }
            break;
        case 'i':
            {
            int LA35_43 = input.LA(2);

            if ( (LA35_43=='n') ) {
                int LA35_135 = input.LA(3);

                if ( ((LA35_135>='#' && LA35_135<='$')||LA35_135=='-'||(LA35_135>='0' && LA35_135<='9')||(LA35_135>='A' && LA35_135<='Z')||LA35_135=='\\'||LA35_135=='_'||(LA35_135>='a' && LA35_135<='z')) ) {
                    alt35=83;
                }
                else {
                    alt35=66;}
            }
            else {
                alt35=83;}
            }
            break;
        case '.':
            {
            int LA35_44 = input.LA(2);

            if ( (LA35_44=='.') ) {
                alt35=67;
            }
            else {
                alt35=68;}
            }
            break;
        case '|':
            {
            alt35=71;
            }
            break;
        case '&':
            {
            alt35=72;
            }
            break;
        case 'x':
            {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA35_138 = input.LA(3);

                if ( (LA35_138=='r') ) {
                    int LA35_202 = input.LA(4);

                    if ( ((LA35_202>='#' && LA35_202<='$')||LA35_202=='-'||(LA35_202>='0' && LA35_202<='9')||(LA35_202>='A' && LA35_202<='Z')||LA35_202=='\\'||LA35_202=='_'||(LA35_202>='a' && LA35_202<='z')) ) {
                        alt35=83;
                    }
                    else {
                        alt35=73;}
                }
                else {
                    alt35=83;}
                }
                break;
            case 'n':
                {
                int LA35_139 = input.LA(3);

                if ( (LA35_139=='o') ) {
                    int LA35_203 = input.LA(4);

                    if ( (LA35_203=='r') ) {
                        int LA35_253 = input.LA(5);

                        if ( ((LA35_253>='#' && LA35_253<='$')||LA35_253=='-'||(LA35_253>='0' && LA35_253<='9')||(LA35_253>='A' && LA35_253<='Z')||LA35_253=='\\'||LA35_253=='_'||(LA35_253>='a' && LA35_253<='z')) ) {
                            alt35=83;
                        }
                        else {
                            alt35=74;}
                    }
                    else {
                        alt35=83;}
                }
                else {
                    alt35=83;}
                }
                break;
            default:
                alt35=83;}

            }
            break;
        case ',':
            {
            alt35=76;
            }
            break;
        case ':':
            {
            int LA35_49 = input.LA(2);

            if ( (LA35_49==':') ) {
                alt35=79;
            }
            else {
                alt35=77;}
            }
            break;
        case ';':
            {
            alt35=78;
            }
            break;
        case '0':
            {
            int LA35_51 = input.LA(2);

            if ( (LA35_51=='B'||LA35_51=='D'||LA35_51=='H'||LA35_51=='O'||LA35_51=='b'||LA35_51=='d'||LA35_51=='h'||LA35_51=='o') ) {
                alt35=80;
            }
            else {
                alt35=82;}
            }
            break;
        case 'f':
            {
            int LA35_52 = input.LA(2);

            if ( (LA35_52=='\'') ) {
                alt35=81;
            }
            else {
                alt35=83;}
            }
            break;
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt35=82;
            }
            break;
        case 'D':
        case 'J':
        case 'M':
        case 'Q':
        case 'V':
        case '_':
        case 'a':
        case 'd':
        case 'g':
        case 'h':
        case 'j':
        case 'k':
        case 'l':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'v':
        case 'y':
        case 'z':
            {
            alt35=83;
            }
            break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
            {
            alt35=84;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( TOK_CTL_SPEC | TOK_CTL_STAR_SPEC | TOK_LTL_SPEC | TOK_INVAR_SPEC | TOK_RTCTL_STAR_SPEC | TOK_LDL_SPEC | TOK_EX | TOK_AX | TOK_EF | TOK_AF | TOK_EG | TOK_AG | TOK_EE | TOK_AA | TOK_BUNTIL | TOK_EBF | TOK_ABF | TOK_EBG | TOK_ABG | TOK_OP_FINALLY | TOK_OP_ONCE | TOK_OP_GLOBALLY | TOK_OP_HISTORICALLY | TOK_OP_NEXT | TOK_OP_PREV | TOK_UNTIL | TOK_SINCE | TOK_RELEASE | TOK_TRIGGERED | TOK_OP_NOTPREVNOT | TOK_OP_BFINALLY | TOK_OP_BGLOBALLY | TOK_BRELEASE | TOK_KNOW | TOK_SKNOW | TOK_LP | TOK_RP | TOK_LB | TOK_RB | TOK_LCB | TOK_RCB | TOK_FALSEEXP | TOK_TRUEEXP | TOK_WORD1 | TOK_WORD | TOK_BOOL | TOK_WAREAD | TOK_WAWRITE | TOK_CASE | TOK_ESAC | TOK_PLUS | TOK_MINUS | TOK_TIMES | TOK_DIVIDE | TOK_MOD | TOK_LSHIFT | TOK_RSHIFT | TOK_EQUAL | TOK_NOTEQUAL | TOK_LE | TOK_GE | TOK_LT | TOK_GT | TOK_NEXT | TOK_UNION | TOK_SETIN | TOK_TWODOTS | TOK_DOT | TOK_IMPLIES | TOK_IFF | TOK_OR | TOK_AND | TOK_XOR | TOK_XNOR | TOK_NOT | TOK_COMMA | TOK_COLON | TOK_SEMI | TOK_CONCATENATION | TOK_NUMBER_WORD | TOK_NUMBER_FRAC | TOK_NUMBER | TOK_ATOM | JTOK_WS | JTOK_MULTI_COMMENT | JTOK_LINE_COMMENT );", 35, 0, input);

            throw nvae;
        }

        switch (alt35) {
            case 1 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:10: TOK_CTL_SPEC
                {
                mTOK_CTL_SPEC(); 

                }
                break;
            case 2 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:23: TOK_CTL_STAR_SPEC
                {
                mTOK_CTL_STAR_SPEC(); 

                }
                break;
            case 3 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:41: TOK_LTL_SPEC
                {
                mTOK_LTL_SPEC(); 

                }
                break;
            case 4 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:54: TOK_INVAR_SPEC
                {
                mTOK_INVAR_SPEC(); 

                }
                break;
            case 5 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:69: TOK_RTCTL_STAR_SPEC
                {
                mTOK_RTCTL_STAR_SPEC(); 

                }
                break;
            case 6 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:89: TOK_LDL_SPEC
                {
                mTOK_LDL_SPEC(); 

                }
                break;
            case 7 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:102: TOK_EX
                {
                mTOK_EX(); 

                }
                break;
            case 8 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:109: TOK_AX
                {
                mTOK_AX(); 

                }
                break;
            case 9 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:116: TOK_EF
                {
                mTOK_EF(); 

                }
                break;
            case 10 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:123: TOK_AF
                {
                mTOK_AF(); 

                }
                break;
            case 11 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:130: TOK_EG
                {
                mTOK_EG(); 

                }
                break;
            case 12 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:137: TOK_AG
                {
                mTOK_AG(); 

                }
                break;
            case 13 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:144: TOK_EE
                {
                mTOK_EE(); 

                }
                break;
            case 14 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:151: TOK_AA
                {
                mTOK_AA(); 

                }
                break;
            case 15 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:158: TOK_BUNTIL
                {
                mTOK_BUNTIL(); 

                }
                break;
            case 16 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:169: TOK_EBF
                {
                mTOK_EBF(); 

                }
                break;
            case 17 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:177: TOK_ABF
                {
                mTOK_ABF(); 

                }
                break;
            case 18 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:185: TOK_EBG
                {
                mTOK_EBG(); 

                }
                break;
            case 19 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:193: TOK_ABG
                {
                mTOK_ABG(); 

                }
                break;
            case 20 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:201: TOK_OP_FINALLY
                {
                mTOK_OP_FINALLY(); 

                }
                break;
            case 21 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:216: TOK_OP_ONCE
                {
                mTOK_OP_ONCE(); 

                }
                break;
            case 22 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:228: TOK_OP_GLOBALLY
                {
                mTOK_OP_GLOBALLY(); 

                }
                break;
            case 23 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:244: TOK_OP_HISTORICALLY
                {
                mTOK_OP_HISTORICALLY(); 

                }
                break;
            case 24 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:264: TOK_OP_NEXT
                {
                mTOK_OP_NEXT(); 

                }
                break;
            case 25 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:276: TOK_OP_PREV
                {
                mTOK_OP_PREV(); 

                }
                break;
            case 26 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:288: TOK_UNTIL
                {
                mTOK_UNTIL(); 

                }
                break;
            case 27 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:298: TOK_SINCE
                {
                mTOK_SINCE(); 

                }
                break;
            case 28 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:308: TOK_RELEASE
                {
                mTOK_RELEASE(); 

                }
                break;
            case 29 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:320: TOK_TRIGGERED
                {
                mTOK_TRIGGERED(); 

                }
                break;
            case 30 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:334: TOK_OP_NOTPREVNOT
                {
                mTOK_OP_NOTPREVNOT(); 

                }
                break;
            case 31 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:352: TOK_OP_BFINALLY
                {
                mTOK_OP_BFINALLY(); 

                }
                break;
            case 32 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:368: TOK_OP_BGLOBALLY
                {
                mTOK_OP_BGLOBALLY(); 

                }
                break;
            case 33 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:385: TOK_BRELEASE
                {
                mTOK_BRELEASE(); 

                }
                break;
            case 34 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:398: TOK_KNOW
                {
                mTOK_KNOW(); 

                }
                break;
            case 35 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:407: TOK_SKNOW
                {
                mTOK_SKNOW(); 

                }
                break;
            case 36 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:417: TOK_LP
                {
                mTOK_LP(); 

                }
                break;
            case 37 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:424: TOK_RP
                {
                mTOK_RP(); 

                }
                break;
            case 38 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:431: TOK_LB
                {
                mTOK_LB(); 

                }
                break;
            case 39 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:438: TOK_RB
                {
                mTOK_RB(); 

                }
                break;
            case 40 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:445: TOK_LCB
                {
                mTOK_LCB(); 

                }
                break;
            case 41 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:453: TOK_RCB
                {
                mTOK_RCB(); 

                }
                break;
            case 42 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:461: TOK_FALSEEXP
                {
                mTOK_FALSEEXP(); 

                }
                break;
            case 43 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:474: TOK_TRUEEXP
                {
                mTOK_TRUEEXP(); 

                }
                break;
            case 44 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:486: TOK_WORD1
                {
                mTOK_WORD1(); 

                }
                break;
            case 45 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:496: TOK_WORD
                {
                mTOK_WORD(); 

                }
                break;
            case 46 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:505: TOK_BOOL
                {
                mTOK_BOOL(); 

                }
                break;
            case 47 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:514: TOK_WAREAD
                {
                mTOK_WAREAD(); 

                }
                break;
            case 48 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:525: TOK_WAWRITE
                {
                mTOK_WAWRITE(); 

                }
                break;
            case 49 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:537: TOK_CASE
                {
                mTOK_CASE(); 

                }
                break;
            case 50 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:546: TOK_ESAC
                {
                mTOK_ESAC(); 

                }
                break;
            case 51 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:555: TOK_PLUS
                {
                mTOK_PLUS(); 

                }
                break;
            case 52 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:564: TOK_MINUS
                {
                mTOK_MINUS(); 

                }
                break;
            case 53 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:574: TOK_TIMES
                {
                mTOK_TIMES(); 

                }
                break;
            case 54 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:584: TOK_DIVIDE
                {
                mTOK_DIVIDE(); 

                }
                break;
            case 55 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:595: TOK_MOD
                {
                mTOK_MOD(); 

                }
                break;
            case 56 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:603: TOK_LSHIFT
                {
                mTOK_LSHIFT(); 

                }
                break;
            case 57 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:614: TOK_RSHIFT
                {
                mTOK_RSHIFT(); 

                }
                break;
            case 58 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:625: TOK_EQUAL
                {
                mTOK_EQUAL(); 

                }
                break;
            case 59 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:635: TOK_NOTEQUAL
                {
                mTOK_NOTEQUAL(); 

                }
                break;
            case 60 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:648: TOK_LE
                {
                mTOK_LE(); 

                }
                break;
            case 61 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:655: TOK_GE
                {
                mTOK_GE(); 

                }
                break;
            case 62 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:662: TOK_LT
                {
                mTOK_LT(); 

                }
                break;
            case 63 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:669: TOK_GT
                {
                mTOK_GT(); 

                }
                break;
            case 64 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:676: TOK_NEXT
                {
                mTOK_NEXT(); 

                }
                break;
            case 65 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:685: TOK_UNION
                {
                mTOK_UNION(); 

                }
                break;
            case 66 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:695: TOK_SETIN
                {
                mTOK_SETIN(); 

                }
                break;
            case 67 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:705: TOK_TWODOTS
                {
                mTOK_TWODOTS(); 

                }
                break;
            case 68 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:717: TOK_DOT
                {
                mTOK_DOT(); 

                }
                break;
            case 69 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:725: TOK_IMPLIES
                {
                mTOK_IMPLIES(); 

                }
                break;
            case 70 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:737: TOK_IFF
                {
                mTOK_IFF(); 

                }
                break;
            case 71 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:745: TOK_OR
                {
                mTOK_OR(); 

                }
                break;
            case 72 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:752: TOK_AND
                {
                mTOK_AND(); 

                }
                break;
            case 73 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:760: TOK_XOR
                {
                mTOK_XOR(); 

                }
                break;
            case 74 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:768: TOK_XNOR
                {
                mTOK_XNOR(); 

                }
                break;
            case 75 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:777: TOK_NOT
                {
                mTOK_NOT(); 

                }
                break;
            case 76 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:785: TOK_COMMA
                {
                mTOK_COMMA(); 

                }
                break;
            case 77 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:795: TOK_COLON
                {
                mTOK_COLON(); 

                }
                break;
            case 78 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:805: TOK_SEMI
                {
                mTOK_SEMI(); 

                }
                break;
            case 79 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:814: TOK_CONCATENATION
                {
                mTOK_CONCATENATION(); 

                }
                break;
            case 80 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:832: TOK_NUMBER_WORD
                {
                mTOK_NUMBER_WORD(); 

                }
                break;
            case 81 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:848: TOK_NUMBER_FRAC
                {
                mTOK_NUMBER_FRAC(); 

                }
                break;
            case 82 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:864: TOK_NUMBER
                {
                mTOK_NUMBER(); 

                }
                break;
            case 83 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:875: TOK_ATOM
                {
                mTOK_ATOM(); 

                }
                break;
            case 84 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:884: JTOK_WS
                {
                mJTOK_WS(); 

                }
                break;
            case 85 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:892: JTOK_MULTI_COMMENT
                {
                mJTOK_MULTI_COMMENT(); 

                }
                break;
            case 86 :
                // /Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK2-TR/PARSERS/SPC.g:1:911: JTOK_LINE_COMMENT
                {
                mJTOK_LINE_COMMENT(); 

                }
                break;

        }

    }


 

}