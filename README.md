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

(1) Download the package of MCTK2 from https://gitlab.com/hovertiger/mctk2-tr and decompress it.

(2) Run the main function of MCTK2Frame.java.

(3) In the GUI window of MCTK2, open the SMV file btp_tr.smv (the model of the bit transmission protocol) in the directory "testcases".
 
(4) Input and verify some RTCTL* formulas of interest. A graphical counterexample will be generated after a formula is checked false.

2. Simple usage of MCTK2

(1) The input language of MCTK2 is fully compatiable with the SMV input language used by NuSMV and nuXmv. We refer to [the NuSMV 2.6 Tutorial](http://nusmv.fbk.eu/NuSMV/tutorial/v26/tutorial.pdf) for more details.

(2) Currently the verified formulas cannot be written in SMV file. You should write them in java source code like TR_Experiment.java. 

(3) The syntax of RTCTL* formulas is fully compatiable with the syntax of LTL in NuSMV and nuXmv. The syntax of the extended bounded temporal operators are listed as follows: 

	(a) bounded until: f BU a..b g (meaning: f holds until g holds in the interval [a,b])
	(b) bounded release: f BR a..b g (meaning: f release g in [a,b])
	(c) We have that f BR a..b g == !(!f BR a..b !g)
	(d) bounded finally: BF a..b f (f will hold at some position in [a,b )
	(e) bounded globally: BG a..b f (f always hold in [a,b])

