package femtocraft;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import femtocraft.blocks.BlockFemtoStone;
import femtocraft.blocks.BlockMicroStone;
import femtocraft.blocks.BlockNanoStone;
import femtocraft.blocks.BlockUnidentifiedAlloy;
import femtocraft.consumables.processing.blocks.BlockCuttingBoard;
import femtocraft.core.items.*;
import femtocraft.core.items.decomposition.*;
import femtocraft.core.liquids.BlockFluidMass;
import femtocraft.core.liquids.FluidMass;
import femtocraft.core.ore.*;
import femtocraft.consumables.items.ItemTomato;
import femtocraft.consumables.farming.items.ItemSeedTomato;
import femtocraft.industry.tiles.TileEntityVacuumTube;
import femtocraft.industry.blocks.BlockMicroDeconstructor;
import femtocraft.industry.blocks.BlockMicroFurnace;
import femtocraft.industry.blocks.BlockMicroReconstructor;
import femtocraft.industry.blocks.BlockVacuumTube;
import femtocraft.industry.items.ItemPaperSchematic;
import femtocraft.managers.ManagerRecipe;
import femtocraft.managers.research.ManagerResearch;
import femtocraft.player.PropertiesNanite;
import femtocraft.power.blocks.*;
import femtocraft.power.items.ItemBlockMicroCube;
import femtocraft.power.items.ItemBlockSpoolGold;
import femtocraft.proxy.ProxyClient;
import femtocraft.proxy.ProxyCommon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.logging.Logger;

@Mod(modid = Femtocraft.ID, name = "Femtocraft", version = Femtocraft.VERSION)
@NetworkMod(channels = {Femtocraft.ID,
        PropertiesNanite.PACKET_CHANNEL, TileEntityVacuumTube.packetChannel}, packetHandler = FemtocraftPacketHandler.class, clientSideRequired = true, serverSideRequired = true)
public class Femtocraft {
    public static final String ID = "Femtocraft";
    public static final String VERSION = "0.1.0";

    @Instance(ID)
    public static Femtocraft instance;

    @SidedProxy(clientSide = "femtocraft.proxy.ProxyClient", serverSide = "femtocraft.proxy.ProxyCommon")
    public static ProxyCommon proxy;

    public static CreativeTabs femtocraftTab = new FemtocraftCreativeTab(
            "Femtocraft");

    public static Logger logger;

    public static ManagerRecipe recipeManager;
    public static ManagerResearch researchManager;

    // blocks
    public static Block oreTitanium;
    public static Block orePlatinum;
    public static Block oreThorium;
    public static Block oreFarenite;
    public static Block oreMalenite;
    public static Block microStone;
    public static Block nanoStone;
    public static Block femtoStone;
    public static Block unidentifiedAlloy;
    public static BlockCable BlockCable;
    public static Block FemtopowerGeneratorTest;
    public static Block FemtopowerConsumerTest;
    public static Block FemtocraftMicroFurnaceUnlit;
    public static Block FemtocraftMicroFurnaceLit;
    public static Block FemtocraftMicroDeconstructor;
    public static Block FemtocraftMicroReconstructor;
    public static Block FemtopowerMicroCube;
    public static Block FemtocraftVacuumTube;
    public static Block FemtopowerMicroChargingBase;
    public static Block FemtopowerMicroChargingCoil;

    // liquids
    public static Fluid mass;
    public static BlockFluidMass mass_block;

    // items
    public static Item ingotTitanium;
    public static Item ingotPlatinum;
    public static Item ingotThorium;
    public static Item ingotFarenite;
    public static Item ingotMalenite;

    public static Item deconstructedIron;
    public static Item deconstructedGold;
    public static Item deconstructedTitanium;
    public static Item deconstructedThorium;
    public static Item deconstructedPlatinum;

    public static Item conductivePowder;
    public static Item board;
    public static Item primedBoard;
    public static Item dopedBoard;
    public static Item microCircuitBoard;

    public static Item spool;
    public static Item spoolGold;

    public static Item paperSchematic;

    public static Item microInterfaceDevice;
    public static Item nanoInterfaceDevice;
    public static Item femtoInterfaceDevice;

    // Decomp items
    // Femto
    public static Item Cubit;
    public static Item Rectangulon;
    public static Item Planeoid;
    // Nano
    public static Item Crystallite;
    public static Item Mineralite;
    public static Item Metallite;
    public static Item Faunite;
    public static Item Electrite;
    public static Item Florite;
    // Micro
    public static Item MicroCrystal;
    public static Item ProteinChain;
    public static Item NerveCluster;
    public static Item ConductiveAlloy;
    public static Item MetalComposite;
    public static Item FibrousStrand;
    public static Item MineralLattice;
    public static Item FungalSpores;
    public static Item IonicChunk;
    public static Item ReplicatingMaterial;
    public static Item SpinyFilament;
    public static Item HardenedBulb;
    public static Item MorphicChannel;
    public static Item SynthesizedFiber;
    public static Item organometallicPlate;

    // Produce
    public static Item tomatoSeed;
    public static Item tomato;

    // Cooking
    public static Block cuttingBoard;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = Logger.getLogger(ID);
        logger.setParent(FMLLog.getLogger());

        Configuration config = new Configuration(
                event.getSuggestedConfigurationFile());
        FemtocraftConfigs.load(config);

        Femtocraft.proxy.registerTileEntities();
        Femtocraft.proxy.registerRendering();
        Femtocraft.proxy.registerBlockRenderers();
        Femtocraft.proxy.registerTickHandlers();

        NetworkRegistry.instance().registerGuiHandler(this,
                new GuiHandlerFemtocraft());
        MinecraftForge.EVENT_BUS.register(new FemtocraftEventHookContainer());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerRendering();

        if (FemtocraftConfigs.worldGen) {
            GameRegistry.registerWorldGenerator(new FemtocraftWorldGenerator());
        }

        // Change the creative tab name
        LanguageRegistry.instance().addStringLocalization(
                "itemGroup.Femtocraft", "en_US", "Femtocraft");

        // item = new Item(Configs.itemId);

        // blocks

        oreTitanium = new BlockOreTitanium(FemtocraftConfigs.oreTitaniumID);
        MinecraftForge.setBlockHarvestLevel(oreTitanium, "pickaxe", 2);
        GameRegistry.registerBlock(oreTitanium, "oreTitanium");
        LanguageRegistry.addName(oreTitanium, "Titanium Ore");
        if (FemtocraftConfigs.registerTitaniumOreInOreDictionary)
            OreDictionary
                    .registerOre("oreTitanium", new ItemStack(oreTitanium));

        orePlatinum = new BlockOrePlatinum(FemtocraftConfigs.orePlatinumID);
        MinecraftForge.setBlockHarvestLevel(orePlatinum, "pickaxe", 2);
        GameRegistry.registerBlock(orePlatinum, "orePlatinum");
        LanguageRegistry.addName(orePlatinum, "Platinum Ore");
        if (FemtocraftConfigs.registerPlatinumOreInOreDictionary)
            OreDictionary
                    .registerOre("orePlatinum", new ItemStack(orePlatinum));

        oreThorium = new BlockOreThorium(FemtocraftConfigs.oreThoriumID);
        MinecraftForge.setBlockHarvestLevel(oreThorium, "pickaxe", 2);
        GameRegistry.registerBlock(oreThorium, "oreThorium");
        LanguageRegistry.addName(oreThorium, "Thorium Ore");
        if (FemtocraftConfigs.registerThoriumOreInOreDictionary)
            OreDictionary.registerOre("oreThorium", new ItemStack(oreThorium));

        oreFarenite = new BlockOreFarenite(FemtocraftConfigs.oreFareniteID);
        MinecraftForge.setBlockHarvestLevel(oreFarenite, "pickaxe", 2);
        GameRegistry.registerBlock(oreFarenite, "oreFarenite");
        LanguageRegistry.addName(oreFarenite, "Farenite Ore");
        OreDictionary.registerOre("oreFarenite", new ItemStack(oreFarenite));

        oreMalenite = new BlockOreMalenite(FemtocraftConfigs.oreMaleniteID);
        MinecraftForge.setBlockHarvestLevel(oreFarenite, "pickaxe", 3);
        GameRegistry.registerBlock(oreMalenite, "oreMalenite");
        LanguageRegistry.addName(oreMalenite, "Malenite Ore");
        OreDictionary.registerOre("oreMalenite", new ItemStack(oreMalenite));

        nanoStone = new BlockNanoStone(FemtocraftConfigs.nanoStoneID);
        GameRegistry.registerBlock(nanoStone, "nanoStone");
        LanguageRegistry.addName(nanoStone, "Nanostone");

        microStone = new BlockMicroStone(FemtocraftConfigs.microStoneID);
        GameRegistry.registerBlock(microStone, "microStone");
        LanguageRegistry.addName(microStone, "Microstone");

        femtoStone = new BlockFemtoStone(FemtocraftConfigs.femtoStoneID);
        GameRegistry.registerBlock(femtoStone, "femtoStone");
        LanguageRegistry.addName(femtoStone, "Femtostone");

        unidentifiedAlloy = new BlockUnidentifiedAlloy(
                FemtocraftConfigs.unidentifiedAlloyID);
        GameRegistry.registerBlock(unidentifiedAlloy, "unidentifiedAlloy");
        LanguageRegistry.addName(unidentifiedAlloy, "Unidentified Alloy");

        BlockCable = new BlockCable(
                FemtocraftConfigs.FemtopowerCableID, Material.rock);
        GameRegistry.registerBlock(BlockCable, "FemtopowerCable");
        LanguageRegistry.addName(BlockCable, "Femtopower Cable");

        FemtopowerGeneratorTest = new BlockGenerator(
                FemtocraftConfigs.FemtopowerGeneratorTestID, Material.rock)
                .setUnlocalizedName("BlockGenerator").setHardness(3.5f)
                .setStepSound(Block.soundStoneFootstep);
        GameRegistry.registerBlock(FemtopowerGeneratorTest,
                "BlockGenerator");
        LanguageRegistry.addName(FemtopowerGeneratorTest,
                "Generator");

        FemtopowerConsumerTest = new BlockConsumer(
                FemtocraftConfigs.FemtopowerConsumerTestBlockID, Material.rock)
                .setUnlocalizedName("BlockConsumer").setHardness(3.5f)
                .setStepSound(Block.soundStoneFootstep);
        GameRegistry
                .registerBlock(FemtopowerConsumerTest, "BlockConsumer");
        LanguageRegistry.addName(FemtopowerConsumerTest, "Consumer");

        FemtocraftMicroFurnaceUnlit = new BlockMicroFurnace(
                FemtocraftConfigs.FemtocraftMicroFurnaceUnlitID, false);
        GameRegistry.registerBlock(FemtocraftMicroFurnaceUnlit,
                "BlockMicroFurnace");
        LanguageRegistry.addName(FemtocraftMicroFurnaceUnlit, "Micro-Furnace");

        FemtocraftMicroFurnaceLit = new BlockMicroFurnace(
                FemtocraftConfigs.FemtocraftMicroFurnaceLitID, true);

        FemtocraftMicroDeconstructor = new BlockMicroDeconstructor(
                FemtocraftConfigs.FemtocraftMicroDeconstructorID);
        GameRegistry.registerBlock(FemtocraftMicroDeconstructor,
                "BlockMicroDeconstructor");
        LanguageRegistry.addName(FemtocraftMicroDeconstructor,
                "Microtech Deconstructor");

        FemtocraftMicroReconstructor = new BlockMicroReconstructor(
                FemtocraftConfigs.FemtocraftMicroReconstructorID);
        GameRegistry.registerBlock(FemtocraftMicroReconstructor,
                "BlockMicroReconstructor");
        LanguageRegistry.addName(FemtocraftMicroReconstructor,
                "Microtech Reconstructor");

        FemtopowerMicroCube = new BlockMicroCube(
                FemtocraftConfigs.FemtopowerMicroCubeID);
        GameRegistry.registerBlock(FemtopowerMicroCube,
                ItemBlockMicroCube.class, "BlockMicroCube");
        LanguageRegistry.addName(FemtopowerMicroCube, "Micro-Cube");

        FemtocraftVacuumTube = new BlockVacuumTube(
                FemtocraftConfigs.FemtocraftVacuumTubeID);
        GameRegistry
                .registerBlock(FemtocraftVacuumTube, "BlockVacuumTube");
        LanguageRegistry.addName(FemtocraftVacuumTube, "Vacuum Tube");

        FemtopowerMicroChargingBase = new BlockBaseMicroCharging(
                FemtocraftConfigs.FemtopowerMicroChargingBaseID);
        GameRegistry.registerBlock(FemtopowerMicroChargingBase,
                "BlockBaseMicroCharging");
        LanguageRegistry.addName(FemtopowerMicroChargingBase,
                "Electrostatic Charging Base");

        FemtopowerMicroChargingCoil = new BlockCoilMicroCharging(
                FemtocraftConfigs.FemtopowerMicroChargingCoilID);
        GameRegistry.registerBlock(FemtopowerMicroChargingCoil,
                "BlockCoilMicroCharging");
        LanguageRegistry.addName(FemtopowerMicroChargingCoil,
                "Electrostatic Charging Coil");

        // Liquids
        mass = new FluidMass();
        FluidRegistry.registerFluid(mass);

        mass_block = new BlockFluidMass(FemtocraftConfigs.FemtocraftMassBlock);
        GameRegistry.registerBlock(mass_block, "Mass");
        LanguageRegistry.addName(mass_block, "Mass");

        // items
        ingotTitanium = new ItemIngotTitanium(FemtocraftConfigs.ingotTitaniumID)
                .setUnlocalizedName("ingotTitanium");
        LanguageRegistry.addName(ingotTitanium, "Titanium Ingot");
        if (FemtocraftConfigs.registerTitaniumIngotInOreDictionary)
            OreDictionary.registerOre("ingotTitanium", new ItemStack(
                    ingotTitanium));

        ingotPlatinum = new ItemIngotPlatinum(FemtocraftConfigs.ingotPlatinumID)
                .setUnlocalizedName("ingotPlatinum");
        LanguageRegistry.addName(ingotPlatinum, "Platinum Ingot");
        if (FemtocraftConfigs.registerPlatinumIngotInOreDictionary)
            OreDictionary.registerOre("ingotPlatinum", new ItemStack(
                    ingotPlatinum));

        ingotThorium = new ItemIngotThorium(FemtocraftConfigs.ingotThoriumID)
                .setUnlocalizedName("ingotThorium");
        LanguageRegistry.addName(ingotThorium, "Thorium Ingot");
        if (FemtocraftConfigs.registerThoriumIngotInOreDictionary)
            OreDictionary.registerOre("ingotThorium", new ItemStack(
                    ingotThorium));

        ingotFarenite = new ItemIngotFarenite(FemtocraftConfigs.ingotFareniteID)
                .setUnlocalizedName("ingotFarenite");
        LanguageRegistry.addName(ingotFarenite, "Farenite Ingot");
        OreDictionary
                .registerOre("ingotFarenite", new ItemStack(ingotFarenite));

        ingotMalenite = new ItemIngotMalenite(FemtocraftConfigs.ingotMaleniteID)
                .setUnlocalizedName("ingotMalenite");
        LanguageRegistry.addName(ingotMalenite, "Malenite Ingot");
        OreDictionary
                .registerOre("ingotMalenite", new ItemStack(ingotMalenite));

        deconstructedIron = new ItemDeconstructedIron(
                FemtocraftConfigs.deconstructedIronID)
                .setUnlocalizedName("deconstructedIron");
        LanguageRegistry.addName(deconstructedIron, "Deconstructed Iron");
        OreDictionary.registerOre("dustIron", new ItemStack(deconstructedIron));

        deconstructedGold = new ItemDeconstructedGold(
                FemtocraftConfigs.deconstructedGoldID)
                .setUnlocalizedName("deconstructedGold");
        LanguageRegistry.addName(deconstructedGold, "Deconstructed Gold");
        OreDictionary.registerOre("dustGold", new ItemStack(deconstructedGold));

        deconstructedTitanium = new ItemDeconstructedTitanium(
                FemtocraftConfigs.deconstructedTitaniumID)
                .setUnlocalizedName("deconstructedTitanium");
        LanguageRegistry.addName(deconstructedTitanium,
                "Deconstructed Titanium");
        if (FemtocraftConfigs.registerTitaniumDustInOreDictionary)
            OreDictionary.registerOre("dustTitanium", new ItemStack(
                    deconstructedTitanium));

        deconstructedThorium = new ItemDeconstructedThorium(
                FemtocraftConfigs.deconstructedThoriumID)
                .setUnlocalizedName("deconstructedThorium");
        LanguageRegistry.addName(deconstructedThorium, "Deconstructed Thorium");
        if (FemtocraftConfigs.registerThoriumDustInOreDictionary)
            OreDictionary.registerOre("dustThorium", new ItemStack(
                    deconstructedThorium));

        deconstructedPlatinum = new ItemDeconstructedPlatinum(
                FemtocraftConfigs.deconstructedPlatinumID)
                .setUnlocalizedName("deconstructedPlatinum");
        LanguageRegistry.addName(deconstructedPlatinum,
                "Deconstructed Platinum");
        if (FemtocraftConfigs.registerPlatinumDustInOreDictionary)
            OreDictionary.registerOre("dustPlatinum", new ItemStack(
                    deconstructedPlatinum));

        //

        conductivePowder = new ItemConductivePowder(
                FemtocraftConfigs.conductivePowderID)
                .setUnlocalizedName("conductivePowder");
        LanguageRegistry.addName(conductivePowder, "Conductive Powder");

        board = new ItemBoard(FemtocraftConfigs.boardID)
                .setUnlocalizedName("board");
        LanguageRegistry.addName(board, "Board");

        primedBoard = new ItemPrimedBoard(FemtocraftConfigs.primedBoardID)
                .setUnlocalizedName("primedBoard");
        LanguageRegistry.addName(primedBoard, "Primed Board");

        dopedBoard = new ItemDopedBoard(FemtocraftConfigs.dopedBoardID)
                .setUnlocalizedName("dopedBoard");
        LanguageRegistry.addName(dopedBoard, "Doped Board");

        microCircuitBoard = new ItemMicroCircuitBoard(
                FemtocraftConfigs.microCircuitID)
                .setUnlocalizedName("microCircuitBoard");
        LanguageRegistry.addName(microCircuitBoard, "Micro Circuit Board");

        spool = new ItemSpool(FemtocraftConfigs.spoolID)
                .setUnlocalizedName("spool");
        LanguageRegistry.addName(spool, "Spool");

        spoolGold = new ItemBlockSpoolGold(FemtocraftConfigs.spoolGoldID)
                .setUnlocalizedName("spoolGold");
        LanguageRegistry.addName(spoolGold, "Gold Wire Spool");

        // Schematics

        paperSchematic = new ItemPaperSchematic(FemtocraftConfigs.paperSchematicID);
        LanguageRegistry.addName(paperSchematic, "Paper Schematic");

        microInterfaceDevice = new ItemMicroInterfaceDevice(
                FemtocraftConfigs.microInterfaceDeviceID)
                .setUnlocalizedName("microInterfaceDevice");
        LanguageRegistry.addName(microInterfaceDevice, "MicroInterface Device");

        nanoInterfaceDevice = new ItemNanoInterfaceDevice(
                FemtocraftConfigs.nanoInterfaceDeviceID)
                .setUnlocalizedName("nanoInterfaceDevice");
        LanguageRegistry.addName(nanoInterfaceDevice, "NanoInterface Device");

        femtoInterfaceDevice = new ItemFemtoInterfaceDevice(
                FemtocraftConfigs.femtoInterfaceDeviceID)
                .setUnlocalizedName("femtoInterfaceDevice");
        LanguageRegistry.addName(femtoInterfaceDevice, "FemtoInterface Device");

        // Decomp
        // Femto
        Cubit = new ItemCubit(FemtocraftConfigs.CubitID)
                .setUnlocalizedName("Cubit");
        LanguageRegistry.addName(Cubit, "Cubit");
        GameRegistry.registerItem(Cubit, "Cubit");

        Rectangulon = new ItemRectangulon(FemtocraftConfigs.RectangulonID)
                .setUnlocalizedName("Rectangulon");
        LanguageRegistry.addName(Rectangulon, "Rectangulon");
        GameRegistry.registerItem(Rectangulon, "Rectangulon");

        Planeoid = new ItemPlaneoid(FemtocraftConfigs.PlaneoidID)
                .setUnlocalizedName("Planeoid");
        LanguageRegistry.addName(Planeoid, "Planeoid");
        GameRegistry.registerItem(Planeoid, "Planeoid");

        // Nano
        Crystallite = new ItemCrystallite(FemtocraftConfigs.CrystalliteID)
                .setUnlocalizedName("Crystallite");
        LanguageRegistry.addName(Crystallite, "Crystallite");
        GameRegistry.registerItem(Crystallite, "Crystallite");

        Mineralite = new ItemMineralite(FemtocraftConfigs.MineraliteID)
                .setUnlocalizedName("Mineralite");
        LanguageRegistry.addName(Mineralite, "Mineralite");
        GameRegistry.registerItem(Mineralite, "Mineralite");

        Metallite = new ItemMetallite(FemtocraftConfigs.MetalliteID)
                .setUnlocalizedName("Metallite");
        LanguageRegistry.addName(Metallite, "Metallite");
        GameRegistry.registerItem(Metallite, "Metallite");

        Faunite = new ItemFaunite(FemtocraftConfigs.FauniteID)
                .setUnlocalizedName("Faunite");
        LanguageRegistry.addName(Faunite, "Faunite");
        GameRegistry.registerItem(Faunite, "Faunite");

        Electrite = new ItemElectrite(FemtocraftConfigs.ElectriteID)
                .setUnlocalizedName("Electrite");
        LanguageRegistry.addName(Electrite, "Electrite");
        GameRegistry.registerItem(Electrite, "Electrite");

        Florite = new ItemFlorite(FemtocraftConfigs.FloriteID)
                .setUnlocalizedName("Florite");
        LanguageRegistry.addName(Florite, "Florite");
        GameRegistry.registerItem(Florite, "Florite");

        // Micro
        MicroCrystal = new ItemMicroCrystal(FemtocraftConfigs.MicroCrystalID)
                .setUnlocalizedName("MicroCrystal");
        LanguageRegistry.addName(MicroCrystal, "Micro Crystal");
        GameRegistry.registerItem(MicroCrystal, "Micro Crystal");

        ProteinChain = new ItemProteinChain(FemtocraftConfigs.ProteinChainID)
                .setUnlocalizedName("ProteinChain");
        LanguageRegistry.addName(ProteinChain, "Protein Chain");
        GameRegistry.registerItem(ProteinChain, "Protein Chain");

        NerveCluster = new ItemNerveCluster(FemtocraftConfigs.NerveClusterID)
                .setUnlocalizedName("NerveCluster");
        LanguageRegistry.addName(NerveCluster, "Nerve Cluster");
        GameRegistry.registerItem(NerveCluster, "Nerve Cluster");

        ConductiveAlloy = new ItemConductiveAlloy(
                FemtocraftConfigs.ConductiveAlloyID)
                .setUnlocalizedName("ConductiveAlloy");
        LanguageRegistry.addName(ConductiveAlloy, "Conductive Alloy");
        GameRegistry.registerItem(ConductiveAlloy, "Conductive Alloy");

        MetalComposite = new ItemMorphicChannel(FemtocraftConfigs.MetalCompositeID)
                .setUnlocalizedName("MetalComposite");
        LanguageRegistry.addName(MetalComposite, "Metal Composite");
        GameRegistry.registerItem(MetalComposite, "Metal Composite");

        FibrousStrand = new ItemFibrousStrand(FemtocraftConfigs.FibrousStrandID)
                .setUnlocalizedName("FibrousStrand");
        LanguageRegistry.addName(FibrousStrand, "Fibrous Strand");
        GameRegistry.registerItem(FibrousStrand, "Fibrous Strand");

        MineralLattice = new ItemMorphicChannel(FemtocraftConfigs.MineralLatticeID)
                .setUnlocalizedName("MineralLattice");
        LanguageRegistry.addName(MineralLattice, "Mineral Lattice");
        GameRegistry.registerItem(MineralLattice, "Mineral Lattice");

        FungalSpores = new ItemFungalSpores(FemtocraftConfigs.FungalSporesID)
                .setUnlocalizedName("FungalSpores");
        LanguageRegistry.addName(FungalSpores, "Fungal Spores");
        GameRegistry.registerItem(FungalSpores, "Fungal Spores");

        IonicChunk = new ItemIonicChunk(FemtocraftConfigs.IonicChunkID)
                .setUnlocalizedName("IonicChunk");
        LanguageRegistry.addName(IonicChunk, "Ionic Chunk");
        GameRegistry.registerItem(IonicChunk, "Ionic Chunk");

        ReplicatingMaterial = new ItemReplicatingMaterial(
                FemtocraftConfigs.ReplicatingMaterialID)
                .setUnlocalizedName("ReplicatingMaterial");
        LanguageRegistry.addName(ReplicatingMaterial, "Replicating Material");
        GameRegistry.registerItem(ReplicatingMaterial, "Replicating Material");

        SpinyFilament = new ItemSpinyFilament(FemtocraftConfigs.SpinyFilamentID)
                .setUnlocalizedName("SpinyFilament");
        LanguageRegistry.addName(SpinyFilament, "Spiny Filament");
        GameRegistry.registerItem(SpinyFilament, "Spiny Filament");

        HardenedBulb = new ItemHardenedBulb(FemtocraftConfigs.HardenedBulbID)
                .setUnlocalizedName("HardenedBulb");
        LanguageRegistry.addName(HardenedBulb, "Hardened Bulb");
        GameRegistry.registerItem(HardenedBulb, "Hardened Bulb");

        MorphicChannel = new ItemMorphicChannel(FemtocraftConfigs.MorphicChannelID)
                .setUnlocalizedName("MorphicChannel");
        LanguageRegistry.addName(MorphicChannel, "Morphic Channel");
        GameRegistry.registerItem(MorphicChannel, "Morphic Channel");

        SynthesizedFiber = new ItemSynthesizedFiber(
                FemtocraftConfigs.SynthesizedFiberID)
                .setUnlocalizedName("SynthesizedFiber");
        LanguageRegistry.addName(SynthesizedFiber, "Synthesized Fiber");
        GameRegistry.registerItem(SynthesizedFiber, "Synthesized Fiber");

        organometallicPlate = new ItemOrganometallicPlate(
                FemtocraftConfigs.OrganometallicPlateID)
                .setUnlocalizedName("organometallicPlate");
        LanguageRegistry.addName(organometallicPlate, "Organometallic Plate");
        GameRegistry.registerItem(organometallicPlate, "Organometallic Plate");

        // Produce
        tomatoSeed = new ItemSeedTomato(FemtocraftConfigs.tomatoSeedID)
                .setUnlocalizedName("tomatoSeed");
        LanguageRegistry.addName(tomatoSeed, "Tomato Seeds");
        GameRegistry.registerItem(tomatoSeed, "Tomato Seeds");

        tomato = new ItemTomato(FemtocraftConfigs.tomatoID)
                .setUnlocalizedName("tomato");
        LanguageRegistry.addName(tomato, "Tomato");
        GameRegistry.registerItem(tomato, "Tomato");

        // Cooking
        cuttingBoard = new BlockCuttingBoard(FemtocraftConfigs.cuttingBoardID)
                .setUnlocalizedName("cuttingBoard");
        LanguageRegistry.addName(cuttingBoard, "Cutting Board");
        GameRegistry.registerBlock(cuttingBoard, "Cutting Board");

        registerRecipes();
        ProxyClient.setCustomRenderers();
        // GameRegistry.registerTileEntity(TileEntity.class, "myTile");
        // GameRegistry.addRecipe(new ItemStack(itemId), new Object[] {});
        // EntityRegistry.registerModEntity(entity.class, "myEntity", 0, this,
        // 32, 10, true)
        recipeManager = new ManagerRecipe();
        researchManager = new ManagerResearch();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ManagerRecipe.assemblyRecipes.registerDefaultRecipes();
    }

    private void registerRecipes() {
        GameRegistry.addSmelting(oreTitanium.blockID, new ItemStack(
                ingotTitanium), 0.1f);
        GameRegistry.addSmelting(orePlatinum.blockID, new ItemStack(
                ingotPlatinum), 0.1f);
        GameRegistry.addSmelting(oreThorium.blockID,
                new ItemStack(ingotThorium), 0.1f);
        GameRegistry.addSmelting(deconstructedIron.itemID, new ItemStack(
                Item.ingotIron), 0.1f);
        GameRegistry.addSmelting(deconstructedGold.itemID, new ItemStack(
                Item.ingotGold), 0.1f);
        GameRegistry.addSmelting(deconstructedTitanium.itemID, new ItemStack(
                ingotTitanium), 0.1f);
        GameRegistry.addSmelting(deconstructedThorium.itemID, new ItemStack(
                ingotThorium), 0.1f);
        GameRegistry.addSmelting(deconstructedPlatinum.itemID, new ItemStack(
                ingotPlatinum), 0.1f);

        GameRegistry.addSmelting(primedBoard.itemID, new ItemStack(dopedBoard),
                0.1f);

        GameRegistry.addShapedRecipe(new ItemStack(primedBoard), "#", "$", '#', conductivePowder, '$', board);
        GameRegistry.addShapedRecipe(new ItemStack(paperSchematic, 3),
                "###", "###", "###", '#', Item.paper);
        GameRegistry.addShapedRecipe(new ItemStack(board), "###", '#', Item.stick);
        GameRegistry.addShapedRecipe(new ItemStack(microCircuitBoard),
                "#", "$", '#', spoolGold, '$', dopedBoard);

        CraftingManager
                .getInstance()
                .getRecipeList()
                .add(new ShapedOreRecipe(new ItemStack(spool),
                        "# #", "#-#", "# #", '#', "plankWood",
                        '-', "stickWood"));

        GameRegistry.addShapedRecipe(new ItemStack(spoolGold, 8),
                "###", "#-#", "###", '#', Item.ingotGold, '-',
                Femtocraft.spool);

        GameRegistry.addShapelessRecipe(new ItemStack(conductivePowder, 2),
                new ItemStack(ingotFarenite),
                new ItemStack(Item.dyePowder, 1, 4));

    }

}
