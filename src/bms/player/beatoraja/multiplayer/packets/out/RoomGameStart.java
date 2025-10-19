package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class RoomGameStart implements Packet {
    private final String topic;

    public RoomGameStart() {
        this.topic = "room.game.start";
    }
}
