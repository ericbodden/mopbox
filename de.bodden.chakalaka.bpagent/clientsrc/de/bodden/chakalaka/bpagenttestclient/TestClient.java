package de.bodden.chakalaka.bpagenttestclient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.bodden.chakalaka.bpagentshared.IFSM;

public class TestClient {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 12345);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		
		oos.writeObject(new IFSM() {

			private static final long serialVersionUID = 1L;

			@Override
			public Set<String> symbols() {
				return new HashSet<String>(Arrays.asList(new String[]{"a","b"}));
			}

			@Override
			public String classNameForSymbol(String symbol) {
				return "Main";
			}

			@Override
			public int lineNumberForSymbol(String symbol) {
				if(symbol.equals("a")) return 10;
				if(symbol.equals("b")) return 11;
				throw new IllegalArgumentException();
			}

			@Override
			public int numberOfStates() {
				return 3;
			}

			@Override
			public int succ(int stateNum, String symbol) {
				if(stateNum==0 && symbol.equals("a")) return 1;  
				if(stateNum==1 && symbol.equals("b")) return 2;
				return -1; 
			}
			
		});
		
		oos.close();
		socket.close();
	}

}
