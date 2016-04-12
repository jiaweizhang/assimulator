package disassemble;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class DisassemblerResponse {
    private String asmId;
    private String mifId;
    private String asm;

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

    public String getAsm() {
        return asm;
    }

    public void setAsm(String asm) {
        this.asm = asm;
    }
}
