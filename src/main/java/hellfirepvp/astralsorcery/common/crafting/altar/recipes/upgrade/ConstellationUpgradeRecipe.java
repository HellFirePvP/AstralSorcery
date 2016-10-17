package hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttenuationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.Axis;
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
public class ConstellationUpgradeRecipe extends AttenuationRecipe implements IAltarUpgradeRecipe {

    //TODO do
    public ConstellationUpgradeRecipe(AbstractCacheableRecipe recipe) {
        super(recipe);
    }

    public ConstellationUpgradeRecipe(IAccessibleRecipe recipe) {
        super(recipe);
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

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return null;
    }

    @Override
    public int craftingTickTime() {
        return super.craftingTickTime() * 20;
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

        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
        if(rand.nextInt(12) == 0) {
            pm.addBlockDestroyEffects(altar.getPos(), BlocksAS.blockMarble.getDefaultState());
        }
        if(tick % 48 == 0 && rand.nextInt(2) == 0) {
            EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Axis.Y_AXIS).setPosition(new Vector3(altar).add(0.5, 0.05, 0.5)).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
        }
        Vector3 pos = new Vector3(altar).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.motion(
                rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1  * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);
    }

}
