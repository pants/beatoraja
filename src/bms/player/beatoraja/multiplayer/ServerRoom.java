package bms.player.beatoraja.multiplayer;

public class ServerRoom {
    public final String id;
    public final int current;
    public final int max;
    public final String name;
    public final boolean ingame;
    public final boolean password;
    public final String join_token;

    public ServerRoom(String id, int current, int max, String name, boolean ingame, boolean password, String join_token) {
        this.id = id;
        this.current = current;
        this.max = max;
        this.name = name;
        this.ingame = ingame;
        this.password = password;
        this.join_token = join_token;
    }
}
