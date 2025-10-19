package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomKick implements Packet {
    private final String topic = "room.kick";
    private final String user;

    public RoomKick(String user) {
        this.user = user;
    }
}
