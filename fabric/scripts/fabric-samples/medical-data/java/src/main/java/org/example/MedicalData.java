/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;

/*
数据类型  数据编号  数据相关平台       数据相关个人     数据产生时间
医疗数据   001  北京市中关村医院-15 Anonymous-001     2022-2-17

数据访问权限             数据描述                    贡献点   对称密钥
中级：保险公司或医院      患者慢性胆囊炎记录，包括个人信   20     sk’’
高级：医院              息、血常规症状、处方等

url
39.15.78.126/data/001
*/


public final class MedicalData {


    private StateType type;


    private String id;


    private User platform;


    private User individual;

    private String describe;

    private String permission;

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

    private Integer point;

    private String url;

    private String keys;


    public String getId() {
        return id;
    }

    public Integer getPoint() {
        return point;
    }

    public User getPlatform() {
        return platform;
    }

    public User getIndividual() {
        return individual;
    }

    public MedicalData(String id, User platform, User invidual, String describe, String permission, Integer point,
                       String url, String keys) {
        this.id = id;
        this.platform = platform;
        this.individual = individual;
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
                + "permission %s, point %d", type, platform, individual, describe, permission, point);
    }
}
