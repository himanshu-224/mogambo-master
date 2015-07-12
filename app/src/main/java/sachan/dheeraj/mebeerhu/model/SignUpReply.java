package sachan.dheeraj.mebeerhu.model;

/**
 * Created by naveen.goel on 12/07/15.
 */
public class SignUpReply {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SignUpReply{" +
                "token='" + token + '\'' +
                '}';
    }
}
