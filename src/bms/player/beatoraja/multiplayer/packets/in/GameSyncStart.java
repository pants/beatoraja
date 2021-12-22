package bms.player.beatoraja.multiplayer.packets.in;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.types.UserType;

public class GameSyncStart implements Packet {
    private String topic;
    private UserType[] users;
}
