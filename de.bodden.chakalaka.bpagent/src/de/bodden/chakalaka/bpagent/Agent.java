package de.bodden.chakalaka.bpagent;
/*******************************************************************************
 * Copyright (c) 2010 Eric Bodden.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Eric Bodden - initial API and implementation
 ******************************************************************************/


import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URISyntaxException;


public class Agent {
		
	public static void premain(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException, UnmodifiableClassException, URISyntaxException, InterruptedException {
		if(!inst.isRetransformClassesSupported()) {
			throw new RuntimeException("retransformation not supported");
		}

		inst.addTransformer(new TransitionInserter(),true /* can retransform */);
		
		Thread thread = new Thread(new IncomingConnectionListener(inst));
		thread.setDaemon(true);	//ok to shut down when thread is still listening
		thread.start();	
	}
}