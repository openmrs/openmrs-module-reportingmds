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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("reportingmds.DefinitionPriorityDependenciesHandler")
public class DefinitionPriorityDependenciesHandler implements MetadataPriorityDependenciesHandler<Definition> {
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataHandler#getPriority()
	 */
	@Override
	public int getPriority() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler#getPriorityDependencies(java.lang.Object)
	 */
	@Override
	public List<Object> getPriorityDependencies(Definition object) {
		final List<Object> dependencies = new ArrayList<Object>();
		MetadataSharing.getInstance().getObjectVisitor().visitFields(object, false, new ObjectVisitor.FieldVisitor() {
			
			@Override
			public void visit(String name, Class<?> type, Class<?> definedIn, Object value) {
				if (value instanceof Mapped) {
					Mapped<?> mapped = (Mapped<?>) value;
					dependencies.add(mapped.getParameterizable());
				} else if (value instanceof Collection) {
					for (Object object : (Collection<?>) value) {
						if (object instanceof Mapped) {
							Mapped<?> mapped = (Mapped<?>) object;
							dependencies.add(mapped.getParameterizable());
						} else if (value instanceof OpenmrsObject) {
							dependencies.add(value);
						}
					}
				} else if (value instanceof Map) {
					for (Object object : ((Map<?, ?>) value).values()) {
						if (object instanceof Mapped) {
							Mapped<?> mapped = (Mapped<?>) object;
							dependencies.add(mapped.getParameterizable());
						} else if (value instanceof OpenmrsObject) {
							dependencies.add(value);
						}
					}
				} else if (value instanceof OpenmrsObject) {
					dependencies.add(value);
				}
			}
			
		});
		
		return dependencies;
	}
	
}
