
// Premises: spec is a NNF STATE formula; n |= spec
// Results: generate witnesses for state formula
boolean witness(Spec spec, Node n){
	if(!specNeedExplainEE(spec)){
		// spec itself is attached to n directly
		// Note that in this case operator ! only restricts assertions
		n.A=n.A union "spec";
	}else{ 
		// spec is the composition of propositional formulas and Ef, composed by /\ or \/
		if(spec=f/\g){
			witness(f,n);
			witness(g,n);
		}else if(spec=f\/g){
			if(n|=f) witness(f,n); else witness(g,n);
		}else if(spec=Ef){
			witnessE(f,n);
		}
	} 
}

// Premises: n |= E spec
// Results: generate a new fair lasso path pi from n; explain spec over pi
boolean witnessE(Spec spec, Node n){
	if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){
		witness(spec,n);
	}else if(specNeedExplainEE(spec)){
		if(spec=f/\g){
			witnessE(f,n);  witnessE(g,n);
		}else if(spec=f\/g){ // if there exists one prop formula among f and g, explain it 
			if(f is a prop formula) {p=f; q=g;} else {p=g; q=f;}
			if(n|=p) witnessE(p,n); else witnessE(q,n);
		}else if(spec=Ef){
			witnessE(f,n);
		}
	}else{ // !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec)
		if(needCreatePath(spec,n)){ 
			create a feasible lasso path pi from n such that pi|=spec;
			explainPath(spec, pi, 0);
		}else{explainOnNode(spec,n)};
	}
}

// Premises: !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec); n|=E spec
// Results: return true if spec need to be explained over a new lasso path
// 			return false if it is enough to explain spec only over node n
boolean needCreatePath(Spec spec, Node n){
	if(spec is a state formula) return false;
	if(spec=f/\g) return needCreatePath(f) || needCreatePath(g);
	if(spec=f\/g){
		if(f is prop formula && n|=f) return false;
		else if(g is prop formula && n|=g) return false;
		if(n|=f) 
			return needCreatePath(f);
		else // n|=g
			return needCreatePath(g);
	}
	if(spec is a principally temporal formula) return true;
	return fasle;
}

// Premises: !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec); n|=E spec; !needCreatePath(spec,n)
// Results: explain spec only over node n
boolean explainOnNode(Spec spec, Node n){
	if(spec is a state formula) witness(spec,n);
	if(spec=f/\g) return explainOnNode(f,n) || explainOnNode(g,n);
	if(spec=f\/g){
		if(f is prop formula && n|=f) return witness(f,n);
		else if(g is prop formula && n|=g) return witness(g,n);
		if(n|=f) 
			return explainOnNode(f,n);
		else // n|=g
			return explainOnNode(g,n);
	}
	if(spec is a principally temporal formula) return false;
	return true;	
}

// Premises: path^pos |= spec
// Results: attached necessary satisfied formulas at some nodes over the suffix path^startPos
boolean explainPath(Spec spec, path, int pos){
	if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){
		//path[pos].A=path[pos].A union "spec";
		witness(spec,path[pos]);
	}else if(specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){
		if(spec=f/\g){
			explainPath(f,path,pos);
			explainPath(g,path,pos);
		}else if(spec=f\/g){
			if(f is prop formula) {p=f; q=g;} else {p=g; q=f;}
			if(n|=p) explainPath(p,path,pos); else explainPath(q,path,pos);
		}else if(spec=Ef){
			witnessE(f,path[pos]);
		}
	}else{ // spec is a principally temporal formula spec=Xf, fUg, fRg, fU a..b g, or f R a..b g
		explain spec according to its semantics over path^pos;
	}
}