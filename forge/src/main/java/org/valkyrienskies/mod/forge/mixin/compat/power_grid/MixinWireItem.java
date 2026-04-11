package org.valkyrienskies.mod.forge.mixin.compat.power_grid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.patryk3211.powergrid.electricity.wire.BlockWireEntity;
import org.patryk3211.powergrid.electricity.wire.IWireEndpoint;
import org.patryk3211.powergrid.electricity.wire.WireItem;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(WireItem.class)
public class MixinWireItem {

    // cancels ship to ship and ship to world connections
    @WrapMethod(method = "connect", remap = false)
    private static InteractionResultHolder<BlockWireEntity> stopShipToWorldConnections(
        Level level, ItemStack stack,
        Player player, IWireEndpoint endpoint1, IWireEndpoint endpoint2,
        Operation<InteractionResultHolder<BlockWireEntity>> original) {

        Vec3 pos1 = endpoint1.getExactPosition(level);
        Vec3 pos2 = endpoint2.getExactPosition(level);

        if (VSGameUtilsKt.isBlockInShipyard(level, pos1) ||
            VSGameUtilsKt.isBlockInShipyard(level, pos2)) {
            Ship ship1 = VSGameUtilsKt.getShipManagingPos(level, pos1);
            Ship ship2 = VSGameUtilsKt.getShipManagingPos(level, pos2);

            if (ship1 != null && ship1.equals(ship2)) {
                return original.call(level, stack, player, endpoint1, endpoint2);
            } else {
                return InteractionResultHolder.fail((BlockWireEntity) null);
            }
        } else {
            return original.call(level, stack, player, endpoint1, endpoint2);
        }
    }
}
