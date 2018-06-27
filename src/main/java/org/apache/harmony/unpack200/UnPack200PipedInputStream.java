/*
 * Copyright 2018 peter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.unpack200;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

/**
 * Utility class for loading Pack200 archives, without requiring uncompressing
 * the entire archive first.  This hasn't been optimized for performance.
 * 
 * @author peter
 */
public class UnPack200PipedInputStream extends JarInputStream {
    private ExecutorService es;
    
    public UnPack200PipedInputStream(InputStream in) throws IOException {
	this(in, true);
    }
    
    public UnPack200PipedInputStream(InputStream in, boolean verify) throws IOException {
	this(new PipedInputStream(16384), in, verify);
    }

    private UnPack200PipedInputStream(PipedInputStream pin, InputStream in, boolean verify) throws IOException{
	this(pin, verify, in, new PipedOutputStream(pin));
    }
    
    private UnPack200PipedInputStream(PipedInputStream pin, boolean verify, InputStream in, PipedOutputStream out) throws IOException{
	this(pin, verify, create(in, out));
    }
    
    /**
     * By the time we get to the super constructor, we're already processing
     * @param in
     * @param out
     * @return 
     */
    private static ExecutorService create(InputStream in, PipedOutputStream out){
	ExecutorService e = Executors.newSingleThreadExecutor();
	ReadInWriteOut ro = new ReadInWriteOut(in, out);
	e.submit(ro);
	return e;
    }
    
    private UnPack200PipedInputStream(PipedInputStream pin, boolean verify, ExecutorService es) throws IOException {
	super(pin, verify);
	this.es = es;
    }
    
    @Override
    public void close() throws IOException {
	es.shutdownNow();
	super.close();
    }
    
    private static class ReadInWriteOut implements Callable{
	private final InputStream in;
	private final OutputStream out;
	
	ReadInWriteOut(InputStream in, OutputStream out){
	    this.in = in;
	    this.out = out;
	}

	public Object call() throws Exception {
	    UnPack200Archive up200 = new UnPack200Archive(in, new JarOutputStream(out));
	    up200.unpack();
	    return null;
	}
	
    }
    
}
