package de.bodden.chakalaka.bpagent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IncomingConnectionListener implements Runnable {

	@Override
	public void run() {
		
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(12345);
			System.out.println("Waiting for connections on port "+ss.getLocalPort());
			while(true) {
				Socket s = ss.accept();
				Thread thread = new Thread(new SingleConnectionListener(s));
				thread.setDaemon(true);
				thread.start();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(ss!=null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
