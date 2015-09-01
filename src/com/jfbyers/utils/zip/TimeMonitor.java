package com.jfbyers.utils.zip;

import java.lang.management.ManagementFactory;

public class TimeMonitor {

	private Status status = Status.STOPPED;
	private long lastProcessTime;

	public Long stop() {
		if (this.status != Status.STARTED) {
			throw new IllegalStateException(
					"The monitor should be started first.");
		}
		
		long processCpuTime = 0;

		if (ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean) {
			processCpuTime = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean()).getProcessCpuTime();
		}

		final long time = processCpuTime - this.lastProcessTime;
		this.lastProcessTime = processCpuTime;
		this.status = Status.STOPPED;
		return Long.valueOf(time);
	}

	public void start() {
		if (this.status == Status.STARTED) {
			throw new IllegalStateException(
					"The monitor has been already started.");
		}

		this.status = Status.STARTED;
		this.lastProcessTime = System.nanoTime();

		if (ManagementFactory.getOperatingSystemMXBean() instanceof com.sun.management.OperatingSystemMXBean) {
			this.lastProcessTime = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean()).getProcessCpuTime();
		}
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	/**
	 * Possible statuses of the monitor.
	 * 
	 */
	enum Status {
		STARTED, STOPPED;
	}
}
