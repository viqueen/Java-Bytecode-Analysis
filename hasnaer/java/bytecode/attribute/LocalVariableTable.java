package hasnaer.java.bytecode.attribute;

import hasnaer.java.bytecode.Descriptor;
import hasnaer.java.bytecode.cp.ConstantPool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hasnae rehioui
 */
public class LocalVariableTable extends AttributeInfo {
 
    private List<Integer> indexes;

    private Map<Integer, Entry> vars;

    
    public int THIS_INDEX = -1;
    
    public LocalVariableTable(int attribute_name_index, 
            int attribute_length, int table_length, ConstantPool constant_pool){
        super(attribute_name_index, attribute_length, constant_pool);
        this.vars = new HashMap<Integer, Entry>();
        this.indexes = new ArrayList<Integer>(table_length);
        this.constant_pool = constant_pool;
    }

    public void init(){
        for(Integer index : indexes){
            String[] tan = getVar(index);
            if(tan[1].equals("this")){
                THIS_INDEX = index;
                break;
            }
        }
    }
    
    public List<Integer> getNamedVars(String descriptor, boolean isStatic){
        int cnt = Descriptor.getParamCount(descriptor);

        List<Integer> result = new ArrayList<Integer>();

        int i = 0;
        Collections.sort(this.indexes);

        if(!isStatic){
            result.add(this.indexes.get(i));
            i++;
        }

        for(int j = 0; j < cnt; j++){
            result.add(this.indexes.get(i));
            i++;
        }
        
        return result;
    }

    public void addVar(Entry entry){
        this.vars.put(entry.index, entry);
        this.indexes.add(entry.index);
    }

    public String[] getVar(int index){
        String[] type_and_name = new String[2];

        Entry var_entry = vars.get(index);
        
        type_and_name[0] = Descriptor.fieldDataType(constant_pool.getUTF8_Info(var_entry.descriptor_index).getValue());
        type_and_name[1] = constant_pool.getUTF8_Info(var_entry.name_index).getValue();
        
        return type_and_name;
    }
    
    
    public ConstantPool getConstantPool(){
        return this.constant_pool;
    }

//    public String[] getVariable(int index){
//        String[] type_and_name = new String[2];
//
//        type_and_name[0] = Descriptor.fieldDataType(constant_pool.getUTF8_Info(table.get(index).descriptor_index).getValue());
//        type_and_name[1] = constant_pool.getUTF8_Info(table.get(index).name_index).getValue();
//
//
//        return type_and_name;
//    }
    
    
    public static class Entry implements Comparable {
        private int start_pc;
        private int length;
        private int name_index;
        private int descriptor_index;
        private int index;
        
        public Entry(int start_pc, int length, 
                int name_index,
                int descriptor_index, int index){
            this.start_pc = start_pc;
            this.length = length;
            this.name_index = name_index;
            this.descriptor_index = descriptor_index;
            this.index = index;
        }
        
        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append("start_pc= " + start_pc);
            builder.append("| length= " + length);
            builder.append("| name_index= " + name_index);
            builder.append("| des_index= " + descriptor_index);
            builder.append("| index= " + index);
            return builder.toString();
        }

        @Override
        public int compareTo(Object o) {
            Entry e = (Entry) o;
            if(e.index < this.index){
                return 1;
            } else if(e.index == this.index){
                return 0;
            }
            return -1;
        }
    }
}