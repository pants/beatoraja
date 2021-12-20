package bms.player.beatoraja.multiplayer.types;

public class UserType {
    private String id;
    private String name;
    private boolean ready;
    private boolean missing_map;
    private int level;
    private int score;
    private int combo;
    private int clear; //0 exit, 1 failed, 2 clear, 3 hard clear, 4 fc
    private String extra_data;

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isMissingMap() {
        return missing_map;
    }
}
