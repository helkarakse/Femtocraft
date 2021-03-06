/**
 * 
 */
package femtocraft.power.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import femtocraft.power.tiles.TileEntityBaseEntityCable;
import femtocraft.power.blocks.BlockCable;
import femtocraft.proxy.ProxyClient;
import femtocraft.render.RenderModel;
import femtocraft.render.RenderPoint;
import femtocraft.render.RenderQuad;
import femtocraft.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @author Chris
 * 
 */
public class RenderCable implements ISimpleBlockRenderingHandler {
	private RenderModel Coil_North;
	private RenderModel Coil_South;
	private RenderModel Coil_East;
	private RenderModel Coil_West;
	private RenderModel Coil_Up;
	private RenderModel Coil_Down;

	private RenderModel Coil_North_Close;
	private RenderModel Coil_South_Close;
	private RenderModel Coil_East_Close;
	private RenderModel Coil_West_Close;
	private RenderModel Coil_Up_Close;
	private RenderModel Coil_Down_Close;

	private boolean CoilInitialized = false;

	public RenderCable() {
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		BlockCable cable = (BlockCable) block;
		if (cable == null)
			return;

		Tessellator tessellator = Tessellator.instance;

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(1, 1, 1);
		renderCable(cable, 0, 0, 0, renderer, new boolean[] { false, false,
				true, true, false, false });
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		BlockCable cable = (BlockCable) block;
		if (block == null)
			return false;

		TileEntity tile = renderer.blockAccess.getBlockTileEntity(x, y, z);
		if (tile == null)
			return false;
		if (!(tile instanceof TileEntityBaseEntityCable))
			return false;
		TileEntityBaseEntityCable cableTile = (TileEntityBaseEntityCable) tile;
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(
				renderer.blockAccess, x, y, z));
		tessellator.setColorOpaque_F(1, 1, 1);
		// tessellator.setBrightness((int)
		// (cable.getBlockBrightness(renderer.blockAccess, x, y, z) * 3200.));

		return renderCable(cable, x, y, z, renderer, cableTile.connections);
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ProxyClient.FemtopowerCableRenderID;
	}

	private boolean renderCable(BlockCable cable, float x, float y,
			float z, RenderBlocks renderer, boolean[] connections) {

		// //Render border
		// renderer.setOverrideBlockTexture(cable.coreBorder);
		// block.setBlockBounds(4.F/16.0F, 4.F/16.0F, 4.F/16.0F, 12.F/16.0F,
		// 12.F/16.0F, 12.F/16.F);
		// renderer.setRenderBoundsFromBlock(block);
		// renderer.renderStandardBlock(block, x, y, z);
		// renderer.clearOverrideBlockTexture();
		//
		// block.setBlockBounds(5.F/16.0F, 5.F/16.0F, 5.F/16.0F, 11.F/16.0F,
		// 11.F/16.0F, 11.F/16.F);
		// renderer.setRenderBoundsFromBlock(block);
		// renderer.renderStandardBlock(block, x, y, z);
		//
		// renderer.clearOverrideBlockTexture();
		//
		// renderer.
		//
		// cable.setBlockBounds();

		// tessellator.setBrightness((int)
		// (renderer.blockAccess.getLightBrightness(x, y, z) * 100));

		drawCore(cable, x, y, z, renderer, connections);

		return true;
	}

	private void drawCore(BlockCable cable, float x, float y, float z,
			RenderBlocks renderer, boolean[] connections) {
		if (!CoilInitialized)
			initializeCoils(cable);

		RenderPoint loc = new RenderPoint(x, y, z);

		if (!connectedAcross(connections))
			drawCoreBlock(cable, x, y, z, renderer, connections);
		else {
			if (connections[0]) {
				drawCoilClose(cable, ForgeDirection.UP, loc);
				drawCoilClose(cable, ForgeDirection.DOWN, loc);
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(0));
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(0).getOpposite());
			} else if (connections[2]) {
				drawCoilClose(cable, ForgeDirection.NORTH, loc);
				drawCoilClose(cable, ForgeDirection.SOUTH, loc);
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(2));
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(2).getOpposite());
			} else if (connections[4]) {
				drawCoilClose(cable, ForgeDirection.EAST, loc);
				drawCoilClose(cable, ForgeDirection.WEST, loc);
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(4));
				// drawCoil(cable, x, y, z, 2.0F/16.0F, renderer,
				// ForgeDirection.getOrientation(4).getOpposite());
			}
		}

		// Draw connecting tubes
		for (int i = 0; i < 6; i++) {
			if (connections[i]) {
				// Draw coil and connectors
				drawCoilFar(cable, ForgeDirection.getOrientation(i), loc);
			}
		}
	}

	private void drawCoreBlock(BlockCable cable, float x, float y,
			float z, RenderBlocks renderer, boolean[] connections) {
		RenderUtils.renderCube(x, y, z, 5.0f / 16.0f, 5.0f / 16.0f,
                5.0f / 16.0f, 11.0f / 16.0f, 11.0f / 16.0f, 11.0f / 16.0f,
                cable.coil);
		RenderUtils.renderDoubleSidedCube(x, y, z, 4.0f / 16.0f,
                4.0f / 16.0f, 4.0f / 16.0f, 12.0f / 16.0f, 12.0f / 16.0f,
                12.0f / 16.0f, cable.coreBorder);

		// Draw connector caps
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.UP, !connections[1]);
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.DOWN, !connections[0]);
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.NORTH, !connections[2]);
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.EAST, !connections[5]);
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.SOUTH, !connections[3]);
		drawConnector(cable, x, y, z, 3.0F / 16.0F, renderer,
				ForgeDirection.WEST, !connections[4]);
	}

	private void drawConnector(BlockCable cable, float x, float y,
			float z, float offset, RenderBlocks renderer,
			ForgeDirection direction, boolean drawCap) {
		ForgeDirection rotaxi = ForgeDirection.UNKNOWN;
		float xoffset = 0;
		float yoffset = 0;
		float zoffset = 0;

		switch (direction) {
		case UP:
			rotaxi = ForgeDirection.EAST;
			xoffset = .5F;
			yoffset = .5F + offset;
			zoffset = .5F;
			break;
		case DOWN:
			rotaxi = ForgeDirection.WEST;
			xoffset = .5F;
			yoffset = .5F - offset;
			zoffset = .5F;
			break;
		case NORTH:
			xoffset = .5F;
			yoffset = .5F;
			zoffset = .5F - offset;
			rotaxi = ForgeDirection.UP;
			break;
		case EAST:
			xoffset = .5F + offset;
			yoffset = .5F;
			zoffset = .5F;
			rotaxi = ForgeDirection.DOWN;
			break;
		case SOUTH:
			xoffset = .5F;
			yoffset = .5F;
			zoffset = .5F + offset;
			rotaxi = ForgeDirection.DOWN;
			break;
		case WEST:
			xoffset = .5F - offset;
			yoffset = .5F;
			zoffset = .5F;
			rotaxi = ForgeDirection.UP;
			break;
		default:
			break;
		}
		ForgeDirection face1, face2, face3, face4;
		face1 = direction.getRotation(rotaxi);
		face2 = face1.getRotation(direction);
		face3 = face2.getRotation(direction);
		face4 = face3.getRotation(direction);

		if (drawCap) {
			RenderUtils.drawArbitraryFace(x, y, z, -1.0F / 16.0F
                    + xoffset, 1.0F / 16.0F + xoffset, -1.0F / 16.0F + yoffset,
                    1.0F / 16.0F + yoffset, -1.0F / 16.0F + zoffset,
                    1.0F / 16.0F + zoffset, direction, cable.connector,
                    cable.connector.getMinU(), cable.connector.getMaxU(),
                    cable.connector.getMinV(), cable.connector.getMaxV());
		}

		RenderUtils.drawArbitraryFace(x, y, z, -1.0F / 16.0F
                + xoffset, 1.0F / 16.0F + xoffset, -1.0F / 16.0F + yoffset,
                1.0F / 16.0F + yoffset, -1.0F / 16.0F + zoffset,
                1.0F / 16.0F + zoffset, face1, cable.connector,
                cable.connector.getMinU(), cable.connector.getMaxU(),
                cable.connector.getMinV(), cable.connector.getMaxV());
		RenderUtils.drawArbitraryFace(x, y, z, -1.0F / 16.0F
                + xoffset, 1.0F / 16.0F + xoffset, -1.0F / 16.0F + yoffset,
                1.0F / 16.0F + yoffset, -1.0F / 16.0F + zoffset,
                1.0F / 16.0F + zoffset, face2, cable.connector,
                cable.connector.getMinU(), cable.connector.getMaxU(),
                cable.connector.getMinV(), cable.connector.getMaxV());
		RenderUtils.drawArbitraryFace(x, y, z, -1.0F / 16.0F
                + xoffset, 1.0F / 16.0F + xoffset, -1.0F / 16.0F + yoffset,
                1.0F / 16.0F + yoffset, -1.0F / 16.0F + zoffset,
                1.0F / 16.0F + zoffset, face3, cable.connector,
                cable.connector.getMinU(), cable.connector.getMaxU(),
                cable.connector.getMinV(), cable.connector.getMaxV());
		RenderUtils.drawArbitraryFace(x, y, z, -1.0F / 16.0F
                + xoffset, 1.0F / 16.0F + xoffset, -1.0F / 16.0F + yoffset,
                1.0F / 16.0F + yoffset, -1.0F / 16.0F + zoffset,
                1.0F / 16.0F + zoffset, face4, cable.connector,
                cable.connector.getMinU(), cable.connector.getMaxU(),
                cable.connector.getMinV(), cable.connector.getMaxV());
	}

	// private void drawCoil(BlockCable cable, float x, float y, float z,
	// float offset, RenderBlocks renderer, ForgeDirection direction) {
	//
	// drawConnector(cable, x, y, z, 1.0F/16.0F + offset, renderer, direction,
	// true);
	// drawConnector(cable, x, y, z, -1.0F/16.0F + offset, renderer, direction,
	// false);
	//
	// if(!CoilInitialized)
	// initializeCoils(cable);
	//
	// double yrot = Math.PI/2.0D;
	// double xrot = 0;
	//
	// switch(direction) {
	// case UP:
	// xrot += Math.PI/2.0D;
	// break;
	// case DOWN:
	// xrot -= Math.PI/2.0D;
	// break;
	// case NORTH:
	// break;
	// case EAST:
	// yrot += Math.PI/2.0D;
	// break;
	// case SOUTH:
	// yrot += Math.PI;
	// break;
	// case WEST:
	// yrot -= Math.PI/2.0D;
	// break;
	// default:
	// break;
	// }
	//
	// RenderModel t = Coil.copy();
	// t.location.x = x+.5f;
	// t.location.y = y+.5f;
	// t.location.z = z+.5f;
	// t.rotateOnYAxis(yrot).rotateOnXAxis(xrot);
	// t.draw();
	// }

	private boolean connectedAcross(boolean[] connections) {
		if (numConnections(connections) == 2) {
			if (connections[0] && connections[1])
				return true;
			if (connections[2] && connections[3])
				return true;
			if (connections[4] && connections[5])
				return true;
		}
		return false;
	}

	private int numConnections(boolean[] connections) {
		int count = 0;
		for (int i = 0; i < 6; i++)
			if (connections[i])
				++count;
		return count;
	}

	private void initializeCoils(BlockCable cable) {
		Coil_North = createCoil(cable, 6.f / 16.f).rotateOnZAxis(Math.PI);
		Coil_South = Coil_North.rotatedOnXAxis(Math.PI);
		Coil_Up = Coil_North.rotatedOnXAxis(Math.PI / 2.d);
		Coil_Down = Coil_North.rotatedOnXAxis(-Math.PI / 2.d);
		Coil_East = Coil_North.rotatedOnYAxis(-Math.PI / 2.d);
		Coil_West = Coil_North.rotatedOnYAxis(Math.PI / 2.d);

		Coil_North_Close = createCoil(cable, 2.f / 16.f);
		Coil_South_Close = Coil_North_Close.rotatedOnXAxis(Math.PI);
		Coil_Up_Close = Coil_North_Close.rotatedOnXAxis(Math.PI / 2.d);
		Coil_Down_Close = Coil_North_Close.rotatedOnXAxis(-Math.PI / 2.d);
		Coil_East_Close = Coil_North_Close.rotatedOnYAxis(-Math.PI / 2.d);
		Coil_West_Close = Coil_North_Close.rotatedOnYAxis(Math.PI / 2.d);

		CoilInitialized = true;
	}

	private void drawCoilFar(BlockCable cable, ForgeDirection dir,
			RenderPoint loc) {
		drawConnector(cable, loc.x, loc.y, loc.z, 7.0F / 16.0f, null, dir, true);
		drawConnector(cable, loc.x, loc.y, loc.z, 5.0F / 16.0F, null, dir,
				false);

		switch (dir) {
		case UP:
			Coil_Up.location = loc;
			Coil_Up.draw();
			break;
		case DOWN:
			Coil_Down.location = loc;
			Coil_Down.draw();
			break;
		case NORTH:
			Coil_North.location = loc;
			Coil_North.draw();
			break;
		case EAST:
			Coil_East.location = loc;
			Coil_East.draw();
			break;
		case SOUTH:
			Coil_South.location = loc;
			Coil_South.draw();
			break;
		case WEST:
			Coil_West.location = loc;
			Coil_West.draw();
			break;
		default:
			break;
		}
	}

	private void drawCoilClose(BlockCable cable, ForgeDirection dir,
			RenderPoint loc) {
		drawConnector(cable, loc.x, loc.y, loc.z, 3.0F / 16.0f, null, dir, true);
		drawConnector(cable, loc.x, loc.y, loc.z, 1.F / 16.0F, null, dir, false);

		switch (dir) {
		case UP:
			Coil_Up_Close.location = loc;
			Coil_Up_Close.draw();
			break;
		case DOWN:
			Coil_Down_Close.location = loc;
			Coil_Down_Close.draw();
			break;
		case NORTH:
			Coil_North_Close.location = loc;
			Coil_North_Close.draw();
			break;
		case EAST:
			Coil_East_Close.location = loc;
			Coil_East_Close.draw();
			break;
		case SOUTH:
			Coil_South_Close.location = loc;
			Coil_South_Close.draw();
			break;
		case WEST:
			Coil_West_Close.location = loc;
			Coil_West_Close.draw();
			break;
		default:
			break;
		}
	}

	private RenderModel createCoil(BlockCable cable, float offset) {
		RenderModel Coil = new RenderModel(new RenderPoint(0, 0, 0), new RenderPoint(.5f, .5f, .5f));

		offset = .5F - offset;

		// West-face, AA - top left, AB - bot left, AC - bot right, AD - top
		// right
		RenderPoint AA = new RenderPoint(4.0F / 16.0F, 12.0F / 16.0F, -2.0F / 16.0F
				+ offset);
		RenderPoint AB = new RenderPoint(4.0F / 16.0F, 4.0F / 16.0F, -2.0F / 16.0F + offset);
		RenderPoint AC = new RenderPoint(4.0F / 16.0F, 4.0F / 16.0F, 2.0F / 16.0F + offset);
		RenderPoint AD = new RenderPoint(4.0F / 16.0F, 12.0F / 16.0F, 2.0F / 16.0F + offset);
		// East-face, BA - top left, BB - bottom left, BC - bottom right, BD -
		// top right
		RenderPoint BA = new RenderPoint(12.0F / 16.0F, 12.0F / 16.0F,
				2.0F / 16.0F + offset);
		RenderPoint BB = new RenderPoint(12.0F / 16.0F, 4.0F / 16.0F, 2.0F / 16.0F + offset);
		RenderPoint BC = new RenderPoint(12.0F / 16.0F, 4.0F / 16.0F, -2.0F / 16.0F
				+ offset);
		RenderPoint BD = new RenderPoint(12.0F / 16.0F, 12.0F / 16.0F, -2.0F / 16.0F
				+ offset);

		RenderQuad a = new RenderQuad(AD.copy(), AC.copy(), AB.copy(), AA.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad ar = new RenderQuad(AA.copy(), AB.copy(), AC.copy(), AD.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad b = new RenderQuad(BD.copy(), BC.copy(), BB.copy(), BA.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad br = new RenderQuad(BA.copy(), BB.copy(), BC.copy(), BD.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad c = new RenderQuad(BA.copy(), AD.copy(), AA.copy(), BD.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad cr = new RenderQuad(BD.copy(), AA.copy(), AD.copy(), BA.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad d = new RenderQuad(BC.copy(), AB.copy(), AC.copy(), BB.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());
		RenderQuad dr = new RenderQuad(BB.copy(), AC.copy(), AB.copy(), BC.copy(),
				cable.border, cable.border.getMinU(),
				cable.border.getInterpolatedU(8.f), cable.border.getMinV(),
				cable.border.getMaxV());

		Coil.addQuad(a);
		Coil.addQuad(ar);
		Coil.addQuad(b);
		Coil.addQuad(br);
		Coil.addQuad(c);
		Coil.addQuad(cr);
		Coil.addQuad(d);
		Coil.addQuad(dr);

		// Draw coil

		// West-face, AA - top left, AB - bot left, AC - bot right, AD - top
		// right
		AA = new RenderPoint(5.0F / 16.0F, 11.0F / 16.0F, -1.0F / 16.0F + offset);
		AB = new RenderPoint(5.0F / 16.0F, 5.0F / 16.0F, -1.0F / 16.0F + offset);
		AC = new RenderPoint(5.0F / 16.0F, 5.0F / 16.0F, 1.0F / 16.0F + offset);
		AD = new RenderPoint(5.0F / 16.0F, 11.0F / 16.0F, 1.0F / 16.0F + offset);
		// East-face, BA - top left, BB - bottom left, BC - bottom right, BD -
		// top right
		BA = new RenderPoint(11.0F / 16.0F, 11.0F / 16.0F, 1.0F / 16.0F + offset);
		BB = new RenderPoint(11.0F / 16.0F, 5.0F / 16.0F, 1.0F / 16.0F + offset);
		BC = new RenderPoint(11.0F / 16.0F, 5.0F / 16.0F, -1.0F / 16.0F + offset);
		BD = new RenderPoint(11.0F / 16.0F, 11.0F / 16.0F, -1.0F / 16.0F + offset);

		RenderQuad e = new RenderQuad(AD.copy(), AC.copy(), AB.copy(), AA.copy(),
				cable.coilEdge, cable.coilEdge.getMinU(),
				cable.coilEdge.getMaxU() - 2.0F
						* (cable.coilEdge.getMaxU() - cable.coilEdge.getMinU())
						/ 3.0F, cable.coilEdge.getMinV(),
				cable.coilEdge.getMaxV());
		RenderQuad f = new RenderQuad(BD.copy(), BC.copy(), BB.copy(), BA.copy(),
				cable.coilEdge, cable.coilEdge.getMinU(),
				cable.coilEdge.getMaxU() - 2.0F
						* (cable.coilEdge.getMaxU() - cable.coilEdge.getMinU())
						/ 3.0F, cable.coilEdge.getMinV(),
				cable.coilEdge.getMaxV());
		RenderQuad g = new RenderQuad(BA.copy(), AD.copy(), AA.copy(), BD.copy(),
				cable.coilEdge, cable.coilEdge.getMinU(),
				cable.coilEdge.getMaxU() - 2.0F
						* (cable.coilEdge.getMaxU() - cable.coilEdge.getMinU())
						/ 3.0F, cable.coilEdge.getMinV(),
				cable.coilEdge.getMaxV());
		RenderQuad h = new RenderQuad(BC.copy(), AB.copy(), AC.copy(), BB.copy(),
				cable.coilEdge, cable.coilEdge.getMinU(),
				cable.coilEdge.getMaxU() - 2.0F
						* (cable.coilEdge.getMaxU() - cable.coilEdge.getMinU())
						/ 3.0F, cable.coilEdge.getMinV(),
				cable.coilEdge.getMaxV());

		Coil.addQuad(e.reverse());
		Coil.addQuad(f.reverse());
		Coil.addQuad(g.reverse());
		Coil.addQuad(h.reverse());

		RenderQuad i = new RenderQuad(BB.copy(), AC.copy(), AD.copy(), BA.copy(),
				cable.coil, cable.coil.getMinU(), cable.coil.getMaxU(),
				cable.coil.getMinV(), cable.coil.getMaxV());
		RenderQuad j = new RenderQuad(AA.copy(), AB.copy(), BC.copy(), BD.copy(),
				cable.coil, cable.coil.getMinU(), cable.coil.getMaxU(),
				cable.coil.getMinV(), cable.coil.getMaxV());
		Coil.addQuad(i.reverse());
		Coil.addQuad(j.reverse());

		return Coil;
	}
}
