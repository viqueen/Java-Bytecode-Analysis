package hasnaer.java.bytecode;

import hasnaer.beans.jd.CLASS;
import hasnaer.beans.jd.CODE;

import hasnaer.beans.jd.FIELD;
import hasnaer.beans.jd.LINE;
import hasnaer.beans.jd.METHOD;
import hasnaer.beans.jd.PARAM;
import hasnaer.beans.jd.THROWS;
import hasnaer.java.bytecode.attribute.Code;
import hasnaer.java.bytecode.attribute.Exceptions;
import hasnaer.java.bytecode.attribute.LineNumberTable;
import hasnaer.java.bytecode.attribute.LocalVariableTable;
import hasnaer.java.bytecode.nodes.ConditionalBlockNode;
import hasnaer.java.bytecode.nodes.JVMNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hasnae rehioui
 */
public class class2xml {

    public static String INDENT = "\t";

    public static String render(ClassFile class_file) throws Exception {

        CLASS _class = new CLASS();

        _class.setATT_ACCESSFLAGS(class_file.getAccess_flags() + "");
        _class.setATT_EXTENDS(class_file.getSuperClassName());
        _class.setATT_NAME(class_file.getClassName());


        List<MethodInfo> methods = class_file.getMethods();
        List<METHOD> _methods = new ArrayList<METHOD>();
        
        for (MethodInfo method : methods) {
            if (method.isStaticInit()) {
                _methods.add(renderMethod(method, true));
            } else {
                _methods.add(renderMethod(method, false));
            }
        }


        List<FieldInfo> fields = class_file.getFields();
        List<FIELD> _fields = new ArrayList<FIELD>();

        for (FieldInfo field : fields) {
            _fields.add(renderField(field));
        }

        _class.set_FIELD(_fields.toArray(new FIELD[_fields.size()]));
        _class.set_METHOD(_methods.toArray(new METHOD[_methods.size()]));



        return _class.object2XMLstring();
    }

    private static METHOD renderMethod(MethodInfo method,
            boolean isStaticInit) {
        METHOD _method = new METHOD();

        _method.setATT_ACCESSFLAGS(method.getAccess_flags() + "");
        _method.setATT_NAME(method.getName());
        if(method.isConstructor() || method.isStaticInit()){
            _method.setATT_RETURNTYPE("");
        } else{
            _method.setATT_RETURNTYPE(
                    Descriptor.getReturnDescriptor(method.getDescriptor()));
        }

        if(method.isStaticInit()){
            _method.setATT_ISSTATICINIT("true");
        } else{
            _method.setATT_ISSTATICINIT("false");
        }
        
        _method.set_CODE(renderCode(method.getCodeAttribute()));
        _method.set_PARAM(renderParams(method));
        
        _method.set_THROWS(renderThrows(method.getExceptionsAttibute()));
        
        return _method;

    }

    private static CODE renderCode(Code code_attribute) {

        CODE _code = new CODE();

        List<LINE> _lines = new ArrayList<LINE>();

        LocalVariableTable lvt_attribute = null;
        LineNumberTable lnt_attribute = null;

        if (code_attribute != null) {
            lvt_attribute = code_attribute.getLocalVariableTableAttribute();
            lnt_attribute = code_attribute.getLineNumberTableAttribute();
        }


        if (lvt_attribute != null) {
            StatementBuilder st_builder = new StatementBuilder(code_attribute, 0,
                    code_attribute.getCode().length, new ArrayList<Integer>());
            try {

                st_builder.build();

            } catch (Exception ex) {
            }

            List<JVMNode> statements = st_builder.getStatements();

            for (JVMNode statement : statements) {
                _lines.addAll(renderLines(statement));
            }
        }

        _code.set_LINE(_lines.toArray(new LINE[_lines.size()]));

        return _code;
    }

    private static FIELD renderField(FieldInfo field) {
        FIELD _field = new FIELD();

        _field.setATT_ACCESSFLAGS(field.getAccess_flags() + "");
        _field.setATT_DESCRIPTOR(field.getDescriptor());
        _field.setATT_NAME(field.getName());

        return _field;
    }

    
    private static PARAM[] renderParams(MethodInfo method) {

        List<PARAM> _params = new ArrayList<PARAM>();

        Code code_attribute = method.getCodeAttribute();
        LocalVariableTable lvt_attribute = null;

        if (code_attribute != null) {
            lvt_attribute = code_attribute.getLocalVariableTableAttribute();

        }

        if (lvt_attribute != null) {
            int numOfParameters = Descriptor.getParamCount(method.getDescriptor());
            if (numOfParameters > 0) {

                for (int i = 1; i <= numOfParameters; i++) {
                    String[] variable = lvt_attribute.getVar(lvt_attribute.THIS_INDEX + i);
                    PARAM _param = new PARAM();
                    _param.setATT_NAME(variable[1]);
                    _param.setATT_TYPE(variable[0]);

                    _params.add(_param);
                }
            }
        } else if (method.isAbstract()) {
            String[] param_types = Descriptor.getParamTypes(method.getDescriptor());

            if (param_types != null) {
                int p = 0;

                for (int i = 0; i < param_types.length; i++) {
                    PARAM _param = new PARAM();
                    _param.setATT_NAME("param" + p++);
                    _param.setATT_TYPE(param_types[i]);
                    _params.add(_param);
                }
            }
        }

        return _params.toArray(new PARAM[_params.size()]);
    }

    private static THROWS[] renderThrows(Exceptions exc_attribute) {
        List<THROWS> _throws = new ArrayList<THROWS>();

        if (exc_attribute != null) {
            int[] exc_ = exc_attribute.getTable();
            if (exc_.length > 0) {

                for (int i = 0; i < exc_.length; i++) {
                    THROWS _throw = new THROWS();
                    _throw.setATT_NAME(exc_attribute.getExceptionClassName(i));
                    _throws.add(_throw);
                }

            }
        }

        return _throws.toArray(new THROWS[_throws.size()]);
    }

    private static List<LINE> renderLines(JVMNode statement) {
        List<LINE> _lines = new ArrayList<LINE>();

        if (!(statement instanceof ConditionalBlockNode)) {
            LINE _line = new LINE();
            _line.set_PCDATA(statement.toJava(INDENT + INDENT) + ";");
            _line.setATT_NUMBER(statement.getLine_number() + "");
            _lines.add(_line);
            
        } else {
        }

        return _lines;
    }
}