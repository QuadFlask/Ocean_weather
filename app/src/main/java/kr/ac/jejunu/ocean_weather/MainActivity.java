package kr.ac.jejunu.ocean_weather;

import android.support.v7.app.ActionBarActivity;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit.RestAdapter;
import rx.functions.Action1;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {
	@ViewById(R.id.btn_mosle)
	Button btnMosle;
	@ViewById(R.id.btn_donongtan)
	Button btnDonongtan;
	@ViewById(R.id.btn_jungmoon)
	Button btnJungmoon;
	@ViewById(R.id.btn_seogui)
	Button btnSeogui;
	@ViewById(R.id.btn_jeju)
	Button btnJeju;
	@ViewById(R.id.btn_sungsan)
	Button btnSungsan;

	Api api;
	Data data;

	@AfterViews
	void afterViews() {
		RestAdapter adapter = getBuilder().build();
		api = adapter.create(Api.class);
		api.fetch().subscribe(new Action1<Data>() {
			@Override
			public void call(Data result) {
				data = result;
			}
		});
	}

	@Click(R.id.btn_mosle)
	void onClickMosle() {
		openInfo("모슬포");
	}

	@Click(R.id.btn_donongtan)
	void onClickDonongtan() {
		openInfo("도농탄");
	}

	@Click(R.id.btn_jungmoon)
	void onClickJungmoon() {
		openInfo("중문해수욕장");
	}

	@Click(R.id.btn_seogui)
	void onClickSeogui() {
		openInfo("서귀포");
	}

	@Click(R.id.btn_jeju)
	void onClickJeju() {
		openInfo("제주");
	}

	@Click(R.id.btn_sungsan)
	void onClickSungsan() {
		openInfo("성산포");
	}

	protected RestAdapter.Builder getBuilder() {
		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("http://sms.khoa.go.kr");
		return builder;
	}

	private void openInfo(final String location) {
		if (data != null)
			Data.extractInfo(data, location, dataString ->
					new MaterialDialog.Builder(MainActivity.this)
							.titleColorRes(R.color.material_text_black)
							.theme(Theme.LIGHT)
							.cancelable(true)
							.title(location)
							.content(dataString)
							.positiveText(android.R.string.ok)
							.show());
		else new MaterialDialog.Builder(this)
				.titleColorRes(R.color.material_text_black)
				.theme(Theme.LIGHT)
				.cancelable(true)
				.title("데이터 가져오는중...").show();
	}
}
