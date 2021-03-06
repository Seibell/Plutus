package Model;

public class Data {

    private double amount;
    private String type;
    private String remark;
    private String id;
    private String date;

    public Data(double amount, String type, String remark, String id, String date) {
        this.amount = amount;
        this.type = type;
        this.remark = remark;
        this.id = id;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Data(){

    }

}
