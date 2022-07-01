/*
*PDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.samples.medicalData;

import java.util.List;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/*
数据类型  数据编号  数据相关平台       数据相关个人     数据产生时间
医疗数据   001  北京市中关村医院-15 Anonymous-001     2022-2-17

数据访问权限                 数据描述              贡献点   对称密钥
低级：无             患者慢性胆囊炎记录，包括个人信   20     sk’’
中级：保险公司或医院    息、血常规症状、处方等
高级：医院

url
39.15.78.126/data/001
*/


@DataType()
public final class MedicalData {

    @Property
    private StateType type;

    @Property
    private String id;

    @Property()
    private User platform;

    @Property()
    private List<String> individualIds;

    @Property()
    private String describe;

    @Property()
    private String permission;

    @Property()
    private Integer point;

    @Property()
    private String url;

    @Property()
    private String keys;

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setKeys(final String keys) {
        this.keys = keys;
    }

    public String getId() {
        return id;
    }

    public Integer getPoint() {
        return point;
    }

    public User getPlatform() {
        return platform;
    }

    public List<String> getIndividualIds() {
        return individualIds;
    }

    public StateType getType() {
        return type;
    }

    public String getDescribe() {
        return describe;
    }

    public String getPermission() {
        return permission;
    }

    public String getUrl() {
        return url;
    }

    public String getKeys() {
        return keys;
    }

    public MedicalData(@JsonProperty("id") final String id,
                       @JsonProperty("platform") final User platform,
                       @JsonProperty("individual") final List<String> individualIds,
                       @JsonProperty("describe") final String describe,
                       @JsonProperty("permission") final String permission,
                       @JsonProperty("url") final String url,
                       @JsonProperty("keys") final String keys,
                       @JsonProperty("point") final Integer point) {
        this.id = id;
        this.platform = platform;
        this.individualIds = individualIds;
        this.describe = describe;
        this.permission = permission;
        this.point = point;
        this.type = StateType.MEDICAL_DATA;
        this.url = url;
        this.keys = keys;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        MedicalData other = (MedicalData) obj;

        return Objects.deepEquals(toString(), other.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public String toString() {
        return String.format("type: %s, platform: %s, individual: %s, describe: %s, "
                + "permission %s, point %d", type, platform, individualIds, describe, permission, point);
    }
}

