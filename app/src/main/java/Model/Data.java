package Model;

public class Data {

    private int amount;
    private String tyoe;
    private String remark;
    private String id;
    private String date;

    public Data(int amount, String tyoe, String remark, String id, String date) {
        this.amount = amount;
        this.tyoe = tyoe;
        this.remark = remark;
        this.id = id;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTyoe() {
        return tyoe;
    }

    public void setTyoe(String tyoe) {
        this.tyoe = tyoe;
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
