package org.activiti.rest.servlet;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.parse.BpmnParseHandler;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.config.model.EntityConfig;
import org.ofbiz.entity.config.model.InlineJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.activiti.rest.conf.ApplicationConfiguration;

import javax.servlet.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
public class WebConfigurer implements ServletContextListener {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    public AnnotationConfigWebApplicationContext context;

    public void setContext(AnnotationConfigWebApplicationContext context) {
        this.context = context;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        initProcessEngine();

        log.debug("Configuring Spring root application context");

        AnnotationConfigWebApplicationContext rootContext = null;

        if (context == null) {
            rootContext = new AnnotationConfigWebApplicationContext();
            rootContext.register(ApplicationConfiguration.class);
            rootContext.refresh();
        } else {
            rootContext = context;
        }

        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootContext);

        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        initSpring(servletContext, rootContext);
        initSpringSecurity(servletContext, disps);

        log.debug("Web application fully configured");
    }

    private void initProcessEngine() {
        Debug.logInfo("Starting initProcessEngine", WebConfigurer.class.getName());
        try {
            ContainerConfig.Container cfg = ContainerConfig.getContainer("activiti-container", null);
            ContainerConfig.Container.Property datasource = cfg.getProperty("datasource-name");

            InlineJdbc inlineJdbc = EntityConfig.getDatasource(datasource.value).getInlineJdbc();

            StandaloneProcessEngineConfiguration activitiCfg = (StandaloneProcessEngineConfiguration) ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
            activitiCfg
                    .setDatabaseSchemaUpdate(cfg.getProperty("databaseSchemaUpdate").value)
                    .setJdbcUrl(inlineJdbc.getJdbcUri()).setJdbcDriver(inlineJdbc.getJdbcDriver()).setJdbcUsername(inlineJdbc.getJdbcUsername()).setJdbcPassword(inlineJdbc.getJdbcPassword())
                    .setAsyncExecutorEnabled(Boolean.parseBoolean(cfg.getProperty("asyncExecutorEnabled").value))
                    .setAsyncExecutorActivate(Boolean.parseBoolean(cfg.getProperty("asyncExecutorActivate").value))
                    .setJobExecutorActivate(Boolean.parseBoolean(cfg.getProperty("jobExecutorActivate").value))
                    .setActivityFontName(cfg.getProperty("activityFontName").value)
                    .setLabelFontName(cfg.getProperty("labelFontName").value)
                    .setHistory(cfg.getProperty("history").value);

            //TODO：数据源配置
//            jdbcMaxActiveConnections: The number of active connections that the connection pool at maximum at any time can contain. Default is 10.
//            jdbcMaxIdleConnections: The number of idle connections that the connection pool at maximum at any time can contain.
//            jdbcMaxCheckoutTime: The amount of time in milliseconds a connection can be checked out from the connection pool before it is forcefully returned. Default is 20000 (20 seconds).
//            jdbcMaxWaitTime: This is a low level setting that gives the pool a chance to print a log status and re-attempt the acquisition of a connection in the case that it is taking unusually long (to avoid failing silently forever if the pool is misconfigured) Default is 20000 (20 seconds).

            //TODO:mail server config

            String idGenerator = cfg.getProperty("idGenerator").value;
            if (StringUtils.isNotBlank(idGenerator)) {
                activitiCfg.setIdGenerator((IdGenerator) Class.forName(idGenerator).newInstance());
            }
            ContainerConfig.Container.Property postBpmnParseHandlers = cfg.getProperty("postBpmnParseHandlers");
            if (postBpmnParseHandlers != null) {
                Collection<ContainerConfig.Container.Property> listeners = postBpmnParseHandlers.properties.values();
                List<BpmnParseHandler> handlers = new ArrayList<>();
                for (ContainerConfig.Container.Property listener : listeners) {
                    String listenerName = listener.value;
                    handlers.add((BpmnParseHandler) Class.forName(listenerName).newInstance());
                }
                activitiCfg.setPostBpmnParseHandlers(handlers);
            }
            ContainerConfig.Container.Property activityBehaviorFactory = cfg.getProperty("activityBehaviorFactory");
            if (activityBehaviorFactory != null) {
                activitiCfg.setActivityBehaviorFactory((ActivityBehaviorFactory) Class.forName(activityBehaviorFactory.value).newInstance());
            }
            ContainerConfig.Container.Property bpmnParser = cfg.getProperty("bpmnParser");
            if (bpmnParser != null) {
                activitiCfg.setBpmnParser((BpmnParser) Class.forName(bpmnParser.value).newInstance());
            }

            activitiCfg.buildProcessEngine();
            Debug.logInfo("Started initProcessEngine", WebConfigurer.class.getName());
        } catch (Exception e) {
            Debug.logError(e, "Activiti 引擎初始化错误", WebConfigurer.class.getName());
        }
    }

    /**
     * Initializes Spring and Spring MVC.
     */
    private ServletRegistration.Dynamic initSpring(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
        log.debug("Configuring Spring Web application context");
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(rootContext);
        dispatcherServletConfiguration.register(DispatcherServletConfiguration.class);

        log.debug("Registering Spring MVC Servlet");
        ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherServletConfiguration));
        dispatcherServlet.addMapping("/service/*");
        dispatcherServlet.setLoadOnStartup(2);
        dispatcherServlet.setAsyncSupported(true);

        return dispatcherServlet;
    }

    /**
     * Initializes Spring Security.
     */
    private void initSpringSecurity(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering Spring Security Filter");
        FilterRegistration.Dynamic springSecurityFilter = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());

        springSecurityFilter.addMappingForUrlPatterns(disps, false, "/service/*");
        springSecurityFilter.setAsyncSupported(true);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Destroying Web application");
        WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
        AnnotationConfigWebApplicationContext gwac = (AnnotationConfigWebApplicationContext) ac;
        gwac.close();
        log.debug("Web application destroyed");
    }
}
