package org.valkyrienskies.mod.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(value = ProjectileUtil.class, priority = 1100)
public class ProjectileUtilMixin {

    @WrapMethod(
        method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
    )
    private static EntityHitResult beforeGetEntityHitResult(
        Entity entity, Vec3 startVec, Vec3 endVec, AABB aABB, Predicate<Entity> predicate, double distance,
        Operation<EntityHitResult> original) {

        EntityHitResult originalHit = original.call(entity, startVec, endVec, aABB, predicate, distance);

        if (!VSGameUtilsKt.getShipsIntersecting(entity.level(), aABB).iterator().hasNext()) {
            return null;
        }

        return RaycastUtilsKt.raytraceEntities(entity.level(), entity, startVec, endVec, aABB, predicate, distance, originalHit);
    }

}
