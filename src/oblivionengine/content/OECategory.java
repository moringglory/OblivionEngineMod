package oblivionengine.content;

import mindustry.type.Category;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static sun.misc.Unsafe.getUnsafe;

public class OECategory {
    public static Category oblivion_special;
    public static Category parts;

    static {
        oblivion_special = addHiddenCategory("oblivion-special", Category.production.ordinal());
        parts = addHiddenCategory("parts", Category.production.ordinal());
    }

    private static Category addHiddenCategory(String name, int ordinal) {
        try {
            Class<Category> clazz = Category.class;
            Unsafe unsafe = getUnsafe();

            Category newCategory = (Category) unsafe.allocateInstance(clazz);

            Field nameField = Class.forName("java.lang.Enum").getDeclaredField("name");
            long nameOffset = unsafe.objectFieldOffset(nameField);
            unsafe.putObject(newCategory, nameOffset, name);

            Field ordinalField = Class.forName("java.lang.Enum").getDeclaredField("ordinal");
            long ordinalOffset = unsafe.objectFieldOffset(ordinalField);
            unsafe.putInt(newCategory, ordinalOffset, ordinal);

            return newCategory;
        } catch (Throwable e) {
            e.printStackTrace();
            return Category.units;
        }
    }
}