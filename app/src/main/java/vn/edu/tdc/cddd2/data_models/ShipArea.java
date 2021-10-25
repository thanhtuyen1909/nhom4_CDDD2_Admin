package vn.edu.tdc.cddd2.data_models;

public class ShipArea {
    public String employeeID;
    public String areaID;
    public String shipperName;

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public ShipArea() {
    }

    public ShipArea(String employeeID, String areaID, String shipperName) {
        this.employeeID = employeeID;
        this.areaID = areaID;
        this.shipperName = shipperName;
    }

    @Override
    public String toString() {
        return shipperName+" - "+areaID;
    }
}
