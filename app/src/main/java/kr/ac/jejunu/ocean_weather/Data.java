package kr.ac.jejunu.ocean_weather;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

public class Data {
	public String state;
	public int key;
	public String[][][] data;
	public static final Map<String, String> cache = Maps.newConcurrentMap();

	public static final Map<Range<Integer>, Integer> warnings = Maps.newConcurrentMap();

	static {
		warnings.put(Range.closedOpen(13, 15), 1);
		warnings.put(Range.closedOpen(15, 18), 2);
		warnings.put(Range.closedOpen(18, 20), 3);
		warnings.put(Range.closedOpen(20, 23), 4);
		warnings.put(Range.closedOpen(23, Integer.MAX_VALUE), 6);
	}

//	13도~15도 1시간 이상 해수욕시 저체온증 위험 있음.
//	15도~18도 2시간 이상 해수욕시 저체온증 위험 있음.
//	18도~20도 3시간 이상 해수욕시 저체온증 위험 있음.
//	20도~23도 4시간 이상 해수욕시 저체온증 위험 있음.
//	23도 이상 6시간 이상 해수욕시 저체온증 위험 있음.

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
											result += "\n" + getWarningString(strings1[14]);

											cache.put(location, result);
										}
										action.call(result);
									});
						}
				);
	}

	private static String getWarningString(String string) {
		for (Range<Integer> integerRange : warnings.keySet()) {
			try {
				if (integerRange.contains((int) Double.parseDouble(string)))
					return warnings.get(integerRange) + "시간 이상 해수욕시 저체온증 위험 있음.";
			} catch (NumberFormatException e) {
			}
		}
		return "";
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