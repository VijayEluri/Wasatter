/**
 *
 */
package jp.senchan.android.wasatter.setting;

import jp.senchan.android.wasatter.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * アカウント設定
 * 
 * @author Senka/Takuji
 * 
 */
public class SettingRoot extends PreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.setting_root);
		PreferenceScreen wassr = (PreferenceScreen) this.getPreferenceScreen()
				.getPreference(0);
		PreferenceScreen twitter = (PreferenceScreen) this
				.getPreferenceScreen().getPreference(1);
		PreferenceScreen display = (PreferenceScreen) this
				.getPreferenceScreen().getPreference(2);
		PreferenceScreen data = (PreferenceScreen) this.getPreferenceScreen()
				.getPreference(3);

		wassr.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				// Wassr設定の表示
				Intent intent = new Intent(SettingRoot.this, WassrAccount.class);
				startActivity(intent);
				return false;
			}
		});

		twitter.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				// 全般設定の表示
				Intent intent = new Intent(SettingRoot.this,
						TwitterAccount.class);
				startActivity(intent);
				return false;
			}
		});

		display.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				// 表示設定の表示
				Intent intent = new Intent(SettingRoot.this,
						DisplaySetting.class);
				startActivity(intent);
				return false;
			}
		});
		data.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				// データ設定の表示
				Intent intent = new Intent(SettingRoot.this,
						Data.class);
				startActivity(intent);
				return false;
			}
		});
	}
}