package io;

public record ActionIO(String type, String page, CredentialsIO credentials, String feature,
                       String subscribedGenre, String startsWith, FiltersIO filters, String count,
                       String movie, int rate, MovieIO addedMovie, String deletedMovie) {
}
