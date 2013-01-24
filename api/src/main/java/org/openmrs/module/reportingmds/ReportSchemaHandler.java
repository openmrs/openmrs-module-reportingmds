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

import org.openmrs.module.metadatasharing.handler.MetadataTypesHandler;
import org.openmrs.report.ReportSchema;
import org.springframework.stereotype.Component;


/**
 * Hide {@link ReportSchema}s.
 */
@Component("reportingmds.ReportSchemaHandler")
public class ReportSchemaHandler implements MetadataTypesHandler<ReportSchema> {

	private Map<Class<? extends ReportSchema>, String> types;
	
	public ReportSchemaHandler() {
		types = new HashMap<Class<? extends ReportSchema>, String>();
		types.put(ReportSchema.class, ""); //hides the type
	}
	
	
	/**
     * @see org.openmrs.module.metadatasharing.handler.MetadataHandler#getPriority()
     */
    @Override
    public int getPriority() {
	    return 0;
    }

	/**
     * @see org.openmrs.module.metadatasharing.handler.MetadataTypesHandler#getTypes()
     */
    @Override
    public Map<Class<? extends ReportSchema>, String> getTypes() {
	    return types;
    }
	
}
