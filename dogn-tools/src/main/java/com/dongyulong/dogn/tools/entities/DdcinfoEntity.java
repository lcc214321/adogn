package com.dongyulong.dogn.tools.entities;

import com.dongyulong.dogn.tools.json.JsonMapper;

import java.io.Serializable;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/86:29 下午
 * @since v1.0
 */
public class DdcinfoEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String version;
    private String mobiletype;
    private String os;
    private String screen;
    private String model;
    private String longitude;
    private String latitude;
    private String dir;
    private String speed;
    private String altitude;
    private String role;
    private String net;
    private String location_time;
    // 2019/7/8 由于app端把该字段传为实体，暂时注释掉该字段
//	private String env_info;
    private String identifier;
    //2020-5-9新增字段
    private String udid;
    private String last_udid;
    private String ap_mac;
    private String lip;
    private String deviceId;

    //ios only
    private String idfa;
    private String deviceToken;

    //android only
    private String channel;
    private String mac;
    private String imei;
    private String cur_imei;
    private String imsi;
    private String aid;
    //广告标识符
    private String oaid;

    //修订ddcinfo规范后废弃
    private String openUDID;
    private String ip;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(String mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getLocation_time() {
        return location_time;
    }

    public void setLocation_time(String location_time) {
        this.location_time = location_time;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCur_imei() {
        return cur_imei;
    }

    public void setCur_imei(String cur_imei) {
        this.cur_imei = cur_imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getOpenUDID() {
        return openUDID;
    }

    public void setOpenUDID(String openUDID) {
        this.openUDID = openUDID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return JsonMapper.toJson(this);
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getLast_udid() {
        return last_udid;
    }

    public void setLast_udid(String last_udid) {
        this.last_udid = last_udid;
    }

    public String getAp_mac() {
        return ap_mac;
    }

    public void setAp_mac(String ap_mac) {
        this.ap_mac = ap_mac;
    }

    public String getLip() {
        return lip;
    }

    public void setLip(String lip) {
        this.lip = lip;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getOaid() {
        return oaid;
    }

    public void setOaid(String oaid) {
        this.oaid = oaid;
    }
}
