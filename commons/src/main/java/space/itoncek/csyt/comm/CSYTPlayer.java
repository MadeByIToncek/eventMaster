/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.comm;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public final class CSYTPlayer {
    private final String name;
    private final int points;
    private final Team team;
    private final long createdAt;

    public CSYTPlayer(String name, int points, Team team) {
        this.name = name;
        this.points = points;
        this.team = team;
        this.createdAt = System.currentTimeMillis();
    }

    public String name() {
        return name;
    }

    public int points() {
        return points;
    }

    public Team team() {
        return team;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        CSYTPlayer that = (CSYTPlayer) obj;
        return Objects.equals(this.name, that.name) &&
                this.points == that.points &&
                Objects.equals(this.team, that.team) &&
                this.createdAt == that.createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, points, team, createdAt);
    }

    @Override
    public String toString() {
        return "CSYTPlayer[" +
                "name=" + name + ", " +
                "points=" + points + ", " +
                "team=" + team + ", " +
                "createdAt=" + LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.of(String.valueOf(ZoneOffset.systemDefault()))) + "]";
    }

}
