/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.ISpecialCraftingEffects;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.useables.ItemShiftingStar;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReAttunementStarRecipe
 * Created by HellFirePvP
 * Date: 13.08.2018 / 20:38
 */
public class ReAttunementStarRecipe extends TraitRecipe implements ISpecialCraftingEffects {

    private final IMajorConstellation attuneCst;

    public ReAttunementStarRecipe(IMajorConstellation cst) {
        super(shapedRecipe("shiftingstar/enhanced/" + cst.getSimpleName(), ItemShiftingStar.createStack(cst))
                .addPart(cst.getConstellationSignatureItems().get(0),
                        ShapedRecipeSlot.UPPER_CENTER,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemsAS.shiftingStar,
                        ShapedRecipeSlot.CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .unregisteredAccessibleShapedRecipe());
        setInnerTraitItem(OreDictAlias.ITEM_STARMETAL_DUST,
                TraitRecipeSlot.values());

        addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST);
        addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack());
        addOuterTraitItem(cst.getConstellationSignatureItems().get(0));
        addOuterTraitItem(OreDictAlias.ITEM_STARMETAL_DUST);
        addOuterTraitItem(ItemUsableDust.DustType.ILLUMINATION.asStack());
        addOuterTraitItem(cst.getConstellationSignatureItems().get(0));

        //Better early than late.
        if (cst == null) {
            throw new IllegalArgumentException("NULL constellation passed to shifting-star enhancement recipe!");
        }

        this.attuneCst = cst;
        setRequiredConstellation(attuneCst);
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new ReAttunementStarRecipe(this.attuneCst);
    }

    @Override
    public boolean needsStrictMatching() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state != ActiveCraftingTask.CraftingState.ACTIVE) {
            return;
        }

        float total = 180;
        float percCycle = (float) (((tick % total) / total) * 2 * Math.PI);
        int parts = 5;
        Vector3 center = new Vector3(altar).add(0.5, 0.1, 0.5);
        float angleSwirl = 70F;
        float dst = 3.5F;

        for (int i = 0; i < parts; i++) {
            Vector3 v = new Vector3(1, 0, 0);
            float originalAngle = (((float) i) / ((float) parts)) * 360F;
            double angle = originalAngle + (MathHelper.sin(percCycle) * angleSwirl);
            v.rotate(-Math.toRadians(angle), Vector3.RotAxis.Y_AXIS).normalize().multiply(dst);
            Vector3 pos = center.clone().add(v);

            Vector3 mot = center.clone().subtract(pos).normalize().multiply(0.07);

            EntityFXFacingParticle particle = playParticle(pos, rand);
            particle.motion(mot.getX(), mot.getY(), mot.getZ());
        }

        float partDone = (tick % total) / total;

        float xSh = partDone >= 0.5F ? 1F : partDone / 0.5F;
        float zSh = partDone < 0.5F ? 1F : 1F - ((partDone - 0.5F) / 0.5F);

        Vector3 offset = new Vector3(altar).add(-3, 0.1, -3);
        offset.add(xSh * 7, 0, zSh * 7);
        EntityFXFacingParticle particle = playParticle(offset, rand);
        particle.scale(0.1F + rand.nextFloat() * 0.5F);
        particle.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);

        if (rand.nextInt(12) == 0) {
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(offset.clone().addY(3 + rand.nextInt(2)), offset, 1);
            lightbeam.setMaxAge(48);
            if (rand.nextInt(4) == 0) {
                lightbeam.setColorOverlay(Color.WHITE);
            } else if (rand.nextInt(3) == 0) {
                lightbeam.setColorOverlay(attuneCst.getConstellationColor().brighter());
            } else {
                lightbeam.setColorOverlay(attuneCst.getConstellationColor());
            }
        }

        xSh = partDone < 0.5F ? partDone / 0.5F : 1F;
        zSh = partDone >= 0.5F ? 1F - ((partDone - 0.5F) / 0.5F) : 1F;

        offset = new Vector3(altar).add(4, 0.1, 4);
        offset.add(-xSh * 7, 0, -zSh * 7);
        particle = playParticle(offset, rand);
        particle.scale(0.2F + rand.nextFloat() * 0.5F);
        particle.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        particle.motion(rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01F,
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

        if (rand.nextInt(12) == 0) {
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(offset.clone().addY(3 + rand.nextInt(2)), offset, 1);
            lightbeam.setMaxAge(48);
            if (rand.nextInt(4) == 0) {
                lightbeam.setColorOverlay(Color.WHITE);
            } else if (rand.nextInt(3) == 0) {
                lightbeam.setColorOverlay(attuneCst.getConstellationColor().brighter());
            } else {
                lightbeam.setColorOverlay(attuneCst.getConstellationColor());
            }
        }

        particle = playParticle(new Vector3(altar).add(-3 + rand.nextFloat() * 7, 0, -3 + rand.nextFloat() * 7), rand);
        particle.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
        particle.setColor(attuneCst.getConstellationColor().brighter());
    }

    @SideOnly(Side.CLIENT)
    private EntityFXFacingParticle playParticle(Vector3 at, Random rand) {
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(at);
        particle.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).scale(rand.nextFloat() * 0.4F + 0.27F);
        particle.setMaxAge(50);
        particle.scale(0.2F + rand.nextFloat());
        if (rand.nextInt(4) == 0) {
            particle.setColor(Color.WHITE);
        } else if (rand.nextInt(3) == 0) {
            particle.setColor(attuneCst.getConstellationColor().brighter());
        } else {
            particle.setColor(attuneCst.getConstellationColor());
        }
        return particle;
    }

}
