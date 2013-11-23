package com.plattysoft.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by shalafi on 11/23/13.
 */
public abstract class GridViewWithHeaderBaseAdapter extends BaseAdapter {

	public interface GridItemClickListener {

		void onGridItemClicked(View v, int position, long itemId);

	}

	private class ListItemClickListener implements OnClickListener {

		private int mPosition;

		public ListItemClickListener(int currentPos) {
			mPosition = currentPos;
		}

		@Override
		public void onClick(View v) {
			onGridItemClicked (v, mPosition);
		}
	}

	private int mNumColumns;
	private Context mContext;
	private GridItemClickListener mGridItemClickListener;

	public GridViewWithHeaderBaseAdapter(Context context) {
		mContext = context;
		mNumColumns = 1;
	}

	public final void setOnGridClickListener(GridItemClickListener listener) {
		mGridItemClickListener = listener;
	}

	private final void onGridItemClicked(View v, int position) {
		if (mGridItemClickListener != null) {
			mGridItemClickListener.onGridItemClicked(v, position, getItemId(position));
		}
	}

	public final int getNumColumns() {
		return mNumColumns;
	}

	public final void setNumColumns(int numColumns) {
		mNumColumns = numColumns;
		notifyDataSetChanged();
	}

	@Override
	public final int getCount() {
		return (int) Math.ceil(getItemCount()*1f / getNumColumns());
	}

	public abstract int getItemCount();

	protected abstract View getItemView(int position, View view, ViewGroup parent);

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		int columnWidth = 0;
		if (parent !=  null) {
			columnWidth = parent.getWidth()/mNumColumns;    		
		}
		else if (convertView != null) {
			columnWidth = convertView.getWidth()/mNumColumns;
		}
		// Make it be rows of the number of columns
		if (convertView == null) {
			// This is items view
			layout = createItemRow(position, parent, columnWidth);            
		}
		else {
			layout = (LinearLayout) convertView;
			updateItemRow(position, parent, layout, columnWidth);
		}
		return layout;
	}

	private LinearLayout createItemRow(int position, ViewGroup viewGroup, int columnWidth) {		
		LinearLayout layout;
		layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// Now add the sub views to it
		for (int i = 0; i < mNumColumns; i++) {
			int currentPos = position * mNumColumns + i;
			// Get the new View
			View insideView;
			if (currentPos < getItemCount()) {            		
				insideView = getItemView(currentPos, null, viewGroup);            	
				insideView.setVisibility(View.VISIBLE);
				insideView.setOnClickListener(new ListItemClickListener (currentPos));				
			}
			else {
				insideView = new View(mContext);
				insideView.setVisibility(View.INVISIBLE);
			}            	
			layout.addView(insideView);
			
			// Set the width of this column
			LayoutParams params = insideView.getLayoutParams();
			params.width = columnWidth;
			params.height = columnWidth;
			insideView.setLayoutParams(params);			
		}
		return layout;
	}	

	private void updateItemRow(int position, ViewGroup parent, LinearLayout layout, int columnWidth) {
		for (int i=0; i<mNumColumns; i++) {
			int currentPos = position * mNumColumns + i;
			View insideView = layout.getChildAt(i);
			// If there are less views than objects. add a view here
			if (insideView == null) {
				insideView = new View(mContext);
				layout.addView(insideView);
			}
			// Set the width of this column
			LayoutParams params = insideView.getLayoutParams();
			params.width = columnWidth;
			insideView.setLayoutParams(params);

			if (currentPos < getItemCount()) {
				insideView.setVisibility(View.VISIBLE);
				// Populate the view, sometimes need to new one
				View theView = new View(mContext);
				if (insideView.getId() <= 0) {
					theView = getItemView(currentPos, null, parent);
					theView.setLayoutParams(params);
					layout.addView(theView, i);
					layout.removeView(insideView);
				} else {
					theView = getItemView(currentPos, insideView, parent);
				}
				theView.setOnClickListener(new ListItemClickListener (currentPos));				
			}
			else {
				insideView.setVisibility(View.INVISIBLE);
			}
		}
	}
}
