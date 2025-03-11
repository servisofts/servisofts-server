package Servisofts.PG;
import org.json.JSONObject;

public class PGConnectionProps {

    public static PGConnectionProps buildFromJson(JSONObject obj) {
        return new PGConnectionProps("localhost", 5432, "bd_name", "user", "pass");
    }

    private String ip;
    private int puerto;
    private String bd_name;
    private String user;
    private String pass;

    public PGConnectionProps(String ip, int puerto, String bd_name, String user, String pass) {
        this.ip = ip;
        this.puerto = puerto;
        this.bd_name = bd_name;
        this.user = user;
        this.pass = pass;
    }

}
