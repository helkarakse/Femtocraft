package femtocraft;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeDirection;

public class FemtocraftUtils {

	public static int indexOfForgeDirection(ForgeDirection dir)
	{
		return Arrays.asList(ForgeDirection.VALID_DIRECTIONS).indexOf(dir);
//		switch(dir)
//		{
//		case UP:
//			return 1;
//		case DOWN:
//			return 0;
//		case NORTH:
//			return 2;
//		case SOUTH:
//			return 3;
//		case EAST:
//			return 5;
//		case WEST:
//			return 4;
//		default:
//			return -1;
//		}
	}
	
	public static boolean isPlayerOnline(String username)
	{
		return Arrays.asList(MinecraftServer.getServer().getAllUsernames()).contains(username);
	}
}
