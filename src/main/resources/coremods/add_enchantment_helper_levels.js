function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.enchantment.EnchantmentHelper',
                'methodName': 'func_77506_a',
                'methodDesc': '(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'add_enchantment_helper_levels\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var iReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.IRETURN, 0);

                while (iReturn !== null) {
                    method.instructions.insertBefore(iReturn, new VarInsnNode(Opcodes.ALOAD, 0)); //Enchantment
                    method.instructions.insertBefore(iReturn, new VarInsnNode(Opcodes.ALOAD, 1)); //ItemStack
                    method.instructions.insertBefore(iReturn, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'getNewEnchantmentLevel',
                        '(ILnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I',
                        ASMAPI.MethodType.STATIC));

                    iReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.IRETURN, method.instructions.indexOf(iReturn) + 1);
                }

                print('[AstralSorcery] Added \'add_enchantment_helper_levels\' ASM patch!');
                return method;
            }
        }
    }
}