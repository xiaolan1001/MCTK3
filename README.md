# MCTK 3.0.0
MCTK3 is a BDD-based symbolic model checker developed in Java and built upon JTLV. MCTK3 is fully compatible with the SMV input language used by NuSMV and nuXmv. It currently supports the verification and graphical counterexample generation of CDL*, which is a branching-time extension of LDL. RTCTL* (bounded) temporal operators are also supported by CDL*.

MCTK3 is now in alpha version 3.0.0, which contains all functions of MCTK2 for CTL* model checking and counterexample generation. You are welcome to test it and report any message to me by email.

Cheers,

Xiangyu Luo

College of Computer Science & Technology

Huaqiao University

Email: luoxy(at)hqu.edu.cn

June 6, 2021

=====================================================
1. How to run MCTK3 and test it?

(1) Download the package of MCTK3 from https://gitlab.com/hovertiger/mctk3 and decompress it. This package includes the Java source code and the jar file (https://gitlab.com/hovertiger/mctk3/-/raw/master/out/artifacts/MCTK3_jar/MCTK3.jar) of MCTK3. 

(2) There are two ways to run MCTK3. The first way is to run MCTK3.jar directly. The second way is to compile the source code by a Java Integrated Development Environment and run the main function of MCTKFrame.java.

(3) In the GUI window of MCTK3, open the SMV file btp_tr.smv (the model of the bit transmission protocol) in the directory "testcases".
 
(4) Input and verify some RTCTL* formulas of interest. A graphical counterexample will be generated after a formula is checked false.

2. Simple usage of MCTK3

(1) The input language of MCTK3 is fully compatible with the SMV input language used by NuSMV and nuXmv. We refer to [the NuSMV 2.6 Tutorial](http://nusmv.fbk.eu/NuSMV/tutorial/v26/tutorial.pdf) for more details.

(2) The syntax of CDL* formulas is fully compatible with the syntax of LTL in NuSMV and nuXmv. The syntax of the extended path quantifiers and bounded temporal operators are listed as follows: 

	(a) existential path quantifier: E (f)
	(b) universal path quantifier: A (f)
	(c) bounded until: f BU a..b g (meaning: f holds until g holds in the interval [a,b])
	(d) bounded release: f BR a..b g (meaning: f release g in [a,b])
	(e) We have that f BR a..b g == !(!f BU a..b !g)
	(f) bounded finally: BF a..b f (f will hold at some position in [a,b )
	(e) bounded globally: BG a..b f (f always hold in [a,b])
