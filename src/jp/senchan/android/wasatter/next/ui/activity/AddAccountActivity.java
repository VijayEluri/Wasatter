package jp.senchan.android.wasatter.next.ui.activity;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.next.ui.fragment.SetTwitterAccountFragment;
import jp.senchan.android.wasatter.next.ui.fragment.SetWassrAccountFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class AddAccountActivity extends WasatterActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		int service = getIntent().getIntExtra("service", 0);
		
		Fragment f = null;
		if (app().getInteger(R.integer.service_id_wassr) == service) {
			f = new SetWassrAccountFragment();
		} else if (app().getInteger(R.integer.service_id_twitter) == service) {
			f = new SetTwitterAccountFragment();
		} else if (app().getInteger(R.integer.service_id_facebook) == service) {
			app().toast("未実装なり").show();
			finish();
			return;
		}
		
		ft.replace(android.R.id.content, f);
		ft.commit();
	}
	
}