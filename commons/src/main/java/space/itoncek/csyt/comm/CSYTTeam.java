/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.comm;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CSYTTeam {
    private final long createdAt;
    private final Team color;
    private final List<CSYTPlayer> members;

    public CSYTTeam(Team color, List<CSYTPlayer> members) {
        this.color = color;
        this.members = members;
        this.createdAt = System.currentTimeMillis();
    }

    public Team color() {
        return color;
    }

    public List<CSYTPlayer> members() {
        return members;
    }

    public int points() {
        int sum = 0;
        for (CSYTPlayer member : members) {
            sum += member.points();
        }
        return sum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        CSYTTeam that = (CSYTTeam) obj;
        return Objects.equals(this.color, that.color()) &&
                Arrays.equals(this.members.toArray(new CSYTPlayer[0]), that.members.toArray(new CSYTPlayer[0])) &&
                this.createdAt == that.createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, members, points(), createdAt);
    }

    @Override
    public String toString() {
        return "CSYTPlayer[" +
                "color=" + color + ", " +
                "points=" + points() + ", " +
                "members=" + Arrays.toString(members.toArray(new CSYTPlayer[0])) + ", " +
                "createdAt=" + LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.of(String.valueOf(ZoneOffset.systemDefault()))) + "]";
    }
}
