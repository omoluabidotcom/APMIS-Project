package com.cinoteck.application.views.utils;

import com.opencsv.CSVWriter;
import com.vaadin.flow.server.StreamResource;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaIndexDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
//public final class DownloadTransposedLqasDataUtility {
//
//	CampaignFormDataCriteria criteria = new CampaignFormDataCriteria();
//
//	public static StreamResource createTransposedLqasDataFromIndexList(CampaignFormDataCriteria criteria,
//			String formName, String campaignName) {
//		CampaignFormDataCriteria criteriax = new CampaignFormDataCriteria();
//
//		criteriax = criteria;
//		
//		
//		
//		String exportFileName = campaignName + "_" + formName + "_LONG_" +  new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime())+ ".csv";// createFileNameWithCurrentDateandEntityNameString(formName+
////      + new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime());
//
//
////		String exportFileName = "APMIS_" + formName + "_" + campaignName + ".csv";// createFileNameWithCurrentDateandEntityNameString(formName+
//																					// "_" + campaignName, ".csv");
//
//		// Using the index list method to get the day-wise form data since it already
//		// used the criteria on the grid to get the data
//		List<CampaignFormDataIndexDto> formDatafromIndexList = FacadeProvider.getCampaignFormDataFacade()
//				.getIndexList(criteriax, null, null, null);
//
//		// Initialize the set to store unique variable parts without the day suffix
//		Set<String> fieldIDWithoutDaySuffix = new HashSet<>();
//
//		// Iterate through all elements in the formDatafromIndexList to build column
//		// headers
//		for (CampaignFormDataIndexDto formData : formDatafromIndexList) {
//			if (formData.getFormValues() != null) {
//				Set<String> fieldIDsFromFormValues = new HashSet<>();
//
//				// Extract the field IDs from the form values and add them to the set to remove
//				// duplicates
//				for (CampaignFormDataEntry formValues : formData.getFormValues()) {
//					fieldIDsFromFormValues.add(formValues.getId());
//				}
//
//				// Convert the set back to a list
//				List<String> fieldIDsforColumnHeaders = new ArrayList<>(fieldIDsFromFormValues);
//
//				// Extract unique variable parts and remove the day suffix
//				for (String uniqueVariable : extractUniqueVariableParts(fieldIDsforColumnHeaders)) {
//					if (uniqueVariable.matches(".*H\\d+$")) {
//					String variableID = uniqueVariable.replaceAll("(H\\d+|H\\d+)$", "");
//					fieldIDWithoutDaySuffix.add(variableID);
//				} else if (!uniqueVariable.startsWith("House")) {
//					// Handle non-H-suffix fields
//					fieldIDWithoutDaySuffix.add(uniqueVariable);
//				}
//				}
//
//			}
//		}
//
//		List<String> columnNames = new ArrayList<>();
////		
//		Map<String, String> fieldIdToCaptionMap = new HashMap<>();
//
//		List<String> fieldCaptions = new ArrayList<>();
//
//		CampaignFormMetaDto formMetaReference = FacadeProvider.getCampaignFormMetaFacade()
//				.getCampaignFormMetaByUuid(criteria.getCampaignFormMeta().getUuid());
//		if (formMetaReference != null) {
//			List<CampaignFormElement> campaignFormElements = formMetaReference.getCampaignFormElements();
//			fieldIdToCaptionMap = matchFieldsWithCaptions(fieldIDWithoutDaySuffix, campaignFormElements);
//		}
//
//		fieldCaptions.add("Campaign");
//		fieldCaptions.add("Form");
//		fieldCaptions.add("Region");
//		fieldCaptions.add("RCode");
//		fieldCaptions.add("Province");
//		fieldCaptions.add("PCode");
//		fieldCaptions.add("District");
//		fieldCaptions.add("DCode");
//		fieldCaptions.add("Cluster");
//		fieldCaptions.add("Cluster Number");
//		fieldCaptions.add("CCode");
//		fieldCaptions.add("Form Phase");
//		fieldCaptions.add("Source");
//		fieldCaptions.add("Creating user");
////		fieldCaptions.add("Verified");
////		fieldCaptions.add("Published");
//		fieldCaptions.add("Household Number");
//		fieldCaptions.add("HouseTotalChildrenSeen");
//		
//		
//		columnNames.add("campaign");
//		columnNames.add("form");
//		columnNames.add("region");
//		columnNames.add("rcode");
//		columnNames.add("province");
//		columnNames.add("pcode");
//		columnNames.add("district");
//		columnNames.add("dcode");
//		columnNames.add("cluster");
//		columnNames.add("clusterNumber");
//		columnNames.add("ccode");
//		columnNames.add("formType");
//		columnNames.add("source");
//		columnNames.add("creatingUser");
////		columnNames.add("isVerified");
////		columnNames.add("isPublished");
//		columnNames.add("housenumber");
//		columnNames.add("houseTotalChildrenSeen");
//
//
//		
//		 Set<String> fieldsNeedingHnSuffix = new HashSet<>(Arrays.asList(
//		            "FM", "Reasons", "Gender", "childrenAge", "House", "Total"
//		        ));
//
//
//		// Generate and write columns to CSV writer
//		// index here has to be 15 because of the number of existing column
//		// remeber to increment the values when new columns are added below the defined
//		// columns
//		Map<String, Integer> fieldIdPositions = new HashMap<>();
//		int ageGroupIndex = 15; //17 was 17 but weve moved ispublished and verified tp the last two columns 
//		for (String fieldGroup : fieldIDWithoutDaySuffix) {			
//			if(fieldGroup.equalsIgnoreCase("TotalChildrenSeen") || fieldGroup.equals("CName")) {				
//				System.out.println("Skipping : " + fieldGroup);
//			}else {			
//			columnNames.add(fieldGroup);
//			fieldIdPositions.put(fieldGroup, ageGroupIndex);
//			ageGroupIndex += 1;
//			}
//		}
//
//		for (String fieldId : fieldIDWithoutDaySuffix) {			
//			if(fieldId.equalsIgnoreCase("TotalChildrenSeen") || fieldId.equals("CName")) {				
//				System.out.println("Skipping : " + fieldId);
//			}else {			
//			String caption = fieldIdToCaptionMap.getOrDefault(fieldId, fieldId);			
//			fieldCaptions.add(caption);
//			fieldIdPositions.put(caption, ageGroupIndex);
//			ageGroupIndex += 1;
//			}
//		}
//		
//		fieldCaptions.add("Verified Status");
//		fieldCaptions.add("Published Status");
//		columnNames.add("isverified");
//		columnNames.add("ispublished");
//		
//		
//		Map<String, Map<String, String>> dayValueMap = new HashMap<>();
//
//		for (CampaignFormDataIndexDto individualTransposedFormData : formDatafromIndexList) {
//			if (individualTransposedFormData.getFormValues() != null) {
//				Map<String, String> formDataMaxp = new HashMap<>();
//				Set<String> fieldIDsFromFormValues = new HashSet<>();
//
//				for (CampaignFormDataEntry formValues : individualTransposedFormData.getFormValues()) {
//					formDataMaxp.put(formValues.getId(), formValues.getValue().toString());
//					fieldIDsFromFormValues.add(formValues.getId());
//				}
//
//				List<String> fieldIDsforColumnHeaders = new ArrayList<>(fieldIDsFromFormValues);
//
//				Set<String> uniqueVariablePartsWithoutDaySuffixForColumnHeader = new HashSet<>();
//
//				for (String uniqueVariable : extractUniqueVariableParts(fieldIDsforColumnHeaders)) {
//
//					if (uniqueVariable.matches(".*H\\d+$")) {
//						// Handle H-suffix fields
//						String variableID = uniqueVariable.replaceAll("(H\\d+|H\\d+)$", "");
//						uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(variableID);
//					} else if (!uniqueVariable.startsWith("House")) {
//						// Handle non-H-suffix fields
//						uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(uniqueVariable);
//					}
//				}
//
//				for (String day : extractUniqueDayValues(fieldIDsforColumnHeaders)) {
//					for (String variable : uniqueVariablePartsWithoutDaySuffixForColumnHeader) {
//						String key = variable + day;
//
////						System.out.println("key666666666221222" + key);
//
//						if (formDataMaxp.containsKey(key)) {
//							String keyValue = formDataMaxp.get(key);
//							dayValueMap.computeIfAbsent(day, k -> new HashMap<>()).put(variable, keyValue);
//						}
//					}
//				}
//
//			}
//		}
//
//		return new StreamResource(exportFileName, () -> {
//			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
//				// Write UTF-8 BOM
//				byteStream.write(0xEF);
//				byteStream.write(0xBB);
//				byteStream.write(0xBF);
//
//				try (CSVWriter writer = CSVUtils.createCSVWriter(
//						new OutputStreamWriter(byteStream, StandardCharsets.UTF_8),
//						FacadeProvider.getConfigFacade().getCsvSeparator())) {
//
//					// Write header twice
//					writer.writeNext(fieldCaptions.toArray(new String[0]));
//
//					writer.writeNext(columnNames.toArray(new String[0]));
//
//					for (CampaignFormDataIndexDto individualTransposedFormData : formDatafromIndexList) {
//						if (individualTransposedFormData.getFormValues() != null) {
//							Map<String, String> formDataMaxp = new HashMap<>();
//							Set<String> fieldIDsFromFormValues = new HashSet<>();
//
//							for (CampaignFormDataEntry formValues : individualTransposedFormData.getFormValues()) {
//								String value = new String(
//										formValues.getValue().toString().getBytes(StandardCharsets.UTF_8),
//										StandardCharsets.UTF_8);
//								formDataMaxp.put(formValues.getId(), value);
//								fieldIDsFromFormValues.add(formValues.getId());
//							}
//
//							List<String> fieldIDsforColumnHeaders = new ArrayList<>(fieldIDsFromFormValues);
//
//							Set<String> uniqueVariablePartsWithoutDaySuffixForColumnHeader = new HashSet<>();
//
//							for (String uniqueVariable : extractUniqueVariableParts(fieldIDsforColumnHeaders)) {
//
//								if (uniqueVariable.matches(".*H\\d+$")) {
//									// Handle H-suffix fields
//									String variableID = uniqueVariable.replaceAll("(H\\d+|H\\d+)$", "");
//									uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(variableID);
//								} else if (!uniqueVariable.startsWith("House")) {
//									// Handle non-H-suffix fields
//									uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(uniqueVariable);
//								}
//							}
//
//							for (String day : extractUniqueDayValues(fieldIDsforColumnHeaders)) {
//								List<String> row = new ArrayList<>(Collections.nCopies(columnNames.size(), ""));
//
//								row.set(0, individualTransposedFormData.getCampaign().toString());
//								row.set(1, individualTransposedFormData.getForm().toString());
//								row.set(2, individualTransposedFormData.getArea().toString());
//								row.set(3, individualTransposedFormData.getRcode().toString());
//								row.set(4, individualTransposedFormData.getRegion().toString());
//								row.set(5, individualTransposedFormData.getPcode() + "");
//								row.set(6, individualTransposedFormData.getDistrict().toString());
//								row.set(7, individualTransposedFormData.getDcode() + "");
//
//								if (individualTransposedFormData.getCommunity() == null) {
//									row.set(8, "");
//									row.set(9, "");
//									row.set(10, "");
//								} else {
//									row.set(8, individualTransposedFormData.getCommunity().toString());
//									row.set(9, individualTransposedFormData.getClusternumber().toString());
//									row.set(10, individualTransposedFormData.getCcode().toString());
//								}
//
//								row.set(11, individualTransposedFormData.getFormType().toString());
//								row.set(12,
//										individualTransposedFormData.getSource() != null
//												? individualTransposedFormData.getSource().toString()
//												: "");
//								row.set(13,
//										individualTransposedFormData.getCreatingUser() != null
//												? individualTransposedFormData.getCreatingUser().toString()
//												: "");
//								
//								row.set(row.size() -2, individualTransposedFormData.isIsverified()+ "" != null
//										? individualTransposedFormData.isIsverified()+ ""
//										: "");
//								
//								row.set(row.size() -1, individualTransposedFormData.isIspublished()+ "" != null
//										? individualTransposedFormData.isIspublished()+ ""
//										: "");
//								
//								row.set(16, day);
//
//								String houseNumber = day.replaceAll("[^0-9]", "");
//
//								// Add House value matching the current day's number
//								String houseKey = "House" + houseNumber;
//								
//								 int houseColumnIndex = columnNames.indexOf("houseTotalChildrenSeen");
//						
//								if (formDataMaxp.containsKey(houseKey)) {
//									
//									
//									// Add a new column for House if not already added
//									if (!columnNames.contains("houseTotalChildrenSeen")) {
//										columnNames.add("houseTotalChildrenSeen");
//										 houseColumnIndex = columnNames.indexOf("houseTotalChildrenSeen");
//										row.add(formDataMaxp.get(houseKey));
//									} else {
//									       if (houseColumnIndex >= 0 && formDataMaxp.containsKey(houseKey)) {
//									            String houseValue = formDataMaxp.get(houseKey);
//									            row.set(houseColumnIndex, houseValue);
////									            System.out.println("Setting house value: " + houseValue + " for house key: " + houseKey);
//									        } else {
//									            System.out.println("Failed to set house value. House Column Index: " + houseColumnIndex + 
//									                             ", House Key exists: " + formDataMaxp.containsKey(houseKey));
//									        }
//									}
//								}
//								
//								for (String variable : uniqueVariablePartsWithoutDaySuffixForColumnHeader) {
//									
//									if(variable.equalsIgnoreCase("TotalChildrenSeen") || variable.equals("CName")) {
//										
//										System.out.println("Skipping : " + variable);
//									}else {									
//									
//								    // Case 1: Handle House values
//								    if (variable.startsWith("House")) {
//								        String houseKeyz = variable + day;
//								        if (formDataMaxp.containsKey(houseKeyz)) {
//								            String houseValue = formDataMaxp.get(houseKey);
//								            int houseColumnIndexz = columnNames.indexOf(variable);
//								            if (houseColumnIndexz >= 0) {
//								                row.set(houseColumnIndexz, houseValue);
//								            }
//								        }
//								    }
//								    // Case 2: Handle all other fields (including those ending with H1, H2, etc.)
//								    else {
//								        // First try with the day suffix
//								        String keyWithDay = variable + day;
//								        if (formDataMaxp.containsKey(keyWithDay)) {
//								            String keyValue = formDataMaxp.get(keyWithDay);
//								            int colIndex = columnNames.indexOf(variable);
//								            if (colIndex >= 0) {
//								                row.set(colIndex, keyValue);
//								            }
//								        } 
//								        // If not found with day suffix, try the original variable name
//								        else if (formDataMaxp.containsKey(variable)) {
//								            String keyValue = formDataMaxp.get(variable);
//								            int colIndex = columnNames.indexOf(variable);
//								            if (colIndex >= 0) {
//								                row.set(colIndex, keyValue);
//								            }
//								        }
//								    }
//									}
//								}
//
//
//								// Ensure proper encoding for each cell in the row
//								String[] rowArray = row.toArray(new String[0]);
//								for (int i = 0; i < rowArray.length; i++) {
//									if (rowArray[i] != null) {
//										rowArray[i] = new String(rowArray[i].getBytes(StandardCharsets.UTF_8),
//												StandardCharsets.UTF_8);
//									}
//								}
//
//								writer.writeNext(rowArray);
//							}
//						}
//					}
//
//					writer.flush();
//				}
//				return new ByteArrayInputStream(byteStream.toByteArray());
//			} catch (IOException e) {
//				// Handle exceptions and show a notification if needed
//				return null;
//			}
//		});
//	}
//	
//	public static List<String> extractUniqueVariableParts(List<String> formEntriesID) {
//	    Set<String> variablePartsSet = new HashSet<>();
//
//	    for (String id : formEntriesID) {
//	        if (id.startsWith("House")) {
//	            // For House fields, keep as is (will be combined with day later)
//	            String baseHouse = id.replaceAll("H\\d+$", "");
//	            variablePartsSet.add(baseHouse);
//	        } else if (id.matches(".*H\\d+$")) {
//	            // For fields ending with H1, H2, etc., remove the H-suffix
//	            String baseVariable = id.replaceAll("H\\d+$", "");
//	            variablePartsSet.add(baseVariable);
//	        } else {
//	            // For regular fields, add as is
//	            variablePartsSet.add(id);
//	        }
//	    }
//	    
//	    return new ArrayList<>(variablePartsSet);
//	}
//
//
//	public static StreamResource createTransposedDataFormExpressions(CampaignFormDataCriteria criteria) {
//	    return new StreamResource(criteria.getCampaignFormMeta().getCaption() + ".csv", () -> {
//	        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//	             OutputStreamWriter writer = new OutputStreamWriter(byteStream, StandardCharsets.UTF_8);
//	             CSVWriter csvWriter = new CSVWriter(writer)) {
//	            
//	            // Write headers
//	            csvWriter.writeNext(new String[]{"Variable Name", "Format", "Variable Caption", "Description"});
//	            
//	            // Fetch data
//	            List<CampaignFormMetaIndexDto> data = FacadeProvider.getCampaignFormMetaFacade()
//	                    .getFormExpressions(criteria.getCampaignFormMeta().getUuid());
//	            
//	            // Write rows
//	            for (CampaignFormMetaIndexDto dto : data) {
//	            	String captionWithoutDelimiter = dto.getFieldcaption();
//	            	if (captionWithoutDelimiter.contains(",")) {
//	            	    int commaIndex = captionWithoutDelimiter.indexOf(",");
//	            	    captionWithoutDelimiter = captionWithoutDelimiter.replace(",", " ");
//	            	    System.out.println("Comma found at index: " + commaIndex);
//	            	    System.out.println("Updated string: " + captionWithoutDelimiter);
//	            	} else {
//	            		captionWithoutDelimiter = dto.getFieldcaption();
//	            	}
//	                csvWriter.writeNext(new String[]{
//	                    dto.getFieldid(),
//	                    dto.getFieldtype(),
//	                    captionWithoutDelimiter,
//	                    dto.getFieldexpression()
//	                });
//	            }
//	            
//	            csvWriter.flush();
//	            return new ByteArrayInputStream(byteStream.toByteArray());
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            return null;
//	        }
//	    });
//	}
//
//	
//	private static List<String> extractUniqueDayValues(List<String> formEntriesID) {
//	    Set<String> dayValuesSet = new HashSet<>();
//	    Pattern pattern = Pattern.compile("H\\d+$");
//
//	    for (String id : formEntriesID) {
//	        Matcher matcher = pattern.matcher(id);
//	        if (matcher.find()) {
//	            dayValuesSet.add(matcher.group());
//	        }
//	    }
//	    return new ArrayList<>(dayValuesSet);
//	}
//
//	private static void debugPrintFormValues(Map<String, String> formDataMaxp, String variable, String day) {
//	    System.out.println("Processing variable: " + variable);
//	    String keyWithDay = variable + day;
//	    System.out.println("Checking key with day: " + keyWithDay);
//	    System.out.println("Has value with day: " + formDataMaxp.containsKey(keyWithDay));
//	    if (formDataMaxp.containsKey(keyWithDay)) {
//	        System.out.println("Value: " + formDataMaxp.get(keyWithDay));
//	    }
//	    System.out.println("Has direct value: " + formDataMaxp.containsKey(variable));
//	    if (formDataMaxp.containsKey(variable)) {
//	        System.out.println("Direct value: " + formDataMaxp.get(variable));
//	    }
//	}
//
//	private static void debugPrintFieldTypes(List<String> formEntriesID) {
//	    System.out.println("\nField Types Analysis:");
//	    for (String id : formEntriesID) {
//	        System.out.println("Field: " + id);
//	        System.out.println("  Starts with House: " + id.startsWith("House"));
//	        System.out.println("  Ends with H\\d+: " + id.matches(".*H\\d+$"));
//	        if (id.matches(".*H\\d+$")) {
//	            System.out.println("  Base part: " + id.replaceAll("H\\d+$", ""));
//	        }
//	    }
//	}
//	private static boolean isHouseField(String fieldId) {
//	    return fieldId.startsWith("House");
//	}
//
//	private static boolean isHSuffixField(String fieldId) {
//	    return fieldId.matches(".*H\\d+$");
//	}
//
//	private static boolean isRegularField(String fieldId) {
//	    return !isHouseField(fieldId) && !isHSuffixField(fieldId);
//	}
//
//	public static String extractDay(String id) {
//		Pattern pattern = Pattern.compile("H\\d+$");
//		Matcher matcher = pattern.matcher(id);
//		if (matcher.find()) {
//			return matcher.group();
//		}
//		return null;
//	}
//
//	public static String createFileNameWithCurrentDate(ExportEntityName entityName, String fileExtension) {
//		String instanceName = FacadeProvider.getConfigFacade().getSormasInstanceName().toLowerCase(); // The export is
//																										// being
//																										// prepared
//		String processedInstanceName = DataHelper.cleanStringForFileName(instanceName);
//		String processedEntityName = DataHelper.cleanStringForFileName(entityName.getLocalizedNameInSystemLanguage());
//		String exportDate = DateHelper.formatDateForExport(new Date());
//		return String.join("_", processedInstanceName, processedEntityName, exportDate, fileExtension);
//	}
//
//	public static String createFileNameWithCurrentDateandEntityNameString(String entityName, String fileExtension) {
//		String instanceName = FacadeProvider.getConfigFacade().getSormasInstanceName().toLowerCase(); // The export is
//																										// being
//																										// prepare
//		String processedInstanceName = DataHelper.cleanStringForFileName(instanceName);
//		String processedEntityName = DataHelper.cleanStringForFileName(entityName);
//		String exportDate = DateHelper.formatDateForExport(new Date());
//		return String.join("_", processedInstanceName, processedEntityName, exportDate, fileExtension);
//
//	}
//
//	private static Map<String, String> matchFieldsWithCaptions(Set<String> fieldIDWithoutDaySuffix,
//			List<CampaignFormElement> campaignFormElements) {
//
//		Map<String, String> fieldIdToCaptionMap = new HashMap<>();
//
//		for (String fieldId : fieldIDWithoutDaySuffix) {
//			for (CampaignFormElement element : campaignFormElements) {
//				String elementId = element.getId();
//				String elementIdWithoutDay = elementId.replaceAll("(H\\d+|H\\d+)$", "");
//
//				if (elementIdWithoutDay.equals(fieldId)) {
//					fieldIdToCaptionMap.put(fieldId, element.getCaption());
//					break;
//				}
//			}
//		}
//
//		return fieldIdToCaptionMap;
//	}
//
//}


public final class DownloadTransposedLqasDataUtility {

	CampaignFormDataCriteria criteria = new CampaignFormDataCriteria();

	public static StreamResource createTransposedLqasDataFromIndexList(CampaignFormDataCriteria criteria,
			String formName, String campaignName) {
		CampaignFormDataCriteria criteriax = new CampaignFormDataCriteria();

		criteriax = criteria;
		
		String exportFileName = campaignName + "_" + formName + "_LONG_" +  new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime())+ ".csv";

		// Using the index list method to get the day-wise form data since it already
		// used the criteria on the grid to get the data
		List<CampaignFormDataIndexDto> formDatafromIndexList = FacadeProvider.getCampaignFormDataFacade()
				.getIndexList(criteriax, null, null, null);

		// Initialize the set to store unique variable parts without the day suffix
		Set<String> fieldIDWithoutDaySuffix = new HashSet<>();

		// Iterate through all elements in the formDatafromIndexList to build column
		// headers
		for (CampaignFormDataIndexDto formData : formDatafromIndexList) {
			if (formData.getFormValues() != null) {
				Set<String> fieldIDsFromFormValues = new HashSet<>();

				// Extract the field IDs from the form values and add them to the set to remove
				// duplicates
				for (CampaignFormDataEntry formValues : formData.getFormValues()) {
					fieldIDsFromFormValues.add(formValues.getId());
				}

				// Convert the set back to a list
				List<String> fieldIDsforColumnHeaders = new ArrayList<>(fieldIDsFromFormValues);

				// Extract unique variable parts and remove the day suffix
				for (String uniqueVariable : extractUniqueVariableParts(fieldIDsforColumnHeaders)) {
					if (uniqueVariable.matches(".*H\\d+$")) {
						String variableID = uniqueVariable.replaceAll("(H\\d+|H\\d+)$", "");
						fieldIDWithoutDaySuffix.add(variableID);
					} else if (!uniqueVariable.startsWith("House")) {
						// Handle non-H-suffix fields
						fieldIDWithoutDaySuffix.add(uniqueVariable);
					}
				}
			}
		}

		// Define fixed column structure with predefined positions
		Map<String, Integer> fixedColumnPositions = new LinkedHashMap<>();
		
		// Base columns with fixed positions
		fixedColumnPositions.put("campaign", 0);
		fixedColumnPositions.put("form", 1);
		fixedColumnPositions.put("region", 2);
		fixedColumnPositions.put("rcode", 3);
		fixedColumnPositions.put("province", 4);
		fixedColumnPositions.put("pcode", 5);
		fixedColumnPositions.put("district", 6);
		fixedColumnPositions.put("dcode", 7);
		fixedColumnPositions.put("cluster", 8);
		fixedColumnPositions.put("clusterNumber", 9);
		fixedColumnPositions.put("ccode", 10);
		fixedColumnPositions.put("formType", 11);
		fixedColumnPositions.put("source", 12);
		fixedColumnPositions.put("creatingUser", 13);
//		fixedColumnPositions.put("housenumber", 14);
//		fixedColumnPositions.put("houseTotalChildrenSeen", 15);


		
		// Define field captions for the fixed columns
		Map<String, String> fieldCaptionsMap = new HashMap<>();
		fieldCaptionsMap.put("campaign", "Campaign");
		fieldCaptionsMap.put("form", "Form");
		fieldCaptionsMap.put("region", "Region");
		fieldCaptionsMap.put("rcode", "RCode");
		fieldCaptionsMap.put("province", "Province");
		fieldCaptionsMap.put("pcode", "PCode");
		fieldCaptionsMap.put("district", "District");
		fieldCaptionsMap.put("dcode", "DCode");
		fieldCaptionsMap.put("cluster", "Cluster");
		fieldCaptionsMap.put("clusterNumber", "Cluster Number");
		fieldCaptionsMap.put("ccode", "CCode");
		fieldCaptionsMap.put("formType", "Form Phase");
		fieldCaptionsMap.put("source", "Source");
		fieldCaptionsMap.put("creatingUser", "Creating user");
//		fieldCaptionsMap.put("housenumber", "Household Number");
//		fieldCaptionsMap.put("houseTotalChildrenSeen", "HouseTotalChildrenSeen");
		fieldCaptionsMap.put("isverified", "Verified Status");
		fieldCaptionsMap.put("ispublished", "Published Status");
		
		// Get field captions from form metadata
		Map<String, String> fieldIdToCaptionMap = new HashMap<>();
		CampaignFormMetaDto formMetaReference = FacadeProvider.getCampaignFormMetaFacade()
				.getCampaignFormMetaByUuid(criteria.getCampaignFormMeta().getUuid());
		if (formMetaReference != null) {
			List<CampaignFormElement> campaignFormElements = formMetaReference.getCampaignFormElements();
			fieldIdToCaptionMap = matchFieldsWithCaptions(fieldIDWithoutDaySuffix, campaignFormElements);
		}
		
		// Now assign specific positions for data fields
		// Starting position after the fixed columns
		int nextAvailablePosition = 14;
		
		// Define the order of specific fields you want to appear first
		String[] priorityFields = {"VisitDate", "Surveyor", "LotNo", "LotClusterNo",  "Village", "villagecode", "housenumber" , "houseTotalChildrenSeen",  "childrenAge", "Gender", "FM", "Reasons","Comment", "Total"};
		for (String field : priorityFields) {
			
			System.out.println( field + "---Fields ---" + fieldIDWithoutDaySuffix.contains(field));
			if (fieldIDWithoutDaySuffix.contains(field)) {
				fixedColumnPositions.put(field, nextAvailablePosition++);
				fieldIDWithoutDaySuffix.remove(field); // Remove to avoid duplicate
			}else if (field.equalsIgnoreCase("housenumber")){
				fixedColumnPositions.put("housenumber", nextAvailablePosition++);
				fieldCaptionsMap.put("housenumber", "Household Number");
			}else if (field.equalsIgnoreCase("houseTotalChildrenSeen")){
				fixedColumnPositions.put("houseTotalChildrenSeen", nextAvailablePosition++);
				fieldCaptionsMap.put("houseTotalChildrenSeen", "HouseTotalChildrenSeen");
			}
		}
		
		// Then add all remaining fields in alphabetical order
		List<String> remainingFields = new ArrayList<>(fieldIDWithoutDaySuffix);
		Collections.sort(remainingFields); // Sort alphabetically
		
		for (String fieldId : remainingFields) {
			if (!fieldId.equalsIgnoreCase("TotalChildrenSeen") && !fieldId.equals("CName")) {
				

				fixedColumnPositions.put(fieldId, nextAvailablePosition++);
			}
		}
		
		// Add verification and published fields at the end
		fixedColumnPositions.put("isverified", nextAvailablePosition++);
		fixedColumnPositions.put("ispublished", nextAvailablePosition++);
		
		// Create ordered lists for column names and captions based on the fixed positions
		String[] columnNames = new String[fixedColumnPositions.size()];
		String[] fieldCaptions = new String[fixedColumnPositions.size()];
		
		for (Map.Entry<String, Integer> entry : fixedColumnPositions.entrySet()) {
			String fieldId = entry.getKey();
			int position = entry.getValue();
			
			System.out.println(fieldId + " ---888888888888888888888855555-----" + position + "column names ----" +  columnNames);
			
			// Set column name
			columnNames[position] = fieldId;
			
			// Set field caption (use from metadata if available, otherwise from mapped captions)
			if (fieldCaptionsMap.containsKey(fieldId)) {
				fieldCaptions[position] = fieldCaptionsMap.get(fieldId);
			} else {
				fieldCaptions[position] = fieldIdToCaptionMap.getOrDefault(fieldId, fieldId);
			}
		}
		
		// Convert arrays to lists for easier handling in the existing code
		List<String> columnNamesList = Arrays.asList(columnNames);
		List<String> fieldCaptionsList = Arrays.asList(fieldCaptions);
		
		return new StreamResource(exportFileName, () -> {
			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
				// Write UTF-8 BOM
				byteStream.write(0xEF);
				byteStream.write(0xBB);
				byteStream.write(0xBF);

				try (CSVWriter writer = CSVUtils.createCSVWriter(
						new OutputStreamWriter(byteStream, StandardCharsets.UTF_8),
						FacadeProvider.getConfigFacade().getCsvSeparator())) {

					// Write headers
					writer.writeNext(fieldCaptions);
					writer.writeNext(columnNames);

					for (CampaignFormDataIndexDto individualTransposedFormData : formDatafromIndexList) {
						if (individualTransposedFormData.getFormValues() != null) {
							Map<String, String> formDataMaxp = new HashMap<>();
							Set<String> fieldIDsFromFormValues = new HashSet<>();

							for (CampaignFormDataEntry formValues : individualTransposedFormData.getFormValues()) {
								String value = new String(
										formValues.getValue().toString().getBytes(StandardCharsets.UTF_8),
										StandardCharsets.UTF_8);
								formDataMaxp.put(formValues.getId(), value);
								fieldIDsFromFormValues.add(formValues.getId());
							}
							
							List<String> fieldIDsforColumnHeaders = new ArrayList<>(fieldIDsFromFormValues);

							Set<String> uniqueVariablePartsWithoutDaySuffixForColumnHeader = new HashSet<>();

							for (String uniqueVariable : extractUniqueVariableParts(fieldIDsforColumnHeaders)) {
								if (uniqueVariable.matches(".*H\\d+$")) {
									// Handle H-suffix fields
									String variableID = uniqueVariable.replaceAll("(H\\d+|H\\d+)$", "");
									uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(variableID);
								} else if (!uniqueVariable.startsWith("House")) {
									// Handle non-H-suffix fields
									uniqueVariablePartsWithoutDaySuffixForColumnHeader.add(uniqueVariable);
								}
							}

							for (String day : extractUniqueDayValues(fieldIDsforColumnHeaders)) {
								// Create a row with the correct size (fixed column structure)
								String[] row = new String[columnNames.length];
								Arrays.fill(row, ""); // Initialize with empty strings
								
								// Set basic field values at their fixed positions
								row[fixedColumnPositions.get("campaign")] = individualTransposedFormData.getCampaign().toString();
								row[fixedColumnPositions.get("form")] = individualTransposedFormData.getForm().toString();
								row[fixedColumnPositions.get("region")] = individualTransposedFormData.getArea().toString();
								row[fixedColumnPositions.get("rcode")] = individualTransposedFormData.getRcode().toString();
								row[fixedColumnPositions.get("province")] = individualTransposedFormData.getRegion().toString();
								row[fixedColumnPositions.get("pcode")] = individualTransposedFormData.getPcode() + "";
								row[fixedColumnPositions.get("district")] = individualTransposedFormData.getDistrict().toString();
								row[fixedColumnPositions.get("dcode")] = individualTransposedFormData.getDcode() + "";

								if (individualTransposedFormData.getCommunity() == null) {
									row[fixedColumnPositions.get("cluster")] = "";
									row[fixedColumnPositions.get("clusterNumber")] = "";
									row[fixedColumnPositions.get("ccode")] = "";
								} else {
									row[fixedColumnPositions.get("cluster")] = individualTransposedFormData.getCommunity().toString();
									row[fixedColumnPositions.get("clusterNumber")] = individualTransposedFormData.getClusternumber().toString();
									row[fixedColumnPositions.get("ccode")] = individualTransposedFormData.getCcode().toString();
								}

								row[fixedColumnPositions.get("formType")] = individualTransposedFormData.getFormType().toString();
								row[fixedColumnPositions.get("source")] = individualTransposedFormData.getSource() != null
										? individualTransposedFormData.getSource().toString() : "";
								row[fixedColumnPositions.get("creatingUser")] = individualTransposedFormData.getCreatingUser() != null
										? individualTransposedFormData.getCreatingUser().toString() : "";
										
								row[fixedColumnPositions.get("isverified")] = individualTransposedFormData.isIsverified() + "";
								row[fixedColumnPositions.get("ispublished")] = individualTransposedFormData.isIspublished() + "";
								System.out.println(fixedColumnPositions.get("housenumber") + "-================33333333333333333333=======" +day );
							
								
								// Process all other variable fields
								for (String variable : uniqueVariablePartsWithoutDaySuffixForColumnHeader) {
									if (!variable.equalsIgnoreCase("TotalChildrenSeen") && !variable.equals("CName") && 
										fixedColumnPositions.containsKey(variable)) {	
										
										if (fixedColumnPositions.containsKey("housenumber")) {
										    row[fixedColumnPositions.get("housenumber")] = day;
										}
										
										
										String houseNumber = day.replaceAll("[^0-9]", "");
										String houseKey = "House" + houseNumber;
										
										if (formDataMaxp.containsKey(houseKey)) {
											String houseValue = formDataMaxp.get(houseKey);
											row[fixedColumnPositions.get("houseTotalChildrenSeen")] = houseValue;
										}
									
										
										String keyWithDay = variable + day;
										if (formDataMaxp.containsKey(keyWithDay)) {
											// Set value in the specified fixed position
											row[fixedColumnPositions.get(variable)] = formDataMaxp.get(keyWithDay);
										} else if (formDataMaxp.containsKey(variable)) {
											// If not found with day suffix, try without it
											row[fixedColumnPositions.get(variable)] = formDataMaxp.get(variable);
										}
									}
								}											
								
								
								
//								row[fixedColumnPositions.get("housenumber")] = day;

//								String houseNumber = day.replaceAll("[^0-9]", "");
//								String houseKey = "House" + houseNumber;
//								
//								if (formDataMaxp.containsKey(houseKey)) {
//									String houseValue = formDataMaxp.get(houseKey);
//									row[fixedColumnPositions.get("houseTotalChildrenSeen")] = houseValue;
//								}

								// Ensure proper encoding for each cell in the row
								for (int i = 0; i < row.length; i++) {
									if (row[i] != null) {
										row[i] = new String(row[i].getBytes(StandardCharsets.UTF_8),
												StandardCharsets.UTF_8);
									}
								}

								writer.writeNext(row);
							}
						}
					}

					writer.flush();
				}
				return new ByteArrayInputStream(byteStream.toByteArray());
			} catch (IOException e) {
				// Handle exceptions and show a notification if needed
				return null;
			}
		});
	}
	
	
	public static StreamResource createTransposedDataFormExpressions(CampaignFormDataCriteria criteria) {
    return new StreamResource(criteria.getCampaignFormMeta().getCaption() + ".csv", () -> {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(byteStream, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            // Write headers
            csvWriter.writeNext(new String[]{"Variable Name", "Format", "Variable Caption", "Description"});
            
            // Fetch data
            List<CampaignFormMetaIndexDto> data = FacadeProvider.getCampaignFormMetaFacade()
                    .getFormExpressions(criteria.getCampaignFormMeta().getUuid());
            
            // Write rows
            for (CampaignFormMetaIndexDto dto : data) {
            	String captionWithoutDelimiter = dto.getFieldcaption();
            	if (captionWithoutDelimiter.contains(",")) {
            	    int commaIndex = captionWithoutDelimiter.indexOf(",");
            	    captionWithoutDelimiter = captionWithoutDelimiter.replace(",", " ");
            	    System.out.println("Comma found at index: " + commaIndex);
            	    System.out.println("Updated string: " + captionWithoutDelimiter);
            	} else {
            		captionWithoutDelimiter = dto.getFieldcaption();
            	}
                csvWriter.writeNext(new String[]{
                    dto.getFieldid(),
                    dto.getFieldtype(),
                    captionWithoutDelimiter,
                    dto.getFieldexpression()
                });
            }
            
            csvWriter.flush();
            return new ByteArrayInputStream(byteStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    });
}
	
	// The rest of the methods remain unchanged
	public static List<String> extractUniqueVariableParts(List<String> formEntriesID) {
	    Set<String> variablePartsSet = new HashSet<>();

	    for (String id : formEntriesID) {
	        if (id.startsWith("House")) {
	            // For House fields, keep as is (will be combined with day later)
	            String baseHouse = id.replaceAll("H\\d+$", "");
	            variablePartsSet.add(baseHouse);
	        } else if (id.matches(".*H\\d+$")) {
	            // For fields ending with H1, H2, etc., remove the H-suffix
	            String baseVariable = id.replaceAll("H\\d+$", "");
	            variablePartsSet.add(baseVariable);
	        } else {
	            // For regular fields, add as is
	            variablePartsSet.add(id);
	        }
	    }
	    
	    return new ArrayList<>(variablePartsSet);
	}

	private static List<String> extractUniqueDayValues(List<String> formEntriesID) {
	    Set<String> dayValuesSet = new HashSet<>();
	    Pattern pattern = Pattern.compile("H\\d+$");

	    for (String id : formEntriesID) {
	        Matcher matcher = pattern.matcher(id);
	        if (matcher.find()) {
	            dayValuesSet.add(matcher.group());
	        }
	    }
	    return new ArrayList<>(dayValuesSet);
	}

	private static Map<String, String> matchFieldsWithCaptions(Set<String> fieldIDWithoutDaySuffix,
			List<CampaignFormElement> campaignFormElements) {

		Map<String, String> fieldIdToCaptionMap = new HashMap<>();

		for (String fieldId : fieldIDWithoutDaySuffix) {
			for (CampaignFormElement element : campaignFormElements) {
				String elementId = element.getId();
				String elementIdWithoutDay = elementId.replaceAll("(H\\d+|H\\d+)$", "");

				if (elementIdWithoutDay.equals(fieldId)) {
					fieldIdToCaptionMap.put(fieldId, element.getCaption());
					break;
				}
			}
		}

		return fieldIdToCaptionMap;
	}

	// Other methods remain unchanged
}