package hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationUpgradeRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 13:03
 */
public class ConstellationUpgradeRecipe extends AttenuationRecipe implements IAltarUpgradeRecipe, INighttimeRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 3, 2,  3),
            new Vector3(-3, 2,  3),
            new Vector3( 3, 2, -3),
            new Vector3(-3, 2, -3)
    };

    public ConstellationUpgradeRecipe() {
        super(new ShapedRecipe(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()))
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                        ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LEFT)
                .addPart(ItemsAS.rockCrystal,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.GLASS_LENS.getItemMeta()),
                        ShapedRecipeSlot.CENTER));
        setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.PILLAR.ordinal()),
                AltarSlot.LOWER_LEFT,
                AltarSlot.LOWER_RIGHT);
        setItem(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()),
                AltarSlot.UPPER_RIGHT,
                AltarSlot.UPPER_LEFT);
    }

    @Override
    public TileAltar.AltarLevel getLevelUpgradingTo() {
        return TileAltar.AltarLevel.CONSTELLATION_CRAFT;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal());
    }

    @Override
    public boolean matches(TileAltar altar) {
        return altar.getAltarLevel().ordinal() < getLevelUpgradingTo().ordinal() && super.matches(altar);
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return null;
    }

    @Override
    public int craftingTickTime() {
        return super.craftingTickTime() * 4;
    }

    @Override
    public void onCraftServerFinish(TileAltar altar, Random rand) {
        super.onCraftServerFinish(altar, rand);

        altar.tryForceLevelUp(getLevelUpgradingTo(), true);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 altarVec = new Vector3(altar);
        Vector3 thisAltar = altarVec.clone().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 3; i++) {
            Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
            dir.multiply(rand.nextFloat()).add(thisAltar.clone());

            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat())).scale(0.2F + (0.2F * rand.nextFloat())).gravity(0.004);
        }

        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
        if(rand.nextInt(12) == 0) {
            pm.addBlockDestroyEffects(altar.getPos(), BlocksAS.blockMarble.getDefaultState());
        }
        if(tick % 48 == 0 && rand.nextInt(2) == 0) {
            EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Axis.Y_AXIS).setPosition(new Vector3(altar).add(0.5, 0.05, 0.5)).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
        }
    }

}
