/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchModifyEnchantmentLevelsTooltip
 * Created by HellFirePvP
 * Date: 27.01.2018 / 13:21
 */
public class PatchModifyEnchantmentLevelsTooltip extends ClassPatch {

    public PatchModifyEnchantmentLevelsTooltip() {
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
        while (peek != -1) {
            AbstractInsnNode node = mn.instructions.get(peek).getNext();
            //The actual enchantment-NBT-list is on the stack right now.
            mn.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
            mn.instructions.insertBefore(node, new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "hellfirepvp/astralsorcery/common/enchantment/amulet/EnchantmentUpgradeHelper",
                    "modifyEnchantmentTags",
                    "(Lnet/minecraft/nbt/NBTTagList;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;",
                    false));

            peek = peekFirstMethodCallAfter(mn,
                    "net/minecraft/item/ItemStack",
                    "getEnchantmentTagList",
                    "func_77986_q",
                    "()Lnet/minecraft/nbt/NBTTagList;",
                    peek + 3);
        }
    }

}
