package com.jfbyers.utils.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class Profiler {

	private static boolean deleteDirectory = false;

	public static void main(String[] args) throws IOException {
		long execTimeStart = System.nanoTime();
		TimeMonitor totalMonitor = new TimeMonitor();
		totalMonitor.start();
		if (args == null || args.length < 2 || args[0] == null) {
			System.out.println("Usage : java -jar ZipProfiler.jar PATH_TO_ZIP_FILE PATH_TO_UNCOMPRESS_FOLDER [delete destination directory true/false]");
			System.exit(0);
		}
		
		final File file = new File(args[0]);
		final String basePath = args[1];
		final File basePathFile = new File(basePath);
		validateArguments(args, file, basePathFile);
		
		System.out.println("Starting process...");
		int numberOfFiles = 0;
		int numberOfEntries = 0;
		long nanoSecsCopying = 0L;
		long nanoSecsEncoding = 0L;
		try {
			
			ZipResource resource = new ZipResource(file);

			while (resource.hasNext()) {
				final ZipEntry entry = resource.next();
				final String outputFilePath = fromStringToPath(entry.getName());
				numberOfEntries++;
				if (isArchive(outputFilePath)) {
					numberOfFiles++;
				
					// Phase 1 copy file
					File outputFile = new File(basePath, outputFilePath);
					TimeMonitor copyMonitor = new TimeMonitor();
					copyMonitor.start();
					try (InputStream contentStream = entry.getContentStream()) {
						System.out.println("Copying zip entry " +	entry.getName()+ " to. " +outputFile.getAbsolutePath());
						FileUtils.copyInputStreamToFile(contentStream,	outputFile);
					}
					nanoSecsCopying += copyMonitor.stop();
					
					// Phase 2 get encoding
					TimeMonitor encodingMonitor = new TimeMonitor();
					encodingMonitor.start();
					try (InputStream contentStream = entry.getContentStream()) {
						IBMEncodingDetector detector = new IBMEncodingDetector();
						Charset charset = detector.detect(entry);
						System.out.println("Detecting encoding of zip entry " + entry.getName()+ ":" +charset.displayName());
					}
					nanoSecsEncoding += encodingMonitor.stop();
				}
			}
			resource.close();
			final long totalMonitorLog = totalMonitor.stop().longValue();
			dumpResults(execTimeStart, numberOfFiles, numberOfEntries,	nanoSecsCopying, nanoSecsEncoding, totalMonitorLog);

		} finally {
			if (deleteDirectory) {
				System.out.println("Deleting directory ... " + basePath);
				FileUtils.deleteDirectory(basePathFile);
			}	
		}
	}

	private static void dumpResults(long execTimeStart, int numberOfFiles,
			int numberOfEntries, long nanoSecsCopying, long nanoSecsEncoding,
			final long totalMonitorLog) {
		System.out.println("Total entries: " + numberOfEntries);
		System.out.println("Uncompressed archives: " + numberOfFiles);
		System.out.println("Existing folders: "
				+ (numberOfEntries - numberOfFiles));
		System.out
				.println("-------------------------------------------------------");


		System.out.println("Copying files time: "
				+ TimeUnit.NANOSECONDS.toMillis(nanoSecsCopying) / 1000D
				+ " secs");
		System.out.println("Detecting encoding time: "
				+ TimeUnit.NANOSECONDS.toMillis(nanoSecsEncoding) / 1000D
				+ " secs");
		long finishExecTime = System.nanoTime() - execTimeStart; 
		System.out.println("Total CPU time: "
				+ TimeUnit.NANOSECONDS.toMillis(totalMonitorLog)
				/ 1000D + " secs");
		System.out.println("Total Execution time: "
				+ TimeUnit.NANOSECONDS.toMillis(finishExecTime)
				/ 1000D + " secs");
		final long idle =  finishExecTime - totalMonitorLog ;
		System.out.println("Idle time: "
				+ TimeUnit.NANOSECONDS.toMillis(idle)
				/ 1000D + " secs");
	}

	private static void validateArguments(String[] args, final File file,
			final File basePathFile) {
		if (args.length > 2) {
			deleteDirectory = Boolean.valueOf(args[2]).booleanValue();
		}
		if (!file.exists() || !file.isFile()
				|| !file.getAbsolutePath().endsWith(".zip")) {
			System.out.println(args[0] + " is not a zip file ");
			System.exit(-1);
		}
		
		if (!basePathFile.exists()){			
			System.out.println(args[1] + " did not exist, creating it.. ");
			basePathFile.mkdirs();
			
		}
		if (!basePathFile.isDirectory() || !basePathFile.canRead()
				|| !basePathFile.canWrite()) {
			System.out.println(args[1] + " is not a valid folder ");
			System.exit(-1);
		}
		
		boolean isEmtpy = isEmptyDir(basePathFile.getAbsolutePath());
		if (!isEmtpy){
			System.out.println("Destination dir is not empty.. ");
			System.exit(-1);
		}
	}

	private static boolean isEmptyDir(String basePath) {
		 return Paths.get(basePath).toFile().listFiles().length == 0;
	}

	private static boolean isArchive(String outputFilePath) {

		return !outputFilePath.trim().endsWith("/");
	}

	 static String fromStringToPath(final String strPath) {
		if (strPath == null) {
			throw new IllegalArgumentException();
		}

		final String completPath = strPath.replace('\\', '/');
		final int separator = completPath.lastIndexOf("/");

		String folder;
		String filename;

		if (separator == completPath.length() - 1 && separator != -1) {
			folder = completPath.substring(0, separator);
			filename = "";
		} else {
			if (separator == -1) {
				folder = "";
				filename = completPath;
			} else {
				folder = completPath.substring(0, separator);
				filename = completPath.substring(separator + 1);
			}
		}
		return folder + "/" + filename;
	}

}
