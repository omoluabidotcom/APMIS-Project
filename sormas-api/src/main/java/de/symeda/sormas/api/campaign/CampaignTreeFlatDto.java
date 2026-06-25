package de.symeda.sormas.api.campaign;

import java.math.BigInteger;
import java.io.Serializable;
import java.math.BigInteger;

public class CampaignTreeFlatDto implements Serializable {

    private static final long serialVersionUID = 1L;
    // Area
    public String areaName, areaUuid;
    public Long   areaId, areaExtId;

    // Region
    public String regionName, regionUuid;
    public Long   regionId;

    // District
    public String  districtName, districtUuid;
    public Long    districtId;
    public Boolean districtSelected;
    public String  districtModality, districtStatus;

    // Cluster
    public String  clusterName, clusterUuid;
    public Long    clusterId;
    public Boolean clusterSelected, clusterFloating;
    public String  clusterModality, clusterStatus;

    // Population
    public Long pop0_4, pop5_10, pop4_23m;

    public CampaignTreeFlatDto(Object[] r) {
        int i = 0;
        areaName        = str(r[i++]);
        areaUuid        = str(r[i++]);
        areaId          = bigInt(r[i++]);
        areaExtId       = bigInt(r[i++]);
        regionName      = str(r[i++]);
        regionUuid      = str(r[i++]);
        regionId        = bigInt(r[i++]);
        districtName    = str(r[i++]);
        districtUuid    = str(r[i++]);
        districtId      = bigInt(r[i++]);
        districtSelected = bool(r[i++]);
        districtModality = str(r[i++]);
        districtStatus   = str(r[i++]);
        clusterName      = str(r[i++]);
        clusterUuid      = str(r[i++]);
        clusterId        = bigInt(r[i++]);
        clusterFloating  = bool(r[i++]);  // floating
        clusterSelected  = bool(r[i++]);
        clusterModality  = str(r[i++]);
        clusterStatus    = str(r[i++]);
        pop0_4           = bigInt(r[i++]);
        pop5_10          = bigInt(r[i++]);
        pop4_23m         = bigInt(r[i++]);
    }

    private static String str(Object o)    { return o != null ? o.toString() : ""; }
    private static Long bigInt(Object o)   { return o != null ? ((BigInteger) o).longValue() : 0L; }
    private static Boolean bool(Object o)  { return o instanceof Boolean ? (Boolean) o : false; }
}
