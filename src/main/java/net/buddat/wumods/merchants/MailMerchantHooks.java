package net.buddat.wumods.merchants;

import java.util.ArrayList;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;

public class MailMerchantHooks
{
	
	private static ArrayList<Creature> merchantList = new ArrayList<Creature>();
	
	public static void addCreature(long cid)
	{
		Creature c = Creatures.getInstance().getCreatureOrNull(cid);
		if (c != null && c.isSalesman() && c.getName().startsWith("Merchant"))
			merchantList.add(c);
	}
	
	public static void deleteCreature(Creature c)
	{
		if (c.isSalesman() && merchantList.contains(c))
			merchantList.remove(c);
	}
	
	public static ArrayList<Creature> getMerchantList()
	{
		return merchantList;
	}
	
}
