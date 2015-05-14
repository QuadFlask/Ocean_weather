package kr.ac.jejunu.ocean_weather;

import com.google.common.collect.Maps;

import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

public class Data {
	public String state;
	public int key;
	public String[][][] data;
	public static final Map<String, String> cache = Maps.newConcurrentMap();

	public static void extractInfo(Data data, String location, Action1<String> action) {
		Observable
				.from(data.data)
				.subscribe(strings -> {
							Observable
									.from(strings)
									.filter(strings1 -> strings1[1].equals(location))
									.subscribe(strings1 -> {
										String result = cache.get(location);
										if (result == null) {
											result = "조위: " + strings1[13];
											result += "\n수온: " + strings1[14];
											result += "\n염분: " + strings1[15];
											result += "\n파고: " + strings1[16];
											result += "\n기온: " + strings1[17];
											result += "\n기압: " + strings1[18];
											result += "\n풍향: " + strings1[19];
											result += "\n풍속: " + strings1[20];
											result += "\n시간: " + strings1[12];

											cache.put(location, result);
										}
										action.call(result);
									});
						}

				);
	}
}
//[
//	0	'13',
//	1	'제주',
//	2	'126.543169',
//	3	'33.527475',
//	4	'0',
//	5	'0',
//	6	'0',
//	7	'2',
//	8	'0',
//	9	'0',
//	0	'0',
//	1	'2',
//	2	'2015.05.13 23:39',
//	3	'124',조위
//	4	'15.9',수온
//	5	'33',염분
//	6	'', 파고
//	7	'18.7',기온
//	8	'1007.9',기압
//	9	'122',풍향
//	0	'1.7', 아마 풍속?
//	1	'',
//	2	'']