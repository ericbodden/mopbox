package de.bodden.chakalaka.bpagent;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.bodden.chakalaka.bpagentshared.IFSM;


public class SingleConnectionListener implements Runnable {
	
	private final Socket s;

	public SingleConnectionListener(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		System.out.println("New connection to "+s.getInetAddress());
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(s.getInputStream());
			Object o;
			while((o=ois.readObject())!=null) {
				FSMConverter.installFSM((IFSM)o);
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
