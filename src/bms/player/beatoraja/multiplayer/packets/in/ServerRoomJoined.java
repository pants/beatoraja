package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.types.RoomType;

public class ServerRoomJoined implements Packet {
    private String topic;
    private RoomType room;

    public RoomType getRoom() {
        return room;
    }
}
