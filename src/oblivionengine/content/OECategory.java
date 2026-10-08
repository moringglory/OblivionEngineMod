package oblivionengine.content;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.type.Category;
import mindustry.world.Block;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class OECategory {
    public static Category oblivion_special;
    public static Category parts;
    public static Seq<Block> specialBlocks = new Seq<>();

    private static final boolean IS_ANDROID;
    private static MethodHandles.Lookup androidLookup;

    static {
        IS_ANDROID = checkClass("android.os.Build");
        if (IS_ANDROID) {
            androidLookup = createLookup(Category.class);
        }

        oblivion_special = addHiddenCategory("oblivion-special", Category.production.ordinal());
        parts = addHiddenCategory("parts", Category.production.ordinal());
    }

    private static boolean checkClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static MethodHandles.Lookup createLookup(Class<?> clazz) {
        try {
            Constructor<MethodHandles.Lookup> ctor =
                    MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            ctor.setAccessible(true);
            return ctor.newInstance(clazz, 15);
        } catch (Throwable e) {
//            Log.err("[OE] createLookup failed", e);
            return null;
        }
    }

    private static Category addHiddenCategory(String name, int ordinal) {
        if (IS_ANDROID && androidLookup != null) {
            Category result = addHiddenCategoryAndroid(name, ordinal);
            if (result != null) return result;
        }
        return addHiddenCategoryDesktop(name, ordinal);
    }

    private static Category addHiddenCategoryAndroid(String name, int ordinal) {
        try {
            // 用 Lookup 定位 Category 的私有构造函数并直接调用
            MethodHandle ctor = androidLookup.findConstructor(
                    Category.class,
                    MethodType.methodType(void.class, String.class, int.class)
            );

            // 用 invokeWithArguments 避开 invokeExact 的 DEX 限制
            Category newCategory = (Category) ctor.invokeWithArguments(name, ordinal);
            if (newCategory != null) {
//                Log.info("[OE] created fake category via Lookup: " + name);
                return newCategory;
            }
        } catch (Throwable e) {
//            Log.err("[OE] android ctor path failed for " + name + ": " + e);
        }
        return null;
    }

    private static Category addHiddenCategoryDesktop(String name, int ordinal) {
        try {
            Unsafe unsafe = getUnsafe();

            Category newCategory = (Category) unsafe.allocateInstance(Category.class);
            if (newCategory == null) {
//                Log.err("[OE] allocateInstance returned null for " + name);
                return Category.units;
            }

            Field nameField = Class.forName("java.lang.Enum").getDeclaredField("name");
            unsafe.putObject(newCategory, unsafe.objectFieldOffset(nameField), name);

            Field ordinalField = Class.forName("java.lang.Enum").getDeclaredField("ordinal");
            unsafe.putInt(newCategory, unsafe.objectFieldOffset(ordinalField), ordinal);

//            Log.info("[OE] created fake category via Unsafe: " + name);
            return newCategory;
        } catch (Throwable e) {
//            Log.err("[OE] desktop path failed for " + name + ": " + e);
            return Category.units;
        }
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}