package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.types.RoomType;
import bms.player.beatoraja.multiplayer.packets.Packet;

public class ServerRooms implements Packet {
    private String topic;
    private RoomType[] rooms;

    public RoomType[] getRooms() {
        return rooms;
    }
}
