package assemble;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class AssemblerResponse {
    private String asmId;
    private String imemMifId;
    private String imemMif;
    private String dmemMifId;
    private String dmemMif;

    public String getAsmId() {
        return asmId;
    }

    public void setAsmId(String asmId) {
        this.asmId = asmId;
    }

    public String getImemMifId() {
        return imemMifId;
    }

    public void setImemMifId(String imemMifId) {
        this.imemMifId = imemMifId;
    }

    public String getImemMif() {
        return imemMif;
    }

    public void setImemMif(String imemMif) {
        this.imemMif = imemMif;
    }

    public String getDmemMifId() {
        return dmemMifId;
    }

    public void setDmemMifId(String dmemMifId) {
        this.dmemMifId = dmemMifId;
    }

    public String getDmemMif() {
        return dmemMif;
    }

    public void setDmemMif(String dmemMif) {
        this.dmemMif = dmemMif;
    }
}
