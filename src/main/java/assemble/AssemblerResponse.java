package assemble;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class AssemblerResponse {
    private String id;
    private String asm;
    private String imem;
    private String dmem;
    private String errors;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsm() {
        return asm;
    }

    public void setAsm(String asm) {
        this.asm = asm;
    }

    public String getImem() {
        return imem;
    }

    public void setImem(String imem) {
        this.imem = imem;
    }

    public String getDmem() {
        return dmem;
    }

    public void setDmem(String dmem) {
        this.dmem = dmem;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
