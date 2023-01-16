package execution;

public final class Notification {
    private String movieName;
    private String message;

    public Notification(final String title, final String msg) {
        movieName = title;
        message = msg;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
