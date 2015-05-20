/**
 * 
 */
package com.alphasystem.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author sali
 * 
 */
public class SerialExecutor implements Executor {

	private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
	private final Executor executor;
	private Runnable active;

	public SerialExecutor() {
		this.executor = Executors.newSingleThreadExecutor();
	}

	public SerialExecutor(Executor executor) {
		this.executor = executor;
	}

	@Override
	public void execute(final Runnable r) {
		tasks.offer(new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					scheduleNext();
				}
			}
		});
		if (active == null) {
			scheduleNext();
		}
	}

	protected synchronized void scheduleNext() {
		active = tasks.poll();
		if (active != null) {
			executor.execute(active);
		}
	}

}
