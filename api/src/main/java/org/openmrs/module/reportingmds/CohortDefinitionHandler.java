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
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.springframework.stereotype.Component;

/**
 * Provides necessary functionality to share Cohort Definitions
 */
@Component("reportingmds.CohortDefinitionHandler")
public class CohortDefinitionHandler extends DefinitionHandler<CohortDefinition> {

	/**
	 * @see DefinitionHandler#getService()
	 */
	@Override
	public DefinitionService<CohortDefinition> getService() {
		return Context.getService(CohortDefinitionService.class);
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataTypesHandler#getTypes()
	 */
	@Override
	public Map<Class<? extends CohortDefinition>, String> getTypes() {
		Map<Class<? extends CohortDefinition>,String> map = new HashMap<Class<? extends CohortDefinition>, String>();
		map.put(CohortDefinition.class, "Cohort Definition");
	    return map;
	}
}
