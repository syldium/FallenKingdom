package fr.devsylone.fkpi.api.event;

public class DayEvent extends FkEvent
{
    private final Type type;
    private final int day;

    public DayEvent(Type type, int day)
    {
        this.type = type;
        this.day = day;
    }

    public int getDay()
    {
        return day;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        NEW_DAY,
        PVP_ENABLED,
        TNT_ENABLED,
        NETHER_ENABLED,
        END_ENABLED
    }
}
