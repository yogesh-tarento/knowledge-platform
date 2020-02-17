package org.sunbird.actors;

import org.sunbird.actor.core.BaseActor;
import org.sunbird.common.dto.Request;
import org.sunbird.common.dto.Response;
import org.sunbird.managers.SessionManager;
import scala.concurrent.Future;

public class SessionActor extends BaseActor {

    private static final String SCHEMA_NAME = "collection";

    public Future<Response> onReceive(Request request) throws Throwable {
        String operation = request.getOperation();
        request.getContext().put("schemaName", SCHEMA_NAME);
        switch(operation) {
            case "addSession":  return addSession(request);
            case "updateSession": return updateSession(request);
            case "removeSession": return removeSession(request);
            default: return ERROR(operation);
        }
    }

    private Future<Response> addSession(Request request) {
        return SessionManager.addSession(request, getContext().getDispatcher());
    }

    private Future<Response> removeSession(Request request) {
        return SessionManager.removeSession(request, getContext().getDispatcher());
    }

    private Future<Response> updateSession(Request request) {
        return SessionManager.updateSession(request, getContext().getDispatcher());
    }
}
