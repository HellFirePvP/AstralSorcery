/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Injector
 * Originally created by makeo for Gadomancy
 * Date: 02.12.13 / 18:45
 */
public class Injector {

    Class clazz;
    Object object;

    public Injector(Object object, Class clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    public Injector() {
        this(null, null);
    }

    public Injector(Object object) {
        this(object, object.getClass());
    }

    public Injector(Class clazz) {
        object = null;
        this.clazz = clazz;
    }

    public Injector(String clazz) throws IllegalArgumentException {
        this.object = null;
        try {
            this.clazz = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class does not exist!");
        }
    }

    public void setObjectClass(Class clazz) {
        this.clazz = clazz;
    }

    public Class getObjectClass() {
        return clazz;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public <T> T invokeConstructor(Object... params) {
        return invokeConstructor(extractClasses(params), params);
    }

    public <T> T invokeConstructor(Class clazz, Object param) {
        return invokeConstructor(new Class[]{clazz}, param);
    }

    public <T> T invokeConstructor(Class[] classes, Object... params) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(classes);
            object = constructor.newInstance(params);
            return (T) object;
        } catch (Exception e) {//NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException
            e.printStackTrace();
        }
        return null;
    }

    public <T> T invokeMethod(String name, Object... params) {
        return invokeMethod(name, extractClasses(params), params);
    }

    public <T> T invokeMethod(String name, Class clazz, Object param) {
        return invokeMethod(name, new Class[]{clazz}, param);
    }

    public <T> T invokeMethod(String name, Class[] classes, Object... params) {
        try {
            Method method = clazz.getDeclaredMethod(name, classes);
            return invokeMethod(method, params);
        } catch (Exception e) {//NoSuchMethodException | ClassCastException
            e.printStackTrace();
        }
        return null;
    }

    public <T> T invokeMethod(Method method, Object... params) {
        try {
            method.setAccessible(true);
            Object result = method.invoke(object, params);
            if (result != null)
                return (T) result;
        } catch (Exception e) {//InvocationTargetException | IllegalAccessException | ClassCastException
            e.printStackTrace();
        }
        return null;
    }

    private Class[] extractClasses(Object... objects) {
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++)
            classes[i] = objects[i].getClass();
        return classes;
    }

    public boolean setField(String name, Object value) {
        try {
            return setField(clazz.getDeclaredField(name), value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setField(Field field, Object value) {
        try {
            if (Modifier.isFinal(field.getModifiers())) {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }

            field.setAccessible(true);
            field.set(object, value);
            return true;
        } catch (Exception e) {//IllegalAccessException | NoSuchFieldException
            e.printStackTrace();
            return false;
        }
    }

    public <T> T getField(String name) {
        try {
            return getField(clazz.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getField(Field field) {
        try {
            field.setAccessible(true);
            Object result = field.get(object);
            if (result != null)
                return (T) result;
        } catch (Exception e) {//IllegalAccessException | ClassCastException
            e.printStackTrace();
        }
        return null;
    }

    public Method findMethod(Class returnType, Class... paramTypes) {
        return findMethod(clazz, returnType, paramTypes);
    }

    public Field findField(Class type) {
        return findField(clazz, type);
    }

    public static Method findMethod(Class clazz, Class returnType, Class[] paramTypes) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (Arrays.equals(m.getParameterTypes(), paramTypes) && m.getReturnType().equals(returnType)) {
                return m;
            }
        }
        return null;
    }

    public static <E> Method findMethod(Class<? super E> clazz, String methodName, String methodObfName, Class<?>... methodTypes) {
        return ReflectionHelper.findMethod(clazz, methodName, methodObfName, methodTypes);
    }

    public static Field findField(Class clazz, String... names) {
        return ReflectionHelper.findField(clazz, names);
    }

    public static Field findField(Class clazz, Class type) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType().equals(type)) {
                return f;
            }
        }
        return null;
    }

    public static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(String name, Class clazz, Class... classes) {
        if (clazz == null)
            return null;

        try {
            return clazz.getDeclaredMethod(name, classes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(String name, String clazz, Class... classes) {
        return getMethod(name, Injector.getClass(clazz), classes);
    }

    public static Field getField(String name, Class clazz) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
