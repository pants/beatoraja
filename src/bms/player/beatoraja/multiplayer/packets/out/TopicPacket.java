package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class TopicPacket implements Packet {
    private final String topic;

    public TopicPacket(String topic) {
        this.topic = topic;
    }
}
