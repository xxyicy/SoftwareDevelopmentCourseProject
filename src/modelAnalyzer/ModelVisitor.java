package modelAnalyzer;

import java.util.ArrayList;
import java.util.List;

import api.IClass;
import api.IField;
import api.IMethod;
import api.IModel;
import api.IPattern;
import api.IRelation;

public class ModelVisitor extends AbstractModelVisitor {
	private List<IPattern> patterns;

	
	
	public ModelVisitor(IModel m){
		super(m);
		this.patterns = new ArrayList<IPattern>();
		
	}
	
	public void setPatterns(List<IPattern> patterns){
		for(IRelation r : this.m.getRelations()){
			r.setVisible(false);
		}
	
		for(IClass c : this.m.getClasses()){
			c.setVisible(false);
		}
		
		System.out.println(this.m);
		this.patterns = patterns;
		this.visitModel();
	}
	
	
	@Override
	protected void visitClass(IClass c) {
		if(this.patterns.isEmpty()){
			c.setVisible(true);
		}
		
	}

	@Override
	protected void visitMethod(IMethod m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void visitField(IField f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void visitPattern(IPattern p) {
		
		if(this.patterns.contains(p)){
			System.out.println("Found pattern " + p);
			for(IClass c: p.getClasses()){
				c.setVisible(true);
			}
		}
		
	}

	@Override
	protected void visitRelation(IRelation r) {
		if(this.patterns.isEmpty()){
			r.setVisible(true);
		}
		else{
			IClass from = this.getClassByName(m, r.getFrom());
			IClass to = this.getClassByName(m, r.getTo());
			
			if(from != null && to!=null){
				
				if(from.isVisible() && to.isVisible()){
					r.setVisible(true);
					
				}
			}	
			
		}
	}
	
	
	private IClass getClassByName(IModel m, String name) {
		name = name.replaceAll("[.]", "/");
		for (IClass c : m.getClasses()) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	
}
