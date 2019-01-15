package de.ks.fxdnd.node;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;

public interface DndTarget {

	default void initAsDndTarget(Node node) {
		node.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				handleTargetDragEntered(event);
			}
		});

		node.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				handleTargetDragOver(event);
			}
		});

		node.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				handleTargetDragExited(event);
			}
		});

		node.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				handleTargetDragDropped(event);
			}
		});
	}

	public void handleTargetDragEntered(DragEvent event);
	public void handleTargetDragOver(DragEvent event);
	public void handleTargetDragExited(DragEvent event);
	
	public void handleTargetDragDropped(DragEvent event);
	
	
}
