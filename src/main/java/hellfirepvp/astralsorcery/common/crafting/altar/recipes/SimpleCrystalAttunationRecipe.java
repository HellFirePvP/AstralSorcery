package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAttunationRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 15:13
 */
public class SimpleCrystalAttunationRecipe extends AttenuationRecipe implements INighttimeRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
            new Vector3( 3, 2,  3),
            new Vector3(-3, 2,  3),
            new Vector3( 3, 2, -3),
            new Vector3(-3, 2, -3)
    };

    private final ItemTunedCrystalBase outBase;

    public SimpleCrystalAttunationRecipe(ItemRockCrystalBase baseCrystal, ItemTunedCrystalBase outAttunedItem) {
        super(new ShapedRecipe(outAttunedItem).addPart(baseCrystal, ShapedRecipeSlot.CENTER).forceEmptySpaces());
        setAttItem(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()), AltarSlot.values());
        this.outBase = outAttunedItem;
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack central = centralGridMap.get(ShapedRecipeSlot.CENTER);
        Constellation c = ((DataActiveCelestials) SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS)).getActiveConstellaionForTier(ConstellationRegistry.getTier(0));
        if(c == null || central == null) return central;
        ItemStack tuned = new ItemStack(outBase);
        ItemTunedCrystalBase.applyConstellation(tuned, c);
        CrystalProperties.applyCrystalProperties(tuned, CrystalProperties.getCrystalProperties(central));
        return tuned;
    }

    @Override
    public boolean matches(TileAltar altar) {
        Constellation c = ((DataActiveCelestials) SyncDataHolder.getDataServer(SyncDataHolder.DATA_CONSTELLATIONS)).getActiveConstellaionForTier(ConstellationRegistry.getTier(0));
        return c != null && super.matches(altar);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 altarVec = new Vector3(altar);
        Vector3 thisAltar = altarVec.clone().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 2; i++) {
            Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
            dir.multiply(rand.nextFloat()).add(thisAltar.clone());

            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat())).scale(0.2F + (0.2F * rand.nextFloat())).gravity(0.004);
        }

        if(tick % 48 == 0 && rand.nextInt(2) == 0) {
            EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteCraftBurst, Axis.Y_AXIS).setPosition(new Vector3(altar).add(0.5, 0.05, 0.5)).setScale(5 + rand.nextInt(2)).setNoRotation(rand.nextInt(360));
        }
    }

    @Override
    public boolean allowsForChaining() {
        return false;
    }
}
