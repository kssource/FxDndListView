package de.ks.fxdnd.node;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

public interface DndSource {

	
	default void initAsDndSource(Node node) {

		node.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleSourceDragDetected(event);
			}
		});

		node.setOnDragDone(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				handleSourceDragDone(event);
			}
		});

	}

	
	public void handleSourceDragDetected(MouseEvent event);
	public void handleSourceDragDone(DragEvent event);

}
