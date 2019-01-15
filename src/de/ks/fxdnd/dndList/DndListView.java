package de.ks.fxdnd.dndList;

import java.util.ArrayList;
import java.util.Collections;

import com.sun.javafx.scene.control.skin.VirtualFlow;

import de.ks.fxdnd.node.DndSource;
import de.ks.fxdnd.node.DndTarget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


// <T> - javafx.scene.Node or any object(toString() used to display)
public class DndListView<T> extends ListView<T> implements DndSource, DndTarget{
	
	public static DataFormat DEFAULT_DATA_FORMAT = new DataFormat("defaultDndList");
	public static DndListView<?> sourceListView;

	// dnd_... properties - First an attempt is made to apply a dnd_...Class variable. 
	// If class = null, dnd_...Style is applied.
	public String dnd_sourceNodeDraggedStyle = "-fx-opacity: 0.3";
	public String dnd_sourceNodeNormalStyle = "-fx-opacity: 1.0";
	public String dnd_sourceNodeDraggedClass = null;

	public String dnd_dropHintNodeActiveStyle = "-fx-border-color: #eea82f; -fx-border-width: 2;";
	public String dnd_dropHintNodeNormalStyle = "";
	public String dnd_dropHintNodeClass = null;
    
	public TransferMode dndTransferMode = TransferMode.MOVE;
    
    // data types putted to clipboard
    public ArrayList<DataFormat> sourceDataFormatItems = new ArrayList<>(); 
    public ArrayList<DataFormat> acceptedDataFormatItems = new ArrayList<>();

	private Node draggedDataNode;// from drag source cell
    
	public DndListView() {
		super();
		init();
	}

	public DndListView(ObservableList<T> items) {
		super(items);
		init();
	}

	private void init() {
		initListCellFactory();

		this.initAsDndTarget(this);// allow drop to emptyList
		
		// add default dragboard marker
		sourceDataFormatItems.add(DndListView.DEFAULT_DATA_FORMAT);
		acceptedDataFormatItems.add(DndListView.DEFAULT_DATA_FORMAT);
	}
	
	// render listCell content
	protected void updateListCell(T dataItem, DndListCell<T> dndListCell) {
		Node dataNode;
		if (dataItem instanceof Node) {
			dataNode = (Node) dataItem;
		} else {
			dataNode = new Label(dataItem.toString());
		}

		dndListCell.setMiddleNode(dataNode);
		
		VBox cellContainer = new VBox();
		cellContainer.getChildren().addAll(
				dndListCell.getHintBeforeNode(), 
				dndListCell.getMiddleNode(), 
				dndListCell.getHintAfterNode());
		
		dndListCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		dndListCell.setGraphic(cellContainer);
	}

	
	
	/////////////// drag and drop handlers
	
	@Override
	public void handleTargetDragEntered(DragEvent event) {
//		System.out.println("handleTargetDragEntered");
		
	}

	@Override
	public void handleTargetDragOver(DragEvent event) {
		Dragboard dragboard = event.getDragboard();

		boolean accept = false;
		for (DataFormat dataFormat : this.acceptedDataFormatItems) {
			if(dragboard.hasContent(dataFormat)) {
				accept = true;
				break;
			}
		}
		
		if(accept == false)	return;

		Object dropTarget = event.getSource();
		if(dropTarget instanceof DndListCell<?>) {
			@SuppressWarnings("unchecked")
			DndListCell<T> dropTagetCell = (DndListCell<T>) dropTarget;
			
			// show insert hint
			double middleOfTargetCell = dropTagetCell.getHeight() / 2;
			double dragY = event.getY();
			
			dropTagetCell.resetDndInsertZoneHint();
			if(dragY < middleOfTargetCell) {
				dropTagetCell.showHintBefore();
			}else {
				dropTagetCell.showHintAfrer();
			}
			autoscroll(dropTagetCell);
		}else {
			// no op
		}
		
		event.acceptTransferModes(this.dndTransferMode);

		event.consume();
	}


	@Override
	public void handleTargetDragExited(DragEvent event) {
		Object dropTarget = event.getSource();
		
		if(dropTarget instanceof DndListCell<?>) {
			@SuppressWarnings("unchecked")
			DndListCell<T> dropTagetCell = (DndListCell<T>) dropTarget;
			dropTagetCell.resetDndInsertZoneHint();
		}else {
			// no op
		}
		event.consume();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleTargetDragDropped(DragEvent event) {
        this.resetDraggedSourceStyle();
		
		// sourceListView
		Dragboard dragboard = event.getDragboard();
		DndListView<?> sourceLView = null;
		for (DataFormat dataFormat : this.acceptedDataFormatItems) {
			Object data = dragboard.getContent(dataFormat);
			if(data != null) {
				sourceLView = DndListView.sourceListView;
				break;
			}
		}
		if(sourceLView == null)	return;

		Object dropTarget = event.getSource();
		if(dropTarget instanceof DndListCell<?>) {
			DndListCell<T> dropTagetCell = (DndListCell<T>) dropTarget;

			// insert index
			int dropIndex = dropTagetCell.getIndex();
			if(dropTagetCell.isInsertBefore() == false) {
				++dropIndex;
			}
			int targetListSize = this.getItems().size();
			if(dropIndex > targetListSize) {
				dropIndex = targetListSize;// add at end
			}
			
			changeListOnDrop((DndListView<T>) sourceLView, dropIndex);
		}else {
			changeListOnDrop((DndListView<T>) sourceLView, 0);
		}

		event.setDropCompleted(true);
		event.consume();
	}

	// manipulate source and target lists
	protected void changeListOnDrop(DndListView<T> sourceLView, int insertIndex) {
		ObservableList<Integer> selectedIndicesObList = 
				sourceLView.getSelectionModel().getSelectedIndices();

		ObservableList<T> olsSourceItemsList = sourceLView.getItems();

		// save selected data items
		SortedList<Integer> selectedIndicesAscending = selectedIndicesObList.sorted();
		ArrayList<T> itemsToMove = new ArrayList<>();
		for (Integer selectedInd : selectedIndicesAscending) {
			itemsToMove.add(olsSourceItemsList.get(selectedInd));
		}
		
		ObservableList<T> oldTargetItemsList = this.getItems();
		ObservableList<T> newTargetItemsList = FXCollections.observableArrayList(oldTargetItemsList);

		ObservableList<T> newSourceItemsList = newTargetItemsList;
		if(sourceLView != this) {
			newSourceItemsList = FXCollections.observableArrayList(olsSourceItemsList);
		}
		
		// first remove items 
		ArrayList<Integer> selectedIndicesDescending = new ArrayList<>(selectedIndicesAscending);
		Collections.reverse(selectedIndicesDescending);

		for (Integer selectedInd : selectedIndicesDescending) {
			int selInd = selectedInd.intValue();
			newSourceItemsList.remove(selInd);
			if(sourceLView == this) {
				if(selInd < insertIndex) {
					--insertIndex;
				}
			}
		}

		int newSelectionStart = insertIndex;
		// insert at new pos
		for (T item : itemsToMove) {
			newTargetItemsList.add(insertIndex, item);
			++insertIndex;
		}
		int newSelectionEnd = insertIndex;
		
		this.setItems(newTargetItemsList);
		this.getSelectionModel().clearSelection();
		this.getSelectionModel().selectRange(newSelectionStart, newSelectionEnd);
		this.getFocusModel().focus(newSelectionStart);
		this.requestFocus();
		
		if(sourceLView != this) {
			sourceLView.setItems(newSourceItemsList);
		}
	}

	@Override
	public void handleSourceDragDetected(MouseEvent event) {
		Object source = event.getSource();
		@SuppressWarnings("unchecked")
		DndListCell<T> cell = (DndListCell<T>) source;
		if(cell.isEmpty())	return;
		
		Dragboard dragboard = cell.startDragAndDrop(this.dndTransferMode);
		ClipboardContent clipboardContent = new ClipboardContent();

		// work with selectedIndices 
		for (DataFormat dataFormat : this.sourceDataFormatItems) {
			// content must be serializable - use variable for transport data instead
			clipboardContent.put(dataFormat, "useStaticSourceList");// dummy
		}
		
		dragboard.setContent(clipboardContent);

		DndListView.sourceListView = this;
		// save reference to dataNode, ListCell is reusable and references become invalid
		this.draggedDataNode = cell.getMiddleNode();
        this.setDraggedSourceStyle();
		
		dragboard.setDragView(cell.snapshot(null, null));
        
        event.consume();
	}

	@Override
	public void handleSourceDragDone(DragEvent event) {
        this.resetDraggedSourceStyle();
		event.consume();
	}


	///////////////// private staff
	
	
	/////////////// listCell
	protected void initListCellFactory() {
        this.setCellFactory(new Callback<ListView<T>, ListCell<T>>(){
            @Override
            public ListCell<T> call(ListView<T> ownerList) {
            	DndListView<T> dndOwnerListView = (DndListView<T>) ownerList;
            	DndListCell<T> cell = new DndListCell<T>(dndOwnerListView);
                
        		DndListView.this.initAsDndSource(cell);
        		DndListView.this.initAsDndTarget(cell);

                return cell;
            }
        });
	}

	@SuppressWarnings({ "rawtypes", "restriction" })
	private void autoscroll(DndListCell<T> dropTagetCell) {
		try {
			DndListView<T> list = dropTagetCell.getOwnerList();
			int indexOfOverCell = dropTagetCell.getIndex();
			
			VirtualFlow virtualFlow = null;
			for(Node node : list.getChildrenUnmodifiable()) {
				if(node instanceof VirtualFlow) {
					virtualFlow = (VirtualFlow) node;
				}
			}
			
			int firstVisibleIndex = virtualFlow.getFirstVisibleCellWithinViewPort().getIndex();
			int lastVisibleIndex = virtualFlow.getLastVisibleCellWithinViewPort().getIndex();
			
			int minIndexToShow = indexOfOverCell - 1;
			if(minIndexToShow < firstVisibleIndex) {
				minIndexToShow = Math.max(minIndexToShow, 0);
				scrollWithDelay(list, minIndexToShow);
			}else {
				int maxIndexToShow = indexOfOverCell + 1;
				if(maxIndexToShow > lastVisibleIndex) {
					maxIndexToShow = Math.min(maxIndexToShow, list.getItems().size()-1);
					// scrolTo(index) sets index as first visible item
					int dif = lastVisibleIndex-firstVisibleIndex;
					int newFirstIndex = maxIndexToShow-dif;
					newFirstIndex = Math.max(newFirstIndex, 0);
					scrollWithDelay(list, newFirstIndex);
				}
			}

		}catch (Exception e) {
//			System.out.println("autoscroll, exception: "+e.getMessage());
		}
	}

	private long lastMark = 0;
	private int lastScrollToIndex = -1;
	
	private void scrollWithDelay(DndListView<T> list, int newFirstIndex) {
		long currMark = System.currentTimeMillis();
		long dif = currMark - lastMark;
		if(dif < 200 || lastScrollToIndex == newFirstIndex) {
			return;// skip update  
		}

		lastScrollToIndex = newFirstIndex;
		list.scrollTo(newFirstIndex);
		lastMark = currMark;
	}
	
	public void setDraggedSourceStyle() {
		@SuppressWarnings("unchecked")
		DndListView<T> sourceLV = (DndListView<T>) DndListView.sourceListView;
		Node parentCell = sourceLV.draggedDataNode.getParent();
		if(parentCell == null)	return;
		
		if(sourceLV.dnd_sourceNodeDraggedClass != null) {
			parentCell.getStyleClass().add(this.dnd_sourceNodeDraggedClass);
		}else {
			parentCell.setStyle(this.dnd_sourceNodeDraggedStyle);
		}
	}

	public void resetDraggedSourceStyle() {
		@SuppressWarnings("unchecked")
		DndListView<T> sourceLV = (DndListView<T>) DndListView.sourceListView;
		Node parentCell = sourceLV.draggedDataNode.getParent();
		if(parentCell == null)	return;

		if(sourceLV.dnd_sourceNodeDraggedClass != null) {
			parentCell.getStyleClass().remove(this.dnd_sourceNodeDraggedClass);
		}else {
			parentCell.setStyle(this.dnd_sourceNodeNormalStyle);
		}
	}

	
}// end public class DndListView<T>
