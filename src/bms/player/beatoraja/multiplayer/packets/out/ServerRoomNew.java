package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class ServerRoomNew implements Packet {
    private final String topic = "server.room.new";
    private final String roomName, password;

    public ServerRoomNew(String roomName, String password) {
        this.roomName = roomName;
        this.password = password;
    }
}
