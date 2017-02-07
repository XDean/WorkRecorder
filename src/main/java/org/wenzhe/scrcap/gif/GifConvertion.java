package org.wenzhe.scrcap.gif;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.wenzhe.scrcap.ScrCapProps;

/**
 * @author wen-zhe.liu@asml.com
 *
 */
public class GifConvertion {
  
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final int timeBetweenFramesMs;
  private final Path exportedGif;
  private volatile Path pngFileConverting = null;
  private volatile boolean stoppedByUser = false;
  
  private static volatile GifConvertion currentConvertion;
  
  public GifConvertion(LocalDateTime start, LocalDateTime end, int timeBetweenFramesMs, Path exportedGif) {
    this.start = start;
    this.end = end;
    this.timeBetweenFramesMs = timeBetweenFramesMs;
    this.exportedGif = exportedGif;
  }
  
  public static GifConvertion getCurrentConvertion() {
    return currentConvertion;
  }
  
  public static void convertToGif(String param, Path exportedGif) {
    String[] arr = param.trim().split(",");
    if (arr.length < 3) {
      // error log
      return;
    }
    try {
      LocalDateTime start = LocalDateTime.parse(arr[0].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      LocalDateTime end = LocalDateTime.parse(arr[1].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      int timeBetweenFramesMs = Integer.parseInt(arr[2].trim());
      currentConvertion = new GifConvertion(start, end, timeBetweenFramesMs, exportedGif);
      currentConvertion.convertToGif();
    } catch (DateTimeParseException e) {
      // log e
      e.printStackTrace();
    }
  }
  
  public void convertToGif() {
    Thread t = new Thread(() -> {
      try {
        GifConverter.convertToGif(ScrCapProps.getSetting().getExportedDir().toPath(), exportedGif, start, end, timeBetweenFramesMs, 
            pngFile -> {
              if (stoppedByUser) {
                return false; // stop convertion
              }
              pngFileConverting = pngFile;
              return true;
            });
        if (!stoppedByUser) {
          ProcessBuilder pb = new ProcessBuilder("explorer", exportedGif.getParent().toString()); 
          try {
            pb.start();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      pngFileConverting = null;
      currentConvertion = null;
    });
    t.setDaemon(true);
    t.start();
  }
  
  public String getProgress() {
    if (pngFileConverting == null) {
      return "";
    }
    String relativePath = ScrCapProps.getSetting().getExportedDir().toPath().relativize(pngFileConverting).toString().replace("\\", "/");
    relativePath = relativePath.substring(0, relativePath.length() - ".png".length());
    LocalDateTime dt = LocalDateTime.parse(relativePath, DateTimeFormatter.ofPattern("yyyy/MM/dd/HH/mm.ss.SSS"));
    return "" + start.until(dt, ChronoUnit.SECONDS) * 100 / start.until(end, ChronoUnit.SECONDS) + "%";
  }

  public Path getExportedGif() {
    return exportedGif;
  }

  public void stopConvertion() {
    stoppedByUser = true;
  }
}
