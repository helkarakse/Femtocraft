package femtocraft.industry.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import femtocraft.Femtocraft;
import femtocraft.FemtocraftConfigs;
import femtocraft.FemtocraftUtils;
import femtocraft.api.IAssemblerSchematic;
import femtocraft.managers.assembler.AssemblerRecipe;
import femtocraft.managers.research.ResearchTechnology;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;

import java.util.List;
import java.util.Random;

/**
 * 
 * @author Itszuvalex
 * 
 * @placeholderIcon <i>static</i> - Icon for use in display slots when no
 *                  schematic is there to draw reference from.
 * @infiniteUseMassMultipler <i>static</i> - Multiplier to use instead of use
 *                           count when calculating mass requirements.
 * @keyedIcon Icon to use instead of itemIcon, when the itemStack has a recipe
 *            associated with it.
 * 
 * @Info This is a base class for Schematics with most of the hard work already
 *       done. This includes tooltip parsing, damage behaviors, support for
 *       infinite use schematics and support for a separate Icon for keyed
 *       Schematics.
 */
public class ItemAssemblySchematic extends Item implements IAssemblerSchematic {
	public static Icon placeholderIcon;
	public static float infiniteUseMassMultiplier = FemtocraftConfigs.schematicInfiniteUseMultiplier;
	public Icon keyedIcon;

	public ItemAssemblySchematic(int itemID) {
		super(itemID);
		setCreativeTab(Femtocraft.femtocraftTab);
		setMaxStackSize(64);
	}

	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		return hasRecipe(stack) ? keyedIcon : this.itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconIndex(ItemStack par1ItemStack) {
		return hasRecipe(par1ItemStack) ? keyedIcon : this.itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		// This is called when displaying the tooltip for an item. par4 is
		// whether it's extended or not, I believe.
		int uses = usesRemaining(par1ItemStack);
		String useString;
		if (uses == -1) {
			useString = "Infinite Uses";
		} else {
			useString = String.valueOf(uses);
		}

		NBTTagCompound itemCompound = par1ItemStack.stackTagCompound;
		if (itemCompound == null || !itemCompound.hasKey("recipe")) {
			par3List.add(String.format(EnumChatFormatting.YELLOW
					+ "Uses good for:" + EnumChatFormatting.RESET + " %s.",
					useString));
			return;
		}

		AssemblerRecipe recipe = AssemblerRecipe
				.loadFromNBTTagCompound((NBTTagCompound) itemCompound
                        .getTag("recipe"));
		if (recipe == null) {
			par3List.add("Invalid Recipe");
			return;
		}

		String useLine = String.format(EnumChatFormatting.YELLOW + "%s"
				+ EnumChatFormatting.RESET + " %s", "Uses Remaining:",
				useString);
		par3List.add(useLine);

		String outputLine = String.format(EnumChatFormatting.YELLOW + "Output:"
				+ EnumChatFormatting.RESET + " %dx%s", recipe.output.stackSize,
				recipe.output.getDisplayName());
		par3List.add(outputLine);

		// End short
		if (!par4)
			return;
		// Begin long
		par3List.add("");

		for (int i = 0; i < 9; ++i) {
			ItemStack item = recipe.input[i];
			String inputString;
			if (item == null) {
				inputString = "empty";
			} else {
				inputString = String.format("%dx%s", item.stackSize,
						item.getDisplayName());
			}
			String inputLine = String.format(EnumChatFormatting.YELLOW
					+ "Input %d:" + EnumChatFormatting.RESET + " %s", i,
					inputString);
			par3List.add(inputLine);
		}

		par3List.add("");

		String massLine = String.format(EnumChatFormatting.YELLOW + "FluidMass:"
				+ EnumChatFormatting.RESET + " %d", recipe.mass);
		par3List.add(massLine);

		String techLevelLine = String.format(EnumChatFormatting.YELLOW
				+ "EnumTechLevel:" + EnumChatFormatting.RESET + " %s",
				FemtocraftUtils.capitalize(recipe.enumTechLevel.key));
		par3List.add(techLevelLine);

		ResearchTechnology tech = recipe.tech;
		String techString;
		if (tech == null) {
			techString = "none";
		} else {
			techString = FemtocraftUtils.capitalize(tech.name);
		}

		String techLine = String.format(EnumChatFormatting.YELLOW
				+ "ResearchTechnology Required:" + EnumChatFormatting.RESET + " %s",
				techString);
		par3List.add(techLine);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		NBTTagCompound stacks = stack.stackTagCompound;
		if (stacks == null)
			return this.maxStackSize;
		return stack.stackTagCompound.hasNoTags() ? this.maxStackSize : 1;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	public boolean hasRecipe(ItemStack stack) {
        return stack.stackTagCompound != null && stack.stackTagCompound.hasKey("recipe");
    }

	@Override
	public AssemblerRecipe getRecipe(ItemStack stack) {
		return getRecipeFromNBT(stack.stackTagCompound);
	}

	@Override
	public boolean setRecipe(ItemStack stack, AssemblerRecipe recipe) {
		return addRecipeToNBT(stack, recipe);
	}

	@Override
	public boolean onAssemble(ItemStack stack) {
		if (usesRemaining(stack) == -1)
			return true;
		stack.attemptDamageItem(1, new Random());
		return !(usesRemaining(stack) == 0);
	}

	private AssemblerRecipe getRecipeFromNBT(NBTTagCompound compound) {
		if (compound == null)
			return null;
		if (!compound.hasKey("recipe"))
			return null;
		return AssemblerRecipe
				.loadFromNBTTagCompound((NBTTagCompound) compound
                        .getTag("recipe"));
	}

	private boolean addRecipeToNBT(ItemStack stack,
			AssemblerRecipe recipe) {
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		} else if (stack.stackTagCompound.hasKey("recipe")) {
			return false;
		}

		NBTTagCompound recipeCompound = new NBTTagCompound();
		recipe.saveToNBTTagCompound(recipeCompound);
		stack.stackTagCompound.setTag("recipe", recipeCompound);

		return true;
	}

	@Override
	public int usesRemaining(ItemStack stack) {
		return stack.getMaxDamage() - stack.getItemDamage();
	}

	@Override
	public ItemStack resultOfBreakdown(ItemStack stack) {
		return new ItemStack(this, 1);
	}

	@Override
	public int massRequired(AssemblerRecipe recipe) {
		float amount = getMaxDamage();
		amount = amount == -1 ? infiniteUseMassMultiplier : amount;
		return (int) (recipe.enumTechLevel.tier * amount);
	}
}
