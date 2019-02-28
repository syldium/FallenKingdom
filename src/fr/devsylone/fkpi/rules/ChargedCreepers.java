package fr.devsylone.fkpi.rules;

public class ChargedCreepers extends Rule
{
	public ChargedCreepers(int spawn, int drop, int tnts)
	{
		super("ChargedCreepers", 0);
		setValue(spawn, drop, tnts);
	}

	public ChargedCreepers()
	{
		this(0, 0, 0);
	}

	public Integer getValue()
	{
		return (Integer) value;
	}

	public void setValue(int spawn, int drop, int tnts)
	{
		super.setValue(spawn * 1000000 + drop * 1000 + tnts);
		value = spawn * 1000000 + drop * 1000 + tnts;
	}

	public Integer getSpawn()
	{
		return getValue() / 1000000;
	}

	public Integer getDrop()
	{
		return (getValue() / 1000) - (getValue() / 1000000) * 1000;
	}

	public Integer getTntAmount()
	{
		return getValue() - ((getValue() / 1000) * 1000);
	}

	public void setSpawn(int v)
	{
		setValue(v, getDrop(), getTntAmount());
	}

	public void setDrop(int v)
	{
		setValue(getSpawn(), v, getTntAmount());
	}

	public void setTntAmount(int v)
	{
		setValue(getSpawn(), getDrop(), v);
	}

}
