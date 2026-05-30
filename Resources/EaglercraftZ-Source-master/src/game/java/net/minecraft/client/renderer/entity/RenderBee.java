package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBee;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;

public class RenderBee extends RenderLiving<EntityAmbientCreature> {
    private static final ResourceLocation BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee.png");

    public RenderBee(RenderManager renderManager) {
        super(renderManager, new ModelBee(), 0.4F);  // Using ModelBee with shadow size of 0.4F
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAmbientCreature entity) {
        return BEE_TEXTURE;
    }
}
