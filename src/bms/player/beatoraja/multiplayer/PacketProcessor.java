package bms.player.beatoraja.multiplayer;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.multiplayer.packets.Packet;
import bms.player.beatoraja.multiplayer.packets.in.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.Arrays;
import java.util.HashMap;

public class PacketProcessor {

    private final MainController main;
    public MPServerConnection server;

    private HashMap<String, Class<? extends Packet>> packets = new HashMap<>();

    public PacketProcessor(MainController main, MPServerConnection serverConnection) {
        this.main = main;
        this.server = serverConnection;

        packets.put("server.info", ServerInfo.class);
        packets.put("server.rooms", ServerRooms.class);
        packets.put("server.room.joined", ServerRoomJoined.class);
        packets.put("room.update", RoomUpdate.class);
        packets.put("game.started", GameStarted.class);
    }

    public void processPacket(String topic, String jsonStr) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        if (!packets.containsKey(topic)) {
            return;
        }

        Class<? extends Packet> e = packets.get(topic);
        Packet packet = json.fromJson(e, jsonStr);

        if (packet instanceof ServerInfo) {
            onServerInfo((ServerInfo) packet);
        } else if (packet instanceof ServerRooms) {
            onServerRooms((ServerRooms) packet);
        } else if (packet instanceof RoomUpdate) {
            onRoomUpdate((RoomUpdate) packet);
        } else if (packet instanceof ServerRoomJoined) {
            onServerRoomJoined((ServerRoomJoined) packet);
        } else if (packet instanceof GameStarted) {
            onGameStarted((GameStarted) packet);
        }
    }

    private void onServerInfo(ServerInfo info) {
        server.setRefreshRate(info.getRefreshRate());
        server.setUserId(info.getUserid());
    }

    private void onServerRooms(ServerRooms packet) {
        if (packet.getRooms() == null) {
            server.getAvailableRooms().clear();
            return;
        }

        server.getAvailableRooms().addAll(Arrays.asList(packet.getRooms()));
    }

    private void onRoomUpdate(RoomUpdate packet) {
        final RoomData roomData = server.getRoomData();
        final String chartHash = packet.getChartHash();

        roomData.updateRoom(packet);

        if (chartHash != null) {
            server.pendingChartUpdateHash = chartHash;
        }
    }

    private void onServerRoomJoined(ServerRoomJoined packet) {
        server.getRoomData().setRoomInfo(packet.getRoom());
        server.joinRoom = true;
    }

    private void onGameStarted(GameStarted gameStarted) {
        server.pendingGameStart = true;
    }

}
