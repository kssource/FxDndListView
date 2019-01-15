package de.ks.fxdnd.dndList;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class DndListCell<T> extends ListCell<T> {
    private DndListView<T> ownerList;
    
    private Node hintBeforeNode = new HBox();
    private Node hintAfterNode = new HBox();
    private Node middleNode = new HBox();

	private T inputItem;
	private boolean isInsertBefore = true;

	public DndListCell(DndListView<T> dndOwnerListView) {
		this.ownerList = dndOwnerListView;
		init();
	}

	private void init() {
		// init normal styles for hint
		resetDndInsertZoneHint();
	}

	@Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        this.inputItem = item;
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
        	ownerList.updateListCell(item, this);// delegate to listView
        }
    }

	
	public boolean isInsertBefore() {
		return isInsertBefore;
	}

	public Node getHintBeforeNode() {
		return hintBeforeNode;
	}

	public void setHintBeforeNode(Node hintBeforeNode) {
		this.hintBeforeNode = hintBeforeNode;
	}

	public Node getHintAfterNode() {
		return hintAfterNode;
	}

	public void setHintAfterNode(Node hintAfterNode) {
		this.hintAfterNode = hintAfterNode;
	}

	public DndListView<T> getOwnerList() {
		return ownerList;
	}

	public void showHintBefore() {
		if(ownerList.dnd_dropHintNodeClass != null) {
			hintBeforeNode.getStyleClass().add(ownerList.dnd_dropHintNodeClass);
		}else {
			hintBeforeNode.setStyle(ownerList.dnd_dropHintNodeActiveStyle);
		}
		isInsertBefore = true;
	}

	public void hideHintBefore() {
		if(ownerList.dnd_dropHintNodeClass != null) {
			hintBeforeNode.getStyleClass().remove(ownerList.dnd_dropHintNodeClass);
		}else {
			hintBeforeNode.setStyle(ownerList.dnd_dropHintNodeNormalStyle);
		}
	}

	public void showHintAfrer() {
		if(ownerList.dnd_dropHintNodeClass != null) {
			hintAfterNode.getStyleClass().add(ownerList.dnd_dropHintNodeClass);
		}else {
			hintAfterNode.setStyle(ownerList.dnd_dropHintNodeActiveStyle);
		}
		isInsertBefore = false;
	}

	public void hideHintAfrer() {
		if(ownerList.dnd_dropHintNodeClass != null) {
			hintAfterNode.getStyleClass().remove(ownerList.dnd_dropHintNodeClass);
		}else {
			hintAfterNode.setStyle(ownerList.dnd_dropHintNodeNormalStyle);
		}
	}

	public void resetDndInsertZoneHint() {
		hideHintBefore();
		hideHintAfrer();
	}

	
	public Node getMiddleNode() {
		return middleNode;
	}

	public void setMiddleNode(Node middleNode) {
		this.middleNode = middleNode;
	}


	@Override
	public String toString() {
		return "DndListCell [inputItem=" + inputItem + ", idx=" + this.getIndex() + "]";
	}
	
	
}
