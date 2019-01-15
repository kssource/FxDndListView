package examples.dndList;

import java.util.ArrayList;
import java.util.Arrays;

import de.ks.fxdnd.dndList.DndListView;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

// two lists, data is Node, that is displayed in cell
// items from rightList can be moved to leftList
public class DndNodeListExample extends Application {

	private final DataFormat rightDataFormat = new DataFormat("rightList");

	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			// left list
			DndListView<Node> leftList = new DndListView<>();// initially empty list
			leftList.acceptedDataFormatItems.add(rightDataFormat);
			
			//rightList
			ObservableList<Node> items = createRightListData();
			DndListView<Node> rightList = new DndListView<>(items);
			rightList.sourceDataFormatItems.clear();
			rightList.sourceDataFormatItems.add(rightDataFormat);
			rightList.acceptedDataFormatItems.clear();
			rightList.acceptedDataFormatItems.add(rightDataFormat);
			
			rightList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			leftList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			BorderPane borderPane = new BorderPane();
			borderPane.setLeft(leftList);
			borderPane.setRight(rightList);
			
			primaryStage.setTitle("DndNodeListExample");

			Scene scene = new Scene(borderPane, 600, 200);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private ObservableList<Node> createRightListData() {
		ArrayList<String> exampleStringsList = new ArrayList<>(Arrays.asList(
	            "chocolate", "salmon", "gold", "coral", "darkorchid",
	            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
	            "blueviolet", "brown"
	            ));
		
		ArrayList<Node> arListOfNodes = new ArrayList<>();

		for (String colorStr : exampleStringsList) {
			Node dataNode = createDataNode(colorStr);
			arListOfNodes.add(dataNode);
		}
		
		ObservableList<Node> dataList = FXCollections.observableArrayList(arListOfNodes);
		return dataList;
	}


	private Node createDataNode(String colorStr) {
		VBox outerContainer = new VBox();
		outerContainer.setPadding(new Insets(1));
		
		HBox firstRowHBox = new HBox();
		HBox secondRowHBox = new HBox();
		outerContainer.getChildren().addAll(firstRowHBox, secondRowHBox);
		
		Rectangle rect = new Rectangle(10,  10);
		rect.setFill(Color.web(colorStr));
		Region space = new Region();
		HBox.setHgrow(space, Priority.ALWAYS);
		CheckBox checkBox = new CheckBox();
		Button button = new Button("A");
		button.setStyle("-fx-font-size: 7");
		
		firstRowHBox.getChildren().addAll(rect, space, checkBox, button);
		
		Label label = new Label(colorStr);
		secondRowHBox.getChildren().add(label);
		return outerContainer;
	}


		
	public static void main(String[] args) {
		launch(args);
	}

}
