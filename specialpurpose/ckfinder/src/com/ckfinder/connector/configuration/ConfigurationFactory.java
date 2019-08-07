/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.ckfinder.connector.configuration;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.PathUtils;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtilProperties;

/**
 * Factory returning configuration instance.
 */
public final class ConfigurationFactory {

	private static ConfigurationFactory instance;
	private IConfiguration configuration;

	/**
	 * private constructor.
	 */
	private ConfigurationFactory() {
	}

	/**
	 * if instance is null creates one and returns it.
	 *
	 * @return configuration instance.
	 */
	public static ConfigurationFactory getInstace() {
		if (instance == null) {
			instance = new ConfigurationFactory();
		}
		return instance;
	}

	/**
	 * Gets base configuration prepared in IConfiguration.init().
	 *
	 * @return the configuration
	 * @throws Exception when error occurs
	 */
	public final IConfiguration getConfiguration() throws Exception {
		if (configuration != null
				&& configuration.checkIfReloadConfig()) {
			configuration.init();
			AccessControlUtil.getInstance(configuration).loadACLConfig();
		}
		return configuration;
	}

	/**
	 * Gets and prepares configuration.
	 *
	 * @param request request
	 * @return the configuration
	 * @throws Exception when error occurs
	 */
	public final IConfiguration getConfiguration(final HttpServletRequest request)
			throws Exception {
		IConfiguration baseConf = getConfiguration();
		return prepareConfiguration(request, baseConf);

	}

	/**
	 * Prepares configuration using request.
	 *
	 * @param request request
	 * @param baseConf base configuration initialized in IConfiguration.init()
	 * @return prepared configuration
	 * @throws Exception when error occurs
	 */
	public IConfiguration prepareConfiguration(final HttpServletRequest request,
											   final IConfiguration baseConf)
			throws Exception {
		if (baseConf != null) {
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			String person = userLogin.getString("partyId");
			IConfiguration conf = baseConf.cloneConfiguration(person);
			conf.prepareConfigurationForRequest(request);
			updateResourceTypesPaths(request, conf);
			return conf;
		}
		return null;

	}

	/**
	 * @param configuration the configuration to set
	 */
	public final void setConfiguration(final IConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Updates resources types paths by request.
	 *
	 * @param request request
	 * @param conf connector configuration.
	 * @throws Exception when error occurs
	 */
	private void updateResourceTypesPaths(final HttpServletRequest request,
										  final IConfiguration conf) throws Exception {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String disc = EntityUtilProperties.getPropertyValue("zxdoc.properties", "dataResourcePath", "localhost", delegator);
		String baseFolder = getBaseFolder(conf, request);
		baseFolder = conf.getThumbsDir().replace(Constants.BASE_DIR_PLACEHOLDER,
				baseFolder);
		baseFolder = PathUtils.escape(baseFolder);
		baseFolder = PathUtils.removeSlashFromEnd(baseFolder);

		File file = new File(disc + baseFolder);
		String init = request.getParameter("command");
		if (!file.exists() && null != init && !request.getParameter("command").equals("Init")) {
			file.mkdir();
		}
		conf.setThumbsPath(file.getAbsolutePath());


		String thumbUrl = conf.getThumbsURL();
		thumbUrl = thumbUrl.replaceAll(
				Constants.BASE_URL_PLACEHOLDER,
				conf.getBasePathBuilder().getBaseUrl(request));
		conf.setThumbsURL(PathUtils.escape(thumbUrl));

		for (ResourceType item : conf.getTypes().values()) {
			String url = item.getUrl();
			url = url.replaceAll(Constants.BASE_URL_PLACEHOLDER,
					conf.getBasePathBuilder().getBaseUrl(request));
			url = PathUtils.escape(url);
			url = PathUtils.removeSlashFromEnd(url);
			item.setUrl(url);

			baseFolder = getBaseFolder(conf, request);
			baseFolder = item.getPath().replace(Constants.BASE_DIR_PLACEHOLDER, baseFolder);
			baseFolder = PathUtils.escape(baseFolder);
			baseFolder = PathUtils.removeSlashFromEnd(baseFolder);

			if ( baseFolder == null || baseFolder.equals("") ) {
				baseFolder = PathUtils.removeSlashFromBeginning(url);
			}

			file = new File(FileUtils.getFullPath(disc + baseFolder));
			if (!file.exists() && null != init && !request.getParameter("command").equals("Init")) {
				FileUtils.createPath(file, false);
			}
			item.setPath(baseFolder);
		}

	}

	/**
	 * Gets the path to base dir from configuration Crates the base dir folder
	 * if it doesn't exists.
	 *
	 * @param conf connector configuration
	 * @param request request
	 * @return path to base dir from conf
	 * @throws ConnectorException when error during creating folder occurs
	 */
	private String getBaseFolder(final IConfiguration conf,
								 final HttpServletRequest request)
			throws ConnectorException {
		String baseFolder = conf.getBasePathBuilder().getBaseUrl(request);

		File baseDir = new File(baseFolder);
		if (!baseDir.exists()) {
			try {
				FileUtils.createPath(baseDir, false);
			} catch (IOException e) {
				throw new ConnectorException(e);
			}
		}

		return PathUtils.addSlashToEnd(baseFolder);
	}
}
