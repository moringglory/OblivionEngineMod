package oblivionengine.content;

import mindustry.type.Category;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class OECategory {
    public static Category oblivion_special;
    public static Category parts;

    private static final boolean IS_ANDROID;
    private static final boolean IS_JAVA9_PLUS;

    static {
        IS_ANDROID = checkClass("android.os.Build");
        IS_JAVA9_PLUS = checkClass("java.lang.Module");

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

    private static Category addHiddenCategory(String name, int ordinal) {
        if (IS_ANDROID) {
            return Category.units;
        }

        try {
            Unsafe unsafe = getUnsafe();

            Category newCategory = (Category) unsafe.allocateInstance(Category.class);
            if (newCategory == null) {
                return Category.units;
            }

            Field nameField = Class.forName("java.lang.Enum").getDeclaredField("name");
            unsafe.putObject(newCategory, unsafe.objectFieldOffset(nameField), name);

            Field ordinalField = Class.forName("java.lang.Enum").getDeclaredField("ordinal");
            unsafe.putInt(newCategory, unsafe.objectFieldOffset(ordinalField), ordinal);

            return newCategory;
        } catch (Throwable e) {
            return Category.units;
        }
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}