package fr.devsylone.fallenkingdom.version.packet.book;

import fr.devsylone.fallenkingdom.version.Version;

final class Provider {

    static final BookViewer VIEWER;

    static {
        if (Version.VERSION_TYPE == Version.VersionType.V1_8) {
            VIEWER = new BookViewer1_8();
        } else if (Version.VERSION_TYPE == Version.VersionType.V1_9_V1_12) {
            VIEWER = new BookViewer1_9();
        } else {
            VIEWER = null;
        }
    }
}
