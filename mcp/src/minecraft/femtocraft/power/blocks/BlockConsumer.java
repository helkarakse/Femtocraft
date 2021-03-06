package femtocraft.power.blocks;

import femtocraft.Femtocraft;
import femtocraft.power.tiles.TileEntityPowerConsumerTest;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockConsumer extends TileContainerPower {

	public BlockConsumer(int par1, Material par2Material) {
		super(par1, par2Material);
		setCreativeTab(Femtocraft.femtocraftTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityPowerConsumerTest();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

}
