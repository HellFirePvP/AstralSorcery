package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.CooldownSetEvent;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeCooldown
 * Created by HellFirePvP
 * Date: 27.07.2020 / 21:38
 */
public class AttributeTypeCooldown extends PerkAttributeType {

    public AttributeTypeCooldown() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_COOLDOWN_REDUCTION, true);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);

        eventBus.addListener(this::onCooldown);
    }

    private void onCooldown(CooldownSetEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();

        if (world.isRemote()) {
            return;
        }
        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!prog.isValid()) {
            return;
        }
        if (player instanceof ServerPlayerEntity) {
            if (MiscUtils.isPlayerFakeMP((ServerPlayerEntity) player)) {
                return;
            }
        }

        float multiplier = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.SERVER)
                .modifyValue(player, prog, this, 1F);
        multiplier -= 1F;
        multiplier = AttributeEvent.postProcessModded(player, this, multiplier);
        multiplier = 1F - MathHelper.clamp(multiplier, 0F, 1F);
        event.setCooldown(Math.round(event.getResultCooldown() * multiplier));
    }

}
