package hasnaer.java.bytecode.attribute;

import hasnaer.java.bytecode.cp.ConstantPool;

/**
 *
 * @author hasnae rehioui
 */
public class LineNumberTable extends AttributeInfo {
    
    private int[][] line_number_table;
    
    public LineNumberTable(int attribute_name_index,
            int attribute_length, int line_number_table_length,
            ConstantPool constant_pool){
        super(attribute_name_index, attribute_length, constant_pool);
        this.line_number_table = new int[line_number_table_length][2];
    }
    
    public void addEntry(int index, int... values){
        line_number_table[index][0] = values[0];
        line_number_table[index][1] = values[1];
    }

    public void print() {
        System.err.println("LINENUMBERTABLE");
        for(int i = 0; i < line_number_table.length; i++){
            System.err.print("start_pc=    " + line_number_table[i][0]);
            System.err.println("   | line_number= " + line_number_table[i][1]);
        }
    }
 
    public int getLine(int pos){
        
        
        int i = 0;
        while(line_number_table[i][0] < pos){
            i++;
        }
        
        return line_number_table[i - 1][1];
    }
}