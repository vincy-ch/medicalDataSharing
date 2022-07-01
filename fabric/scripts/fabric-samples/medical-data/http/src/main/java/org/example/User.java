package org.example;

import java.util.Objects;

public final class User {

    public enum UserType {
        INDIVIDUAL,
        PLATFORM
    }

    private UserType userType;

    public UserType getUserType() {
        return userType;
    }

    public String getId() {
        return id;
    }

    public Integer getPoint() {
        return point;
    }

    public void increasePoint(final Integer increase) {
        point += increase;
    }

    public void decreasePoint(final Integer decrease) {
        point -= decrease;
    }

    private StateType type;

    public StateType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public User(String id, Integer point, String name, UserType userType) {
        this.id = id;
        this.point = point;
        this.name = name;
        this.userType = userType;
    }

    private String id;

    private Integer point;

    private String name;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        User other = (User) obj;

        return Objects.deepEquals(new String[] {toString()},
                new String[] {other.toString()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public String toString() {
        return String.format("type: %s, name: %s, id: %s, user type: %s", type, name, id, userType);
    }
}
