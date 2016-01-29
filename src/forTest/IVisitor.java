package forTest;

import api.IClass;
import api.IDeclaration;
import api.IField;
import api.IMethod;
import api.IRelation;

public interface IVisitor {
	void visit(IClass c);
	void visit(IRelation r);
	
	void visit(String s);
	
	
	void visit(IMethod m);
	void visit(IDeclaration d);
	void visit(IField f);
	void postVisit(IClass c);
}