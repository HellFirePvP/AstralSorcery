/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import net.minecraftforge.fml.relauncher.Side;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchModifyEnchantmentLevelsTooltipEvent
 * Created by HellFirePvP
 * Date: 20.05.2018 / 17:06
 */
public class PatchModifyEnchantmentLevelsTooltipEvent extends ClassPatch {

    public PatchModifyEnchantmentLevelsTooltipEvent() {
        super("net.minecraft.item.ItemStack");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethodLazy(cn, "getTooltip", "func_82840_a");
        int peek = peekFirstMethodCallAfter(mn,
                "net/minecraft/item/ItemStack",
                "getEnchantmentTagList",
                "func_77986_q",
                "()Lnet/minecraft/nbt/NBTTagList;",
                0);

        MethodInsnNode min = getFirstMethodCallBefore(mn,
                "net/minecraft/item/ItemStack",
                "hasTagCompound",
                "func_77942_o",
                "()Z",
                peek);

        AbstractInsnNode n = min.getPrevious();
        mn.instructions.insertBefore(n, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
        mn.instructions.insertBefore(n, new TypeInsnNode(Opcodes.NEW, "hellfirepvp/astralsorcery/common/event/ItemEnchantmentTooltipEvent"));
        mn.instructions.insertBefore(n, new InsnNode(Opcodes.DUP));
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 0)); //thisStack
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 1)); //player
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 3)); //tooltipList
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 2)); //tooltipAdvEnumFlag
        mn.instructions.insertBefore(n, new MethodInsnNode(Opcodes.INVOKESPECIAL, "hellfirepvp/astralsorcery/common/event/ItemEnchantmentTooltipEvent", "<init>", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Lnet/minecraft/client/util/ITooltipFlag;)V", false));
        mn.instructions.insertBefore(n, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
        mn.instructions.insertBefore(n, new InsnNode(Opcodes.POP));
    }
    
    @Override
    public boolean canExecuteForSide(Side side) {
        return side == Side.CLIENT;
    }
}
