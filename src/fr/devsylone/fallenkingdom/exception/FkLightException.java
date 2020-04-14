package fr.devsylone.fallenkingdom.exception;

import fr.devsylone.fallenkingdom.utils.Messages;

public class FkLightException extends RuntimeException
{
	private static final long serialVersionUID = 9155427550616609713L;

	public FkLightException(String message)
	{
		super(message);
	}

	public FkLightException(Messages message)
	{
		super(message.getMessage());
	}
}
