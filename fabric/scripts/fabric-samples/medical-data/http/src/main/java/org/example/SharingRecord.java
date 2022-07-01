package org.example;

import java.util.Objects;

public final class SharingRecord {

    public String getId() {
        return id;
    }

    private StateType type;

    private String id;

    private String dataId;

    private User ownPlatform;

    private User ownIndividual;

    private String time;

    private User requester;

    public StateType getType() {
        return type;
    }

    public String getDataId() {
        return dataId;
    }

    public User getOwnPlatform() {
        return ownPlatform;
    }

    public User getOwnIndividual() {
        return ownIndividual;
    }

    public String getTime() {
        return time;
    }

    public User getRequester() {
        return requester;
    }

    public Integer getPoint() {
        return point;
    }

    private Integer point;

    public SharingRecord(final String id, final String dataId, final User requester, final User ownPlatform,
                         final User ownIndividual, final String time, final Integer point) {
        this.type = StateType.SHARING_RECORD;
        this.id = id;
        this.dataId = dataId;
        this.ownPlatform = ownPlatform;
        this.ownIndividual = ownIndividual;
        this.time = time;
        this.requester = requester;
        this.point = point;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        SharingRecord other = (SharingRecord) obj;

        return Objects.deepEquals(new String[] {toString()},
                new String[] {other.toString()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public String toString() {
        return String.format("type: %s, dataId: %s, ownPlatform: %s, ownIndividual: %s, "
                        + "time: %s, requester: %s, point: %03d", type, dataId, ownPlatform, ownIndividual,
                time, requester, point);
    }
}


