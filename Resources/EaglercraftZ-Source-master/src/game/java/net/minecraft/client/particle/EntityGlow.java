package net.minecraft.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;


public class EntityGlow extends EntityFX {
    public EntityGlow(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleRed = 1.0F; // Set the red color component
        this.particleGreen = 0.0F; // Set the green color component
        this.particleBlue = 0.0F; // Set the blue color component
        this.particleAlpha = 1.0F; // Set the alpha (transparency)
        this.particleScale = 1.0F; // Set the size of the particle
        this.setParticleTextureIndex(80); 
    }

    @Override
    public void onUpdate() {
        // Update the particle's position and behavior
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        // Add movement or fading logic
        this.posY += 0.1; // Move the particle up
        this.particleAlpha -= 0.01; // Fade out
        if (this.particleAlpha <= 0) {
            this.setDead(); // Remove the particle when it fades out
        }
    }
}
