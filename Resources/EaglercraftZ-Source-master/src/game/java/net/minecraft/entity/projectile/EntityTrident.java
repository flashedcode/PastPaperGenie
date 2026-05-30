package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTrident extends EntityThrowable {
    public EntityTrident(World world) {
        super(world);
    }

    public EntityTrident(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected void onImpact(MovingObjectPosition position) {
        if (!this.worldObj.isRemote) {
            // Spawn particles or play sounds when the trident hits something
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false);
            this.setDead(); // Remove the trident entity after impact
        }
    }
}