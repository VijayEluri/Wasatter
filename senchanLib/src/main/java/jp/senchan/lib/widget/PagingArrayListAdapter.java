package jp.senchan.lib.widget;

import java.util.ArrayList;

import jp.senchan.lib.app.ArrayListAdapter;
import android.content.Context;
import android.view.View;
import android.widget.AbsListView.LayoutParams;

public abstract class PagingArrayListAdapter<T> extends ArrayListAdapter<T> {
	
	private int mViewHeight;

	public PagingArrayListAdapter(Context context, ArrayList<T> list) {
		super(context, list);
	}
	
	public void notifyDataSetChanged(int viewHeight) {
		mViewHeight = viewHeight;
		super.notifyDataSetChanged();
	}
	
	public void setViewHeight(int viewHeight) {
		mViewHeight = viewHeight;
	}

	@Override
	public View createView(int position, T item, View v) {
		View createdView = createNonLayoutedView(position, item, v);
		if (mViewHeight != 0) {
			LayoutParams params = (LayoutParams) createdView.getLayoutParams();
			if (params == null) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT, mViewHeight);
			} else {
				params.height = mViewHeight;
			}
			createdView.setMinimumHeight(mViewHeight);
		}
		return v;
	}
	
	public abstract View createNonLayoutedView(int position, T item, View v);

}
