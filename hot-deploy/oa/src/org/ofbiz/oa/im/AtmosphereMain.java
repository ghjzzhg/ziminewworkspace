/*
 * Copyright 2015 Async-IO.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ofbiz.oa.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.service.*;
import org.atmosphere.cpr.*;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

/**
 * Simple annotated class that demonstrate the power of Atmosphere. This class supports all transports, support
 * message length garantee, heart beat, message cache thanks to the {@link ManagedService}.
 */
@ManagedService(path = "/atmosphere/{channel: [a-zA-Z][a-zA-Z_0-9]*}")
public class AtmosphereMain {
    private final Logger logger = LoggerFactory.getLogger(AtmosphereMain.class);

    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<String, String>();
    private ObjectMapper mapper = new ObjectMapper();

    private final static String CHAT = "/atmosphere/";

    @PathParam("channel")
    private String chatroomName;

// Uncomment for changing response's state
    @Get
    public void init(AtmosphereResource r) {
        r.getResponse().setCharacterEncoding("UTF-8");
    }

    // For demonstrating injection.
    @Inject
    private BroadcasterFactory factory;

    @Inject
    private AtmosphereResourceFactory resourceFactory;

    @Inject
    private MetaBroadcaster metaBroadcaster;

    @Heartbeat
    public void onHeartbeat(final AtmosphereResourceEvent event) {
        logger.trace("Heartbeat send by {}", event.getResource());
    }

    /**
     * Invoked when the connection as been fully established and suspended, e.g ready for receiving messages.
     *
     * @param r
     */
    @Ready(encoders = {JacksonEncoder.class})
    @DeliverTo(DeliverTo.DELIVER_TO.ALL)
    public IMProtocal onReady(final AtmosphereResource r) {//return的消息deliver to all
        logger.info("Browser {} connected.", r.uuid());
        String author = r.getRequest().getParameter("author");
        Delegator delegator = (Delegator) r.session().getServletContext().getAttribute("delegator");
        String fullName = "";
        try {
            GenericValue genericValue = EntityQuery.use(delegator).select().from("Person").where(EntityCondition.makeCondition("partyId", author)).queryOne();
            fullName = genericValue.get("fullName").toString();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(author)) {
            users.put(author, r.uuid());
//            metaBroadcaster.broadcastTo("/*", new IMProtocal(author, "上线", users.keySet(), getRooms(factory.lookupAll())));
        }
        return new IMProtocal(fullName, "上线", users.keySet(), getRooms(factory.lookupAll()));
    }

    private static Collection<String> getRooms(Collection<Broadcaster> broadcasters) {
        Collection<String> result = new ArrayList<String>();
        for (Broadcaster broadcaster : broadcasters) {
            if (!("/*".equals(broadcaster.getID()))) {
                // if no room is specified, use ''
                String[] p = broadcaster.getID().split("/");
                result.add(p.length > 2 ? p[2] : "");
            }
        };
        return result;
    }

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            // We didn't get notified, so we remove the user.
            users.values().remove(event.getResource().uuid());
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", event.getResource().uuid());
        }
    }

    /**
     * Simple annotated class that demonstrate how {@link org.atmosphere.config.managed.Encoder} and {@link org.atmosphere.config.managed.Decoder
     * can be used.
     *
     * @param message an instance of {@link ProtocolDecoder }
     * @return
     * @throws IOException
     */
    @org.atmosphere.config.service.Message(decoders = {ProtocolDecoder.class})
    public void onMessage(IMProtocal message) throws IOException {
        String author = message.getAuthor();
        if (UtilValidate.isNotEmpty(author) && !users.containsKey(author)) {
            users.put(author, message.getUuid());
        }

        if (message.getMessage().contains("disconnecting")) {
            users.remove(author);
            IMProtocal m = new IMProtocal (author, "下线", users.keySet(), getRooms(factory.lookupAll()));
            metaBroadcaster.broadcastTo("/*", mapper.writeValueAsString(m));
        }

        if(UtilValidate.isNotEmpty(message.getTo()) && UtilValidate.isNotEmpty(message.getMessage())){
            String userUUID = users.get(message.getTo());
            if (userUUID != null) {
                // Retrieve the original AtmosphereResource
                AtmosphereResource r = resourceFactory.find(userUUID);
                IMProtocal m = new IMProtocal(message.getFrom(),message.getMessage(), users.keySet(), getRooms(factory.lookupAll()));
                if (r != null) {
                    if (!message.getTo().equalsIgnoreCase("all")) {
                        factory.lookup(CHAT + chatroomName).broadcast(mapper.writeValueAsString(m), r);
                    }
                }
                //发回给自己
                r = resourceFactory.find(users.get(message.getFrom()));
                if (r != null) {
                    factory.lookup(CHAT + chatroomName).broadcast(mapper.writeValueAsString(m), r);
                }
            }
        }
    }
/*
    @org.atmosphere.config.service.Message(decoders = {UserDecoder.class})
    public void onPrivateMessage(UserMessage user) throws IOException {
        if(UtilValidate.isEmpty(user.getTo())){
            return;
        }
        String userUUID = users.get(user.getTo());
        if (userUUID != null) {
            // Retrieve the original AtmosphereResource
            AtmosphereResource r = resourceFactory.find(userUUID);
            IMProtocal m = new IMProtocal(user.getFrom(),user.getMessage(), users.keySet(), getRooms(factory.lookupAll()));
            if (r != null) {
                if (!user.getTo().equalsIgnoreCase("all")) {
                    factory.lookup(CHAT + chatroomName).broadcast(m, r);
                }
            }
            //发回给自己
            r = resourceFactory.find(users.get(user.getFrom()));
            if (r != null) {
                factory.lookup(CHAT + chatroomName).broadcast(m, r);
            }
        } else {
            IMProtocal m = new IMProtocal(user.getFrom(), user.getMessage(), users.keySet(), getRooms(factory.lookupAll()));
            metaBroadcaster.broadcastTo("*//*", m);
        }
    }*/
}