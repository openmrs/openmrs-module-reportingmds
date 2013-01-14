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

import java.util.HashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.springframework.stereotype.Component;


@Component("reportingmds.PersonDataDefinitionHandler")
public class PersonDataDefinitionHandler extends DefinitionHandler<PersonDataDefinition> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataTypesHandler#getTypes()
	 */
	@Override
	public Map<Class<? extends PersonDataDefinition>, String> getTypes() {
		Map<Class<? extends PersonDataDefinition>, String> map = new HashMap<Class<? extends PersonDataDefinition>, String>();
		map.put(PersonDataDefinition.class, "Person Data Definition");
		return map;
	}
	
	/**
	 * @see org.openmrs.module.reportingmds.DefinitionHandler#getService()
	 */
	@Override
	public DefinitionService<PersonDataDefinition> getService() {
		return Context.getService(PersonDataService.class);
	}
	
}
