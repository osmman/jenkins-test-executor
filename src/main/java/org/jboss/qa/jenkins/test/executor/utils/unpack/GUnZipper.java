package org.jboss.qa.jenkins.test.executor.utils.unpack;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GUnZipper extends UnPacker {

	private static final String TYPE = "tar.gz";

	private static Set<TarArchiveEntry> getEntries(TarArchiveInputStream tarIn) throws IOException{
		Set<TarArchiveEntry> entries = new HashSet<>();
		while (true) {
			TarArchiveEntry entry = tarIn.getNextTarEntry();
			if (entry == null) {
				break;
			}
			entries.add(entry);
		}
		return entries;
	}

	public String type() {
		return TYPE;
	}

	@Override
	public void unpack(File archive, File destination) throws IOException {
		try (FileInputStream in = new FileInputStream(archive); GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in); TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn)) {
			Set<TarArchiveEntry> entries = getEntries(tarIn);

			if (ignoreRootFolders) {
				pathSegmentsToTrim = countRootFolders(entries);
			}

			for (TarArchiveEntry entry : entries) {
				if (entry.isDirectory()) {
					continue;
				}

				File file = new File(destination, trimPathSegments(entry.getName(), pathSegmentsToTrim));
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				try(FileOutputStream fos = new FileOutputStream(file)) {
					IOUtils.copy(tarIn, fos);

					// check for user-executable bit on entry and apply to file
					if ((entry.getMode() & 0100) != 0) {
						file.setExecutable(true);
					}
				}
			}
		}
	}
}
