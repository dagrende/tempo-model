package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ToolPalette extends VerticalPanel {
	private List<ToolSelectionListener> toolSelectionListeners = new ArrayList<ToolPalette.ToolSelectionListener>();
	private String selectedId = null;
	private List<ToolItem> tools = new ArrayList<ToolPalette.ToolItem>();
	
	public ToolPalette() {
	}

	public void addTool(String id, String label, String toolTip) {
		Button b = new Button(label);
		b.setTitle(toolTip);
		tools.add(new ToolItem(id, label, b));
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button button = (Button) event.getSource();
				selectTool(idByButton(button));
			}

		});
		add(b);
	}

	protected String idByButton(Button button) {
		for (ToolItem toolItem : tools) {
			if (toolItem.button == button) {
				return toolItem.getId();
			}
		}
		return null;
	}

	private void fireToolSelectionEvent(String title) {
		for (ToolSelectionListener toolSelectionListener : toolSelectionListeners) {
			toolSelectionListener.onSelect(new ToolSelectionEvent(title));
		}
	}

	/**
	 * Event describing a tool selection.
	 * 
	 */
	public class ToolSelectionEvent {
		private final String id;

		public ToolSelectionEvent(String id) {
			super();
			this.id = id;
		}

		/**
		 * The id of the newly selected tool.
		 * @return
		 */
		public String getId() {
			return id;
		}
	}
	
	static class ToolItem {
		private final String id;
		private final String label;
		private final Button button;
		
		public ToolItem(String id, String label, Button button) {
			super();
			this.id = id;
			this.label = label;
			this.button = button;
		}
		
		public String getId() {
			return id;
		}
		
		public String getLabel() {
			return label;
		}
		
		public Button getButton() {
			return button;
		}
	}

	/**
	 * Listener informed by a tool selection change.
	 * 
	 */
	public interface ToolSelectionListener {
		/**
		 * A new tool has been selected in the palette.
		 * 
		 * @param toolSelectionEvent
		 *            object describing the new selected tool.
		 */
		void onSelect(ToolSelectionEvent toolSelectionEvent);
	}

	public void addSelectionListener(ToolSelectionListener toolSelectionListener) {
		toolSelectionListeners.add(toolSelectionListener);
	}

	/**
	 * Select the tool with the specified id.
	 * @param id
	 */
	public void selectTool(String id) {
		if (id == null && selectedId != null || id != null && !id.equals(selectedId)) {
			for (ToolItem toolItem : tools) {
				if (id != null && id.equals(toolItem.id)) {
					toolItem.getButton().setHTML("<b>" + toolItem.getLabel() + "</b>");
				} else {
					toolItem.getButton().setHTML(toolItem.getLabel());
				}
			}
			selectedId = id;
			fireToolSelectionEvent(selectedId);
		}
	}

	public String getSelectedTool() {
		return selectedId;
	}

}
