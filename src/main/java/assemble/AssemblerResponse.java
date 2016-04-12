package assemble;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class AssemblerResponse {
    private String asmId;
    private String mifId;
    private String mif;

    public String getAsmId() {
        return asmId;
    }

    public void setAsmId(String asmId) {
        this.asmId = asmId;
    }

    public String getMifId() {
        return mifId;
    }

    public void setMifId(String mifId) {
        this.mifId = mifId;
    }

    public String getMif() {
        return mif;
    }

    public void setMif(String mif) {
        this.mif = mif;
    }
}
