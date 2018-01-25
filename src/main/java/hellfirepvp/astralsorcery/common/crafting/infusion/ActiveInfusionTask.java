/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ActiveInfusionTask
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:19
 */
public class ActiveInfusionTask {

    private final AbstractInfusionRecipe recipeToCraft;
    private final UUID playerCraftingUUID;
    private int ticksCrafting = 0;

    private List<TileChalice> supportingChalices = new LinkedList<>();
    private List<BlockPos> pendingChalicePositions = new LinkedList<>();

    public ActiveInfusionTask(AbstractInfusionRecipe recipeToCraft, UUID playerCraftingUUID) {
        this.recipeToCraft = recipeToCraft;
        this.playerCraftingUUID = playerCraftingUUID;
    }

    public void addChalices(List<TileChalice> chaliceList) {
        this.supportingChalices.addAll(chaliceList);
    }

    public int getChaliceRequiredAmount() {
        return recipeToCraft.doesConsumeMultiple() ?
                MathHelper.floor(recipeToCraft.getLiquidStarlightConsumptionChance() * 400 * 12) :
                MathHelper.floor(recipeToCraft.getLiquidStarlightConsumptionChance() * 400);
    }

    public UUID getPlayerCraftingUUID() {
        return playerCraftingUUID;
    }

    @Nullable
    public EntityPlayer tryGetCraftingPlayerServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerCraftingUUID);
    }

    public boolean tick(TileStarlightInfuser infuser) {
        ticksCrafting++;
        boolean change = this.pendingChalicePositions.size() > 0;

        for (BlockPos bp : this.pendingChalicePositions) {
            TileChalice test = MiscUtils.getTileAt(infuser.getWorld(), bp, TileChalice.class, true);
            if(test != null) {
                this.supportingChalices.add(test);
            }
        }
        this.pendingChalicePositions.clear();

        FluidStack fl = new FluidStack(BlocksAS.fluidLiquidStarlight, getChaliceRequiredAmount());
        Iterator<TileChalice> iterator = supportingChalices.iterator();
        while (iterator.hasNext()) {
            TileChalice tc = iterator.next();
            if (tc.isInvalid()) {
                iterator.remove();
                change = true;
            } else {
                TileChalice test = MiscUtils.getTileAt(infuser.getWorld(), tc.getPos(), TileChalice.class, true);
                if(test == null ||
                        test.getTank() == null ||
                        test.getTank().getFluid() == null ||
                        !test.getTank().getFluid().containsFluid(fl)) {
                    iterator.remove();
                    change = true;
                }
            }
        }
        return change;
    }

    public int getTicksCrafting() {
        return ticksCrafting;
    }

    public AbstractInfusionRecipe getRecipeToCraft() {
        return recipeToCraft;
    }

    public List<TileChalice> getSupportingChalices() {
        return supportingChalices;
    }

    //Used on clientside for rendering since we don't resolve the tile there
    @SideOnly(Side.CLIENT)
    public List<BlockPos> getPendingChalicePositions() {
        return pendingChalicePositions;
    }

    public void addPendingChalicePositions(List<BlockPos> chaliceList) {
        this.pendingChalicePositions.addAll(chaliceList);
    }

    public boolean isFinished() {
        return ticksCrafting >= (recipeToCraft.craftingTickTime() *
                (recipeToCraft.canBeSupportedByChalice() && !supportingChalices.isEmpty() ? 0.3F : 1F));
    }

    public void forceTick(int tick) {
        this.ticksCrafting = tick;
    }

}
