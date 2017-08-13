/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewayRecipe
 * Created by HellFirePvP
 * Date: 22.04.2017 / 11:03
 */
public class CelestialGatewayRecipe extends AttunementRecipe {

    public CelestialGatewayRecipe() {
        super(shapedRecipe("gateway", BlocksAS.celestialGateway)
                .addPart(OreDictAlias.ITEM_STARMETAL_INGOT,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(ItemUsableDust.DustType.ILLUMINATION.asStack(),
                        ShapedRecipeSlot.UPPER_LEFT,
                        ShapedRecipeSlot.UPPER_RIGHT)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT)
                .addPart(ItemHandle.getCrystalVariant(false, false),
                        ShapedRecipeSlot.CENTER)
        .unregisteredAccessibleShapedRecipe());
        setAttItem(OreDictAlias.ITEM_STARMETAL_DUST, AttunementAltarSlot.values());
    }

    @Override
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if(state == ActiveCraftingTask.CraftingState.ACTIVE) {
            int scale = 2;
            if(altar.getAltarLevel().ordinal() > TileAltar.AltarLevel.ATTUNEMENT.ordinal()) {
                scale = 3;
            }
            int edgeScale = (scale * 2 + 1);
            for (int i = 0; i < 3; i++) {
                Vector3 offset = new Vector3(altar).add(-scale, 0, -scale);
                if(rand.nextBoolean()) {
                    offset.add(edgeScale * (rand.nextBoolean() ? 1 : 0), 0, rand.nextFloat() * edgeScale);
                } else {
                    offset.add(rand.nextFloat() * edgeScale, 0, edgeScale * (rand.nextBoolean() ? 1 : 0));
                }
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
                p.gravity(0.007).scale(0.25F + rand.nextFloat() * 0.15F).setMaxAge(20 + rand.nextInt(30));
                Color c = new Color(60, 0, 255);
                switch (rand.nextInt(4)) {
                    case 0:
                        c = Color.WHITE;
                        break;
                    case 1:
                        c = new Color(0x69B5FF);
                        break;
                    case 2:
                        c = new Color(0x0078FF);
                        break;
                }
                p.setColor(c);
            }
        }
    }

}
