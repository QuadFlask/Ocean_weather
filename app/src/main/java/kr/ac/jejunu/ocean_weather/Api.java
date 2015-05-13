package kr.ac.jejunu.ocean_weather;

import retrofit.http.GET;
import rx.Observable;

public interface Api {
	@GET("/koofs/dao/nmap_data.asp?obsItem=ALL&key=31&site_div=KOOFS")
	Observable<Data> fetch();

}