package jp.senchan.android.wasatter.task;

import java.util.ArrayList;

import org.json.JSONException;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.WasatterItem;
import jp.senchan.android.wasatter.adapter.Odai;
import jp.senchan.android.wasatter.adapter.Timeline;
import jp.senchan.android.wasatter.client.OldTwitterClient;
import jp.senchan.android.wasatter.client.OldWassrClient;
import jp.senchan.android.wasatter.utils.StatusItemComparator;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class TaskReloadTimeline extends
        AsyncTask<String, String, ArrayList<WasatterItem>> {
    protected ListView listview;
    protected int mode;
    public static final int MODE_TIMELINE = 1;
    public static final int MODE_REPLY = 2;
    public static final int MODE_MYPOST = 3;
    public static final int MODE_ODAI = 4;
    public static final int MODE_TODO = 5;
    public static final int MODE_CHANNEL_LIST = 6;
    public static final int MODE_CHANNEL = 7;
    public static final String[] msg = new String[] { "Timeline", "Reply",
            "My post", "Odai", "TODO", "Channel list", "Channel status" };

    private WasatterActivity mActivity;

    // コンストラクタ
    public TaskReloadTimeline(WasatterActivity activity, ListView lv, int mode) {
        this.listview = lv;
        this.mode = mode;
        mActivity = activity;
    }

    private OldWassrClient wassrClient () {
    	return mActivity.app().wassrClient;
    }
    private OldTwitterClient twitterClient () {
    	return mActivity.app().twitterClient;
    }

    // バックグラウンドで実行する処理
    protected ArrayList<WasatterItem> doInBackground(String... param) {
        ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
        switch (this.mode) {
        case TaskReloadTimeline.MODE_TIMELINE:
            try {
                ret = wassrClient().getTimeLine();
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_WASSR,
                        String.valueOf(e.getStatusCode()));
            }

            try {
                ret.addAll(twitterClient().getTimeLine());
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_TWITTER,
                        String.valueOf(e.getStatusCode()));
            }
            break;
        case TaskReloadTimeline.MODE_REPLY:
            try {
                ret = wassrClient().getReply();
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_WASSR,
                        String.valueOf(e.getStatusCode()));
            }

            try {
                ret.addAll(twitterClient().getReply());
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_TWITTER,
                        String.valueOf(e.getStatusCode()));
            }

            break;
        case TaskReloadTimeline.MODE_MYPOST:
            try {
                ret = wassrClient().getMyPost();
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_WASSR,
                        String.valueOf(e.getStatusCode()));
            }

            try {
                ret.addAll(twitterClient().getMyPost());
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_TWITTER,
                        String.valueOf(e.getStatusCode()));
            }

            break;
        case TaskReloadTimeline.MODE_ODAI:
            try {
                ret = wassrClient().getOdai();
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_WASSR,
                        String.valueOf(e.getStatusCode()));
            }

            break;
        case TaskReloadTimeline.MODE_CHANNEL_LIST:
            try {
                ret = wassrClient().getChannelList();
            } catch (TwitterException e) {
                publishProgress(Wasatter.SERVICE_WASSR,
                        String.valueOf(e.getStatusCode()));
            }
            break;
        case TaskReloadTimeline.MODE_CHANNEL:
            try {
                ret = wassrClient().getChannel(param[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
                String cause;
                if (e.getCause() instanceof JSONException) {
                    cause = "JSON";
                } else {
                    cause = String.valueOf(e.getStatusCode());
                }
                publishProgress(Wasatter.SERVICE_WASSR, cause);
            }
            break;
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // まず、何が起こってここに飛んできたか判定
        String service = values[0];
        String error = values[1];
        mActivity.app().displayHttpError(error, service);
    }

    // 進行中に出す処理
    protected void onPreExecute() {
    	//TODO ローディングの処理
    	/*
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("Loading ");
        sb.append(TaskReloadTimeline.msg[this.mode - 1]);
        sb.append("...");
        Wasatter.main.loading_timeline_text.setText(sb.toString());
        Wasatter.main.layout_progress_timeline.setVisibility(View.VISIBLE);
        */
    };

    // メインスレッドで実行する処理
    @Override
    protected void onPostExecute(ArrayList<WasatterItem> result) {
        // 取得結果の代入
        boolean set = false;
        switch (this.mode) {
        case TaskReloadTimeline.MODE_TIMELINE:
            Wasatter.main.list_timeline = result;
            set = Wasatter.main.button_timeline.isChecked();
            break;
        case TaskReloadTimeline.MODE_REPLY:
            Wasatter.main.list_reply = result;
            set = Wasatter.main.button_reply.isChecked();
            break;
        case TaskReloadTimeline.MODE_MYPOST:
            Wasatter.main.list_mypost = result;
            set = Wasatter.main.button_mypost.isChecked();
            break;

        case TaskReloadTimeline.MODE_ODAI:
            Wasatter.main.list_odai = result;
            set = Wasatter.main.button_odai.isChecked();
            break;
        case TaskReloadTimeline.MODE_CHANNEL_LIST:
            Wasatter.main.list_channel_list = result;
            set = Wasatter.main.button_channel.isChecked();
            break;
        case TaskReloadTimeline.MODE_CHANNEL:
            Wasatter.main.list_channel = result;
            set = Wasatter.main.button_channel.isChecked();
            break;
        }

        // ListViewへの挿入
        if (set) {
            switch (this.mode) {
            case TaskReloadTimeline.MODE_ODAI:
                Odai adapter_odai = new Odai(this.listview.getContext(),
                        R.layout.old_odai_row, result);
                this.listview.setAdapter(adapter_odai);
                break;
            case TaskReloadTimeline.MODE_CHANNEL_LIST:
                ArrayAdapter<WasatterItem> adapter_channel_list = new ArrayAdapter<WasatterItem>(
                        this.listview.getContext(),
                        android.R.layout.simple_spinner_item, result);
                adapter_channel_list
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner channel_list = (Spinner) Wasatter.main
                        .findViewById(R.id.channel_list);
                channel_list.setAdapter(adapter_channel_list);
                break;
            default:
                boolean channel = this.mode == TaskReloadTimeline.MODE_CHANNEL;
                Timeline adapter = new Timeline(mActivity,
                        R.layout.old_timeline_row, result, channel);
                if (!channel) {
                    adapter.sort(new StatusItemComparator());
                }
                this.listview.setAdapter(adapter);
                break;
            }
            this.listview.requestFocus();
        }
        Wasatter.main.layout_progress_timeline.setVisibility(View.GONE);
    }
}