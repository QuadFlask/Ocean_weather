package kr.ac.jejunu.ocean_weather;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.collect.Maps;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import rx.functions.Action1;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements View.OnTouchListener {
	@ViewById(R.id.iv_jeju)
	ImageView iv;
	@ViewById(R.id.tv_time)
	TextView tv;

	Api api;
	Data data;

	Timer timer;

	private static Map<String, PointF> locations = Maps.newHashMap();

	static {
		float imageWidth = 761;
		float imageHeight = 756;

		locations.put("제주", new PointF(370 / imageWidth, 270 / imageHeight));
		locations.put("모슬포", new PointF(137 / imageWidth, 563 / imageHeight));
		locations.put("서귀포", new PointF(385 / imageWidth, 543 / imageHeight));
		locations.put("성산포", new PointF(680 / imageWidth, 320 / imageHeight));
		locations.put("도농탄", new PointF(154 / imageWidth, 629 / imageHeight));
		locations.put("중문해수욕장", new PointF(260 / imageWidth, 543 / imageHeight));
	}

	@AfterViews
	void afterViews() {
		RestAdapter adapter = getBuilder().build();
		api = adapter.create(Api.class);
		fetchData();
		measureRealSize();
		iv.setOnTouchListener(this);
	}

	private void fetchData() {
		if (api != null)
			api.fetch().subscribe(
					result -> {
						data = result;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String text = simpleDateFormat.format(new Date());
						updateText(text);
					},
					throwable -> {
						throwable.printStackTrace();
						new MaterialDialog.Builder(MainActivity.this)
								.titleColorRes(R.color.material_text_black)
								.theme(Theme.LIGHT)
								.cancelable(true)
								.title("error").show();
					});
	}

	@UiThread
	void updateText(String text) {
		tv.setText(text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				fetchData();
			}
		}, 10000, 60000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		timer.purge();
		timer = null;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			String location = findLocation(event.getX(), event.getY());
			if (location != null)
				openInfo(location);
		}
		return true;
	}

	private String findLocation(float x, float y) {
		int width = iv.getWidth();
		int height = iv.getHeight();
		PointF clickPosition = new PointF(x, y);

		for (Map.Entry<String, PointF> entry : locations.entrySet()) {
			PointF location = new PointF(entry.getValue().x, entry.getValue().y);
			location.x *= width;
			location.y *= height;
			if (dist(location, clickPosition) < dp2px(24)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private double dist(PointF a, PointF b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public int dp2px(float dp) {
		return (int) (density * dp + .5f);
	}

	public void measureRealSize() {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// since SDK_INT = 1;
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				width = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				height = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
				width = realSize.x;
				height = realSize.y;
			} catch (Exception e) {
				e.printStackTrace();
			}
		density = metrics.density;
	}

	private float density = 1;

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
