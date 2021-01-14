package com.jfbyers.utils.zip;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;


public class ZipEntry {
	
	private final ZipResource resource;
	private final ZipArchiveEntry entry;
	
	private final String hashcode;
	private final long size;
	private final String name;

	public ZipEntry(ZipResource resource, ZipArchiveEntry entry) {
		this.resource = resource;
		this.entry = entry;
		this.name = entry.getName();
		this.size = entry.getSize();
		this.hashcode = Long.toHexString(entry.getCrc());		
	}
	
	public InputStream getContentStream() throws IOException {
		final InputStream inputStream = this.resource.getZipFile().getInputStream(this.entry);
		if (inputStream == null) {
			throw new IOException("null content from file");
		}
		return inputStream;
	}

	public long getSize() {
		return this.size;
	}

	public String getHashCode() {
		return this.hashcode;
	}

	public String getName() {	
		return this.name;
	}

}
