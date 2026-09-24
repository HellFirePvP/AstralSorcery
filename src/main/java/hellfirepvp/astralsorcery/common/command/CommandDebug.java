/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.client.util.ComponentEffectUtil;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.entity.EntityShootingStar;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CommandDebug
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CommandDebug {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("debug")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandDebug::debug));
    }

    private static int debug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = (ServerPlayer) context.getArgument("player", EntitySelector.class).findSingleEntity(context.getSource());

        //EntityFlare flare = EntitiesAS.FLARE.get().create(player.serverLevel());
        //flare.setPos(player.getX(), player.getY(), player.getZ());
        //player.serverLevel().addFreshEntity(flare);

        Vector3 dir = Vector3.random(player.getRandom()).setY(0).normalize().multiply(0.15F + player.getRandom().nextFloat() * 0.06F);
        Vector3 pos = new Vector3(player).addY(200);//.setY(EntityShootingStar.getYLevelCutoff(player.level()) + 40 + player.getRandom().nextInt(30));
        EntityShootingStar shootingStar = EntityShootingStar.create(player.level(), pos, dir);
        player.level().addFreshEntity(shootingStar);

        //BlockPos height = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.blockPosition());
        //ShootingStarExplosion.at(Vector3.atCenter(height),
        //        ColorWrapper.ofHSB(player.getRandom().nextFloat() * 360F, 1F, 1F)).sendToNearby(player.serverLevel());

        //player.teleportTo(pos.getX(), pos.getY() + 2, pos.getZ());

        //ResearchManager.getProgress(player, LogicalSide.SERVER);
        //ResearchHelper.removeAttunedConstellation(player);
        //ResearchHelper.attuneConstellation(player, ConstellationsAS.AEVITAS.get());
        //for (BaseConstellation cst : RegistriesAS.REGISTRY_CONSTELLATIONS) {
        //    ResearchHelper.discoverConstellation(player, cst);
        //}
        /*BlockPos pos = player.blockPosition();


        Vector3 offset = new Vector3(pos).add(0.5, 6, 0.5);
        CameraPathBuilder builder = CameraPathBuilder.builder(offset.copy().add(4, 0, 4), new Vector3(pos).add(0.5, 0.5, 0.5));
        builder.addCircularPoints(offset,
                CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease( 5,  0.025), 200, 2);
        builder.addCircularPoints(offset,
                CameraPathBuilder.DynamicRadiusGetter.dyanmicIncrease(10, -0.01) , 200, 2);

        builder.finishAndStart();*/

        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty()) {
            /*StoredLumenComponent lumenCmp = new StoredLumenComponent(List.of(
                    new StoredLumenComponent.StoredLumen(LumenAS.AEVITAS.get(), 1000, 1000),
                    new StoredLumenComponent.StoredLumen(LumenAS.ARMARA.get(), 800, 1000),
                    new StoredLumenComponent.StoredLumen(LumenAS.DISCIDIA.get(), 600, 1000),
                    new StoredLumenComponent.StoredLumen(LumenAS.EVORSIO.get(), 400, 1000),
                    new StoredLumenComponent.StoredLumen(LumenAS.VICIO.get(), 200, 1000)
                    //new StoredLumenComponent.StoredLumen(LumenAS.LUMEN_PRIMAL.get(), 0, 1000)
            ));
            held.set(DataComponentsAS.STORED_LUMEN, lumenCmp);
            IdentifierComponent.createIdentifierIfNotExists(held);*/

            //held.set(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY
            //        .updateLumenStack(LumenAS.AEVITAS.get(), StoredLumenComponent.DEFAULT_CAPACITY, StoredLumenComponent.DEFAULT_CAPACITY));
            //LumenBindingType.applyBinding(LumenAS.AEVITAS.get(), LumenAS.AEVITAS.getId(), held);

            //DynamicModifiersComponent cmp = DynamicModifiersComponent.EMPTY;
            //cmp = cmp.add(new DynamicAttributeModifier("testing5t",
            //        PerksAS.AttributeTypes.BLOCK_BREAK_SPEED.get(), ModifierType.STACKING_MULTIPLY, 6F));
            //held.set(DataComponentsAS.DYNAMIC_MODIFIERS, cmp);
            //IdentifierComponent.createIdentifierIfNotExists(held);
        }
        return Command.SINGLE_SUCCESS;
    }
}
