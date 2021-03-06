package femtocraft.core.ore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import femtocraft.Femtocraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockOreTitanium extends BlockOre {

	public BlockOreTitanium(int id) {
		super(id);
		this.setCreativeTab(Femtocraft.femtocraftTab);
		setTextureName(Femtocraft.ID.toLowerCase() + ":" + "BlockOreTitanium");
		setUnlocalizedName("BlockOreTitanium");
		setHardness(3.0f);
		setStepSound(Block.soundStoneFootstep);
		setResistance(1f);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(Femtocraft.ID
				.toLowerCase() + ":" + "BlockOreTitanium");
	}
}
