package bms.player.beatoraja.multiplayer.packets.out;

import bms.player.beatoraja.multiplayer.packets.Packet;

public class UserReadyToggle implements Packet {
    private String topic;

    public UserReadyToggle() {
        topic = "user.ready.toggle";
    }
}
