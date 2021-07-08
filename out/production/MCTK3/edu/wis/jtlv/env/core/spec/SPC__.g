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

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1863
TOK_CTL_SPEC				: 'CTLSPEC' | 'SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1864
TOK_CTL_STAR_SPEC			: 'CTL*SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1865
TOK_LTL_SPEC				: 'LTLSPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1866
TOK_INVAR_SPEC				: 'INVARSPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1867
TOK_RTCTL_STAR_SPEC			: 'RTCTL*SPEC';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1868
TOK_CDLs_SPEC				: 'RTCDL*SPEC';

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1870
TOK_EX						: 'EX';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1871
TOK_AX						: 'AX';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1872
TOK_EF						: 'EF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1873
TOK_AF						: 'AF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1874
TOK_EG						: 'EG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1875
TOK_AG						: 'AG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1876
TOK_EE						: 'E';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1877
TOK_AA						: 'A';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1878
TOK_BUNTIL					: 'BU';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1879
TOK_EBF						: 'EBF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1880
TOK_ABF						: 'ABF';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1881
TOK_EBG						: 'EBG';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1882
TOK_ABG						: 'ABG';
// the last is the TLV notation.
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1884
TOK_OP_FINALLY				: 'F' | 'FINALLY' | 'EVENTUALLY';  // '<>' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1885
TOK_OP_ONCE				: 'O' | 'ONCE';  // '<_>' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1886
TOK_OP_GLOBALLY				: 'G' | 'GLOBALLY' | 'ALWAYS';  // '[]' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1887
TOK_OP_HISTORICALLY			: 'H' | 'HISTORICALLY'; // '[_]' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1888
TOK_OP_NEXT					: 'X' | 'NEXT'; // '()' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1889
TOK_OP_PREV					: 'Y' | 'PREV'; // '(_)' |
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1890
TOK_UNTIL					: 'Until' | 'U' | 'UNTIL';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1891
TOK_SINCE					: 'Since' | 'S' | 'SINCE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1892
TOK_RELEASE				: 'Awaits' | 'R' | 'RELEASE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1893
TOK_TRIGGERED				: 'Backto' | 'T' | 'TRIGGERED';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1894
TOK_OP_NOTPREVNOT			: 'Z';

// more bounded
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1897
TOK_OP_BFINALLY				: 'BF' | 'BFINALLY' | 'BEVENTUALLY';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1898
TOK_OP_BGLOBALLY			: 'BG' | 'BGLOBALLY' | 'BALWAYS';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1899
TOK_BRELEASE				: 'BR' | 'BRELEASE';


//epistemic
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1903
TOK_KNOW				: 'K' | 'KNOW' | 'Know';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1904
TOK_SKNOW				: 'SK' | 'SKNOW' | 'Sknow';

//TOK_MMIN					: 'MIN';// !!!
//TOK_MMAX					: 'MAX';// !!!

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1909
TOK_LP						: '(';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1910
TOK_RP						: ')';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1911
TOK_LB						: '[';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1912
TOK_RB						: ']';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1913
TOK_LCB						: '{';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1914
TOK_RCB						: '}';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1915
TOK_FALSEEXP				: 'FALSE';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1916
TOK_TRUEEXP					: 'TRUE';

// ALL NON SIMPLE OPERATOR SHOULD BE REMOVED OR ELSE THEY
// WOULD NOT HAVE MEANING IN BETWEEN TL STATEMENTS
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1920
TOK_WORD1					: 'word1';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1921
TOK_WORD					: 'word' | 'Word';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1922
TOK_BOOL					: 'bool';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1923
TOK_WAREAD					: 'READ';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1924
TOK_WAWRITE					: 'WRITE';// ???

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1926
TOK_CASE					: 'case';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1927
TOK_ESAC					: 'esac';// ???
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1928
TOK_PLUS					: '+';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1929
TOK_MINUS					: '-';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1930
TOK_TIMES					: '*';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1931
TOK_DIVIDE					: '/';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1932
TOK_MOD						: 'mod';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1933
TOK_LSHIFT					: '<<';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1934
TOK_RSHIFT					: '>>';
//TOK_LROTATE					: '<<<';
//TOK_RROTATE					: '>>>';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1937
TOK_EQUAL					: '=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1938
TOK_NOTEQUAL					: '!=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1939
TOK_LE						: '<=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1940
TOK_GE						: '>=';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1941
TOK_LT						: '<';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1942
TOK_GT						: '>';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1943
TOK_NEXT					: 'next';
//TOK_SELF					: 'self';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1945
TOK_UNION					: 'union';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1946
TOK_SETIN					: 'in';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1947
TOK_TWODOTS					: '..';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1948
TOK_DOT						: '.';

// basic logic operators...
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1951
TOK_IMPLIES					: '->';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1952
TOK_IFF						: '<->';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1953
TOK_OR						: '|';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1954
TOK_AND						: '&';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1955
TOK_XOR						: 'xor';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1956
TOK_XNOR					: 'xnor';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1957
TOK_NOT						: '!';

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1959
TOK_COMMA					: ',';//                     {yylval.lineno = yylineno; return(TOK_COMMA);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1960
TOK_COLON					: ':';//                     {yylval.lineno = yylineno; return(TOK_COLON);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1961
TOK_SEMI					: ';';//                     {yylval.lineno = yylineno; return(TOK_SEMI);}
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1962
TOK_CONCATENATION				: '::';//                    {yylval.lineno = yylineno; return(TOK_CONCATENATION);}

// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1964
TOK_LDL_OR					: '||';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1965
TOK_LDL_AND					: '&&';
// TOK_LDL_CONC					: '~';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1967
TOK_LDL_REPEAT_LB 				: '[*';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1968
TOK_LDL_TEST					: '?';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1969
TOK_LDL_SERE_SAT					: ':-';
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1970
TOK_LDL_SERE_IMP					: ':=';



/////////////////////////////////////////////////////////////////////
// basic JTLV extension - atoms, whitespaces and comments
/////////////////////////////////////////////////////////////////////

/* word constants */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1979
TOK_NUMBER_WORD					: '0' ('b' | 'B' | 'o' | 'O' | 'd' | 'D' | 'h' | 'H') ('0'..'9')* '_' ('0'..'9' | 'a'..'f' | 'A'..'F') ('0'..'9' | 'a'..'f' | 'A'..'F' | '_')*;

 /* real, fractional and exponential constants */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1982
TOK_NUMBER_FRAC					: ('f' | 'F') '\'' ('0'..'9')+ '/' ('0'..'9')+;

/* integer number */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1985
TOK_NUMBER						: ('0'..'9')+;

/* identifier */
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1988
TOK_ATOM						: ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '_' | '\\' | '$' | '#' | '-')*;


// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1991
JTOK_WS 						:   (   ' '
								|   '\t'
								|   '\r'
								|   '\n'
								)+
								{ $channel=HIDDEN; };
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 1997
JTOK_MULTI_COMMENT				: ('/*' (
								options { greedy=false;}
								:  // '\r' '\n' |
								'\r'
								|   '\n'
								|   ~('\n'|'\r')
								)*
								'*/'
								{$channel=HIDDEN;});
// $ANTLR src "/Users/lxy/Documents/Doc-LXY-iMac/RecentDoc/Development/JTLV/MCTK3/PARSERS/SPC.g" 2006
JTOK_LINE_COMMENT				: ('--' (~('\n'|'\r'))* (('\n'|'\r'('\n')?))? {$channel=HIDDEN;})
								| ('//' (~('\n'|'\r'))* (('\n'|'\r'('\n')?))? {$channel=HIDDEN;});

