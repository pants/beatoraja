package bms.player.beatoraja.multiplayer.packets;

public class UserAuth implements Packet {
    public final String topic = "user.auth";
    private final String password;
    private final String name;
    private final String version;

    public UserAuth(String password, String name, String version) {
        this.password = password;
        this.name = name;
        this.version = version;
    }
}
