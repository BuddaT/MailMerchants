package net.buddat.wumods.merchants;

import java.util.Arrays;
import java.util.List;

import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public class MailMerchantAction implements ModAction, BehaviourProvider, ActionPerformer
{

	private final short actionId;
	
	private final ActionEntry actionEntry;
	
	public MailMerchantAction()
	{
		actionId = (short) ModActions.getNextActionId();
		actionEntry = new ActionEntryBuilder(actionId, "View Merchants", "viewing merchants", new int[]
                { 0 /*ACTION_TYPE_QUICK*/, 37 /*ACTION_TYPE_NEVER_USE_ACTIVE_ITEM*/, 44 /*ACTION_TYPE_SAME_BRIDGE*/ }).build();
		ModActions.registerAction(actionEntry);
	}
	
	@Override
	public short getActionId()
	{
		return actionId;
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target)
	{
		if (performer.isPlayer() && target != null && target.isMailBox() && target.getSpellCourierBonus() > 0)
			return Arrays.asList(actionEntry);

		return null;
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target)
	{
		return getBehavioursFor(performer, target);
	}
	
	@Override
	public boolean action(Action act, Creature performer, Item target, short num, float counter)
	{
		new MailMerchantQuestion(performer, target);
		return true;
	}

}
