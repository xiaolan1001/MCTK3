package edu.wis.jtlv.env.core.spec;

public enum InternalSpecLanguage { // all language includes real-time intervals and epistemic modalities
	UNDEF, // undefined in this spec
	CTL,
	LTL,
	RTCTLs, // RTCTL*
	ATLs, // ATL*
	LDL, // RTLDL + RTCTL*
	INVAR
}
