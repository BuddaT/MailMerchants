package net.buddat.wumods.merchants;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Properties;

import org.gotti.wurmunlimited.modsupport.questions.ModQuestion;
import org.gotti.wurmunlimited.modsupport.questions.ModQuestions;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.Trade;
import com.wurmonline.server.questions.Question;
import com.wurmonline.server.utils.BMLBuilder;
import com.wurmonline.server.utils.BMLBuilder.TextType;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;

public class MailMerchantQuestion implements ModQuestion
{
	
	private static final int QUESTION_ID = 2001;
	
	private ArrayList<Creature> merchants = new ArrayList<Creature>();

	protected MailMerchantQuestion(Creature aResponder, Item aTarget)
	{
		buildFilteredMerchantList();
		ModQuestions.createQuestion(aResponder, "Merchants by Mail", "Select a Merchant", aTarget.getWurmId(), this).sendQuestion();
	}
	
	@Override
	public int getType()
	{
		return QUESTION_ID;
	}

	@Override
	public void answer(Question q, Properties answers)
	{
		boolean clickedTrade = Boolean.parseBoolean(answers.getProperty("trade"));
		if (clickedTrade)
		{
			String merchantSelect = answers.getProperty("merchantSel");
			int merchantId = Integer.parseInt(merchantSelect.substring(8));
			if (merchants.size() > merchantId)
			{
				Creature targetMerchant = merchants.get(merchantId);
				if (targetMerchant.isTrading())
					q.getResponder().getCommunicator().sendNormalServerMessage(targetMerchant.getName() + " is already trading with someone.");
				else
				{
					final Trade trade = new Trade(q.getResponder(), targetMerchant);
					
					q.getResponder().setTrade(trade);
					targetMerchant.setTrade(trade);
			        q.getResponder().getCommunicator().sendStartTrading(targetMerchant);
			        targetMerchant.addItemsToTrade();
				}
			}
		}
	}

	@Override
	public void sendQuestion(Question q)
	{
		BMLBuilder bml = BMLBuilder.createBMLBorderPanel(
				BMLBuilder.createCenteredNode(BMLBuilder.createHorizArrayNode(false)
						.addLabel("Select a merchant from the list below and click trade to open their trading window.", null, TextType.ITALIC, Color.RED.darker())),
				BMLBuilder.createHorizArrayNode(false)
                		.addPassthrough("id", Integer.toString(q.getId()))
                		.addLabel(""), 
				BMLBuilder.createCenteredNode(BMLBuilder.createVertArrayNode(false)
						.addString(buildMerchantListBML())), 
				null, 
				BMLBuilder.createCenteredNode(BMLBuilder.createHorizArrayNode(false)
                        .addButton("close", "Cancel", 80, 20, true)
                        .addLabel("", null, null, null, 20, 20)
                        .addButton("trade", "Trade", 80, 20, true)));
		
		int height = 100 + merchants.size() * 20;
		q.getResponder().getCommunicator().sendBml(750, height, true, false, bml.toString(), 200, 200, 200, q.getTitle());
	}
	
	private String buildMerchantListBML()
	{
		BMLBuilder tableBML = BMLBuilder.createTable(4).addText("", null, null, null, 20, 20)
				.addText("Merchant Name", null, TextType.BOLD, Color.LIGHT_GRAY, 150, 20)
				.addText("Village", null, TextType.BOLD, Color.LIGHT_GRAY, 150, 20)
				.addText("Item Preview", null, TextType.BOLD, Color.LIGHT_GRAY, 400, 20);
		
		for (int id = 0; id < merchants.size(); id++)
		{
			Creature c = merchants.get(id);
			String previewText = "";
			String villageName = "None";
			if (c.getCurrentVillage() != null)
				villageName = c.getCurrentVillage().getName();
			
			ArrayList<Integer> addedTypes = new ArrayList<Integer>();
			for (Item i : c.getInventory().getItems())
			{
				if (addedTypes.contains(i.getTemplateId()))
					continue;
				if (i.isCoin())
					continue;
				if (previewText.isEmpty() == false)
					previewText += ", ";
				previewText += i.getTemplate().getName();
				addedTypes.add(i.getTemplateId());
			}
			
			tableBML.addRadioButton("merchant" + id, "merchantSel", "", id == 0)
					.addLabel(c.getName().substring(0, Math.min(c.getName().length(), 25)), c.getName(), TextType.BOLD, null)
					.addLabel(villageName.substring(0, Math.min(villageName.length(), 25)), villageName, null, null)
					.addText(previewText.length() > 75 ? previewText.substring(0, 75).trim() + "..." : previewText, previewText, null, Color.LIGHT_GRAY, 300, 20);
		}
		
		return tableBML.toString();
	}
	
	private void buildFilteredMerchantList()
	{
		ArrayList<Creature> allMerchants = MailMerchantHooks.getMerchantList();
		for (Creature c : allMerchants)
		{
			Village v = c.getCurrentVillage();
			if (v != null && v.isPermanent)
			{
				merchants.add(c);
				continue;
			}
			
			VolaTile t = Zones.getTileOrNull(c.getTileX(), c.getTileY(), c.isOnSurface());
			if (t == null || t.getItems().length == 0)
				continue;
			for (Item i : t.getItems())
				if (i.isMailBox() && i.getSpellCourierBonus() > 0)
				{
					merchants.add(c);
					break;
				}
		}
	}

}
