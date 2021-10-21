package vn.edu.tdc.cddd2.data_models;

public class ShipArea {
    public String shipperID;
    public String areaID;

    public String getShipperID() {
        return shipperID;
    }

    public void setShipperID(String shipperID) {
        this.shipperID = shipperID;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public ShipArea(String shipperID, String areaID) {
        this.shipperID = shipperID;
        this.areaID = areaID;
    }

    public ShipArea() {
    }
}
