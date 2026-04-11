package org.valkyrienskies.mod.forge.mixin.compat.power_grid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.patryk3211.powergrid.electricity.info.TerminalHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(TerminalHandler.class)
public class MixinTerminalHandler {
    @WrapOperation(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"),
        remap = false
    )
    private static Vec3 redirectBlockHitLocation(BlockHitResult instance, Operation<Vec3> original,
        @Local(argsOnly = true, name = "arg0") ClientLevel level) {
        Vec3 pos = original.call(instance);

        if (VSGameUtilsKt.isBlockInShipyard(level, instance.getBlockPos())) {
            pos = CompatUtil.INSTANCE.toSameSpaceAs(level, instance.getLocation(), instance.getBlockPos());
        }

        return pos;
    }
}
