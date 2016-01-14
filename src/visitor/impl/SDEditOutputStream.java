package visitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import api.IMethod;
import api.IMethodRelation;
import app.Utility;

public class SDEditOutputStream {
	private Map<String, String> declaration;
	private List<String> initializedArray;
	private StringBuffer classes;
	private StringBuffer content;
	private String name;
	private int depth;
	// private int currentDepth;
	private Hashtable<IMethod, IMethodRelation> methodRelations;
	private int count = 0;

	public SDEditOutputStream(int depth, String name, Hashtable<IMethod, IMethodRelation> methodRelations) {
		this.declaration = new HashMap<String, String>();
		this.content = new StringBuffer();
		this.name = name;
		this.depth = depth;
		this.methodRelations = methodRelations;
		this.initializedArray = new ArrayList<String>();
		this.classes = new StringBuffer();
		// this.currentDepth = 0;
		IMethodRelation start = getMethodRelationbyName(this.name);
		System.out.println(start.toString());
		parse(start, 0);
	}

	public void parse(IMethodRelation start, int depth) {
		if (depth == this.depth) {
			return;
		}
		if (this.initializedArray.contains(start.getFrom().getClassName())) {
			classes.append("/");
		}

		String name = "arg" + count;
		this.declaration.put(start.getFrom().getClassName(), name);
		classes.append(name + ":" + Utility.simplifyClassName(start.getFrom().getClassName()) + "\n");
		count++;
		List<IMethod> start_to = start.getTo();
		for (int i = 0; i < start_to.size(); i++) {
			this.content.append(this.declaration.get(start.getFrom().getClassName()));
			this.content.append(":");
			if (this.declaration.get(start_to.get(i).getClassName()) == null) {
				if (start_to.get(i).getName().equals("init")) {
					classes.append("/");
				}
				name = "arg" + count;
				this.declaration.put(start_to.get(i).getClassName(), name);
				classes.append(name + ":" + Utility.simplifyClassName(start_to.get(i).getClassName()) + "\n");
				count++;
			}
			this.content.append(this.declaration.get(start_to.get(i).getClassName()));
			this.content.append(".");
			if (start_to.get(i).getName().equals("init")) {

				this.content.append("new\n");
				this.initializedArray.add(start_to.get(i).getClassName());
			} else {
				this.content.append(Utility.simplifyClassName(start_to.get(i).getName()) + "(");
				List<String> params = start_to.get(i).getParamTypes();
				for (int j = 0; j < params.size() - 1; j++) {
					this.content.append(Utility.simplifyType(params.get(j)) + ",");
				}
				if (!params.isEmpty())
					this.content.append(Utility.simplifyType(params.get(params.size() - 1)) + ")");
				else {
					this.content.append(")");
				}
				this.content.append(":");
				this.content.append(Utility.simplifyClassName(start_to.get(i).getType()) + "\n");
			}
			if (methodRelations.get(start_to.get(i)) != null) {
				parse(methodRelations.get(start_to.get(i)), depth++);
			}

		}
	}

	public IMethodRelation getMethodRelationbyName(String name) {
		System.out.println(name);
		int lastDotIndex = name.lastIndexOf(".");
		String className = name.substring(0, lastDotIndex);
		className.replaceAll("/", ".");
		System.out.println(className);
		String methodNameAndParams = name.substring(lastDotIndex + 1);
		int firstparIndex = methodNameAndParams.lastIndexOf("(");
		String method = methodNameAndParams.substring(0, firstparIndex);
		System.out.println(method);
		String params = methodNameAndParams.substring(firstparIndex + 1, methodNameAndParams.length() - 1);
		String[] paramArr = new String[0];
		if (params.contains(",")) {
			paramArr = params.split(",");
		}
		String[] paramTypeArr = new String[paramArr.length];
		for (int i = 0; i < paramArr.length; i++) {
			paramTypeArr[i] = paramArr[i].split(" ")[0];
			int index = paramTypeArr[i].lastIndexOf("<");
			if (index > 0) {
				paramTypeArr[i] = paramTypeArr[i].substring(0, index);
				System.out.println(paramTypeArr[i]);
			} else {
				System.out.println(paramTypeArr[i]);
			}
		}

		for (IMethodRelation m : this.methodRelations.values()) {
			String newClassName = m.getFrom().getClassName().replaceAll("/", ".");
			if (m.getFrom().getName().equals(method) && newClassName.equals(className)) {
				System.out.println("true");
				System.out.println(m.getFrom().getParamTypes().size());
				System.out.println(paramTypeArr.length);
				if (m.getFrom().getParamTypes().size() != paramTypeArr.length) {
					continue;
				}
				System.out.println("true");
				boolean isTheSame = true;
				for (int i = 0; i < m.getFrom().getParamTypes().size(); i++) {
					String param = Utility.simplifyClassName(m.getFrom().getParamTypes().get(i));
					System.out.println(param + " " + paramTypeArr[i]);
					if (!param.equals(paramTypeArr[i])) {
						isTheSame = false;
					}
				}
				if (isTheSame) {
					return m;
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return this.classes.toString() + "\n" + this.content.toString();

	}

}
