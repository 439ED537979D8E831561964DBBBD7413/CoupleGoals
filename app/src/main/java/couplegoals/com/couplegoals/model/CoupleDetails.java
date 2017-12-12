package couplegoals.com.couplegoals.model;

/**
 * Created by Brijesh on 8/8/2017.
 */

public class CoupleDetails {

    String sCoupleName;
    String sYourNumber;
    String sPartnerNumber;
    String sCouplePicturePath;
    String sYourEmailId;
    String sPartnerEmailId;
    String sYourDisplayName;
    String sPartnerDisplayName;

    public CoupleDetails(){

    }

    public CoupleDetails(String sCoupleName, String sYourNumber, String sPartnerNumber, String sCouplePicturePath,String sYourEmailId,String sPartnerEmailId,String sYourDisplayName,String sPartnerDisplayName) {
        this.sCoupleName = sCoupleName;
        this.sYourNumber = sYourNumber;
        this.sPartnerNumber = sPartnerNumber;
        this.sCouplePicturePath = sCouplePicturePath;
        this.sYourEmailId = sYourEmailId;
        this.sPartnerEmailId = sPartnerEmailId;
        this.sYourDisplayName = sYourDisplayName;
        this.sPartnerDisplayName = sPartnerDisplayName;
    }

    public String getsYourEmailId() {
        return sYourEmailId;
    }

    public String getsPartnerEmailId() {
        return sPartnerEmailId;
    }

    public String getsCoupleName() {
        return sCoupleName;
    }

    public String getsYourNumber() {
        return sYourNumber;
    }

    public String getsPartnerNumber() {
        return sPartnerNumber;
    }

    public String getsCouplePicturePath() {
        return sCouplePicturePath;
    }

    public String getsYourDisplayName() {
        return sYourDisplayName;
    }

    public void setsYourDisplayName(String sYourDisplayName) {
        this.sYourDisplayName = sYourDisplayName;
    }

    public String getsPartnerDisplayName() {
        return sPartnerDisplayName;
    }

    public void setsPartnerDisplayName(String sPartnerDisplayName) {
        this.sPartnerDisplayName = sPartnerDisplayName;
    }
    public void setsPartnerEmailId(String sPartnerEmailId) {
        this.sPartnerEmailId = sPartnerEmailId;
    }
}
