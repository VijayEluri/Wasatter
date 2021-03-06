package jp.senchan.android.wasatter.app;

import java.util.ArrayList;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import jp.senchan.android.wasatter.IntentCode;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.WasatterItem;
import jp.senchan.android.wasatter.adapter.Odai;
import jp.senchan.android.wasatter.adapter.Timeline;
import jp.senchan.android.wasatter.adapter.WasatterAdapter;
import jp.senchan.android.wasatter.task.TaskReloadTimeline;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class TimelineActivity extends WasatterActivity {
    public ListView ls;
    public ArrayList<WasatterItem> list_timeline;
    public ArrayList<WasatterItem> list_reply;
    public ArrayList<WasatterItem> list_mypost;
    public ArrayList<WasatterItem> list_odai;
    public ArrayList<WasatterItem> list_channel_list;
    public ArrayList<WasatterItem> list_channel;
    public Button button_reload_channel_list;
    public ToggleButton button_timeline;
    public ToggleButton button_reply;
    public ToggleButton button_mypost;
    public ToggleButton button_channel;
    public ToggleButton button_odai;
    public ProgressBar progress_image;
    public LinearLayout layout_progress_timeline;
    public LinearLayout layout_channel_list;
    public Spinner spinner_channel_list;
    public TextView loading_timeline_text;
    public boolean first_load = true;
    public boolean reload_image = false;
    public boolean from_config = false;
    public int mode = TaskReloadTimeline.MODE_TIMELINE;
    private int selctedButtonId;
    public String selected_channel;
    public WasatterItem selectedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.old_timeline);
        this.ls = (ListView) this.findViewById(R.id.timeline_list);
        this.progress_image = (ProgressBar) this
                .findViewById(R.id.load_image_progress);
        this.layout_progress_timeline = (LinearLayout) this
                .findViewById(R.id.layout_load_timeline);
        this.layout_channel_list = (LinearLayout) this
                .findViewById(R.id.layout_channel_list);
        this.loading_timeline_text = (TextView) this
                .findViewById(R.id.text_loading_timeline);
        this.spinner_channel_list = (Spinner) this
                .findViewById(R.id.channel_list);
        this.button_reload_channel_list = (Button) this
                .findViewById(R.id.button_reload_channel_list);
        this.button_reload_channel_list
                .setOnClickListener(new ChannelReloadButtonClickListener());
        // トグルボタンを代入
        this.button_timeline = (ToggleButton) this
                .findViewById(R.id.toggle_button_home);
        this.button_reply = (ToggleButton) this
                .findViewById(R.id.toggle_button_reply);
        this.button_mypost = (ToggleButton) this
                .findViewById(R.id.toggle_button_mypost);
        this.button_odai = (ToggleButton) this
                .findViewById(R.id.toggle_button_odai);
        this.button_channel = (ToggleButton) this
                .findViewById(R.id.toggle_button_channel);

        // トグルボタンにListViewの中身を切り替えるイベントを割り当て
        this.button_timeline
                .setOnClickListener(new TimelineButtonClickListener());
        this.button_reply.setOnClickListener(new ReplyButtonClickListener());
        this.button_mypost.setOnClickListener(new MyPostButtonClickListener());
        this.button_odai.setOnClickListener(new OdaiButtonClickListener());
        this.button_channel
                .setOnClickListener(new ChannelButtonClickListener());

        // トグルボタンの初期値をタイムラインに設定
        this.buttonSelect(R.id.toggle_button_home);
        // ボタンにイベントを割り当て
        Button button_new = (Button) this.findViewById(R.id.button_new_post);
        button_new.setOnClickListener(new ButtonNewPostListener());
        Button button_setting = (Button) this
                .findViewById(R.id.button_open_setting);
        button_setting.setOnClickListener(new ButtonOpenSettingListener());
        Button button_reload = (Button) this.findViewById(R.id.button_reload);
        button_reload.setOnClickListener(new ButtonReloadListener());
        this.spinner_channel_list
                .setOnItemSelectedListener(new ChannelListClickListener());
        Wasatter.main = this;
    }

    @Override
    protected void onResume() {
        // TODO 自動生成されたメソッド・スタブ
        super.onResume();
        // ボタンの表示設定
        Wasatter app = app();
        LinearLayout layout_buttons = (LinearLayout) this
                .findViewById(R.id.layout_buttons);
        if (app.isDisplayButtons()) {
            layout_buttons.setVisibility(View.VISIBLE);
        } else {
            layout_buttons.setVisibility(View.GONE);
        }
        // テーマの設定
        // TwitterもしくはWassrが有効になっているかチェックする
        boolean enable = (!app.isTwitterEnabled())
                && (!app.isWassrEnabled());
        boolean wassr_empty = (app.isWassrEnabled() && ("".equals(app
                .getWassrId()) || "".equals(app.getWassrPass())));
        boolean twitter_oauth_empty = app.isTwitterEnabled()
                && ("".equals(app.getTwitterToken()) || "".equals(app
                        .getTwitterTokenSecret()));
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        if (enable) {
            adb.setMessage(R.string.notice_message_no_enable);
            adb.setTitle(R.string.notice_title_no_enable);
            adb.setPositiveButton("OK", new OpenSettingClickListener());
            adb.show();
        } else if (wassr_empty) {
            adb.setMessage(R.string.notice_message_no_value);
            adb.setTitle(R.string.notice_title_no_value);
            adb.setPositiveButton("OK", new OpenSettingClickListener());
            adb.show();
        } else if (twitter_oauth_empty) {
            adb.setMessage(R.string.notice_message_no_twitter_oauth_token);
            adb.setPositiveButton("OK", new OpenSettingClickListener());
            adb.show();
        } else if (this.first_load) {
            this.doReloadTask(this.mode);
        } else if (this.from_config) {
            WasatterAdapter adapter = (WasatterAdapter) this.ls.getAdapter();
            if (adapter != null) {
                adapter.updateView();
            }
            buttonSelect(checkWassrEnabled(selctedButtonId));
        }
    }

    public void getTimeLine() {
        this.doReloadTask(TaskReloadTimeline.MODE_TIMELINE);
    }

    public void getReply() {
        this.doReloadTask(TaskReloadTimeline.MODE_REPLY);
    }

    public void getMyPost() {
        this.doReloadTask(TaskReloadTimeline.MODE_MYPOST);
    }

    public void getOdai() {
        this.doReloadTask(TaskReloadTimeline.MODE_ODAI);
    }

    public void getChannelList() {
        this.doReloadTask(TaskReloadTimeline.MODE_CHANNEL_LIST);
    }

    public void getChannel(String channel) {
        this.ls.setAdapter(null);
        this.first_load = false;
        TaskReloadTimeline rt = new TaskReloadTimeline(this, this.ls,
                TaskReloadTimeline.MODE_CHANNEL);
        rt.execute(channel);
    }

    public void doReloadTask(int mode) {
        this.ls.setAdapter(null);
        this.first_load = false;
        TaskReloadTimeline rt = new TaskReloadTimeline(this, this.ls, mode);
        rt.execute();
    }

    // メニューが生成される際に起動される。
    // この中でメニューのアイテムを追加したりする。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // メニューインフレーターを取得
        getSupportMenuInflater().inflate(R.menu.main, menu);
        // できたらtrueを返す
        return true;
    }

    // メニューのアイテムが選択された際に起動される。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_open_setting:
            this.openSetting();
            break;
        case R.id.menu_open_version:
            this.openVersion();
            break;
        case R.id.menu_status_update:
            this.openNewPost();
            break;
        case R.id.menu_status_reload:
            this.reload();
            break;
        default:
            break;
        }
        return true;
    }

    public void buttonSelect(int id) {
        // Wassr固有の機能はWassrを有効にしていないと利用出来ないようにする
        id = checkWassrEnabled(id);
        this.button_timeline.setChecked(false);
        this.button_timeline.setClickable(true);
        this.button_reply.setChecked(false);
        this.button_reply.setClickable(true);
        this.button_mypost.setChecked(false);
        this.button_mypost.setClickable(true);
        this.button_channel.setChecked(false);
        this.button_channel.setClickable(true);
        this.button_odai.setChecked(false);
        this.button_odai.setClickable(true);
        ToggleButton btn = (ToggleButton) this.findViewById(id);
        btn.setChecked(true);
        btn.setClickable(false);
        this.ls.setOnItemClickListener(new TimelineItemClickListener());
        int channnel_list_visible = View.GONE;
        if (id == R.id.toggle_button_channel) {
            channnel_list_visible = View.VISIBLE;
        }
        layout_channel_list.setVisibility(channnel_list_visible);
        this.selctedButtonId = id;
    }

    public int checkWassrEnabled(int id) {
        boolean wassr = app().isWassrEnabled();
        this.button_odai.setEnabled(wassr);
        this.button_channel.setEnabled(wassr);
        ArrayList<Integer> wassr_function_list = new ArrayList<Integer>();
        wassr_function_list.add(R.id.toggle_button_channel);
        wassr_function_list.add(R.id.toggle_button_odai);
        if (wassr_function_list.indexOf(Integer.valueOf(id)) != -1 && !wassr) {
            id = R.id.toggle_button_home;
        }
        return id;
    }

    /**
     * 設定ダイアログを開くメソッド
     */
    public void openSetting() {
        Intent intent_setting = new Intent(this, Setting.class);
        this.from_config = true;
        this.startActivity(intent_setting);
    }

    /**
     * 投稿ウィンドウを開くメソッド
     */
    public void openNewPost() {
        Intent intent_status = new Intent(this, Update.class);
        this.startActivity(intent_status);
    }

    /**
     * リロードを実行するメソッド
     */
    public void reload() {
        if (this.button_timeline.isChecked()) {
            this.getTimeLine();
        } else if (this.button_reply.isChecked()) {
            this.getReply();
        } else if (this.button_mypost.isChecked()) {
            this.getMyPost();
        } else if (this.button_odai.isChecked()) {
            this.getOdai();
        } else if (this.button_channel.isChecked()) {
            if (this.selected_channel != null) {
                this.getChannel(this.selected_channel);
            }
        }

    }

    public void openVersion() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.menu_title_version);
        SpannableStringBuilder sb = new SpannableStringBuilder(
                getString(R.string.app_name));
        sb.append(" ");
        try {
            sb.append(getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_META_DATA).versionName);
        } catch (NameNotFoundException e) {
            // ありえない
            e.printStackTrace();
        }
        ad.setMessage(sb.toString());
        ad.setPositiveButton("OK", null);
        ad.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentCode.MAIN_ITEMDETAIL) {
            try {
                Timeline adapter = (Timeline) this.ls.getAdapter();
                adapter.updateView();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Innner Class
     */
    // OnClickListenerの定義
    private class TimelineButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.mode = TaskReloadTimeline.MODE_TIMELINE;
            TimelineActivity.this.buttonSelect(v.getId());
            if (TimelineActivity.this.list_timeline == null) {
                TimelineActivity.this.getTimeLine();
            } else {
                Timeline adapter = new Timeline(TimelineActivity.this,
                        R.id.timeline_list, TimelineActivity.this.list_timeline,
                        false);
                TimelineActivity.this.ls.setAdapter(adapter);
                TimelineActivity.this.ls.requestFocus();
            }
        }
    }

    private class ReplyButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.mode = TaskReloadTimeline.MODE_REPLY;
            TimelineActivity.this.buttonSelect(v.getId());
            if (TimelineActivity.this.list_reply == null) {
                TimelineActivity.this.getReply();
            } else {
                Timeline adapter = new Timeline(TimelineActivity.this,
                        R.id.timeline_list, TimelineActivity.this.list_reply, false);
                TimelineActivity.this.ls.setAdapter(adapter);
                TimelineActivity.this.ls.requestFocus();
            }
        }
    }

    private class MyPostButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.mode = TaskReloadTimeline.MODE_MYPOST;
            TimelineActivity.this.buttonSelect(v.getId());
            if (TimelineActivity.this.list_mypost == null) {
                TimelineActivity.this.getMyPost();
            } else {
                Timeline adapter = new Timeline(TimelineActivity.this,
                        R.id.timeline_list, TimelineActivity.this.list_mypost,
                        false);
                TimelineActivity.this.ls.setAdapter(adapter);
                TimelineActivity.this.ls.requestFocus();
            }
        }
    }

    private class OdaiButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.mode = TaskReloadTimeline.MODE_ODAI;
            TimelineActivity.this.buttonSelect(v.getId());
            if (TimelineActivity.this.list_odai == null) {
                TimelineActivity.this.getOdai();
            } else {
                Odai adapter = new Odai(getBaseContext(), R.id.timeline_list,
                        TimelineActivity.this.list_odai);
                TimelineActivity.this.ls.setAdapter(adapter);
                TimelineActivity.this.ls.requestFocus();
            }

        }
    }

    private class ChannelButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.mode = TaskReloadTimeline.MODE_CHANNEL_LIST;
            TimelineActivity.this.buttonSelect(v.getId());
            if (TimelineActivity.this.list_channel_list == null) {
                TimelineActivity.this.getChannelList();
            }
            // チャンネルの内容取ってたら表示する。
            if (TimelineActivity.this.list_channel != null) {
                Timeline adapter = new Timeline(
                		TimelineActivity.this,
                        R.layout.old_timeline_row, TimelineActivity.this.list_channel,
                        true);
                TimelineActivity.this.ls.setAdapter(adapter);
            }
        }
    }

    private class ChannelReloadButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            TimelineActivity.this.getChannelList();
        }
    }

    /**
     * ダイアログから設定画面を開くOnClickListener
     *
     * @author takuji
     *
     */
    private class OpenSettingClickListener implements
            DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            TimelineActivity.this.openSetting();
        }
    }

    /**
     * タイムラインをクリックした時のOnClickListener
     *
     * @author takuji
     *
     */
    private class TimelineItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            ListView listView = (ListView) parent;
            // 選択されたアイテムを取得します
            TimelineActivity.this.selectedItem = (WasatterItem) listView
                    .getAdapter().getItem(position);
            Intent intent_detail = new Intent(TimelineActivity.this, Detail.class);
            TimelineActivity.this.startActivityForResult(intent_detail,
                    IntentCode.MAIN_ITEMDETAIL);
        }
    }

    private class ChannelListClickListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            Spinner spinner = (Spinner) parent;
            WasatterItem item = (WasatterItem) spinner.getAdapter().getItem(
                    position);
            TimelineActivity.this.getChannel(item.id);
            TimelineActivity.this.selected_channel = item.id;

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO 自動生成されたメソッド・スタブ

        }

    }

    /*
     * ボタンクリック時のリスナー
     */

    /**
     * 投稿ウィンドウを開くOnClickListener
     */
    private class ButtonNewPostListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO 自動生成されたメソッド・スタブ
            openNewPost();
        }

    }

    /**
     * 設定ダイアログを開くOnClickListener
     */
    private class ButtonOpenSettingListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO 自動生成されたメソッド・スタブ
            openSetting();
        }

    }

    /**
     * タイムラインをリロードするOnClickListener
     */
    private class ButtonReloadListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO 自動生成されたメソッド・スタブ
            reload();
        }

    }
}