package jp.senchan.android.wasatter.task;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WassrClient;
import jp.senchan.android.wasatter.WassrTodo;
import jp.senchan.android.wasatter.R.layout;
import jp.senchan.android.wasatter.adapter.Todo;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

public class TaskReloadTodo extends AsyncTask<Void, Void, ArrayList<WassrTodo>> {
	protected ListView listview;

	// コンストラクタ
	public TaskReloadTodo(ListView lv) {
		this.listview = lv;
	}

	// バックグラウンドで実行する処理
	protected ArrayList<WassrTodo> doInBackground(Void... param) {
		ArrayList<WassrTodo> ret = WassrClient.getTodo();
		return ret;
	}

	// 進行中に出す処理
	protected void onPreExecute() {
		Wasatter.main.loading_timeline_text.setText("Loadig TODO...");
		Wasatter.main.layout_progress_timeline.setVisibility(View.VISIBLE);
	};

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO 自動生成されたメソッド・スタブ
		super.onProgressUpdate(values);
	}

	// メインスレッドで実行する処理
	@Override
	protected void onPostExecute(ArrayList<WassrTodo> result) {
		// 取得結果の代入
		Wasatter.main.list_todo = result;
		Todo adapter_todo = new Todo(this.listview.getContext(),
				R.layout.todo_row, result);
		this.listview.setAdapter(adapter_todo);
		this.listview.requestFocus();
		Wasatter.main.layout_progress_timeline.setVisibility(View.GONE);
	}
}