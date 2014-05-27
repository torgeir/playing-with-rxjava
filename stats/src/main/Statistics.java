import rx.Observable;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.FuncN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Statistics {

    public static void main(String[] args) {

        List<Observable<ObservableHttpResponse>> observableRequests = Arrays.asList(
                Http.getAsync("http://localhost:3001/stream"),
                Http.getAsync("http://localhost:3002/stream"),
                Http.getAsync("http://localhost:3003/stream"),
                Http.getAsync("http://localhost:3004/stream"));

        List<Observable<Stats>> observableStats = observableRequests.stream()
                .map(observableRequest ->
                        observableRequest.flatMap(response ->
                                response.getContent()
                                        .map(new EventStreamJsonMapper<>(Stats.class))))
                .collect(toList());

        Observable<List<Stats>> joinedObservables = Observable.zip(
                observableStats.get(0),
                observableStats.get(1),
                observableStats.get(2),
                observableStats.get(3),
                Arrays::asList);

        joinedObservables
                .take(10)
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
