
package hr.algebra.utils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.swing.filechooser.FileSystemView;


public class FileUtils {
      private static final String LOAD = "Load";
    private static final String SAVE = "Save";

    public static File uploadFileDialog(Window owner, String...extensions) {
        FileChooser chooser = new FileChooser();
     
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> {
            chooser.getExtensionFilters().add(
                   new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
               );            
        });         
        chooser.setTitle(LOAD);
        File file = chooser.showOpenDialog(owner);
        return file;
    }
  
    public static File saveFileDialog(Window owner, String...extensions) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        Stream.of(extensions).forEach(e -> {
            chooser.getExtensionFilters().add(
                   new FileChooser.ExtensionFilter(e.toUpperCase(), "*." + e)
               );            
        });     
        chooser.setTitle(SAVE);
        File file = chooser.showSaveDialog(owner);
        if (file != null) {
            file.createNewFile();
        }
        return file;
    }

    public static String GetDirPath(Window owner) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new java.io.File(".\\src\\hr\\algebra"));
        File file = chooser.showDialog(owner);
        return file.getPath();
    }
    
}
