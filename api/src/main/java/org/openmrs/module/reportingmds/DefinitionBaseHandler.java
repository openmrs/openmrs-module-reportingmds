/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reportingmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.metadatasharing.handler.MetadataSaveHandler;
import org.openmrs.module.metadatasharing.handler.MetadataSearchHandler;
import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Provides necessary functionality to share reporting module definition classes
 */
public abstract class DefinitionBaseHandler<T extends Definition> implements MetadataTypesHandler<T>, MetadataSearchHandler<T>, MetadataSaveHandler<T> {
	
	/**
	 * @return the DefinitionService which handles the given Definition
	 */
	public abstract DefinitionService<T> getService();
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataTypesHandler#getTypes()
	 */
	@SuppressWarnings("unchecked")
    @Override
	public Map<Class<? extends T>, String> getTypes() {
		Map<Class<? extends T>, String> types = new HashMap<Class<? extends T>, String>();
		types.put((Class<? extends T>) Definition.class, null);
	    return types;
	}
	
	/**
	 * @see MetadataSaveHandler#saveItem(Object)
	 */
	public T saveItem(T definition) throws DAOException {
		return getService().saveDefinition(definition);
	}
	
	/**
	 * @see MetadataSearchHandler#getItem(Class, String)
	 */
	@Override
	public T getItemByUuid(Class<? extends T> type, String uuid) throws DAOException {
		return getService().getDefinition(uuid, type);
	}
	
	/**
	 * @see MetadataSearchHandler#getItemById(java.lang.Class, java.lang.Integer)
	 */
	@Override
	public T getItemById(Class<? extends T> type, Integer id) throws DAOException {
		return getService().getDefinition(type, id);
	}
	
	/**
	 * @see MetadataSearchHandler#getItems(Class, boolean, String, Integer, Integer)
	 */
	public List<T> getItems(Class<? extends T> type, boolean includeRetired, String phrase, Integer firstResult,
	                        Integer maxResults) throws DAOException {
		List<T> definitions = getService().getAllDefinitions(includeRetired);
		List<T> ret = new ArrayList<T>();
		firstResult = (firstResult == null || firstResult < 1 ? 1 : firstResult);
		for (int i = firstResult - 1; i < definitions.size() && (maxResults == null || ret.size() < maxResults); i++) {
			T definition = definitions.get(i);
			if (type.isInstance(definition) && nameMatchesPhrase(definition.getName(), phrase)) {
				ret.add(definition);
			}
		}
		return ret;
	}
	
	/**
	 * @see MetadataSearchHandler#getItemsCount(Class, boolean, String)
	 */
	public int getItemsCount(Class<? extends T> type, boolean includeRetired, String phrase) throws DAOException {
		int count = 0;
		for (T definition : getService().getAllDefinitions(includeRetired)) {
			if (type.isInstance(definition) && nameMatchesPhrase(definition.getName(), phrase)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return true if the phrase is null or if the name contains the passed phrase
	 */
	private boolean nameMatchesPhrase(String name, String phrase) {
		return (phrase == null || ObjectUtil.nvlStr(name, "").toLowerCase().contains(phrase.toLowerCase()));
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataHandler#getPriority()
	 */
	@Override
	public int getPriority() {
		return 0;
	}
}
