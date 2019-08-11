function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.enchantment.EnchantmentHelper',
                'methodName': 'getEnchantments',
                'methodDesc': '(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'add_enchantment_helper_enchantments\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.ARETURN, 0);

                while (aReturn !== null) {
                    method.instructions.insertBefore(aReturn, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
                    method.instructions.insertBefore(aReturn, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'applyNewEnchantmentLevels',
                        '(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)Ljava/util/Map;',
                        ASMAPI.MethodType.STATIC));

                    aReturn = ASMAPI.findFirstInstructionAfter(method, Opcodes.ARETURN, method.instructions.indexOf(aReturn) + 1);
                }

                print('[AstralSorcery] Added \'add_enchantment_helper_enchantments\' ASM patch!');
                return method;
            }
        }
    }
}