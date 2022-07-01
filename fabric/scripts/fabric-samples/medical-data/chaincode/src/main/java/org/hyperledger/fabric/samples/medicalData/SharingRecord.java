package org.hyperledger.fabric.samples.medicalData;
import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;
import java.util.Objects;
/*
数据类型  编号 数据ID  数据相关平台   数据相关个人
   交易时间         使用方       贡献点
共享记录  001  002  北京市中关村医院  Anonymous-001
  2022-2-18  北京市海淀医院-005  20
 */

@DataType()
public final class SharingRecord {

    public String getId() {
        return id;
    }

    public StateType getType() {
        return type;
    }

    public String getDataId() {
        return dataId;
    }

    public User getOwnPlatform() {
        return ownPlatform;
    }

    public List<String> getOwnIndividual() {
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

    @Property()
    private StateType type;

    @Property()
    private String id;

    @Property()
    private String dataId;

    @Property
    private User ownPlatform;

    @Property
    private List<String> ownIndividual;

    @Property
    private String time;

    @Property
    private User requester;

    @Property
    private Integer point;

    public SharingRecord(@JsonProperty("id") final String id, @JsonProperty("dataId") final String dataId,
                         @JsonProperty("requester") final User requester,
                         @JsonProperty("ownPlatform") final User ownPlatform,
                         @JsonProperty("ownIndividual") final List<String> ownIndividual,
                         @JsonProperty("time") final String time, @JsonProperty("point") final Integer point) {
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
        return String.format("type: %s, dataId: %s, ownPlatform: %s,"
                        + "time: %s, requester: %s, point: %03d", type, dataId, ownPlatform.getName(),
                time, requester.getName(), point);
    }
}

