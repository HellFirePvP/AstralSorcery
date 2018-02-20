/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ISpecialCraftingEffects;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitUpgradeRecipe
 * Created by HellFirePvP
 * Date: 18.10.2017 / 01:23
 */
public class TraitUpgradeRecipe extends ConstellationRecipe implements IAltarUpgradeRecipe, INighttimeRecipe, ISpecialCraftingEffects {

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 4, 4,  4),
            new Vector3(-4, 4,  4),
            new Vector3( 4, 4, -4),
            new Vector3(-4, 4, -4)
    };

    private static Object sprite;

    public TraitUpgradeRecipe() {
        super(shapedRecipe("upgrade_tier4", new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()))
                .addPart(ItemHandle.getCrystalVariant(false, true),
                        ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
        .unregisteredAccessibleShapedRecipe());
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AttunementAltarSlot.values());
        setCstItem(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
                ConstellationAtlarSlot.UP_RIGHT_RIGHT,
                ConstellationAtlarSlot.UP_LEFT_LEFT,
                ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
                ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        setCstItem(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
                ConstellationAtlarSlot.UP_UP_LEFT,
                ConstellationAtlarSlot.UP_UP_RIGHT,
                ConstellationAtlarSlot.DOWN_DOWN_LEFT,
                ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
    }

    @Override
    public TileAltar.AltarLevel getLevelUpgradingTo() {
        return TileAltar.AltarLevel.TRAIT_CRAFT;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal());
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return ItemStack.EMPTY;
    }

    @Override
    public AbstractAltarRecipe copyNewEffectInstance() {
        return new TraitUpgradeRecipe();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarVec = new Vector3(altar);
            Vector3 thisAltar = altarVec.clone().add(0.5, 0.5, 0.5);
            for (int i = 0; i < 4; i++) {
                Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
                dir.multiply(rand.nextFloat());
                dir.setY(dir.getY() * rand.nextFloat());
                dir.add(thisAltar.clone());

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
                particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat())).scale(0.4F + (0.2F * rand.nextFloat())).gravity(0.004);
                particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
                particle.setColor(Color.WHITE).scale(0.1F + (0.2F * rand.nextFloat())).gravity(0.004);
            }

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                    altar.getPos().getY() + 0.02,
                    altar.getPos().getZ() - 3 + rand.nextFloat() * 7
            );
            p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.15F);
            p.setColor(Color.WHITE);

            for (int i = 0; i < 1; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(1.3 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(2.2));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }
            for (int i = 0; i < 2; i++) {
                Vector3 r = Vector3.random().setY(0).normalize().multiply(2 + rand.nextFloat() * 0.5).add(thisAltar.clone().addY(1.3));
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.004).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setColor(Color.WHITE);
            }

            for (int i = 0; i < 10; i++) {
                Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
                dir.setY(rand.nextFloat() * dir.getY());
                dir.add(-0.3 + 1.6 * rand.nextFloat(), 0, -0.3 + 1.6 * rand.nextFloat());

                Vector3 r = altarVec.clone().add(dir);
                p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
                p.gravity(0.01 + 0.02 * rand.nextFloat()).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).scale(rand.nextFloat() * 0.5F + 0.3F);
                p.setColor(Color.WHITE);
            }

            if(rand.nextInt(10) == 0) {
                Vector3 from = new Vector3(
                        altar.getPos().getX() - 3 + rand.nextFloat() * 7,
                        altar.getPos().getY() + 0.02,
                        altar.getPos().getZ() - 3 + rand.nextFloat() * 7);
                MiscUtils.applyRandomOffset(from, rand, 0.4F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
                lightbeam.setMaxAge(64);
                lightbeam.setColorOverlay(new Color(0x5369FF));
            }

            if(sprite == null || ((TextureSpritePlane) sprite).isRemoved() ) {
                TextureSpritePlane pl = EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteFlare2, Vector3.RotAxis.Y_AXIS.clone());
                pl.setPosition(new Vector3(altar).add(0.5, 0.04, 0.5));
                pl.setScale(9F).setAlphaMultiplier(0.65F).setNoRotation(0);
                pl.setColorOverlay(new Color(0x5066EC));
                pl.setRefreshFunc(() -> {
                    return !altar.isInvalid() &&
                            altar.getWorld().getTileEntity(altar.getPos()) != null &&
                            altar.getWorld().getTileEntity(altar.getPos()).equals(altar) &&
                            altar.getActiveCraftingTask() != null &&
                            altar.getActiveCraftingTask().getState() == ActiveCraftingTask.CraftingState.ACTIVE &&
                            !altar.getActiveCraftingTask().getRecipeToCraft().getOutputForMatching().isEmpty() &&
                            altar.getActiveCraftingTask().getRecipeToCraft().getOutputForMatching().getItem() instanceof ItemBlockAltar &&
                            altar.getActiveCraftingTask().getRecipeToCraft().getOutputForMatching().getItemDamage() == 3;
                });
                sprite = pl;
            }
        }
    }

}
