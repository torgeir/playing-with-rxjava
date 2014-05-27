import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Stats extends Pojo {

    public final int ongoingRequests;

    @JsonCreator
    public Stats(
            @JsonProperty("ongoingRequests") int ongoingRequests) {
        this.ongoingRequests = ongoingRequests;
    }


}
