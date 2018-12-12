package example.dsjac.nexus;
import com.loopj.android.http.*;

public class NexusRestClient {

    // Base URL for API Endpoints
    private static final String BASE_URL = "http://ec2-18-217-233-59.us-east-2.compute.amazonaws.com:3000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
