import exceptions.StatsException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;

import java.io.IOException;

public class Http {

    public final static CloseableHttpAsyncClient httpClient;

    static {
        httpClient = createClient();
        httpClient.start();
    }

    public static Observable<ObservableHttpResponse> getAsync(String url) {
        return ObservableHttp.createRequest(HttpAsyncMethods.createGet(url), httpClient).toObservable();
    }

    public static void shutdown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new StatsException("could not close httpasyncclient", e);
        }
    }

    private static CloseableHttpAsyncClient createClient() {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(2000)
                .build();

        return HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(50)
                .build();
    }
}