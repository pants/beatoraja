package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class ServerRoomJoin implements Packet {

    private final String topic = "server.room.join";
    private final String id;
    private final String password;
    private final String token;

    public ServerRoomJoin(String id, String password, String token) {
        this.id = id;
        this.password = password;
        this.token = token;
    }
}
