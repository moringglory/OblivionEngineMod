package oblivionengine;

import arc.struct.Seq;
import mindustry.type.Category;
import mindustry.world.Block;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class OECategory {
    public static Category OBLIVION_SPECIAL;
    public static Category OBLIVION_PARTS;
    public static Seq<Block> specialBlocks = new Seq<>();

    static {
        OBLIVION_SPECIAL = addHiddenCategory("oblivion-special", Category.production.ordinal());
        OBLIVION_PARTS = addHiddenCategory("oblivion-parts", Category.production.ordinal());
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

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}