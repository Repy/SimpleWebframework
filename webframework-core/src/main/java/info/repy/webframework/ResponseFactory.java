package info.repy.webframework;

public class ResponseFactory{
    public static Response notFound(){
        try(Response res = new Response(404)){
            return res;
        } catch (Exception e) {
            return null;
        }
    }
}
