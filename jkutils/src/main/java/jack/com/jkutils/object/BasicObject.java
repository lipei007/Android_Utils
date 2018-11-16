package jack.com.jkutils.object;

import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 基础对象，方便使用json初始化对象，属性必须声明为public
 * 如果json中元素为json对象，那么属性对应的应该为Object类型
 * */
public class BasicObject {

    /**
     * Utils
     * */

    private static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * Setter
     * */
    private boolean invokeSetter(Class cls, Class type, String key, Object value) {

        String setter = "set" + toUpperCaseFirstOne(key);
        try {

            Class<?> myClassType = Class.forName(cls.getName());

            Method method = myClassType.getMethod(setter,type);
            if (method != null) {

                method.invoke(this,value);

                return true;
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return false;
    }

    private void setValue(Class cls, Field f, Class type, String key, Object value) {

        if (!invokeSetter(cls, type, key, value)) {
            try {
                f.set(this,value);
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }
        }
    }

    private void setValue(Class cls, Field f, String key, Object value) {

        Class type = f.getType();

        if (value == null) {

            boolean isInt = (type == int.class || type == long.class);
            boolean isFloat = (type == float.class || type == double.class);
            boolean isBool = (type == Boolean.class || type == boolean.class);

            if (isInt) {
                value = 0;
            } else if (isFloat) {
                value = 0.0;
            } else if (isBool) {
                value = false;
            }

            setValue(cls, f, type, key, value);

        } else {

            if (type == value.getClass()) {

                setValue(cls, f, type, key, value);

            } else {

                boolean isInt = ((type == int.class || type == long.class) && value.getClass() == Integer.class);
                boolean isFloat = ((type == float.class || type == double.class) && (value.getClass() == Double.class || value.getClass() == Float.class));
                boolean isBool = ((type == Boolean.class || type == boolean.class) && (value.getClass() == boolean.class || value.getClass() == Boolean.class));

                if (isInt || isFloat || isBool) {
                    setValue(cls, f, type, key, value);
                } else {

                    if (value.getClass() == String.class) {

                        String string = (String)value;

                        if (type == Integer.class || type == int.class || type == long.class) {

                            value = Integer.valueOf(string);


                        } else if (type == Double.class || type == double.class || type == float.class) {

                            value = Double.valueOf(string);

                        } else if (type == Boolean.class || type == boolean.class) {

                            value = Boolean.valueOf(string);
                        }

                        setValue(cls, f, type, key, value);

                    } else {

                        if (type == Object.class) {
                            setValue(cls, f, type, key, value);
                        }

                    }
                }
            }
        }

    }

    public void setValueForKey(String key, Object value) {

        if (key == null) {
            return;
        }

        Class cls = getClass();
//        out:while (cls != null && cls != Class.class) {
//
//            Field[] fields = cls.getFields();
//            for (Field f : fields) {
//
//                String name = f.getName();
//                if (name.equals(key)) {
//
//                    setValue(cls, f, key, value);
//                    break out;
//                }
//            }
//
//            cls = cls.getSuperclass();
//        }

        try {
            Field f = cls.getField(key);
            setValue(cls, f, key, value);
        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        }
    }

    public void setValueForKeyPath(String keyPath, Object value) {

        if (keyPath != null) {
            if (keyPath.contains(".")) {

                Object receiver = this;
                String[] nodes = keyPath.split("\\."); // split 实际上是正则表达式，所以特殊字符需要转译
                for (int i = 0; i < nodes.length; i++) {
                    String key = nodes[i];
                    if (i == nodes.length - 1) {

                        if (receiver != null) {

                            if (receiver instanceof BasicObject) {
                                ((BasicObject) receiver).setValueForKey(key,value);
                            } else {

                                try {

                                    Class cls = receiver.getClass();
                                    Field f = cls.getField(key);
                                    setValue(cls, f, key, value);
                                } catch (NoSuchFieldException e) {
//                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {

                        receiver = getValueForKey(receiver, key);
                        if (receiver == null) {
                            break;
                        }
                    }
                }

            } else {
                setValueForKey(keyPath,value);
            }
        }
    }

    public void setValuesForKeysWithJSON(JSONObject json) {

        if (json == null || json.length() == 0) {
            return;
        }


        try {
            Class cls = getClass();
            while (cls != null && cls != Class.class) {

                Field[] fields = cls.getFields();
                for (Field f : fields) {

                    String key = f.getName();

                    Object value = json.opt(key);

//                    if (value == null) {
//                        continue;
//                    }

                    setValue(cls, f, key, value);
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    /**
     * Getter
     * */

    private Object invokeGetter(Class cls, String key) {

        String setter = "get" + toUpperCaseFirstOne(key);
        try {

            Class<?> myClassType = Class.forName(cls.getName());

            Method method = myClassType.getMethod(setter,void.class);
            if (method != null) {
                return method.invoke(this);
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return null;
    }

    private Object getValueForKey(Object receiver, String key) {

        if (receiver == null || TextUtils.isEmpty(key)) {
            return null;
        }

        try {

            Class cls = receiver.getClass();

            Object obj = invokeGetter(cls, key);

            if (obj == null) {

                Field field = cls.getField(key);
                if (field != null) {
                    obj = field.get(receiver);
                    return obj;
                } else {
                    return null;
                }
            } else {
                return obj;
            }

        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        return null;

    }

    public Object getValueForKeyPath(String keyPath) {

        if (keyPath != null) {

            if (keyPath.contains(".")) {

                Object receiver = this;
                String[] nodes = keyPath.split("\\."); // split 实际上是正则表达式，所以特殊字符需要转译
                for (int i = 0; i < nodes.length; i++) {
                    String key = nodes[i];
                    receiver = getValueForKey(receiver, key);
                    if (receiver == null) {
                        break;
                    }
                }
                return receiver;

            } else {
                return getValueForKey(this,keyPath);
            }

        }
        return null;
    }
}
