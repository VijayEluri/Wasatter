package jp.senchan.android.wasatter2.setting;

import jp.senchan.android.wasatter2.R;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WassrAccount extends Setting {
	public static final String ID = "wassr_id";
	public static final String PASS = "wassr_pass";
	public static final String LOAD_TL = "wassr_load_timeline";
	public static final String LOAD_FAVORITE = "display_load_favorite_image";
	public static final String POST_ENABLE = "enable_wassr";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wassr_account);
		// IDとパスワードを入力
		final EditText id = (EditText) findViewById(R.id.id);
		final EditText password = (EditText) findViewById(R.id.password);
		id.setText(Setting.get(ID, ""));
		password.setText(Setting.get(PASS, ""));

		// パスワードを表示のチェックボックスにイベントを割り当てる
		CheckBox showPassword = (CheckBox) findViewById(R.id.showPassword);
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// チェックされていればパスワードを表示する。
				password.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
						: InputType.TYPE_CLASS_TEXT
								| InputType.TYPE_TEXT_VARIATION_PASSWORD);

			}
		});

		// タイムラインとイイネアイコンのロード設定を読み込む
		final CheckBox loadTimeline = (CheckBox) findViewById(R.id.loadTimeline);
		final CheckBox loadFavorite = (CheckBox) findViewById(R.id.loadFavorite);
		final CheckBox postEnable = (CheckBox) findViewById(R.id.postEnable);
		loadTimeline.setChecked(Setting.get(LOAD_TL, false));
		loadFavorite.setChecked(Setting.get(LOAD_FAVORITE, true));
		postEnable.setChecked(Setting.get(POST_ENABLE, false));

		// 保存とキャンセルボタンにイベントを割り当てる
		Button saveButton = (Button) findViewById(R.id.saveButton);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Setting.set(ID, id.getText().toString());
				Setting.set(PASS, password.getText().toString());
				Setting.set(LOAD_TL, loadTimeline.isChecked());
				Setting.set(LOAD_FAVORITE, loadFavorite.isChecked());
				Setting.set(POST_ENABLE, postEnable.isChecked());
				Toast.makeText(WassrAccount.this, MESSAGE, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// キャンセルボタンが押されたら前の画面に戻る
				WassrAccount.this.finish();
			}
		});
	}
}