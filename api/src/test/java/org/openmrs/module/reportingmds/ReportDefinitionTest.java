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
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ReportDefinitionTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldImportBasicReportDefinitionWithConcept() throws Exception {
		PackageImporter importer = MetadataSharing.getInstance().newPackageImporter();
		importer.loadSerializedPackageStream(getClass().getResourceAsStream("/report_definitions-1.zip"));
		
		importer.setImportConfig(ImportConfig.valueOf(ImportMode.PARENT_AND_CHILD));
		importer.importPackage();
	}
}
