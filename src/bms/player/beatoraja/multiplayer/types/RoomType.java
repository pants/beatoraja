package bms.player.beatoraja.multiplayer.types;

public class RoomType {
    private String id;
    private int current;
    private int max;
    private String name;
    private boolean ingame;
    private boolean password;
    private String join_token;

    public RoomType() {
    }

    public RoomType(String id, int current, int max, String name, boolean ingame, boolean password, String join_token) {
        this.id = id;
        this.current = current;
        this.max = max;
        this.name = name;
        this.ingame = ingame;
        this.password = password;
        this.join_token = join_token;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getMax() {
        return max;
    }
}
