package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import se.findout.tempo.client.model.Participant;
import se.findout.tempo.client.model.ParticipantsModel;
import se.findout.tempo.client.model.PropertyChangeEvent;
import se.findout.tempo.client.model.PropertyChangeListener;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;

public class ParticpantsView extends FlowPanel implements PropertyChangeListener {
	private CellList<String> cellList;
	private ParticipantsModel model;
	private ListDataProvider<String> listDataProvider;

	public ParticpantsView(ParticipantsModel model) {
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
