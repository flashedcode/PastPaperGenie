package net.minecraft.client.renderer.entity;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityGlowSquid;
import net.minecraft.util.ResourceLocation;

public class RenderGlowSquid extends RenderLiving<EntityGlowSquid> {
    private static final ResourceLocation glowsquidTextures = new ResourceLocation("textures/entity/glow_squid.png");

    public RenderGlowSquid(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGlowSquid entity) {
        return glowsquidTextures;
    }

    @Override
    protected void preRenderCallback(EntityGlowSquid entity, float partialTickTime) {
        // Disable lighting and blend the color to give the illusion of glowing
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        // Set a constant glow color (for example, white with full alpha)
        GlStateManager.color(1.5F, 1.5F, 1.5F, 1.5F); // Change RGB to adjust glow color
    }

    @Override
    protected void rotateCorpse(EntityGlowSquid entity, float ageInTicks, float rotationYaw, float partialTicks) {
        float pitch = entity.prevGlowSquidPitch + (entity.glowSquidPitch - entity.prevGlowSquidPitch) * partialTicks;
        float yaw = entity.prevGlowSquidYaw + (entity.glowSquidYaw - entity.prevGlowSquidYaw) * partialTicks;
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -1.2F, 0.0F);
    }

    @Override
    protected void renderModel(EntityGlowSquid entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        GlStateManager.enableLighting(); // Re-enable lighting for other entities
    }

    @Override
    protected float handleRotationFloat(EntityGlowSquid entity, float partialTicks) {
        return entity.lastTentacleAngle + (entity.tentacleAngle - entity.lastTentacleAngle) * partialTicks;
    }
}
