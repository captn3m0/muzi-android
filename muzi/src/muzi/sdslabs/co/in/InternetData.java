package muzi.sdslabs.co.in;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class InternetData {

	public String getInternetData(String url_input) throws Exception {
		BufferedReader in = null;
		String data = null;
		try {
			HttpClient client = new DefaultHttpClient();

			URL url = new URL(url_input);
			URI website = new URI(url.getProtocol(), url.getUserInfo(),
					url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
			// url = uri.toURL();

			Log.i("Internet Data", "URL:  " + website.toString());

			// URI website = new URI(url);
			// "https://api.twitter.com/1/users/lookup.json?screen_name=09mangla&include_entities=true");
			HttpGet request = new HttpGet();
			request.setURI(website);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");
			while ((l = in.readLine()) != null) {
				sb.append(l + nl);
			}
			in.close();
			data = sb.toString();
			// Log.i("RETURNED", data);
			return data;
		} finally {
			if (in != null) {
				try {
					in.close();
					return data;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}