package examples.dndPanes;

import java.util.Collections;

import de.ks.fxdnd.node.DndSource;
import de.ks.fxdnd.node.DndTarget;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PanesContainer implements DndSource, DndTarget {

	private static final String ORDER_NUM = "orderNum";// titledPane id
    private static final String DROP_HINT_STYLE = 
    		"-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
    private static final String TITLED_PANE_STYLE = 
    		"-fx-border-color: white; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	
    
    private VBox rootPane;


	public Pane getRootPane() {
		rootPane = new VBox();

		TitledPane titledPane1 = createTitledPane(1);
		TitledPane titledPane2 = createTitledPane(2);
		TitledPane titledPane3 = createTitledPane(3);
		TitledPane titledPane4 = createTitledPane(4);
		ObservableList<Node> listOfTitledPanes = FXCollections.observableArrayList(titledPane1, titledPane2,
				titledPane3, titledPane4);

		rootPane.getChildren().addAll(listOfTitledPanes);

		return rootPane;
	}

	private TitledPane createTitledPane(int number) {
		// pane content
		Button butt = new Button("Button" + number);
		HBox contentHbox = new HBox(butt);
		contentHbox.setPadding(new Insets(3.0));
		contentHbox.getProperties().put(PanesContainer.ORDER_NUM, number);

		TitledPane titledPane = new TitledPane("Title" + number, contentHbox);
		titledPane.setPrefWidth(200.0);
		titledPane.setStyle(TITLED_PANE_STYLE);

		// drag zone
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Node dragZone = null;
				try {
					ObservableList<Node> tpChildren = titledPane.getChildrenUnmodifiable();
					dragZone = tpChildren.get(1);// titleRegion
				} catch (Exception e) {
					dragZone = titledPane;// whole titledPane
				}

				PanesContainer.this.initAsDndSource(dragZone);
				dragZone.getProperties().put(PanesContainer.ORDER_NUM, number);
			}
		});

		// drop zone
		this.initAsDndTarget(titledPane);
		titledPane.getProperties().put(PanesContainer.ORDER_NUM, number);

		return titledPane;
	}

	private void swapPanes(int idSource, int idTarget) {
		ObservableList<Node> childrenList = rootPane.getChildren();
		ObservableList<Node> newList = FXCollections.observableArrayList(childrenList);
		
		int index1 = findIndexOfPane(newList, idSource);
		int index2 = findIndexOfPane(newList, idTarget);
		Collections.swap(newList, index1, index2);
		rootPane.getChildren().setAll(newList);
	}

	
	private int findIndexOfPane(ObservableList<Node> newList, int id) {
		for (int i = 0; i < newList.size(); i++) {
			Node node = newList.get(i);
			int nodeId = (int) node.getProperties().get(PanesContainer.ORDER_NUM);
			if(nodeId  == id)	return i;
		}
		return -1;
	}

	////////////////  drag and drop handlers
	
	@Override
	public void handleTargetDragEntered(DragEvent event) {
		Object dropTarget = event.getSource();
		
		if (dropTarget instanceof Node) {
			Node targetNode = (Node) dropTarget;
			int idTarget = (int) targetNode.getProperties().get(PanesContainer.ORDER_NUM);
			if (event.getDragboard().hasString()) {
				String idStr = event.getDragboard().getString();
				int idSource = Integer.parseInt(idStr);
				
				if(idSource != idTarget) {
					targetNode.setStyle(DROP_HINT_STYLE);
				}
			}
		}
        
		event.consume();

	}

	@Override
	public void handleTargetDragOver(DragEvent event) {
        if (!event.getDragboard().hasString()) return;

		Object dragSource = event.getGestureSource();
		Object dropTarget = event.getSource();
		
		if (dragSource instanceof Node && dropTarget instanceof Node) {
			Node sourceNode = (Node) dragSource;
			Node targetNode = (Node) dropTarget;
			int idSource = (int) sourceNode.getProperties().get(PanesContainer.ORDER_NUM);
			int idTarget = (int) targetNode.getProperties().get(PanesContainer.ORDER_NUM);
			
			if(idSource == idTarget) {//same node
				targetNode.setStyle(TITLED_PANE_STYLE);
			}else {
		        event.acceptTransferModes(TransferMode.MOVE);
			}
		}

		event.consume();
	}

	@Override
	public void handleTargetDragExited(DragEvent event) {
		Object dropTarget = event.getSource();
		
		if (dropTarget instanceof Node) {
			Node targetNode = (Node) dropTarget;
			targetNode.setStyle(TITLED_PANE_STYLE);
		}

        event.consume();
	}

	@Override
	public void handleTargetDragDropped(DragEvent event) {
        if (!event.getDragboard().hasString()) return;

		Object dragSource = event.getGestureSource();
		Object dropTarget = event.getSource();
		
		if (dragSource instanceof Node && dropTarget instanceof Node) {
			Node sourceNode = (Node) dragSource;
			Node targetNode = (Node) dropTarget;
			int idSource = (int) sourceNode.getProperties().get(PanesContainer.ORDER_NUM);
			int idTarget = (int) targetNode.getProperties().get(PanesContainer.ORDER_NUM);
			
			if(idSource == idTarget) {//same node
				//no op
			}else {
				swapPanes(idSource, idTarget);
			}
			
			sourceNode.getParent().setOpacity(1.0);
		}

		event.consume();

	}

	@Override
	public void handleSourceDragDetected(MouseEvent event) {
		Object source = event.getSource();
		if (source instanceof Node) {
			Node sourceNode = (Node) source;
			int idSource = (int) sourceNode.getProperties().get(PanesContainer.ORDER_NUM);
			Dragboard dragboard = sourceNode.startDragAndDrop(TransferMode.MOVE);

			ClipboardContent clipboardContent = new ClipboardContent();
			clipboardContent.putString("" + idSource);
			dragboard.setContent(clipboardContent);

	        dragboard.setDragView(sourceNode.snapshot(null, null));
			sourceNode.getParent().setOpacity(0.3);
	        
			event.consume();
		}
	}

	@Override
	public void handleSourceDragDone(DragEvent event) {
		event.consume();// not used
	}

}
