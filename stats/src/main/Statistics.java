import rx.Observable;
import rx.apache.http.ObservableHttpResponse;
import rx.functions.FuncN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class Statistics {

    public static void main(String[] args) {

        int port = 3001;
        int servers = 3;

        List<Observable<ObservableHttpResponse>> observableResponses = new ArrayList<>();

        for (int n = 0; n < servers; n++) {
            int serverPort = port + n;
            observableResponses.add(Http.getAsync(String.format("http://localhost:%s/stream", serverPort)));
        }

        List<Observable<Stats>> observableStatses = observableResponses.stream()
            .map(request ->
                request.flatMap(response ->
                    response.getContent().map(new EventStreamJsonMapper<>(Stats.class))))
            .collect(toList());


        Observable<List<Stats>> observableJoinedStats = Observable.zip(observableStatses, objects -> {
            ArrayList<Stats> statses = new ArrayList<>();
            for (Object object : objects) {
                statses.add((Stats) object);
            }
            return statses;
        });

        observableJoinedStats
            .subscribe(
                (List<Stats> joinedStats) -> {
                    System.out.println(joinedStats);

                    double average = joinedStats.stream()
                        .mapToInt(stats -> stats.ongoingRequests)
                        .average()
                        .getAsDouble();

                    System.out.println("avg: " + average);
                },
                System.err::println,
                Http::shutdown);
    }

}
