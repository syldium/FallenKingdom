package fr.devsylone.fallenkingdom.fkboard.websocket.responses;

import javax.annotation.Nonnull;

public interface Response {

    int getStatusCode();

    @Nonnull
    String toJSON();
}