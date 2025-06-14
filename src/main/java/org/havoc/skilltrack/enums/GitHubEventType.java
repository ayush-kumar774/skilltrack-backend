package org.havoc.skilltrack.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum GitHubEventType {
    PUSH("PushEvent"),
    PR("PullRequestEvent");

    private final String type;

    GitHubEventType(String type) {
        this.type = type;
    }

    public static boolean isLoggable(String type) {
        return Arrays.stream(values()).anyMatch(e -> e.type.equalsIgnoreCase(type));
    }

    public static Optional<GitHubEventType> from(String type) {
        return Arrays.stream(values())
                .filter(e -> e.type.equals(type))
                .findFirst();
    }

}
