package fr.devsylone.fkpi.api.event;

public class DayEvent extends FkEvent
{
    private final Type type;
    private final int day;
    private String message;

    public DayEvent(Type type, int day, String message)
    {
        this.type = type;
        this.day = day;
        this.message = message;
    }

    public int getDay()
    {
        return day;
    }

    public Type getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
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
