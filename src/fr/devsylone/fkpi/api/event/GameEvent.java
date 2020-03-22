package fr.devsylone.fkpi.api.event;

public class GameEvent extends FkEvent
{
    private final Type type;

    public GameEvent(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        PAUSE_EVENT,
        RESUME_EVENT,
        START_EVENT
    }
}
