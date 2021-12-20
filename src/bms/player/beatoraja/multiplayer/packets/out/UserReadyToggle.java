package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class UserReadyToggle implements Packet {
    private final String topic = "user.ready.toggle";
}
