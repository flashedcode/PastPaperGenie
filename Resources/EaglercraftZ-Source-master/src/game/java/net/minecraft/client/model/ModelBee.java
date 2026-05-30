package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBee extends ModelBase {
    private ModelRenderer body;
    private ModelRenderer rightWing;
    private ModelRenderer leftWing;
    private ModelRenderer frontLeg;
    private ModelRenderer midLeg;
    private ModelRenderer backLeg;
    private ModelRenderer stinger;
    private ModelRenderer leftAntenna;
    private ModelRenderer rightAntenna;
    
    public ModelBee() {
        // Initialize the body
        this.body = new ModelRenderer(this, 0, 0);
        //this.body.setTextureOffset(0, -2);
        this.body.addBox(-3.5F, -4.0F, -5.0F, 7, 7, 10);
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);

        // Stinger
        this.stinger = new ModelRenderer(this, 26, 7);
        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0, 1, 2);
        this.body.addChild(this.stinger);

        // Antennae
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.leftAntenna.addBox(1.5F, -4.0F, -8.0F, 1, 2, 3);
        this.body.addChild(this.leftAntenna);
        
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightAntenna.addBox(-2.5F, -4.0F, -8.0F, 1, 2, 3);
        this.body.addChild(this.rightAntenna);

        // Wings
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9, 0, 6);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.body.addChild(this.rightWing);

        this.leftWing = new ModelRenderer(this, 0, 18);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9, 0, 6);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.body.addChild(this.leftWing);

        // Legs
        this.frontLeg = new ModelRenderer(this, 26, 1);
        this.frontLeg.addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0);
        this.frontLeg.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLeg);

        this.midLeg = new ModelRenderer(this, 26, 3);
        this.midLeg.addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0);
        this.midLeg.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.midLeg);

        this.backLeg = new ModelRenderer(this, 26, 5);
        this.backLeg.addBox(-5.0F, 0.0F, 0.0F, 7, 2, 0);
        this.backLeg.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLeg);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        // Animating wings
        float wingFlap = MathHelper.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.25F;
        this.rightWing.rotateAngleZ = wingFlap;
        this.leftWing.rotateAngleZ = -wingFlap;

        // Legs - steady angle when idle
        float legAngle = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.frontLeg.rotateAngleX = legAngle;
        this.midLeg.rotateAngleX = legAngle;
        this.backLeg.rotateAngleX = legAngle;
    }
}
