package com.jfbyers.utils.zip;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;


import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;


public class ZipResource  {
	
	private final ZipFile zipFile;
	private final String sourceName;
	
	private boolean initialize;
	private Enumeration<ZipArchiveEntry> entries;

	public ZipResource(File file)  {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		try {
			this.sourceName = file.getAbsolutePath();
			this.zipFile = new ZipFile(file);
			
			this.initialize = false;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSource() {
		return this.sourceName;
	}

	public boolean hasNext() {
		if (!this.initialize) {
			this.entries = this.zipFile.getEntries();
			this.initialize = true;
		}
		return this.entries.hasMoreElements();
	}

	public ZipEntry next() {
		if (!this.initialize) {
			throw new IllegalStateException();
		}
		ZipArchiveEntry element = this.entries.nextElement();
		return new ZipEntry(this, element);
	}

	public void close() throws IOException {
		if (!this.initialize) {
			throw new IllegalStateException();
		}
		this.zipFile.close();
	}

	protected ZipFile getZipFile() {
		return this.zipFile;
	}
}