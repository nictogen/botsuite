package com.nic.botsuite.common.init;

import com.nic.botsuite.gwen.Gwen;
import com.nic.botsuite.mercedes.Mercedes;

/**
 * Created by Nictogen on 1/20/18
 */
public class Main
{

	public static Gwen gwen;

	public static void main(String[] args)
	{
//		new Chloe(PrivateTokens.INSTANCE.getChloeAPI()).setup();
		new Mercedes(PrivateTokens.INSTANCE.getMercedesAPI()).setup();
		gwen = (Gwen) new Gwen(PrivateTokens.INSTANCE.getGwenAPI()).setup();
	}



}
