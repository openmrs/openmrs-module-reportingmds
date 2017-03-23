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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.handler.MetadataMergeHandler;
import org.openmrs.module.metadatasharing.handler.MetadataPriorityDependenciesHandler;
import org.openmrs.module.metadatasharing.handler.impl.ObjectHandler;
import org.openmrs.module.metadatasharing.visitor.ObjectVisitor;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorAndDimensionDataSetDefinition;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("reportingmds.DefinitionCustomHandler")
public class DefinitionCustomHandler implements MetadataPriorityDependenciesHandler<Definition>, MetadataMergeHandler<Definition> {
	
	@Autowired
	private ObjectVisitor visitor;
	
	@Autowired
	private ObjectHandler objectHandler;
	
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
				if (pullDependencies(value)) {
					//done!
				} else if (value instanceof Collection) {
					for (Object object : (Collection<?>) value) {
						pullDependencies(object);
					}
				} else if (value instanceof Map) {
					for (Object object : ((Map<?, ?>) value).values()) {
						pullDependencies(object);
					}
				}
			}

			private boolean pullDependencies(Object object) {
				if (object instanceof Mapped) {
					Mapped<?> mapped = (Mapped<?>) object;
					dependencies.add(mapped.getParameterizable());
					return true;
				}

				if (object instanceof CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification) {
					CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification specification = (CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification) object;
					dependencies.add(specification.getIndicator().getParameterizable());
					return true;
				}

				if (object instanceof OpenmrsObject) {
					dependencies.add(object);
					return true;
				}

				return false;
			}

		});
		
		return dependencies;
	}
	
	/**
	 * @see org.openmrs.module.metadatasharing.handler.MetadataMergeHandler#merge(java.lang.Object,
	 *      java.lang.Object, org.openmrs.module.metadatasharing.ImportType, java.util.Map)
	 */
	@Override
	public void merge(final Definition existing, final Definition incoming, final ImportType importType,
	                  final Map<Object, Object> incomingToExisting) {
		objectHandler.merge(existing, incoming, importType, incomingToExisting);
		
		if (existing == null) {
			//Replace incoming object's fields with existing objects
			visitor.visitFields(incoming, false, new ObjectVisitor.FieldVisitor() {
				
				@Override
				public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object incomingField) {
					if (setExistingOnMappedField(incomingField, incomingToExisting)) {
						//done!
					} else if (incomingField instanceof Collection) {
						@SuppressWarnings("unchecked")
						Collection<Object> collection = (Collection<Object>) incomingField;
						Iterator<Object> it = collection.iterator();
						
						while (it.hasNext()) {
							setExistingOnMappedField(it.next(), incomingToExisting);
						}
					} else if (incomingField instanceof Map) {
						Map<?, ?> map = (Map<?, ?>) incomingField;
						for (Object value : map.values()) {
							setExistingOnMappedField(value, incomingToExisting);
						}
					}
				}
			});
		} else if (importType.isPreferTheirs() || importType.isPreferMine() || importType.isOverwriteMine()) {
			//Copy properties from the incoming object to the existing object				
			Integer id = Handler.getId(existing);
			
			visitor.visitFields(incoming, false, new ObjectVisitor.FieldVisitor() {
				
				@Override
				public void visit(String fieldName, Class<?> type, Class<?> definedIn, Object incomingField) {
					if (Collection.class.isAssignableFrom(type)) {
						//If the collection field is null then do nothing
						if (incomingField == null) {
							return;
						}
						
						@SuppressWarnings("unchecked")
						Collection<Object> incomingCollection = (Collection<Object>) incomingField;
						
						for (Object incomingElement : incomingCollection) {
							setExistingOnMappedField(incomingElement, incomingToExisting);
						}
					} else if (Map.class.isAssignableFrom(type)) {
						if (incomingField == null) {
							return;
						}
						
						Map<?, ?> map = (Map<?, ?>) incomingField;
						for (Object value : map.values()) {
							setExistingOnMappedField(value, incomingToExisting);
						}
					} else if (!importType.isPreferMine()) {
						setExistingOnMappedField(incomingField, incomingToExisting);
					}
				}
				
			});
			
			if (id != null) {
				Handler.setId(existing, id);
			}
		}
	}

	private boolean setExistingOnMappedField(Object incomingField, final Map<Object, Object> incomingToExisting) {
		if (incomingField instanceof Mapped) {
			@SuppressWarnings("unchecked")
			Mapped<Parameterizable> mappedField = (Mapped<Parameterizable>) incomingField;
			Object existing = incomingToExisting.get(mappedField.getParameterizable());
			if (existing != null) {
				mappedField.setParameterizable((Parameterizable) existing);
			}
			return true;
		}
		if (incomingField instanceof CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification) {
			CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification specField = (CohortIndicatorAndDimensionDataSetDefinition.CohortIndicatorAndDimensionSpecification) incomingField;
			Object existing = incomingToExisting.get(specField.getIndicator().getParameterizable());
			if (existing != null) {
				specField.getIndicator().setParameterizable((CohortIndicator) existing);
			}
			return true;
		}

		return false;
	}
	
}
