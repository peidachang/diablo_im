package com.pajk.diablo.im.common.util;

import java.lang.reflect.Array;
import java.util.*;

import com.google.common.base.Preconditions;

/**
 * <pre>
 * Created by zhaoming on 14-5-29 下午8:21
 * </pre>
 */
public abstract class ClassUtils {

    /** Suffix for array class names: "[]" */
    public static final String  ARRAY_SUFFIX            = "[]";

    /** Prefix for internal array class names: "[L" */
    private static final String INTERNAL_ARRAY_PREFIX   = "[L";

    /** The package separator character '.' */
    private static final char   PACKAGE_SEPARATOR       = '.';

    /** The inner class separator character '$' */
    private static final char   INNER_CLASS_SEPARATOR   = '$';

    /** The CGLIB class separator character "$$" */
    public static final String  CGLIB_CLASS_SEPARATOR   = "$$";

    /** The ".class" file suffix */
    public static final String  CLASS_FILE_SUFFIX       = ".class";

    private static final Map    primitiveWrapperTypeMap = new HashMap(8);

    private static final Map    primitiveTypeNameMap    = new HashMap(16);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        Set primitiveTypeNames = new HashSet(16);
        primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
        primitiveTypeNames.addAll(Arrays.asList(new Class[] { boolean[].class, byte[].class, char[].class,
            double[].class, float[].class, int[].class, long[].class, short[].class }));
        for (Iterator it = primitiveTypeNames.iterator(); it.hasNext();) {
            Class primitiveClass = (Class) it.next();
            primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
        }
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static Class forName(String name) throws ClassNotFoundException, LinkageError {
        return forName(name, getDefaultClassLoader());
    }

    /**
     * Replacement for <code>Class.forName()</code> that also returns Class instances for primitives (like "int") and
     * array class names (like "String[]").
     * 
     * @param name the name of the Class
     * @param classLoader the class loader to use (may be <code>null</code>, which indicates the default class loader)
     * @return Class instance for the supplied name
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError if the class file could not be loaded
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        // Assert.notNull(name, "Name must not be null");
        Preconditions.checkNotNull(name, "Name must not be null");

        Class clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
        if (internalArrayMarker != -1 && name.endsWith(";")) {
            String elementClassName = null;
            if (internalArrayMarker == 0) {
                elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
            } else if (name.startsWith("[")) {
                elementClassName = name.substring(1);
            }
            Class elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getDefaultClassLoader();
        }
        return classLoaderToUse.loadClass(name);
    }

    /**
     * Resolve the given class name as primitive class, if appropriate, according to the JVM's naming rules for
     * primitive classes.
     * <p>
     * Also supports the JVM's internal class names for primitive arrays. Does <i>not</i> support the "[]" suffix
     * notation for primitive arrays; this is only supported by {@link #forName}.
     * 
     * @param name the name of the potentially primitive class
     * @return the primitive class, or <code>null</code> if the name does not denote a primitive class or primitive
     * array class
     */
    public static Class resolvePrimitiveClassName(String name) {
        Class result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = (Class) primitiveTypeNameMap.get(name);
        }
        return result;
    }

}
