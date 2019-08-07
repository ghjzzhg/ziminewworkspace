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
package com.ckfinder.connector.handlers.commands;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.errors.ConnectorException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

/**
 * Class to handle errors via HTTP headers (for non-XML commands).
 */
public class ErrorCommand extends Command {

	private HttpServletResponse response;

	@Override
	public void execute(final OutputStream out) throws ConnectorException {
		try {
			response.setHeader("X-CKFinder-Error", String.valueOf(e.getErrorCode()));
			switch (e.getErrorCode()) {
				case Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST:
				case Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_NAME:
				case Constants.Errors.CKFINDER_CONNECTOR_ERROR_THUMBNAILS_DISABLED:
				case Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED:
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					break;
				case Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED:
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					break;
				default:
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					break;
			}

		} catch (IOException e) {
			throw new ConnectorException(e);
		}
	}

	@Override
	public void setResponseHeader(final HttpServletResponse response,
			final ServletContext sc) {
		response.reset();
		this.response = response;

	}

	/**
	 * for error command there should be no exection throw becouse there is no
	 * more excetpion handlers.
	 *
	 * @param reqParam request param
	 * @return true if validation passed
	 * @throws ConnectorException it should never throw an exception
	 */
	@Override
	protected boolean checkParam(final String reqParam) throws ConnectorException {
		if (reqParam == null || reqParam.equals("")) {
			return true;
		}
		if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(reqParam).find()) {
			return false;
		}
		return true;
	}

}
