package hasnaer.java.bytecode.nodes;

/**
 *
 * @author hasnae rehioui
 */
public abstract class JVMNode {

    private int line_number;
    
    public abstract String toJava(String indent);

    /**
     * @return the line_number
     */
    public int getLine_number() {
        return line_number;
    }

    /**
     * @param line_number the line_number to set
     */
    public void setLine_number(int line_number) {
        this.line_number = line_number;
    }
    
    
    
}