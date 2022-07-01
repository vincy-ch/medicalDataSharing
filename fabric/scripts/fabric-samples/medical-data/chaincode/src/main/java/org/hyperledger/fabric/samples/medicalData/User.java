
package org.hyperledger.fabric.samples.medicalData;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class User {

    public enum UserType {
        INDIVIDUAL,
        PLATFORM
    }
    public static UserType string2UserType(final String userType) {
        if (userType.equals("INDIVIDUAL")) {
            return UserType.INDIVIDUAL;
        } else if (userType.equals("PLATFORM")) {
            return UserType.PLATFORM;
        }
        return null;
    }
    public String getId() {
        return id;
    }

    public Integer getPoint() {
        return point;
    }

    public StateType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void increasePoint(final Integer increase) {
        point += increase;
    }

    public void decreasePoint(final Integer decrease) {
        point -= decrease;
    }

    @Property()
    private StateType type;

    @Property()
    private String id;

    @Property()
    private Integer point;

    @Property()
    private String name;

    @Property()
    private UserType userType;

    public UserType getUserType() {
        return userType;
    }


    public User(@JsonProperty("id") final String id, @JsonProperty("name") final String name,
                @JsonProperty("point") final Integer point, @JsonProperty("userType") final UserType userType) {
        this.id = id;
        this.point = point;
        this.name = name;
        this.type = StateType.USER;
        this.userType = userType;
    }

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
