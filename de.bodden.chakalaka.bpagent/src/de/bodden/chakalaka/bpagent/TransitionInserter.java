package de.bodden.chakalaka.bpagent;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

public class TransitionInserter implements
		ClassFileTransformer {
	@Override
	public byte[] transform(ClassLoader loader, final String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if(!className.equals("Main")) return null;		
		
	    try {
	    	// scan class binary format to find fields for toString() method
	    	ClassReader creader = new ClassReader(classfileBuffer);
	    	ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
	    	
	        ClassVisitor visitor = new ClassAdapter(writer) {
	        	
	        	public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
	        		//delegate
	        		MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
	        		if(!methodName.equals("main")) return mv;
	        		
	        		mv = new MethodAdapter(mv) {
	        			
	        			int i = 0;
	        			public void visitInsn(int arg0) {
	        				if(i==0) {
	        					Label l = new Label();
	        					mv.visitLabel(l);
	        					mv.visitLineNumber(7, l);
	        				}
	        				i++;
	        				super.visitInsn(arg0);
	        			}
	        		};

	        		return mv;
	        	};
	        	
	        };
	        creader.accept(visitor, 0);
	        try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("Main.class"));
				bos.write(writer.toByteArray());
				bos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return writer.toByteArray();
	    } catch (IllegalStateException e) {
	        throw new IllegalClassFormatException("Error: " + e.getMessage() +
	            " on class " + className);
		} catch(RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
}