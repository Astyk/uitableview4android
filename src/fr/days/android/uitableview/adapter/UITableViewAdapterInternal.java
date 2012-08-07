package fr.days.android.uitableview.adapter;

import java.util.HashMap;
import java.util.Map;

import fr.days.android.uitableview.model.IndexPath;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Internal adapter used by Android ListView.
 * 
 * @author dvilleneuve
 * 
 */
public class UITableViewAdapterInternal extends BaseAdapter {

	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CELL = 1;

	private final Context context;
	private final UITableViewAdapter tableViewAdapter;
	private Map<Integer, IndexPath> indexPaths = new HashMap<Integer, IndexPath>();

	/**
	 * @param uiTableViewAdapter
	 */
	public UITableViewAdapterInternal(Context context, UITableViewAdapter uiTableViewAdapter) {
		this.context = context;
		this.tableViewAdapter = uiTableViewAdapter;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		IndexPath indexPath = retrieveIndexPathByPosition(position);
		if (indexPath == null)
			return IGNORE_ITEM_VIEW_TYPE;
		else if (indexPath.isHeader())
			return VIEW_TYPE_HEADER;
		else
			return VIEW_TYPE_CELL;
	}

	@Override
	public int getCount() {
		int numberOfGroups = tableViewAdapter.numberOfGroups();
		int countItems = numberOfGroups;
		for (int group = 0; group < numberOfGroups; group++) {
			countItems += tableViewAdapter.numberOfRows(group);
		}
		return countItems;
	}

	@Override
	public Object getItem(int position) {
		IndexPath indexPath = retrieveIndexPathByPosition(position);
		if (indexPath == null)
			return null;
		else if (indexPath.isHeader())
			return tableViewAdapter.headerForGroup(context, indexPath);
		else
			return tableViewAdapter.cellViewForRow(context, indexPath);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IndexPath indexPath = retrieveIndexPathByPosition(position);
		if (indexPath == null)
			return null;
		else if (indexPath.isHeader())
			return tableViewAdapter.headerForGroup(context, indexPath);
		else
			return tableViewAdapter.cellViewForRow(context, indexPath);
	}

	/**
	 * Retrieve an {@link IndexPath} according to a ListView's item position.
	 * 
	 * @param position
	 * @return An {@link IndexPath} if position is valid, <code>null</code> else
	 */
	public IndexPath retrieveIndexPathByPosition(int position) {
		IndexPath indexPath = indexPaths.get(position);
		if (indexPath == null) {
			if (position == 0) { // Shortcut
				indexPath = new IndexPath(0, 0);
			} else {
				int numberOfRowsBefore = 0;
				int numberOfGroups = tableViewAdapter.numberOfGroups();
				for (int group = 0; group < numberOfGroups; group++) {
					int numberOfRows = tableViewAdapter.numberOfRows(group);

					if (position == numberOfRowsBefore) {
						indexPath = new IndexPath(position, group);
						break;
					}
					if (position <= numberOfRowsBefore + numberOfRows) {
						if (numberOfRowsBefore == 0) {
							indexPath = new IndexPath(position, group, position - 1, numberOfRows);
							break;
						}
						indexPath = new IndexPath(position, group, (position % numberOfRowsBefore) - 1, numberOfRows);
						break;
					}

					numberOfRowsBefore += numberOfRows + 1; // rows + header
				}
			}

			indexPaths.put(position, indexPath);
		}
		return indexPath;
	}

	@Override
	public void notifyDataSetChanged() {
		indexPaths.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		indexPaths.clear();
		super.notifyDataSetInvalidated();
	}

}