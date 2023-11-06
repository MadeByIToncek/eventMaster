/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.graphicscreator;

import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static space.itoncek.csyt.graphicscreator.GraphicsCreator.gcc;

public abstract class GCCExporter {
    public abstract void success(String info);

    public abstract void info(String info);

    public abstract void debug(String info);

    public abstract void error(String info);

    public void process(Location pos1, Location pos2) {
        if (pos1 == null) {
            error("Position 1 is not defined");
            return;
        }
        if (pos2 == null) {
            error("Position 2 is not defined");
            return;
        }

        try {
            downloadMcTextures();


            int xsize = max(pos1, pos2).getBlockX() - min(pos1, pos2).getBlockX();
            int zsize = max(pos1, pos2).getBlockZ() - min(pos1, pos2).getBlockZ();

            BufferedImage img = new BufferedImage(xsize * 16, zsize * 16, BufferedImage.TYPE_INT_ARGB);
            img.setAccelerationPriority(1);
            info("Stitching image of size %dx%d (%dx%d blocks)".formatted(img.getWidth(), img.getHeight(), xsize, zsize));
            for (int x = min(pos1, pos2).getBlockX(); x < max(pos1, pos2).getBlockX(); x++) {
                for (int z = min(pos1, pos2).getBlockZ(); z < max(pos1, pos2).getBlockZ(); z++) {
                    int finalX = x;
                    int finalZ = z;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Graphics graphics = img.createGraphics();
                            Block hb = pos1.getWorld().getHighestBlockAt(finalX, finalZ);
                            Material mat = hb.getType();
                            BufferedImage matImg = null;
                            //debug(mat.name());
                            try {
                                matImg = ImageIO.read(new File("./gcc/textures/" + mat.name().toLowerCase() + ".png"));
                            } catch (IOException e) {
                                error(e.getLocalizedMessage() + mat.name().toLowerCase());
                            }
                            if (matImg == null) {
                                error("image not yet loaded");
                            }
                            graphics.drawImage(matImg, (finalX - min(pos1, pos2).getBlockX()) * 16, (finalZ - min(pos1, pos2).getBlockZ()) * 16, null);
                            graphics.dispose();
                        }
                    }.runTask(gcc);
                }
            }
            ImageIO.write(img, "png", new File("./gcc/" + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        //success(Arrays.deepToString(output));
    }

    private void downloadMcTextures() throws IOException {
        if (!new File("./gcc/").exists()) new File("./gcc/").mkdirs();
        info("Downloading client.jar");
        if (!new File("./gcc/client.jar").exists()) FileUtils.copyURLToFile(
                new URL("https://piston-data.mojang.com/v1/objects/958928a560c9167687bea0cefeb7375da1e552a8/client.jar"),
                new File("./gcc/client.jar"),
                3000,
                3000);
        if (!new File("./gcc/textures/").exists()) {
            FileUtils.deleteDirectory(new File("./gcc/client/"));
            info("Extracting client.jar");
            unzip(new File("./gcc/client.jar"), new File("./gcc/client/"));
            info("Clearing up unnecessary files");
            for (File text : new File("./gcc/client/assets/minecraft/textures/block").listFiles()) {
                if (!text.toPath().getFileName().endsWith(".png")) {
                    FileUtils.moveFileToDirectory(text, new File("./gcc/textures/"), true);
                    debug("Moved " + text.toPath().getFileName());
                }
            }
            FileUtils.deleteDirectory(new File("./gcc/client/"));
            info("Finished clearing up, processing started");
        }
    }

    private void unzip(File file, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private Location min(Location pos1, Location pos2) {
        return new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    private Location max(Location pos1, Location pos2) {
        return new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }
}
