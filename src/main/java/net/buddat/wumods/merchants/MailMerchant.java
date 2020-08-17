package net.buddat.wumods.merchants;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class MailMerchant implements WurmServerMod, PreInitable, ServerStartedListener
{

	private static Logger logger = Logger.getLogger(MailMerchant.class.getName());

	public static void logException(final String message, final Throwable t)
	{
		logger.log(Level.SEVERE, message, t);
	}

	@Override
	public void preInit()
	{
		ModActions.init();
		
		try
		{
			ClassPool classes = HookManager.getInstance().getClassPool();
			CtClass ctZone = classes.getCtClass("com.wurmonline.server.zones.Zone");

			ctZone.getMethod("addCreature", "(J)I")
					.insertAfter("net.buddat.wumods.merchants.MailMerchantHooks.addCreature($1);");
			ctZone.getMethod("deleteCreature", "(Lcom/wurmonline/server/creatures/Creature;Z)V")
					.insertAfter("net.buddat.wumods.merchants.MailMerchantHooks.deleteCreature($1);");
			
		} 
		catch (NotFoundException e)
		{
			logException("Class not found", e);
		} 
		catch (CannotCompileException e)
		{
			logException("Cannot compile method change", e);
		}
	}

	@Override
	public void onServerStarted()
	{
		ModActions.registerAction(new MailMerchantAction());
	}

}
