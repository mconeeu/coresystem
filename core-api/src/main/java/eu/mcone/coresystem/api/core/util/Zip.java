/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Zip {

    /**
     * Zips a folder
     * @param sourceDir source directory
     * @param zipFile zip file
     */
    public Zip(File sourceDir, File zipFile) {
        try {
            if (zipFile.exists()) zipFile.delete();
            zipFile.createNewFile();

            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            Files.walkFileTree(sourceDir.toPath(), new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourceDir.toPath().relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });

            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
