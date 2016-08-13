package org.wenzhe.scrcap; 

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author liuwenzhe2008@qq.com
 *
 */
public class ScreenCapture {  

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd/HH/mm.ss.SSS");

  // singleton design pattern  
  private static ScreenCapture defaultCapturer = new ScreenCapture();  
  private Robot robot;  
  //private BufferedImage fullScreenImage;  
  //private BufferedImage pickedImage;  
  //private final String defaultImageFormater = "png";  
  private File lastCapturedFile;  

  private int x1, y1, x2, y2;  
  private int recX, recY, recH, recW;
  private boolean isFirstPoint = true; 
  private final BackgroundImage labFullScreenImage = new BackgroundImage(); 
  private final JDialog dialog = new JDialog();

  private RenderedImage lastSavedImage; 

  private ScreenCapture() {  
    try {  
      robot = new Robot();  
    } catch (AWTException e) {  
      System.err.println("Internal Error: " + e);  
      e.printStackTrace();  
    }

    labFullScreenImage.addMouseListener(new MouseAdapter() {  
      @Override
      public void mouseReleased(MouseEvent evn) {  
        if (isFirstPoint) {
          return;
        }
        isFirstPoint = true;  
        Icon icon = labFullScreenImage.getIcon();
        if (!(icon instanceof ImageIcon)) {
          return;
        }
        Image image = ((ImageIcon) icon).getImage();
        if (!(image instanceof BufferedImage)) {
          return;
        }
        BufferedImage fullScreenImage = (BufferedImage) image;
        BufferedImage pickedImage = fullScreenImage.getSubimage(recX, recY, recW, recH);  
        dialog.setVisible(false);  
        setClipboardImage(pickedImage);
        save(pickedImage);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          isFirstPoint = true;  
          dialog.setVisible(false); 
        }
      }  
    });  

    labFullScreenImage.addMouseMotionListener(new MouseMotionAdapter() {  
      @Override
      public void mouseDragged(MouseEvent evn) {  
        if (isFirstPoint) {  
          x1 = evn.getX();  
          y1 = evn.getY();  
          isFirstPoint = false;  
        } else {  
          x2 = evn.getX();  
          y2 = evn.getY();  
          int maxX = Math.max(x1, x2);  
          int maxY = Math.max(y1, y2);  
          int minX = Math.min(x1, x2);  
          int minY = Math.min(y1, y2);  
          recX = minX;  
          recY = minY;  
          recW = maxX - minX;  
          recH = maxY - minY;  
          labFullScreenImage.drawRectangle(recX, recY, recW, recH);  
        }  
      }  

      @Override
      public void mouseMoved(MouseEvent e) {  
        labFullScreenImage.drawCross(e.getX(), e.getY());  
      }  
    }); 
    JPanel cp = (JPanel) dialog.getContentPane();  
    cp.setLayout(new BorderLayout());  
    cp.add(BorderLayout.CENTER, labFullScreenImage);  
    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));  
    dialog.setAlwaysOnTop(true);  
    dialog.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());  
    dialog.setUndecorated(true);  
    dialog.setLocation(new Point(0, 0));
    dialog.setSize(dialog.getMaximumSize());  
    dialog.setModal(true);  
  }

  // Singleton Pattern  
  public static ScreenCapture getInstance() {  
    return defaultCapturer;  
  }  

  public synchronized void captScreen(boolean toClipboard) {
    BufferedImage pickedImage = captureFullScreen();
    if (toClipboard) {
      setClipboardImage(pickedImage);
    }
    save(pickedImage);
  }

  private void save(RenderedImage image) {
    File exportedDir = ScrCapProps.getSetting().getExportedDir();
    if (!exportedDir.isDirectory()) {
      exportedDir.mkdirs();
    }
    File f = new File(exportedDir, getFileName());  
    if (!f.getParentFile().isDirectory()) {
      f.getParentFile().mkdirs();
    }
    if (diffImage(lastSavedImage, image) <= ScrCapProps.getSetting().getMaxAllowDiffForImageCompare()) {
      return;
    }
    try {
      saveAsPNG(f, image);
      lastSavedImage = image;
      lastCapturedFile = f;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private int diffImage(RenderedImage image1, RenderedImage image2) {
    if (image1 == image2) {
      return 0;
    }
    if (image1 == null || image2 == null) {
      return Integer.MAX_VALUE;
    }
    int w = image1.getWidth();
    if (w != image2.getWidth()) {
      return Integer.MAX_VALUE;
    }
    int h = image1.getHeight();
    if (h != image2.getHeight()) {
      return Integer.MAX_VALUE;
    }
    if (!(image1 instanceof BufferedImage) || !(image2 instanceof BufferedImage)) {
      return Integer.MAX_VALUE;
    }
    BufferedImage img1 = (BufferedImage) image1;
    BufferedImage img2 = (BufferedImage) image2;
    int diffCount = 0;
    for (int i = 0; i < w; ++i) {
      for (int j = 0; j < h; ++j) {
        if (img1.getRGB(i, j) != img2.getRGB(i, j)) {
          ++diffCount;
        }
      }
    }
    return diffCount;
  }

  private String getFileName() {
    Date now = Calendar.getInstance().getTime();
    return DATE_FORMAT.format(now) + ".png";
  }

  public BufferedImage captureFullScreen() {  
    return robot.createScreenCapture(new Rectangle(Toolkit  
        .getDefaultToolkit().getScreenSize()));  
  }  

  public void saveAsPNG(File file, RenderedImage image) throws IOException {  
    ImageIO.write(image, "png", file);  
  }  

  public void saveAsJPEG(File file, RenderedImage image) throws IOException {  
    ImageIO.write(image, "JPEG", file);  
  }  

  public File getLastCaptureFolder() {
    if (lastCapturedFile == null) {
      return ScrCapSetting.getWorkDir();
    }
    File parentFile = lastCapturedFile.getParentFile();
    return parentFile.exists() ? parentFile : ScrCapSetting.getWorkDir();
  }

  public void clipCaptScreen() { 
    BufferedImage fullScreenImage = robot.createScreenCapture(new Rectangle(
        Toolkit.getDefaultToolkit().getScreenSize()));  
    ImageIcon icon = new ImageIcon(fullScreenImage);  
    labFullScreenImage.setIcon(icon); 
    Point pt = MouseInfo.getPointerInfo().getLocation();
    labFullScreenImage.drawCross((int) pt.getX(), (int) pt.getY());
    labFullScreenImage.drawRectangle(0, 0, 0, 0);
    dialog.setVisible(true);
  }

  public void setClipboardImage(final Image image)
  {
    Transferable trans = new Transferable(){
      @Override
      public Object getTransferData(DataFlavor flavor)
          throws UnsupportedFlavorException, IOException {
        // TODO Auto-generated method stub
        if (isDataFlavorSupported(flavor))
        {
          return image;
        }                      
        throw new UnsupportedFlavorException(flavor);
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
        // TODO Auto-generated method stub
        return new DataFlavor[] { DataFlavor.imageFlavor };
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
        // TODO Auto-generated method stub
        return DataFlavor.imageFlavor.equals(flavor);
      }             
    };
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(trans, null);
  }
}  
