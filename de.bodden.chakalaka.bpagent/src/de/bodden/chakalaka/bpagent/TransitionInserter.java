package de.bodden.chakalaka.bpagent;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.bodden.chakalaka.bpagent.IMonitorTemplateRegistry.SymbolAndTemplateNumber;

/**
 * Inserts transitions in the classes to be monitored.
 */
public class TransitionInserter implements
		ClassFileTransformer {
	
	@Override
	public byte[] transform(ClassLoader loader, final String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if(!Registry.v().needsMonitoring(className)) return null;				
		
		try {
	    	// scan class binary format to find fields for toString() method
	    	ClassReader creader = new ClassReader(classfileBuffer);
	    	ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
	    	
	        ClassVisitor visitor = new ClassAdapter(writer) {
	        	
	        	public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
	        		//delegate
	        		MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
	        		
	        		mv = new MethodAdapter(mv) {

	        			public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
	        				super.visitMethodInsn(arg0, arg1, arg2, arg3);	        				
	        			}
	        			
	        			public void visitLineNumber(int num, Label l) {
	        				if(Registry.v().needsMonitoring(className,num)) {
	        					System.out.println("Instrumenting line "+num+" of class "+className);
	        					String name = Registry.class.getName().replace('.', '/');
	        					for(SymbolAndTemplateNumber symAndNum: Registry.v().transitionInfos(className, num)) {
	        						mv.visitLdcInsn(symAndNum.sym);
	        						mv.visitLdcInsn(symAndNum.templateNumber);
	        						mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "notify", "(Ljava/lang/String;I)V"); 
	        					}
	        				}
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