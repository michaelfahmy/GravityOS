//package apps;
//
//import javafx.scene.Scene;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//import org.jpedal.examples.viewer.Commands;
//import org.jpedal.examples.viewer.ViewerFX;
//
//import java.io.File;
//
//
//public class PDFViewer {
//
//    public PDFViewer() {
//        Stage stage = new Stage();
//        StackPane s = new StackPane();
//
//        ViewerFX viewer;
//        viewer = new ViewerFX (s, "PDF Viewer");
//        viewer.setupViewer();
//
//        stage.setScene(new Scene(s));
//        stage.show();
//    }
//
//    public PDFViewer (String fileUrl){
//        Stage stage = new Stage();
//        StackPane s = new StackPane();
//
//        ViewerFX viewer;
//        viewer = new ViewerFX (s, "PDF Viewer");
//        viewer.setupViewer();
//        viewer.executeCommand(Commands.OPENFILE, new Object[]{new File(fileUrl)});
//
//        stage.setScene(new Scene(s));
//        stage.show();
//    }
//}
