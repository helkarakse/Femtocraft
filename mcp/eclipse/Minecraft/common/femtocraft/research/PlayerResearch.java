package femtocraft.research;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerResearch {
	public String username;
	private HashMap<String, TechnologyStatus> techStatus;
	
	public PlayerResearch(String username)
	{
		this.username = username;
		techStatus = new HashMap<String, TechnologyStatus>();
	}
	
	//---------------------------------------------------------
	
	public TechnologyStatus addTechnology(String name)
	{
		return techStatus.put(name, new TechnologyStatus(name));
	}
	
	public TechnologyStatus getTechnology(String name)
	{
		return techStatus.get(name);
	}
	
	public TechnologyStatus removeTechnology(String name)
	{
		return techStatus.remove(name);
	}
	
	public boolean canAddTechnology(Technology tech)
	{
		if(tech.prerequisites == null) return true;
		
		for(Technology prereq : tech.prerequisites)
		{
			TechnologyStatus ts = techStatus.get(prereq.name);
			if(ts == null) return false;
			if(ts.researched == false) return false;
		}
		
		return true;
	}
	
	//---------------------------------------------------------
	
	public void saveToNBTTagCompound(NBTTagCompound compound)
	{
		NBTTagList list = new NBTTagList();
		
		for(TechnologyStatus status : techStatus.values())
		{
			NBTTagCompound cs = new NBTTagCompound();
			cs.setString("username", username);
			
			NBTTagCompound data = new NBTTagCompound();
			status.saveToNBTTagCompound(data);
			
			compound.setTag("data", data);
			list.appendTag(cs);
		}
		
		compound.setTag("techMap", list);
	}
	
	public void loadFromNBTTagCompound(NBTTagCompound compound)
	{
		NBTTagList list = compound.getTagList("techMap");
		
		for(int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound cs = (NBTTagCompound) list.tagAt(i);
			String username = cs.getString("username");
			
			NBTTagCompound data = (NBTTagCompound) cs.getTag("data");
			TechnologyStatus status = new TechnologyStatus(username);
			status.loadFromNBTTagCompound(data);
			
			techStatus.put(username, status);
		}
	}
}
