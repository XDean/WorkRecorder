package org.wenzhe.scrcap.gif;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifConverter {
  
  public static void convertToGif(Path folder, Path exportedGif,
      LocalDateTime start, LocalDateTime end, int timeBetweenFramesMs, 
      Predicate<Path> onPngWritten) throws FileNotFoundException, IOException {
    try (ImageOutputStream output = new FileImageOutputStream(exportedGif.toFile());
        GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_3BYTE_BGR, timeBetweenFramesMs, false)) {
      for (LocalDateTime time = start; !time.isAfter(end); time = time.plusHours(1).withMinute(0).withSecond(0).withNano(0)) {
        Path dir = folder.resolve(time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")));
        if (Files.isDirectory(dir)) {
          List<Path> pngFiles = Files.list(dir)
          .filter(path -> path.toString().endsWith(".png"))
          .sorted((path1, path2) -> path1.getFileName().toString().compareTo(path2.getFileName().toString()))
          .collect(Collectors.toList());
          
          for (Path pngFile : pngFiles) {
            String relativePath = folder.relativize(pngFile).toString().replace("\\", "/");
            relativePath = relativePath.substring(0, relativePath.length() - ".png".length());
            LocalDateTime dt = null;
            try {
              dt = LocalDateTime.parse(relativePath, DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm.ss.SSS"));
            } catch (DateTimeParseException e) {
              // log e
              continue;
            }
            if (dt.isBefore(time)) {
              continue;
            }
            time = dt;
            if (time.isAfter(end)) {
              return;
            }
            try {
              BufferedImage image = ImageIO.read(pngFile.toFile());
              assert image.getType() == BufferedImage.TYPE_3BYTE_BGR;
              writer.writeToSequence(image);
              if (onPngWritten != null && !onPngWritten.test(pngFile)) {
                return;
              }
            } catch (Exception e) {
              // error log
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Path folder = Paths.get("C:\\Users\\weliu\\.workrecorder\\exported");
    LocalDateTime start = LocalDateTime.of(2017, Month.of(1), 20, 8, 55, 0);
    LocalDateTime end = LocalDateTime.of(2017, Month.of(1), 20, 9, 5, 0);
    int timeBetweenFramesMs = 1000;
    Path exportedGif = folder.resolve("exported.gif");
    
    convertToGif(folder, exportedGif, start, end, timeBetweenFramesMs, 
        pngFile -> {
          System.out.println("File " + pngFile.getFileName().toString() + " is written into gif.");
          return true;
        });
    
    System.out.println("Done!!");
  }
}
