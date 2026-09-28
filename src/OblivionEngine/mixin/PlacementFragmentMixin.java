package oblivionengine.mixin;

import arc.scene.Group;
import arc.scene.ui.layout.Table;
import mindustry.ui.fragments.PlacementFragment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = PlacementFragment.class, remap = false)
public abstract class PlacementFragmentMixin {

    @Inject(method = "build", at = @At("TAIL"), remap = false)
    public void oe_hideCategoryUI(Group parent, CallbackInfo ci) {
        try {
            Field f = PlacementFragment.class.getDeclaredField("blockCatTable");
            f.setAccessible(true);
            Table blockCatTable = (Table) f.get(this);
            if (blockCatTable != null) {
                blockCatTable.remove();
            }
        } catch (Throwable t) {
            arc.util.Log.err("[OE-Mixin] failed", t);
        }
    }
}