/**
 * 
 */
package femtocraft.api;

import net.minecraftforge.common.ForgeDirection;
import femtocraft.managers.research.TechLevel;

/**
 * @author Itszuvalex
 * 
 */
public interface IFemtopowerBlockContainer {

	/**
	 * 
	 * @param level
	 *            TechLevel of power
	 * @param from
	 *            Direction power will be coming from
	 * @return True if can accept power of that level from the given direciton
	 */
	public boolean canAcceptPowerOfLevel(TechLevel level, ForgeDirection from);

	/**
	 * 
	 * @param to
	 *            Direction from this Container
	 * @return TechLevel of power this machine will give to the given direciton
	 */
	public TechLevel getTechLevel(ForgeDirection to);

	/**
	 * 
	 * @return Current storage amount of container
	 */
	public int getCurrentPower();

	/**
	 * 
	 * @return Max storage amount of container - used for percentage
	 *         approximations during charging
	 */
	public int getMaxPower();

	/**
	 * 
	 * @return Actual fill percentage - for things like damage values, etc.
	 */
	public float getFillPercentage();

	/**
	 * 
	 * @param from
	 *            Direction attempting to charge from.
	 * @return Fill percentage for purposes of charging - allows tanks and
	 *         whatnot to trick pipes into filling them I.E. return
	 *         getFillPercentage() < .25f ? getFillPercentage() : .25f;
	 */
	public float getFillPercentageForCharging(ForgeDirection from);

	/**
	 * 
	 * @param to
	 *            Direction attempting to output to
	 * @return Fill percentage for purposes of output - allows tanks and other
	 *         TileEntities to trick pipes into not pulling all of their power.
	 */
	public float getFillPercentageForOutput(ForgeDirection to);

	/**
	 * 
	 * @param from
	 *            Direction attempting to input power from
	 * @return True if container has room and can accept charging from direction
	 *         @from, false otherwise
	 */
	public boolean canCharge(ForgeDirection from);

	/**
	 * 
	 * @param from
	 *            Direction attempting to
	 * @return True if container can be connected to from a given direction
	 */
	public boolean canConnect(ForgeDirection from);

	/**
	 * 
	 * @param from
	 *            Direction charge is coming from.
	 * @param amount
	 *            Amount attempting to charge.
	 * @return Total amount of @amount used to fill internal tank.
	 */
	public int charge(ForgeDirection from, int amount);

	/**
	 * 
	 * @param amount
	 *            Amount of power to drain from internal storage
	 * @return True if all power was consumed, false otherwise. This anticipates
	 *         all or nothing behavior.
	 */
	public boolean consume(int amount);

}