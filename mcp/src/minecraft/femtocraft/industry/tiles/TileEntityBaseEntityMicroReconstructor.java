package femtocraft.industry.tiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import femtocraft.Femtocraft;
import femtocraft.FemtocraftUtils;
import femtocraft.api.IAssemblerSchematic;
import femtocraft.managers.ManagerRecipe;
import femtocraft.managers.assembler.AssemblerRecipe;
import femtocraft.power.tiles.TileEntityPowerConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileEntityBaseEntityMicroReconstructor extends TileEntityPowerConsumer implements
        ISidedInventory, IFluidHandler {
    private FluidTank tank;

    public TileEntityBaseEntityMicroReconstructor() {
        super();
        setMaxStorage(800);
        tank = new FluidTank(600);

        // setFluidAmount(600);
        //
        // ItemStack sch = new ItemStack(Femtocraft.paperSchematic);
        // reconstructorItemStacks[15] = sch;
        // ((ItemAssemblySchematic)sch.getItem()).setRecipe(sch,
        // Femtocraft.recipeManager.assemblyRecipes.getRecipe(new
        // ItemStack(Femtocraft.ItemProteinChain)));
    }

    private int powerToCook = 40;
    private int ticksToCook = 100;

    /**
     * The ItemStacks that hold the items currently being used in the furnace
     */
    /**
     * Slots 0-8 are for recipe area - these are dummy items, and should never
     * be touched except when setting for display purposes Slot 9 is for output
     * Slot 10 is for schematic card Slots 11-28 are internal inventory, to pull
     * from when building
     */
    private ItemStack[] reconstructorItemStacks = new ItemStack[29];

    /**
     * The number of ticks that the current item has been cooking for
     */
    public int cookTime = 0;
    public int currentPower = 0;
    private String field_94130_e;
    public ItemStack[] reconstructingStacks = null;

    public int getMassAmount() {
        return tank.getFluidAmount();
    }

    public int getMassCapacity() {
        return tank.getCapacity();
    }

    private IAssemblerSchematic getSchematic() {
        ItemStack is = reconstructorItemStacks[10];
        if (is == null)
            return null;
        if (is.getItem() instanceof IAssemblerSchematic)
            return (IAssemblerSchematic) is.getItem();

        return null;
    }

    @Override
    public void onInventoryChanged() {
        IAssemblerSchematic as = getSchematic();
        if (as != null) {
            AssemblerRecipe recipe = as
                    .getRecipe(reconstructorItemStacks[10]);
            if (recipe == null) {
                for (int i = 0; i < 9; i++) {
                    reconstructorItemStacks[i] = null;
                }
            } else {
                for (int i = 0; i < recipe.input.length; ++i) {
                    ItemStack is = recipe.input[i];
                    reconstructorItemStacks[i] = (is == null) ? null : is
                            .copy();
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                reconstructorItemStacks[i] = null;
            }
        }

        super.onInventoryChanged();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.reconstructorItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1) {
        return this.reconstructorItemStacks[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2) {
        if (par1 < 9)
            return null;

        if (this.reconstructorItemStacks[par1] != null) {
            ItemStack itemstack;

            if (this.reconstructorItemStacks[par1].stackSize <= par2) {
                itemstack = this.reconstructorItemStacks[par1];
                this.reconstructorItemStacks[par1] = null;
                return itemstack;
            } else {
                itemstack = this.reconstructorItemStacks[par1].splitStack(par2);

                if (this.reconstructorItemStacks[par1].stackSize == 0) {
                    this.reconstructorItemStacks[par1] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (par1 < 9)
            return null;

        if (this.reconstructorItemStacks[par1] != null) {
            ItemStack itemstack = this.reconstructorItemStacks[par1];
            this.reconstructorItemStacks[par1] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.reconstructorItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null
                && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName() {
        return this.isInvNameLocalized() ? this.field_94130_e
                : "Microtech Reconstructor";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized
     * name, and translated into the player's language. Otherwise it will be
     * used directly.
     */
    public boolean isInvNameLocalized() {
        return this.field_94130_e != null && this.field_94130_e.length() > 0;
    }

    public void func_94129_a(String par1Str) {
        this.field_94130_e = par1Str;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
        this.reconstructorItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount() - 1; ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist
                    .tagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.reconstructorItemStacks.length) {
                this.reconstructorItemStacks[b0] = ItemStack
                        .loadItemStackFromNBT(nbttagcompound1);
            }
        }

        NBTTagCompound nbttagcompoundsmelt = (NBTTagCompound) nbttaglist
                .tagAt(nbttaglist.tagCount() - 1);
        if (nbttagcompoundsmelt.getBoolean("isReconstructing")) {
            NBTTagList smeltList = nbttagcompoundsmelt.getTagList("input");
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound ss = (NBTTagCompound) smeltList.tagAt(i);
                byte slot = ss.getByte("Slot");
                this.reconstructingStacks[slot] = ItemStack
                        .loadItemStackFromNBT(ss);
            }
        } else {
            this.reconstructingStacks = null;
        }

        this.cookTime = par1NBTTagCompound.getShort("CookTime");

        if (par1NBTTagCompound.hasKey("CustomName")) {
            this.field_94130_e = par1NBTTagCompound.getString("CustomName");
        }

        tank.readFromNBT(par1NBTTagCompound);
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("CookTime", (short) this.cookTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.reconstructorItemStacks.length; ++i) {
            if (this.reconstructorItemStacks[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.reconstructorItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        NBTTagCompound nbttagcompoundsmelt = new NBTTagCompound();
        nbttagcompoundsmelt.setBoolean("isReconstructing", isReconstructing());
        if (isReconstructing()) {
            NBTTagList smeltList = new NBTTagList();

            for (int i = 0; i < 9; ++i) {
                if (reconstructingStacks[i] != null) {
                    NBTTagCompound ss = new NBTTagCompound();
                    ss.setByte("Slot", (byte) i);
                    reconstructingStacks[i].writeToNBT(ss);
                    smeltList.appendTag(ss);
                }
            }

            nbttagcompoundsmelt.setTag("input", smeltList);
        }
        nbttaglist.appendTag(nbttagcompoundsmelt);

        par1NBTTagCompound.setTag("Items", nbttaglist);

        if (this.isInvNameLocalized()) {
            par1NBTTagCompound.setString("CustomName", this.field_94130_e);
        }

        tank.writeToNBT(par1NBTTagCompound);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    public int getCookProgressScaled(int par1) {
        return this.cookTime * par1 / ticksToCook;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity() {
        super.updateEntity();

        boolean flag1 = false;

        if (worldObj.isRemote) {
            return;
        }

        if (reconstructingStacks != null) {
            if (cookTime == ticksToCook) {
                cookTime = 0;
                endWork();
                flag1 = true;
            }

            ++this.cookTime;
        } else if (this.canWork()) {
            startWork();
            flag1 = true;
        } else {
            cookTime = 0;
        }

        if (flag1) {
            onInventoryChanged();
        }
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item,
     * destination stack isn't full, etc.
     */
    private boolean canWork() {
        if (reconstructorItemStacks[0] == null
                && reconstructorItemStacks[1] == null
                && reconstructorItemStacks[2] == null
                && reconstructorItemStacks[3] == null
                && reconstructorItemStacks[4] == null
                && reconstructorItemStacks[5] == null
                && reconstructorItemStacks[6] == null
                && reconstructorItemStacks[7] == null
                && reconstructorItemStacks[8] == null || getSchematic() == null || reconstructingStacks != null || this.getCurrentPower() < this.powerToCook) {
            return false;
        } else {
            AssemblerRecipe recipe = getSchematic().getRecipe(
                    reconstructorItemStacks[10]);
            return recipe != null && tank.getFluidAmount() >= recipe.mass && roomForItem(recipe.output) && hasItems(recipe.input);
        }
    }

    private boolean roomForItem(ItemStack item) {
        ItemStack[] fake = new ItemStack[1];
        ItemStack output = reconstructorItemStacks[9];
        fake[0] = output == null ? null : output.copy();
        return FemtocraftUtils.placeItem(item, fake, new int[]{});
    }

    private boolean hasItems(ItemStack[] items) {
        int offset = 11;
        int size = reconstructorItemStacks.length;
        ItemStack[] inv = new ItemStack[size - offset];
        for (int i = offset; i < size - 1; ++i) {
            ItemStack it = reconstructorItemStacks[i];
            inv[i - offset] = it == null ? null : it.copy();
        }

        for (ItemStack item : items) {
            if (!FemtocraftUtils.removeItem(item, inv, new int[]{}))
                return false;
        }

        return true;
    }

    public void startWork() {
        reconstructingStacks = new ItemStack[9];

        for (int i = 0; i < 9; ++i) {
            ItemStack s = reconstructorItemStacks[i];
            reconstructingStacks[i] = s == null ? null : s.copy();
        }
        IAssemblerSchematic as = getSchematic();
        AssemblerRecipe recipe = as
                .getRecipe(reconstructorItemStacks[10]);

        for (int i = 0; i < recipe.input.length; ++i) {
            if (recipe.input[i] == null)
                continue;
            FemtocraftUtils.removeItem(reconstructingStacks[i],
                    reconstructorItemStacks, new int[]{0, 1, 2, 3, 4, 5, 6,
                    7, 8, 9, 10});
        }

        tank.getFluid().amount -= recipe.mass;
        this.consume(powerToCook);

        if (!as.onAssemble(reconstructorItemStacks[10])) {
            reconstructorItemStacks[10] = as
                    .resultOfBreakdown(reconstructorItemStacks[10]);
        }

    }

    public void endWork() {
        // Cannot look off schematic, cause schematic can change mid-build
        AssemblerRecipe recipe = ManagerRecipe.assemblyRecipes
                .getRecipe(reconstructingStacks);

        if (recipe != null) {
            FemtocraftUtils.placeItem(recipe.output.copy(),
                    reconstructorItemStacks, new int[]{0, 1, 2, 3, 4, 5, 6,
                    7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28});
            reconstructingStacks = null;
        }
    }

    public boolean isReconstructing() {
        return reconstructingStacks != null;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord,
                this.zCoord) == this && par1EntityPlayer.getDistanceSq(
                (double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
                (double) this.zCoord + 0.5D) <= 64.0D;
    }

    public void openChest() {
    }

    public void closeChest() {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    public boolean isStackValidForSlot(int par1, ItemStack par2ItemStack) {
        return par1 > 10
                || (par1 == 10 && par2ItemStack.getItem() instanceof IAssemblerSchematic);
    }

    /**
     * Get the size of the side inventory.
     */
    public int[] getSizeInventorySide(int par1) {
        // Regular inventory
        if (par1 == 1)
            return new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                    23, 24, 25, 26, 27, 28};
            // Output
        else
            return new int[]{9};
    }

    public boolean func_102007_a(int par1, ItemStack par2ItemStack, int par3) {
        return this.isStackValidForSlot(par1, par2ItemStack);
    }

    public boolean func_102008_b(int par1, ItemStack par2ItemStack, int par3) {
        return true;
    }

    /**
     * ********************************************************************************
     * This function is here for compatibilities sake, Modders should Check for
     * Sided before ContainerWorldly, Vanilla Minecraft does not follow the
     * sided standard that Modding has for a while.
     * <p/>
     * In vanilla:
     * <p/>
     * Top: Ores Sides: Fuel Bottom: Output
     * <p/>
     * Standard Modding: Top: Ores Sides: Output Bottom: Fuel
     * <p/>
     * The Modding one is designed after the GUI, the vanilla one is designed
     * because its intended use is for the hopper, which logically would take
     * things in from the top.
     * <p/>
     * This will possibly be removed in future updates, and make vanilla the
     * definitive standard.
     */

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return i > 10
                || (i == 10 && itemstack.getItem() instanceof IAssemblerSchematic);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        switch (var1) {
            case (1):
                return new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
                        23, 24, 25, 26, 27, 28};
            case (0):
            case (2):
            case (3):
            case (4):
            case (5):
                return new int[]{9};
            default:
                return new int[]{};
        }
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return i > 10;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return i == 9 || i > 10;
    }

    // IFluidHandler

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource,
                            boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == Femtocraft.mass;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }

    public void setFluidAmount(int amount) {
        if (tank.getFluid() != null) {
            tank.setFluid(new FluidStack(tank.getFluid().fluidID, amount));
        } else {
            tank.setFluid(new FluidStack(Femtocraft.mass, amount));
        }
    }

    public void clearFluid() {
        tank.setFluid(null);
    }
}
