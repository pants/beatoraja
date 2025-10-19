package bms.player.beatoraja.multiplayer.types;

public class UserType {
    private String id;
    private String name;
    private boolean ready;
    private boolean missing_map;
    private int level;
    private Integer score;
    private int combo;
    private int clear; //0 exit, 1 failed, 2 clear, 3 hard clear, 4 fc
    private String extra_data;

    public UserType(){}
    public UserType(String id, String name, boolean ready, boolean missing_map, int level, Integer score, int combo, int clear, String extra_data) {
        this.id = id;
        this.name = name;
        this.ready = ready;
        this.missing_map = missing_map;
        this.level = level;
        this.score = score;
        this.combo = combo;
        this.clear = clear;
        this.extra_data = extra_data;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isMissingMap() {
        return missing_map;
    }

    public String getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public int getClear() {
        return clear;
    }
}
