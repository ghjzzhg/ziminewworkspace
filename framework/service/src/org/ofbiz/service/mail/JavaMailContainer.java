/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.service.mail;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;

import javax.mail.*;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JavaMailContainer implements Container {

    public static final String module = JavaMailContainer.class.getName();
    public static final String INBOX = "INBOX";

    protected Delegator delegator = null;
    protected LocalDispatcher dispatcher = null;
    protected GenericValue userLogin = null;
    protected long timerDelay = 300000;
    protected long maxSize = 1000000;
    protected ScheduledExecutorService pollTimer = null;
    protected boolean deleteMail = false;    // whether to delete emails after fetching them.

    protected String configFile = null;
    private String imapHost;
    private String imapPort;
    protected Map<Store, Session> stores = null;
    private String name;
    /**
     * Initialize the container
     *
     * @param args       args from calling class
     * @param configFile Location of master OFBiz configuration file
     * @throws org.ofbiz.base.container.ContainerException
     *
     */
    @Override
    public void init(String[] args, String name, String configFile) throws ContainerException {
        this.name = name;
        this.configFile = configFile;
        this.stores = new LinkedHashMap<Store, Session>();
        this.pollTimer = Executors.newScheduledThreadPool(1);
    }

    /**
     * Start the container
     *
     * @return true if server started
     * @throws org.ofbiz.base.container.ContainerException
     *
     */
    @Override
    public boolean start() throws ContainerException {
        ContainerConfig.Container cfg = ContainerConfig.getContainer(name, configFile);
        String dispatcherName = ContainerConfig.getPropertyValue(cfg, "dispatcher-name", "JavaMailDispatcher");
        String delegatorName = ContainerConfig.getPropertyValue(cfg, "delegator-name", "default");
        this.deleteMail = "true".equals(ContainerConfig.getPropertyValue(cfg, "delete-mail", "false"));

        this.delegator = DelegatorFactory.getDelegator(delegatorName);
        this.dispatcher = ServiceContainer.getLocalDispatcher(dispatcherName, delegator);
        this.timerDelay = ContainerConfig.getPropertyValue(cfg, "poll-delay", 300000);
        this.maxSize = ContainerConfig.getPropertyValue(cfg, "maxSize", 1000000); // maximum size in bytes
        this.imapHost = EntityUtilProperties.getPropertyValue("general.properties", "mail.imap.host", "localhost", delegator);
        this.imapPort = EntityUtilProperties.getPropertyValue("general.properties", "mail.imap.port", "993", delegator);


        // load the userLogin object
        String runAsUser = ContainerConfig.getPropertyValue(cfg, "run-as-user", "system");
        try {
            this.userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", runAsUser).queryOne();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to load run-as-user UserLogin; cannot start container", module);
            return false;
        }

        // load the MCA configuration
        ServiceMcaUtil.readConfig();

        // load the listeners
        List<ContainerConfig.Container.Property> configs = cfg.getPropertiesWithValue("store-listener");
        for (ContainerConfig.Container.Property prop: configs) {
            Session session = this.makeSession(prop);
            Store store = this.getStore(session);
            if (store != null) {
                stores.put(store, session);
                store.addStoreListener(new LoggingStoreListener());
            }
        }

        //load listener from database configuration
        try {
            List<GenericValue> imapAccounts = delegator.findAll("UserMailAccount", true);
            for (GenericValue imapAccount : imapAccounts) {
                Session session = this.makeSession(imapAccount);
                Store store = this.getStore(session);
                if (store != null) {
                    stores.put(store, session);
                    store.addStoreListener(new LoggingStoreListener());
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to load mail imap account", module);
        }

        // start the polling timer
        if (UtilValidate.isNotEmpty(stores)) {
            pollTimer.scheduleAtFixedRate(new PollerTask(dispatcher, userLogin), timerDelay, timerDelay, TimeUnit.MILLISECONDS);
        } else {
            Debug.logWarning("No JavaMail Store(s) configured; poller disabled.", module);
        }

        return true;
    }

    public static final class AuthenticatorGenerator {

        /**
         * 根据用户名和密码，生成Authenticator
         *
         * @param userName
         * @param password
         * @return
         */
        public static Authenticator getAuthenticator(final String userName, final String password) {
            return new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            };
        }

    }

    /**
     * Stop the container
     *
     * @throws org.ofbiz.base.container.ContainerException
     *
     */
    @Override
    public void stop() throws ContainerException {
        // stop the poller
        this.pollTimer.shutdown();
        Debug.logWarning("stop JavaMail poller", module);
    }

    @Override
    public String getName() {
        return name;
    }

    // java-mail methods
    protected Session makeSession(ContainerConfig.Container.Property client) {
        Properties props = new Properties();
        Map<String, ContainerConfig.Container.Property> clientProps = client.properties;
        if (clientProps != null) {
            for (ContainerConfig.Container.Property p: clientProps.values()) {
                props.setProperty(p.name.toLowerCase(), p.value);
            }
        }
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // don't fallback to normal IMAP connections on failure.
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        // use the simap port for imap/ssl connections.
//        props.setProperty("mail.imap.socketFactory.port", "993");

        Session session = Session.getInstance(props, AuthenticatorGenerator.getAuthenticator(props.getProperty("mail.user"), props.getProperty("mail.pass")));
//        session.setDebug(true);
        return session;
    }
    protected Session makeSession(GenericValue clientProps) {
        Properties props = new Properties();
        if (clientProps != null) {
//            props.put("mail.imap.ignorebodystructuresize", true);
            props.put("mail.store.protocol", "imap");
            String host = clientProps.getString("imapHost");
            props.put("mail.imap.host", UtilValidate.isEmpty(host) ? this.imapHost : host);
            String port = clientProps.getString("imapPort");
            props.put("mail.imap.port", UtilValidate.isEmpty(port) ? this.imapPort : port);
            props.put("mail.user", clientProps.getString("userAccount"));
            props.put("mail.pass", clientProps.getString("pass"));
            String factoryClass = clientProps.getString("factoryClass");
            props.put("mail.imap.socketFactory.class", UtilValidate.isEmpty(factoryClass) ? "javax.net.ssl.SSLSocketFactory" : factoryClass);
            String fallback = clientProps.getString("fallback");
            props.put("mail.imap.socketFactory.fallback", UtilValidate.isEmpty(fallback) ? true : Boolean.parseBoolean(fallback));
            String loginDisable = clientProps.getString("loginDisable");
            props.put("mail.imap.auth.login.disable", UtilValidate.isEmpty(loginDisable) ? false : Boolean.parseBoolean(loginDisable));
            String debug = clientProps.getString("debug");
            props.put("mail.debug", UtilValidate.isEmpty(debug) ? false : Boolean.parseBoolean(debug));
            props.put("mail.imap.timeout", UtilProperties.getPropertyValue("general.properties", "mail.imap.connect.timeout", "10000"));//10秒
//            props.put("mail.imap.partialfetch", false);
            /*props.put("mail.imap.fetchsize", 1024*4);
            props.put("mail.imap.separatestoreconnection", true);
            props.put("mail.imap.closefoldersonstorefailure", false);*/
        }

        Session session = Session.getInstance(props, AuthenticatorGenerator.getAuthenticator(props.getProperty("mail.user"), props.getProperty("mail.pass")));
//        session.setDebug(true);
        return session;
    }

    protected Store getStore(Session session) throws ContainerException {
        // create the store object
        Store store;
        try {
            store = session.getStore();
        } catch (NoSuchProviderException e) {
            throw new ContainerException(e);
        }

        // re-write the URLName including the password for this store
        /*if (store != null && store.getURLName() != null) {
            URLName urlName = this.updateUrlName(store.getURLName(), session.getProperties());
            if (Debug.verboseOn()) Debug.logVerbose("URLName - " + urlName.toString(), module);
            try {
                store = session.getStore(urlName);
            } catch (NoSuchProviderException e) {
                throw new ContainerException(e);
            }
        }*/

        if (store == null) {
            throw new ContainerException("No store configured!");
        }

        // test the store
        try {
            store.connect();
            store.close();
        } catch (MessagingException e) {
            Debug.logError("Unable to connect to mail store : " + store.getURLName().toString() + " : " + e.getMessage(), module);
        }

        return store;
    }

    protected URLName updateUrlName(URLName urlName, Properties props) {
        String protocol = urlName.getProtocol();
        String userName = urlName.getUsername();
        String password = urlName.getPassword();
        String host = urlName.getHost();
        String file = urlName.getFile();
        int port = urlName.getPort();

        // check the username
        if (UtilValidate.isEmpty(userName)) {
            userName = props.getProperty("mail." + protocol + ".user");
            if (UtilValidate.isEmpty(userName)) {
                userName = props.getProperty("mail.user");
            }
        }

        // check the password; update with the non-standard property
        if (UtilValidate.isEmpty(password)) {
            password = props.getProperty("mail." + protocol + ".pass");
            if (UtilValidate.isEmpty(password)) {
                password = props.getProperty("mail.pass");
            }
        }

        // check the host
        if (UtilValidate.isEmpty(host)) {
            host = props.getProperty("mail." + protocol + ".host");
            if (UtilValidate.isEmpty(host)) {
                host = props.getProperty("mail.host");
            }
        }

        // check the port
        int portProps = 0;
            String portStr = props.getProperty("mail." + protocol + ".port");
        if (!UtilValidate.isEmpty(portStr)) {
            try {
                portProps = Integer.valueOf(portStr);
            } catch (NumberFormatException e) {
                Debug.logError("The port given in property mail." + protocol + ".port is wrong, please check", module);
            }
            }
        if (portProps == 0) {
            portStr = props.getProperty("mail.port");
            if (!UtilValidate.isEmpty(portStr)) {
                try {
                    portProps = Integer.valueOf(props.getProperty("mail.port"));
                } catch (NumberFormatException e) {
                    Debug.logError("The port given in property mail.port is wrong, please check", module);
                }
            }
        }
        // override the port if have found one.
        if (portProps != 0) {
            port = portProps;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Update URL - " + protocol + "://" + userName + "@" + host + ":" + port + "!" + password + ";" + file, module);
        return new URLName(protocol, host, port, file, userName, password);
    }

    class LoggingStoreListener implements StoreListener {

        @Override
        public void notification(StoreEvent event) {
            String typeString = "";
            switch (event.getMessageType()) {
                case StoreEvent.ALERT:
                    typeString = "ALERT: ";
                    break;
                case StoreEvent.NOTICE:
                    typeString = "NOTICE: ";
                    break;
            }

            if (Debug.verboseOn()) Debug.logVerbose("JavaMail " + typeString + event.getMessage(), module);
        }
    }

    class PollerTask implements Runnable {

        LocalDispatcher dispatcher;
        GenericValue userLogin;

        public PollerTask(LocalDispatcher dispatcher, GenericValue userLogin) {
            this.dispatcher = dispatcher;
            this.userLogin = userLogin;
        }

        @Override
        public void run() {
            if (UtilValidate.isNotEmpty(stores)) {
                for (Map.Entry<Store, Session> entry: stores.entrySet()) {
                    Store store = entry.getKey();
                    Session session = entry.getValue();
                    try {
                        checkMessages(store, session);
                    } catch (Exception e) {
                        // Catch all exceptions so the loop will continue running
                        Debug.logError("Mail service invocation error for mail store " + store + ": " + e, module);
                    }
                    if (store.isConnected()) {
                        try {
                            store.close();
                        } catch (Exception e) {}
                    }
                }
            }
        }

        protected void checkMessages(Store store, Session session) throws MessagingException {
            if (!store.isConnected()) {
                store.connect();
            }

            // open the default folder
            Folder folder = store.getDefaultFolder();
            if (!folder.exists()) {
                throw new MessagingException("No default (root) folder available");
            }

            // open the inbox
            folder = folder.getFolder(INBOX);
            if (!folder.exists()) {
                throw new MessagingException("No INBOX folder available");
            }

            // get the message count; stop if nothing to do
            folder.open(Folder.READ_WRITE);
            int totalMessages = folder.getMessageCount();
            if (totalMessages == 0) {
                folder.close(false);
                return;
            }

            // get all messages
//            Message[] messages = folder.getMessages();
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            profile.add(FetchProfile.Item.FLAGS);
            profile.add("X-Mailer");
            folder.fetch(messages, profile);

            // process each message
            for (Message message: messages) {
                // process each un-read message
                if (!message.isSet(Flags.Flag.SEEN)) {
                    long messageSize = message.getSize();
                    if (message instanceof MimeMessage && messageSize >= maxSize) {
                        Debug.logWarning("Message from: " + message.getFrom()[0] + "not received, too big, size:" + messageSize + " cannot be more than " + maxSize + " bytes", module);

                        // set the message as read so it doesn't continue to try to process; but don't delete it
                        message.setFlag(Flags.Flag.SEEN, true);
                    } else {
                        this.processMessage(message, session);
                        if (Debug.verboseOn()) Debug.logVerbose("Message from " + UtilMisc.toListArray(message.getFrom()) + " with subject [" + message.getSubject() + "]  has been processed." , module);
                        message.setFlag(Flags.Flag.SEEN, true);
                        if (Debug.verboseOn()) Debug.logVerbose("Message [" + message.getSubject() + "] is marked seen", module);

                        // delete the message after processing
                        if (deleteMail) {
                            if (Debug.verboseOn()) Debug.logVerbose("Message [" + message.getSubject() + "] is being deleted", module);
                            message.setFlag(Flags.Flag.DELETED, true);
                        }
                    }
                }
            }

            // expunge and close the folder
            folder.close(true);
        }

        protected void processMessage(Message message, Session session) {
            if (message instanceof MimeMessage) {
                MimeMessageWrapper wrapper = new MimeMessageWrapper(session, (MimeMessage) message);
                try {
                    ServiceMcaUtil.evalRules(dispatcher, wrapper, userLogin);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem processing message", module);
                }
            }
        }
    }
}