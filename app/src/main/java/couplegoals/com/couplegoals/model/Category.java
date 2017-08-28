package couplegoals.com.couplegoals.model;

/**
 * Created by Rini Banerjee on 28-08-2017.
 */

public class Category {
    String sCategoryId;
    String sCoupleName;
    String sCategoryName;

    public Category() {
    }

    public Category(String sCategoryId, String sCoupleName, String sCategoryName) {
        this.sCategoryId = sCategoryId;
        this.sCoupleName = sCoupleName;
        this.sCategoryName = sCategoryName;
    }

    public String getsCategoryId() {
        return sCategoryId;
    }

    public String getsCoupleName() {
        return sCoupleName;
    }

    public String getsCategoryName() {
        return sCategoryName;
    }
}
