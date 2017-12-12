package couplegoals.com.couplegoals.model;

/**
 * Created by Brijesh on 8/8/2017.
 */

public class Expense {
    String sExpenseId;
    String sCoupleName;
    String sAmount;
    String sNotes;
    String sPaidBy;
    String sWhen;
    String sExpenseImagePath;
    String sExpenseCategory;
    String sDisplayName;

    public Expense() {
    }

    public Expense(String sExpenseId,String sCoupleName, String sAmount, String sNotes, String sPaidBy, String sWhen, String sExpenseImagePath, String sExpenseCategory,String sDisplayName) {
        this.sExpenseId = sExpenseId;
        this.sCoupleName = sCoupleName;
        this.sAmount = sAmount;
        this.sNotes = sNotes;
        this.sPaidBy = sPaidBy;
        this.sWhen = sWhen;
        this.sExpenseImagePath = sExpenseImagePath;
        this.sExpenseCategory = sExpenseCategory;
        this.sDisplayName = sDisplayName;
    }

    public String getsExpenseId() {
        return sExpenseId;
    }

    public String getsCoupleName() {
        return sCoupleName;
    }

    public String getsAmount() {
        return sAmount;
    }

    public String getsNotes() {
        return sNotes;
    }

    public String getsPaidBy() {
        return sPaidBy;
    }

    public String getsWhen() {
        return sWhen;
    }

    public String getsExpenseImagePath() {
        return sExpenseImagePath;
    }

    public String getsExpenseCategory() {
        return sExpenseCategory;
    }

    public String getsDisplayName() {
        return sDisplayName;
    }

    public void setsDisplayName(String sDisplayName) {
        this.sDisplayName = sDisplayName;
    }


}
