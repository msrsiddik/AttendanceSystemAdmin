package msr.attend.admin.Model;

public class CoordinatorModel {
    private String id;
    private String batch;

    public CoordinatorModel() {
    }

    public CoordinatorModel(String id, String batch) {
        this.id = id;
        this.batch = batch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    @Override
    public String toString() {
        return "{ "+ batch +" }";
    }
}
