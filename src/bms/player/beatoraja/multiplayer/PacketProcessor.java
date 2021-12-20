package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.in.ServerInfo;
import bms.player.beatoraja.multiplayer.packets.in.ServerRooms;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;

public class PacketProcessor {

    public MPServerConnection serverConnection;

    private HashMap<String, Class<? extends Packet>> packets = new HashMap<>();

    public PacketProcessor(MPServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        packets.put("server.info", ServerInfo.class);
        packets.put("server.rooms", ServerRooms.class);
    }

    public void processPacket(String topic, String jsonStr) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        if (!packets.containsKey(topic)) {
            return;
        }

        Class<? extends Packet> e = packets.get(topic);
        serverConnection.notifySubscriber(topic, json.fromJson(e, jsonStr));
    }
}
