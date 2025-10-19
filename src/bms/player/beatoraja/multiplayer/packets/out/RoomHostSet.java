package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomHostSet implements Packet {
    private final String topic = "room.host.set";
    private final String host;

    public RoomHostSet(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
