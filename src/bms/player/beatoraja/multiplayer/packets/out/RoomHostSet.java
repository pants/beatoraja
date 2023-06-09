package bms.player.beatoraja.multiplayer.packets.out;

public class RoomHostSet {
    private final String topic = "room.host.set";
    private final String host;

    public RoomHostSet(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
