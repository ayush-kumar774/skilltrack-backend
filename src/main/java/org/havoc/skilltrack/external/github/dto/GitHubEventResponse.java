package org.havoc.skilltrack.external.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEventResponse {
    private String type;
    private Repo repo;
    private Actor actor;
    private ZonedDateTime created_at;
    private Payload payload;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Repo {
        private String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Actor {
        private String login;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class Payload {
        private int size;
        private List<Commit> commits;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter @Setter
        public static class Commit {
            private String message;
            private String url;
        }
    }
}
