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

    public Expense() {
    }

    public Expense(String sExpenseId,String sCoupleName, String sAmount, String sNotes, String sPaidBy, String sWhen, String sExpenseImagePath) {
        this.sExpenseId = sExpenseId;
        this.sCoupleName = sCoupleName;
        this.sAmount = sAmount;
        this.sNotes = sNotes;
        this.sPaidBy = sPaidBy;
        this.sWhen = sWhen;
        this.sExpenseImagePath = sExpenseImagePath;
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
}
