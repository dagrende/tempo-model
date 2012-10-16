package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import se.findout.tempo.client.model.Participant;
import se.findout.tempo.client.model.ParticipantModel;
import se.findout.tempo.client.model.PropertyChangeEvent;
import se.findout.tempo.client.model.PropertyChangeListener;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;

public class ParticpantView extends FlowPanel implements PropertyChangeListener {
	private CellList<String> cellList;
	private ParticipantModel model;
	private ListDataProvider<String> listDataProvider;

	public ParticpantView(ParticipantModel model) {
		this.model = model;
		initUI();
	    setRows();
	    model.addPropertyChangeListener(this);
	}

	private void setRows() {
		List<String> values = new ArrayList<String>();
		for (Participant participant : model.getParticipants()) {
			values.add(participant.getNickname());
		}
		listDataProvider.setList(values);
		listDataProvider.refresh();
	}

	private void initUI() {
	    cellList = new CellList<String>(new TextCell());
	    listDataProvider = new ListDataProvider<String>();
	    listDataProvider.addDataDisplay(cellList);
	    add(cellList);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setRows();
	}
}
