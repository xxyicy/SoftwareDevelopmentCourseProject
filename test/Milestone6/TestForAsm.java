package Milestone6;

import static org.junit.Assert.assertEquals;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import api.IClass;
import api.IDeclaration;
import api.IModel;
import api.IRelation;
import asm.ClassDeclarationVisitor;
import asm.ClassFieldVisitor;
import asm.ClassMethodVisitor;
import impl.Clazz;
import impl.Model;
import pattern.api.IDetector;
import pattern.impl.AdapterDetector;
import pattern.impl.CompositeDetector;
import pattern.impl.DecoratorDetector;
import visitor.impl.GraphVizOutputStream;

public class TestForAsm {
	private IModel m;
	private IClass c;
	private ClassVisitor visitor;
	private GraphVizOutputStream v;

	@Before
	public void setUp() throws Exception {
		m = new Model();
		c = new Clazz();
		ClassVisitor decVisitor = new ClassDeclarationVisitor(Opcodes.ASM5, c, m);
		ClassVisitor fieldVisitor = new ClassFieldVisitor(Opcodes.ASM5, decVisitor, c, m);
		visitor = new ClassMethodVisitor(Opcodes.ASM5, fieldVisitor, c, m);
		v = new GraphVizOutputStream(new FileOutputStream("./output/output.txt"));
	}

	@Test
	public void TestDecorator1() throws Exception {
		List<String> cs = new ArrayList<>();
		cs.add("problem/sprites/AbstractSprite");

		IModel m = new Model();

		IDetector detect = new CompositeDetector();
		List<String> classRead = new ArrayList<>();

		while (!cs.isEmpty()) {
			String clazz = cs.get(0);
			cs.remove(0);

			ClassReader reader = new ClassReader(clazz);
			IClass c = new Clazz();
			// make class declaration visitor to get superclass and interfaces
			ClassVisitor decVisitor = new ClassDeclarationVisitor(Opcodes.ASM5, c, m, cs);
			// DECORATE declaration visitor with field visitor
			ClassVisitor fieldVisitor = new ClassFieldVisitor(Opcodes.ASM5, decVisitor, c, m);
			// DECORATE field visitor with method visitor
			ClassVisitor methodVisitor = new ClassMethodVisitor(Opcodes.ASM5, fieldVisitor, c, m);

			reader.accept(methodVisitor, ClassReader.EXPAND_FRAMES);

			if (!c.getName().contains("$") && !classRead.contains(clazz)) {
				m.addClass(c);
				classRead.add(clazz);
			}

		}

		detect.detect(m);
		System.out.println(m);
		for (IClass c : m.getClasses()) {
		
			if (c.getName().equals("problem/sprites/AbstarctSprite")) {
				System.out.println(c.getName() + "blah");
				assertEquals(true, c.getTags().contains("composite"));
			}

		}

	}
}