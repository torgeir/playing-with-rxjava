import rx.Observable;
import rx.apache.http.ObservableHttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;

public class Statistics {

    public static void main(String[] args) {

        Observable<ObservableHttpResponse> observableRequest1 = Http.getAsync("http://localhost:3001/stream");
        Observable<ObservableHttpResponse> observableRequest2 = Http.getAsync("http://localhost:3002/stream");
        Observable<ObservableHttpResponse> observableRequest3 = Http.getAsync("http://localhost:3003/stream");
        Observable<ObservableHttpResponse> observableRequest4 = Http.getAsync("http://localhost:3004/stream");

        Observable<Stats> observableLoad1 = observableRequest1.flatMap(response -> response.getContent().map(new EventStreamJsonMapper<>(Stats.class)));
        Observable<Stats> observableLoad2 = observableRequest2.flatMap(response -> response.getContent().map(new EventStreamJsonMapper<>(Stats.class)));
        Observable<Stats> observableLoad3 = observableRequest3.flatMap(response -> response.getContent().map(new EventStreamJsonMapper<>(Stats.class)));
        Observable<Stats> observableLoad4 = observableRequest4.flatMap(response -> response.getContent().map(new EventStreamJsonMapper<>(Stats.class)));

        Observable<List<Stats>> joinedObservables = Observable.zip(
                observableLoad1,
                observableLoad2,
                observableLoad3,
                observableLoad4,
                Arrays::asList);

        joinedObservables
                .take(4)
                .subscribe(
                    (List<Stats> statslist) -> {
                        System.out.println(statslist);

                        double average = statslist.stream()
                                .mapToInt(stats -> stats.ongoingRequests)
                                .average()
                                .getAsDouble();

                        System.out.println("avg: " + average);
                    },
                    System.err::println,
                    Http::shutdown);
    }

}
