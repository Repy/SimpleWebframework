package info.repy.webframework;

public interface WebHandler {
    public boolean handle(Request req);
    public Response request(Request req);
}
