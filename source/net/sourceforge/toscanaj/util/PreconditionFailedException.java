package net.sourceforge.toscanaj.util;

import java.lang.Exception;

public class PreconditionFailedException
  extends Exception
{
    public PreconditionFailedException(String reason) 
    {
	super(reason);
    }
}
