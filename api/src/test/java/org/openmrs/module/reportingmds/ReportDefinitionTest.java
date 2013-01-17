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

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.ImportedItem;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportDefinitionTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	ConceptService conceptService;
	
	@Test
	public void shouldImportBasicReportDefinitionWithConcept() throws Exception {
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/report_definitions-2.zip"));
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		
		for (ImportedItem importedItem : importer.getImportedItems(0)) {
	        if (importedItem.getIncoming() instanceof Concept) {
	        	importedItem.setExistingUuid("c607c80f-1ea9-4da3-bb88-6276ce8868dd");
	        	importedItem.setImportType(ImportType.OVERWRITE_MINE);
	        	importedItem.setAssessed(true);
	        }
        }
		
		importer.importPackage();
	}
	
	@Test
	public void shouldImportBasicReportDefinitionWithConceptTwice() throws Exception {
		shouldImportBasicReportDefinitionWithConcept();
		
		Context.flushSession();
		Context.clearSession();
		
		shouldImportBasicReportDefinitionWithConcept();
	}
	
	@Test
	@SkipBaseSetup
	public void shouldImportBasicReportDefinitionWithConceptToEmptyDB() throws Exception {
		initializeInMemoryDatabase();
		authenticate();
		executeDataSet("requiredTestDataset.xml");
		
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/report_definitions-2.zip"));
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		
		importer.importPackage();
	}
}
