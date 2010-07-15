package de.bodden.chakalaka.bpagent;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.instrument.Instrumentation;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import de.bodden.chakalaka.bpagentshared.IFSM;


public class SingleConnectionListener implements Runnable {
	
	private final Socket s;
	private final Instrumentation inst;

	public SingleConnectionListener(Socket s, Instrumentation inst) {
		this.s = s;
		this.inst = inst;
	}

	@Override
	public void run() {
		System.out.println("New connection to "+s.getInetAddress());
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(s.getInputStream());
			Object o;
			while((o=ois.readObject())!=null) {
				IFSM fsm = (IFSM)o;
				
				FSMConverter.installFSM(fsm);
				
				Set<String> classNames = new HashSet<String>();
				for(String s: fsm.symbols()) {
					classNames.add(fsm.classNameForSymbol(s));
				}
				for(Class<?> c: inst.getAllLoadedClasses()) {
					if(inst.isModifiableClass(c) && classNames.contains(c.getName())) {
						System.out.println("Retransforming loaded class "+c.getName());
						inst.retransformClasses(c);
					}
				}
			}
		} catch(EOFException e) {
			//end of input; do nothing
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ois!=null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
