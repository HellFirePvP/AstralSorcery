function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.item.ItemStack',
                'methodName': 'func_82840_a',
                'methodDesc': '(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'add_enchantment_tooltip\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var callGetEnchantmentTagList = ASMAPI.mapMethod('func_77986_q');

                var methodCall = ASMAPI.findFirstMethodCallAfter(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        callGetEnchantmentTagList,
                        '()Lnet/minecraft/nbt/ListNBT;',
                        0);

                while (methodCall !== null) {
                    var node = methodCall.getNext();

                    method.instructions.insertBefore(node, new VarInsnNode(Opcodes.ALOAD, 0)); //ItemStack
                    method.instructions.insertBefore(node, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'addNewEnchantmentLevelsTag',
                        '(Lnet/minecraft/nbt/ListNBT;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/ListNBT;',
                        ASMAPI.MethodType.STATIC));

                    methodCall = ASMAPI.findFirstMethodCallAfter(method,
                            ASMAPI.MethodType.VIRTUAL,
                            'net/minecraft/item/ItemStack',
                            callGetEnchantmentTagList,
                            '()Lnet/minecraft/nbt/ListNBT;',
                            method.instructions.indexOf(methodCall) + 2);
                }

                print('[AstralSorcery] Added \'add_enchantment_tooltip\' ASM patch!');
                return method;
            }
        }
    }
}