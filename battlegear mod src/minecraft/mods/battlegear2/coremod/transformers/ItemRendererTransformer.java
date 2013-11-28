package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class ItemRendererTransformer extends TransformerBase {

    public ItemRendererTransformer() {
		super("net.minecraft.client.renderer.ItemRenderer");
	}

	private String itemStackClass;
    private String itemRendererClass;
    private String minecraftClass;

    private String itemRendererMinecraftField;
    private String itemRendereriteToRenderField;

    private String renderItem1stPersonMethodName;
    private String renderItem1stPersonMethodDesc;
    private String updateEquippedItemMethodName;
    private String updateEquippedItemMethodDesc;

    private void processupdateEquippedMethod(MethodNode mn) {
        sendPatchLog("updateEquippedItem");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == RETURN) {
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendereriteToRenderField, "L" + itemStackClass + ";"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "updateEquippedItem"
                        , "(L" + itemRendererClass + ";L" + minecraftClass + ";L" + itemStackClass + ";)V"));
            }

            newList.add(insn);
        }

        mn.instructions = newList;
    }

    private void processRenderItemMethod(MethodNode mn) {

        sendPatchLog("renderItemInFirstPerson");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == RETURN) {
                newList.add(new VarInsnNode(FLOAD, 1));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, "offHandItemToRender", "L" + itemStackClass + ";"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "renderItemInFirstPerson"
                        , "(FL" + minecraftClass + ";L" + itemRendererClass + ";L" + itemStackClass + ";)V"));
            }

            newList.add(insn);
        }

        mn.instructions = newList;
    }

	@Override
	void processMethods(List<MethodNode> methods) {
		for (MethodNode mn : methods) {
            if (mn.name.equals(renderItem1stPersonMethodName) &&
                    mn.desc.equals(renderItem1stPersonMethodDesc)) {
                processRenderItemMethod(mn);
            } else if (mn.name.equals(updateEquippedItemMethodName) &&
                    mn.desc.equals(updateEquippedItemMethodDesc)) {
                processupdateEquippedMethod(mn);
            }
        }
	}

	@Override
	void processFields(List<FieldNode> fields) {
		System.out.println("\tAdding new fields to ItemRenderer");
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "offHandItemToRender", "L" + itemStackClass + ";", null, null));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "equippedItemOffhandSlot", "I", null, 0));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "equippedOffHandProgress", "F", null, 0F));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "prevEquippedOffHandProgress", "F", null, 0F));
    }

	@Override
	void setupMappings() {
		itemStackClass = BattlegearTranslator.getMapedClassName("ItemStack");
        itemRendererClass = BattlegearTranslator.getMapedClassName("ItemRenderer");
        minecraftClass = BattlegearTranslator.getMapedClassName("Minecraft");

        itemRendererMinecraftField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78455_a");
        itemRendereriteToRenderField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78453_b");

        renderItem1stPersonMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78440_a");
        renderItem1stPersonMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78440_a");

        updateEquippedItemMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78441_a");
        updateEquippedItemMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78441_a");

	}
}
