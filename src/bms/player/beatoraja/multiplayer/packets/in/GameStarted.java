package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class GameStarted implements Packet {
    private String topic;
    private boolean hard, mirror;
}
