lexer grammar SPC;
@members {
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
}
@header {
package edu.wis.jtlv.env.core.spec;
import edu.wis.jtlv.env.Env;
}

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1765
TOK_CTL_SPEC				: 'CTLSPEC' | 'SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1766
TOK_CTL_STAR_SPEC			: 'CTL*SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1767
TOK_LTL_SPEC				: 'LTLSPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1768
TOK_INVAR_SPEC				: 'INVARSPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1769
TOK_RTCTL_STAR_SPEC			: 'RTCTL*SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1770
TOK_LDL_SPEC				: 'RTCDL*SPEC';

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1772
TOK_EX						: 'EX';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1773
TOK_AX						: 'AX';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1774
TOK_EF						: 'EF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1775
TOK_AF						: 'AF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1776
TOK_EG						: 'EG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1777
TOK_AG						: 'AG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1778
TOK_EE						: 'E';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1779
TOK_AA						: 'A';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1780
TOK_BUNTIL					: 'BU';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1781
TOK_EBF						: 'EBF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1782
TOK_ABF						: 'ABF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1783
TOK_EBG						: 'EBG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1784
TOK_ABG						: 'ABG';
// the last is the TLV notation.
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1786
TOK_OP_FINALLY				: 'F' | 'FINALLY' | 'EVENTUALLY';  // '<>' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1787
TOK_OP_ONCE				: 'O' | 'ONCE';  // '<_>' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1788
TOK_OP_GLOBALLY				: 'G' | 'GLOBALLY' | 'ALWAYS';  // '[]' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1789
TOK_OP_HISTORICALLY			: 'H' | 'HISTORICALLY'; // '[_]' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1790
TOK_OP_NEXT					: 'X' | 'NEXT'; // '()' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1791
TOK_OP_PREV					: 'Y' | 'PREV'; // '(_)' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1792
TOK_UNTIL					: 'Until' | 'U' | 'UNTIL';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1793
TOK_SINCE					: 'Since' | 'S' | 'SINCE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1794
TOK_RELEASE				: 'Awaits' | 'R' | 'RELEASE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1795
TOK_TRIGGERED				: 'Backto' | 'T' | 'TRIGGERED';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1796
TOK_OP_NOTPREVNOT			: 'Z';

// more bounded
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1799
TOK_OP_BFINALLY				: 'BF' | 'BFINALLY' | 'BEVENTUALLY';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1800
TOK_OP_BGLOBALLY			: 'BG' | 'BGLOBALLY' | 'BALWAYS';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1801
TOK_BRELEASE				: 'BR' | 'BRELEASE';


//epistemic
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1805
TOK_KNOW				: 'K' | 'KNOW' | 'Know';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1806
TOK_SKNOW				: 'SK' | 'SKNOW' | 'Sknow';

//TOK_MMIN					: 'MIN';// !!!
//TOK_MMAX					: 'MAX';// !!!

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1811
TOK_LP						: '(';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1812
TOK_RP						: ')';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1813
TOK_LB						: '[';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1814
TOK_RB						: ']';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1815
TOK_LCB						: '{';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1816
TOK_RCB						: '}';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1817
TOK_FALSEEXP				: 'FALSE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1818
TOK_TRUEEXP					: 'TRUE';

// ALL NON SIMPLE OPERATOR SHOULD BE REMOVED OR ELSE THEY
// WOULD NOT HAVE MEANING IN BETWEEN TL STATEMENTS
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1822
TOK_WORD1					: 'word1';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1823
TOK_WORD					: 'word' | 'Word';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1824
TOK_BOOL					: 'bool';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1825
TOK_WAREAD					: 'READ';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1826
TOK_WAWRITE					: 'WRITE';// ???

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1828
TOK_CASE					: 'case';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1829
TOK_ESAC					: 'esac';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1830
TOK_PLUS					: '+';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1831
TOK_MINUS					: '-';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1832
TOK_TIMES					: '*';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1833
TOK_DIVIDE					: '/';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1834
TOK_MOD						: 'mod';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1835
TOK_LSHIFT					: '<<';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1836
TOK_RSHIFT					: '>>';
//TOK_LROTATE					: '<<<';
//TOK_RROTATE					: '>>>';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1839
TOK_EQUAL					: '=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1840
TOK_NOTEQUAL					: '!=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1841
TOK_LE						: '<=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1842
TOK_GE						: '>=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1843
TOK_LT						: '<';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1844
TOK_GT						: '>';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1845
TOK_NEXT					: 'next';
//TOK_SELF					: 'self';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1847
TOK_UNION					: 'union';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1848
TOK_SETIN					: 'in';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1849
TOK_TWODOTS					: '..';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1850
TOK_DOT						: '.';

// basic logic operators...
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1853
TOK_IMPLIES					: '->';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1854
TOK_IFF						: '<->';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1855
TOK_OR						: '|';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1856
TOK_AND						: '&';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1857
TOK_XOR						: 'xor';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1858
TOK_XNOR					: 'xnor';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1859
TOK_NOT						: '!';

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1861
TOK_COMMA					: ',';//                     {yylval.lineno = yylineno; return(TOK_COMMA);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1862
TOK_COLON					: ':';//                     {yylval.lineno = yylineno; return(TOK_COLON);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1863
TOK_SEMI					: ';';//                     {yylval.lineno = yylineno; return(TOK_SEMI);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1864
TOK_CONCATENATION				: '::';//                    {yylval.lineno = yylineno; return(TOK_CONCATENATION);}

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1866
TOK_LDL_OR					: '||';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1867
TOK_LDL_AND					: '&&';
// TOK_LDL_CONC					: '~';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1869
TOK_LDL_REPEAT_LB 				: '[*';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1870
TOK_LDL_TEST					: '?';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1871
TOK_LDL_SAT					: '#';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1872
TOK_LDL_IMP					: '@';



/////////////////////////////////////////////////////////////////////
// basic JTLV extension - atoms, whitespaces and comments
/////////////////////////////////////////////////////////////////////

/* word constants */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1881
TOK_NUMBER_WORD					: '0' ('b' | 'B' | 'o' | 'O' | 'd' | 'D' | 'h' | 'H') ('0'..'9')* '_' ('0'..'9' | 'a'..'f' | 'A'..'F') ('0'..'9' | 'a'..'f' | 'A'..'F' | '_')*;

 /* real, fractional and exponential constants */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1884
TOK_NUMBER_FRAC					: ('f' | 'F') '\'' ('0'..'9')+ '/' ('0'..'9')+;

/* integer number */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1887
TOK_NUMBER						: ('0'..'9')+;

/* identifier */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1890
TOK_ATOM						: ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_' | '\\' | '$' | '#' | '-')*;


// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1893
JTOK_WS 						:   (   ' '
								|   '\t'
								|   '\r'
								|   '\n'
								)+
								{ $channel=HIDDEN; };
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1899
JTOK_MULTI_COMMENT				: ('/*' (
								options { greedy=false;}
								:  // '\r' '\n' |
								'\r'
								|   '\n'
								|   ~('\n'|'\r')
								)*
								'*/'
								{$channel=HIDDEN;});
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1908
JTOK_LINE_COMMENT				: ('--' (~('\n'|'\r'))* (('\n'|'\r'('\n')?))? {$channel=HIDDEN;})
								| ('//' (~('\n'|'\r'))* (('\n'|'\r'('\n')?))? {$channel=HIDDEN;});

