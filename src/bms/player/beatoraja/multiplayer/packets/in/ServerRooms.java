package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.ServerRoom;
import bms.player.beatoraja.multiplayer.packets.Packet;

public class ServerRooms implements Packet {
    private String topic;
    private ServerRoom[] rooms;

    public ServerRoom[] getRooms() {
        return rooms;
    }
}
