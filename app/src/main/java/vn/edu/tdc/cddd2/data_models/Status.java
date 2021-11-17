package vn.edu.tdc.cddd2.data_models;

public class Status {
    private String keyStatus ;
    private Object valuesStatus;

    public Status(String keyStatus, Object valuesStatus) {
        this.keyStatus = keyStatus;
        this.valuesStatus = valuesStatus;
    }

    public Status() {
    }

    public String getKeyStatus() {
        return keyStatus;
    }

    public Object getValuesStatus() {
        return valuesStatus;
    }
}
