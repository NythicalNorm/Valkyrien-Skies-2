package org.valkyrienskies.mod.mixin.mod_compat.power_grid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.patryk3211.powergrid.electricity.wire.IWireEndpoint;
import org.patryk3211.powergrid.electricity.wire.WirePreview;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(WirePreview.class)
public class MixinWirePreview {
    @WrapOperation (
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"),
        remap = false
    )
    private static Vec3 changeHitResultIfInShipyard(HitResult instance, Operation<Vec3> original,
        @Local(argsOnly = true, name = "arg3") LocalPlayer player, @Local(name = "endpoint") IWireEndpoint endpoint) {
        Vec3 ogPos = original.call(instance);

        if (VSGameUtilsKt.isBlockInShipyard(player.level(), endpoint.getExactPosition(player.level()))) {
            return CompatUtil.INSTANCE.toSameSpaceAs(player.level(), ogPos, endpoint.getExactPosition(player.level()));
        }
        return ogPos;
    }

    @WrapOperation (
        method = "distanceOverlay",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"),
        remap = false
    )
    private static double distanceToIncludingShips(Vec3 playerPos, Vec3 endpoint, Operation<Double> original,
        @Local(argsOnly = true, name = "arg0") Player player) {
        return VSGameUtilsKt.squaredDistanceBetweenInclShips(player.level(), playerPos, endpoint, original);
    }

//    @Inject(method = "render",
//    at = @At(value = "INVOKE", target = "Lorg/patryk3211/powergrid/electricity/wire/IWireEndpoint;type()Lorg/patryk3211/powergrid/electricity/wire/WireEndpointType;"))
//    private static void applyShipyardTransformToPreview(SuperRenderTypeBuffer buffer, PoseStack matrixStack,
//        ClientLevel world, LocalPlayer player, HitResult target, CallbackInfo ci, @Local(name = "currentPos") Vec3 currentPos) {
//
//        if (VSGameUtilsKt.isBlockInShipyard(world, currentPos)) {
//            Ship transformShip = VSGameUtilsKt.getShipManagingPos(world, currentPos);
//            if (transformShip == null) {
//                return;
//            }
//            ShipTransform transform = transformShip.getTransform();
//
//            Vec3 entityPosition = currentPos;
//            val transformed = transform.shipToWorld.transformPosition(entityPosition.toJOML())
//
//            double camX = currentPos - entityPosition.x;
//            double camY = y - entityPosition.y;
//            double camZ = z - entityPosition.z;
//            val scale = transform.getShipToWorldScaling()
//
//            matrixStack.translate(transformed.x + camX, transformed.y + camY, transformed.z + camZ)
//            matrixStack.mulPose(Quaternionf(transform.shipToWorldRotation))
//            matrixStack.scale(scale.x().toFloat(), scale.y().toFloat(), scale.z().toFloat())
//        }
//    }

//    @WrapOperation(method = "render",
//    at = @At(value = "INVOKE", target = "Lorg/patryk3211/powergrid/electricity/wire/BlockWireRenderer;renderSegment(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IILnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;FFI)V"))
//    private static void applyShipyardTransformToPreview(
//        PoseStack matrixStack,
//        VertexConsumer consumer,
//        int light,
//        int color,
//        Vec3 currentPos,
//        Direction direction,
//        float thickness,
//        float length,
//        int uvOffset,
//        Operation<Void> original,
//        @Local(argsOnly = true, name = "arg3") LocalPlayer player) {
//
//        Vec3 posNew = CompatUtil.INSTANCE.toSameSpaceAs(player.level(), currentPos, player.position());
//
//        Ship ship = VSGameUtilsKt.getShipManagingPos(player.level(), currentPos);
//        if (ship != null) {
//            matrixStack.pushPose();
//            // matrixStack.mulPose(new Quaternionf(ship.getTransform().getRotation()));
//
//            original.call(matrixStack, consumer, light, color, posNew, direction, thickness, length, uvOffset);
//            matrixStack.popPose();
//        } else {
//            original.call(matrixStack, consumer, light, color, posNew, direction, thickness, length, uvOffset);
//        }
//
//    }
}
