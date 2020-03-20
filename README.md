# MCTK2 (2.0.0)
MCTK2 is a BDD-based symbolic model checker developed in Java and built upon JTLV. MCTK2 is fully compatiable with the SMV input language used by NuSMV and nuXmv. It currently supports the verification and graphical counterexample generation of RTCTL*, which is an extension of CTL* with bounded temporal operators.

MCTK2 is now in alpha version 2.0.0. You are welcome to test it and report any message to me by email.

Cheers,

Xiangyu Luo

College of Computer Science & Technology

Huaqiao University

Email: luoxy(at)hqu.edu.cn

March 8, 2020

=====================================================
1. How to run MCTK2 and test it?

(1) Download the package of MCTK2 from https://gitlab.com/hovertiger/mctk2-tr and decompress it. This package includes the Java source code and the jar file (https://gitlab.com/hovertiger/mctk2-tr/-/raw/master/out/artifacts/MCTK2_TR_jar/MCTK2-TR.jar) of MCTK2. 

(2) There are two ways to run MCTK2. The first way is to run MCTK2-TR.jar directly. The second way is to compile the source code by a Java Integrated Development Environment and run the main function of MCTK2Frame.java.

(3) In the GUI window of MCTK2, open the SMV file btp_tr.smv (the model of the bit transmission protocol) in the directory "testcases".
 
(4) Input and verify some RTCTL* formulas of interest. A graphical counterexample will be generated after a formula is checked false.

2. Simple usage of MCTK2

(1) The input language of MCTK2 is fully compatible with the SMV input language used by NuSMV and nuXmv. We refer to [the NuSMV 2.6 Tutorial](http://nusmv.fbk.eu/NuSMV/tutorial/v26/tutorial.pdf) for more details.

(2) The syntax of RTCTL* formulas is fully compatible with the syntax of LTL in NuSMV and nuXmv. The syntax of the extended path quantifiers and bounded temporal operators are listed as follows: 

	(a) existential path quantifier: E (f)
	(b) universal path quantifier: A (f)
	(c) bounded until: f BU a..b g (meaning: f holds until g holds in the interval [a,b])
	(d) bounded release: f BR a..b g (meaning: f release g in [a,b])
	(e) We have that f BR a..b g == !(!f BR a..b !g)
	(f) bounded finally: BF a..b f (f will hold at some position in [a,b )
	(e) bounded globally: BG a..b f (f always hold in [a,b])

