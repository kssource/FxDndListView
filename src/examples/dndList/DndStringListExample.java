package examples.dndList;

import de.ks.fxdnd.dndList.DndListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DndStringListExample  extends Application{
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Node dndList = createDndList();
			BorderPane borderPane = new BorderPane(dndList);
			Scene scene = new Scene(borderPane, 400, 200);
			primaryStage.setScene(scene);
			primaryStage.setTitle("DndStringListExample");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}

	
	private Node createDndList() {
	    DndListView<String> dndListView = new DndListView<String>();

	    ObservableList<String> data = FXCollections.observableArrayList(
	            "chocolate", "salmon", "gold", "coral", "darkorchid",
	            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
	            "blueviolet", "brown");

	    dndListView.setItems(data);

	    dndListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    
		return dndListView;
	}

}
