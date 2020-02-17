package org.sunbird.actors;

import akka.dispatch.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.actor.core.BaseActor;
import org.sunbird.cache.impl.RedisCache;
import org.sunbird.common.JsonUtils;
import org.sunbird.common.dto.Request;
import org.sunbird.common.dto.Response;
import org.sunbird.common.dto.ResponseHandler;
import org.sunbird.graph.dac.model.Node;
import org.sunbird.graph.nodes.DataNode;
import org.sunbird.graph.utils.NodeUtil;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;
import scala.concurrent.Promise$;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SchoolActor extends BaseActor {

    public Future<Response> onReceive(Request request) throws Throwable {
        String operation = request.getOperation();
        switch(operation) {
            case "create": return create(request);
            case "read": return read(request);
            case "addUserData": return addUserData(request);
            case "getUserData": return getUserData(request);
            default: return ERROR(operation);
        }
    }
    private Future<Response> read(Request request) throws Exception {
        return DataNode.read(request, getContext().dispatcher())
                .map(new Mapper<Node, Response>() {
                    @Override
                    public Response apply(Node node) {
                        // Added for backward compatibility in mobile
                        Map<String, Object> metadata = NodeUtil.serialize(node, new ArrayList<String>(), (String) request.getContext().get("schemaName"), (String)request.getContext().get("version"));
                        metadata.put("identifier", node.getIdentifier().replace(".img", ""));
                        Response response = ResponseHandler.OK();
                        response.put(((String)request.getContext().get("objectType")).toLowerCase(), metadata);
                        return response;
                    }
                }, getContext().dispatcher());
    }

    private Future<Response> create(Request request) throws Exception {
        return DataNode.create(request, getContext().dispatcher())
                .map(new Mapper<Node, Response>() {
                    @Override
                    public Response apply(Node node) {
                        Response response = ResponseHandler.OK();
                        response.put("node_id", node.getIdentifier());
                        response.put("identifier", node.getIdentifier());
                        response.put("versionKey", node.getMetadata().get("versionKey"));
                        return response;
                    }
                }, getContext().dispatcher());
    }


    private Future<Response> addUserData(Request request) throws Exception {
        String objectType = (String) request.getContext().get("objectType");
        String sessionid = "";
        if(StringUtils.equalsIgnoreCase("Teacher", objectType)) {
            sessionid = "teacher_" + request.get("sessionId");
        } else {
            sessionid = "attendance_" + request.get("sessionId");
        }
        String existingData = RedisCache.get(sessionid, null, 0);
        if(StringUtils.isBlank(existingData)){
            RedisCache.set(sessionid, JsonUtils.serialize(request.get("userDetails")), 1200);
        } else {
            Map<String, Object> dataMap = JsonUtils.deserialize(existingData, Map.class);
            dataMap.putAll((Map<String, Object>) request.get("userDetails"));
            RedisCache.set(sessionid, JsonUtils.serialize(dataMap), 1200);
        }
        return Promise$.MODULE$.successful(ResponseHandler.OK()).future();
    }

    private Future<Response> getUserData(Request request) throws Exception {
        String objectType = (String) request.getContext().get("objectType");
        String sessionid = "";
        if(StringUtils.equalsIgnoreCase("Teacher", objectType)) {
            sessionid = "teacher_" + request.get("sessionId");
        } else {
            sessionid = "attendance_" + request.get("sessionId");
        }
        String existingData = RedisCache.get(sessionid, null, 0);
        Map<String, Object> dataMap = JsonUtils.deserialize(existingData, Map.class);
        Response response = ResponseHandler.OK();
        response.put(objectType.toLowerCase(), dataMap);
        return Promise$.MODULE$.successful(response).future();
    }
}

