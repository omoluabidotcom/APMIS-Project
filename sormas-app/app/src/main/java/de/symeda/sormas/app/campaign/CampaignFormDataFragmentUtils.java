/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.campaign;

import static de.symeda.sormas.api.campaign.ExpressionProcessorUtils.refreshEvaluationContext;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormElement;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormTranslations;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.app.component.controls.ControlCheckBoxField;
import de.symeda.sormas.app.component.controls.ControlCheckBoxGroupField;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlDecimalEditField;
import de.symeda.sormas.app.component.controls.ControlPhoneField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ControlTextEditFieldRange;
import de.symeda.sormas.app.component.controls.ControlTextReadField;
import de.symeda.sormas.app.component.controls.ControlTimeField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.util.YesNo;

public class CampaignFormDataFragmentUtils {
    public static final int DEFAULT_MIN_LENGTH = 1;

    private CampaignFormDataFragmentUtils() {
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");


    private static void applyExpressionValue(
            CampaignFormElement formElement,
            List<CampaignFormDataEntry> formValues,
            Object rawValue) {

        CampaignFormDataEntry entry = getOrCreateCampaignFormDataEntry(formValues, formElement);

        if (rawValue == null || rawValue.toString().trim().isEmpty()){
            entry.setValue(rawValue);
        }else if ("0".equals(rawValue.toString().trim())) {
            entry.setValue("0");
        } else {
            entry.setValue(rawValue);
        }
    }


    public static void handleExpressionSecEdit(
            ExpressionParser expressionParser,
            List<CampaignFormDataEntry> formValues,
            CampaignFormElementType type,
            ControlPropertyField dynamicField,
            String expressionString,
            Boolean isDisIgnore,
            Object orginalValue) {
        try {
            if(!expressionString.isEmpty() && expressionString != null && !expressionString.equals("")) {
                final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
                String valuex = expressionValue + "";

                System.out.println("handleExpression_SEC__))))))))))))1111111111= " + valuex);
                if (!valuex.isEmpty() && !valuex.equals("") && expressionValue != null) { //&& !valuex.equals("0")

                    if (expressionValue != null) {
                        if (type == CampaignFormElementType.YES_NO) {
                            ControlSwitchField.setValue((ControlSwitchField) dynamicField, expressionValue, true, YesNo.class, null);
                        } else if (type == CampaignFormElementType.RANGE) {
 

                            // IMPROVED: Better handling of range values
                            String valudex = valuex;
                            System.out.println("type == CampaignFormElementType.RANGE==== " + valuex);

                            System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 222222222222222222222");
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    valudex = String.valueOf((int) num); // Whole number
                                } else {
                                    // For range fields, round to integer
                                    valudex = String.valueOf((int) Math.round(num));
                                }
                                System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 333333333333333333333333");
                            } catch (NumberFormatException e) {

                                System.out.println(" NumberFormatExceptiontype == CampaignFormElementType.RANGE==== " + valuex);
                                valudex = valuex;
                                System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 44444444444444444444444");
                            }

                            if (valudex != null && !valudex.isEmpty()) {
                                System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 5555555555555555555555555");
                                // IMPROVED: Use field-specific update to prevent interference
                                if (dynamicField instanceof ControlTextEditFieldRange) {
                                    System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 6666666666666666666666666");
                                    if(valudex.equals("") || "0".equals(valudex)){
                                        ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, "0");
                                    }else{
                                        ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, valudex);

                                    }
                                    System.out.println("Sysypiytttttttttttttttttttttttt" + dynamicField.getValue());
                                    System.out.println(dynamicField.getCaption() + "descrptionSysypiytttttttttttttttttttttttt" + dynamicField.getDescription());
                                    System.out.println("Sysypiytttttttttttttttttttttttt" + dynamicField.getId());

                                }
                            }
                        }else if (type == CampaignFormElementType.NUMBER) {


                            String formatted;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    formatted = String.valueOf((int) num);
                                } else {
                                    formatted = String.format("%.2f", num);
                                }
                            } catch (NumberFormatException e) {
                                formatted = valuex;
                            }
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, formatted);

                        } else if (type == CampaignFormElementType.DECIMAL) {

                            String currentFieldValue = ((ControlDecimalEditField) dynamicField).getValue();

                            if (currentFieldValue != null && currentFieldValue.endsWith(".")) {
                                return;
                            }

                            if(orginalValue != null){
                                String currentValue = orginalValue.toString();
                                String newValue = expressionValue.toString();

                                if (newValue.contains(".") && !newValue.endsWith(".0")) {
                                    if(!currentValue.equals(newValue)){
                                        if(!(currentValue.isEmpty() && newValue.equals("0"))){
                                            ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                        }
                                    }
                                } else {
                                    String valudex = valuex.equals("0") ? null : valuex.endsWith(".0") ? valuex.replace(".0", "") : valuex;
                                    if(!currentValue.equals(valudex)){
                                        if(!(currentValue.isEmpty() && valudex == null)){
                                            ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                                        }
                                    }
                                }
                            } else {
                                String newValue = expressionValue.toString();

                                if (newValue.contains(".") && (newValue.endsWith(".") || newValue.split("\\.")[1].length() < 2)) {
                                    ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                } else {
                                    try {
                                        double num = Double.parseDouble(newValue);
                                        String valudex;
                                        if (num == 0) {
                                            valudex = String.valueOf((int) num);
                                        } else if (num == Math.floor(num)) {
                                            valudex = String.valueOf((int) num);
                                        } else {
                                            valudex = String.format("%.2f", num);
                                        }
                                        ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                                    } catch (NumberFormatException e) {
                                        ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                    }
                                }
                            }
                        } else if (expressionValue.getClass().isAssignableFrom(Boolean.class)) {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, (Double) (!Double.isFinite((double) expressionValue) ? 0 : expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : df.format((double) expressionValue)));
                        } else {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue == null ? null : expressionValue.toString());
                        }
                    }

                }else{
                    System.out.println("Expression not calculated handle expression sec");
                }
            }
        } catch (SpelEvaluationException e) {
            Log.e("xxx handle expError evaluating expression on field2 : " + dynamicField.getCaption(), e.getMessage());
        }
        if (type == CampaignFormElementType.RANGE) {
            dynamicField.setEnabled(true);
        }  else if (type == CampaignFormElementType.DECIMAL) {
            dynamicField.setEnabled(true);
        } else if (isDisIgnore) {
            dynamicField.setEnabled(true);
        }else{
            dynamicField.setEnabled(false);
        }

    }
 
    public static void handleExpressionSec(
            ExpressionParser expressionParser,
            List<CampaignFormDataEntry> formValues,
            CampaignFormElementType type,
            ControlPropertyField dynamicField,
            String expressionString,
            Boolean isDisIgnore,
            Object orginalValue,
            CampaignFormElement formElement) {
        try {
            if(!expressionString.isEmpty() && expressionString != null && !expressionString.equals("")) {
                final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
                String valuex = expressionValue + "";

                System.out.println("handleExpression_SEC__))))))))))))1111111111= " + valuex);
                if (!valuex.isEmpty() && !valuex.equals("") && expressionValue != null) { //&& !valuex.equals("0")

                    if (expressionValue != null) {
                        if (type == CampaignFormElementType.YES_NO) {
                            ControlSwitchField.setValue((ControlSwitchField) dynamicField, expressionValue, true, YesNo.class, null);
                        } else if (type == CampaignFormElementType.RANGE) {


                            // IMPROVED: Better handling of range values
                            String valudex = valuex;

                            boolean isZeroOrEmpty = (valudex == null || valudex.isEmpty() || valudex.equals("") ||
                                    valudex.equals("-9999"));


                            if (!isZeroOrEmpty) {

                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    valudex = String.valueOf((int) num); // Whole number
                                } else {
                                    // For range fields, round to integer
                                    valudex = String.valueOf((int) Math.round(num));
                                }
                            } catch (NumberFormatException e) {
                                valudex = valuex;
                            }

                            if (valudex != null && !valudex.isEmpty() && !valudex.equals("")) {
                                // IMPROVED: Use field-specific update to prevent interference
                                if (dynamicField instanceof ControlTextEditFieldRange) {
                                    ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, valudex);
                                    applyExpressionValue(formElement, formValues, valudex);

                                }
                            }

                        }else{
                                System.out.println("Expression resulted in zero or empty - allowing user input");
                                dynamicField.setEnabled(true);

                                if (valuex != null && (valuex.equals("-9999"))) {
                                    if (dynamicField instanceof ControlTextEditFieldRange) {
                                        ControlTextEditFieldRange rangeField = (ControlTextEditFieldRange) dynamicField;
                                        // Clear invalid values
                                        rangeField.setFieldValueFromExpression("");
                                        // Also clear the form data entry
                                        CampaignFormDataEntry entry = getOrCreateCampaignFormDataEntry(formValues, formElement);
                                        entry.setValue(null);
                                    }
                                }

                            }
                        }else if (type == CampaignFormElementType.NUMBER) {


                            String formatted;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    formatted = String.valueOf((int) num);
                                } else {
                                    formatted = String.format("%.2f", num);
                                }
                            } catch (NumberFormatException e) {
                                formatted = valuex;
                            }
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, formatted);

                        } else if (type == CampaignFormElementType.DECIMAL) {
 
                            String currentFieldValue = ((ControlDecimalEditField) dynamicField).getValue();

                            if (currentFieldValue != null && currentFieldValue.endsWith(".")) {
                                return;
                            }

                            if(orginalValue != null){
                                String currentValue = orginalValue.toString();
                                String newValue = expressionValue.toString();

                                if (newValue.contains(".") && !newValue.endsWith(".0")) {
                                    if(!currentValue.equals(newValue)){
                                        if(!(currentValue.isEmpty() && newValue.equals("0"))){
                                            ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                        }
                                    }
                                } else {
                                    String valudex = valuex.equals("0") ? null : valuex.endsWith(".0") ? valuex.replace(".0", "") : valuex;
                                    if(!currentValue.equals(valudex)){
                                        if(!(currentValue.isEmpty() && valudex == null)){
                                            ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                                        }
                                    }
                                }
                            } else {
                                String newValue = expressionValue.toString();

                                if (newValue.contains(".") && (newValue.endsWith(".") || newValue.split("\\.")[1].length() < 2)) {
                                    ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                } else {
                                    try {
                                        double num = Double.parseDouble(newValue);
                                        String valudex;
                                        if (num == 0) {
                                            valudex = String.valueOf((int) num);
                                        } else if (num == Math.floor(num)) {
                                            valudex = String.valueOf((int) num);
                                        } else {
                                            valudex = String.format("%.2f", num);
                                        }
                                        ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                                    } catch (NumberFormatException e) {
                                        ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, newValue);
                                    }
                                }
                             }
                        } else if (expressionValue.getClass().isAssignableFrom(Boolean.class)) {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, (Double) (!Double.isFinite((double) expressionValue) ? 0 : expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : df.format((double) expressionValue)));
                        } else {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue == null ? null : expressionValue.toString());
                        }
                    }
 
                }else{
                    System.out.println("Expression not calculated handle expression sec");
                }
            }
        } catch (SpelEvaluationException e) {
            Log.e("xxx handle expError evaluating expression on field2 : " + dynamicField.getCaption(), e.getMessage());
         }
        if (type == CampaignFormElementType.RANGE) {
            dynamicField.setEnabled(true);
        }  else if (type == CampaignFormElementType.DECIMAL) {
            dynamicField.setEnabled(true);
        } else if (isDisIgnore) {
            dynamicField.setEnabled(true);
        }else{
            dynamicField.setEnabled(false);
        }

    }


 // Runs only when a new form is opened
    public static void handleExpressionNewForm(
             ExpressionParser expressionParser,
            List<CampaignFormDataEntry> formValues,
            CampaignFormElementType type,
            ControlPropertyField dynamicField,
            String expressionString,
            Boolean isDisIgnore,
            Boolean isNewForm) {
         try {
            if(!expressionString.isEmpty() && expressionString != null && !expressionString.equals("")) {
                final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
                String valuex = expressionValue + "";
 

                System.out.println("handleExpression___))))))))))))1111111111NewForm= " + valuex);

                 if (!valuex.isEmpty() && !valuex.equals("") && expressionValue != null) {//&& !valuex.equals("0")

                    if (expressionValue != null) {
                        if (type == CampaignFormElementType.YES_NO) {
                            ControlSwitchField.setValue((ControlSwitchField) dynamicField, expressionValue, true, YesNo.class, null);
                        } else if (type == CampaignFormElementType.RANGE) {
 
                            // IMPROVED: Better handling of range values
                            String valudex = valuex;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    valudex = String.valueOf((int) num); // Whole number
                                } else {
                                    // For range fields, round to integer
                                    valudex = String.valueOf((int) Math.round(num));
                                }
                            } catch (NumberFormatException e) {
                                valudex = valuex;
                            }

                            if (valudex != null && !valudex.isEmpty() && !valudex.equals("")) {

                                System.out.println("NAH ME CAUSE AMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM 111111111");
                                // IMPROVED: Use field-specific update to prevent interference
                                if(isNewForm && (valudex.equals(0) || valudex.equals("0")) ){

                                    System.out.println("99999999999999New Form and ");
                                    if (dynamicField instanceof ControlTextEditFieldRange) {
                                        ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, "");
                                    }
                                }else{
                                    if (dynamicField instanceof ControlTextEditFieldRange) {
                                        ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, valudex);
                                    }
                                }

                            }
                        } else if (type == CampaignFormElementType.NUMBER) {

                            String formatted;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    formatted = String.valueOf((int) num);
                                } else {
                                    formatted = String.format("%.2f", num);
                                }
                            } catch (NumberFormatException e) {
                                formatted = valuex;
                            }
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, formatted);
                            //                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue.toString().equals("0") ? "0" : (expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : expressionValue.toString()));

                        } else if (type == CampaignFormElementType.DECIMAL) {
                            String valudex = valuex;
                            System.out.println("handleExpressionhandleExpressionhandleExpressionhandleExpression");
 //                            if (!valudex.isEmpty()) {
//                                    ControlTextEditField.setValue((ControlTextEditField) dynamicField, valudex.endsWith(".0") ? valudex.replace(".0","") : valudex);
//                                }
//                            }
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num != 0) {
                                    if (num == Math.floor(num)) {
                                        valudex = String.valueOf((int) num); // Whole number
                                    } else {
                                        valudex = String.format("%.2f", num); // Decimal to 2 dp
                                    }
                                }else{
                                    valudex = String.valueOf((int) num); // Whole number
                                }
                            } catch (NumberFormatException e) {
                                valudex = valuex; // fallback for non-numeric input
                            }

                            if (valudex != null && !valudex.isEmpty() && !valudex.toString().equals("")) {
 
                                System.out.println(valudex + "valudexvaludexvaludexvaludexvaludexvaludexvaludexdecimallllllllllll");
                                ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                            }
                        } else if (expressionValue.getClass().isAssignableFrom(Boolean.class)) {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, (Double) (!Double.isFinite((double) expressionValue) ? 0 : expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : df.format((double) expressionValue)));
                        } else {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue == null ? null : expressionValue.toString());
                        }
                    }
                }
            }else{
                System.out.println("Expression not calculated handle expression new form ");
            }
        } catch (SpelEvaluationException e) {
            Log.e("new xxxError evaluating expression on field2 : " + dynamicField.getCaption(), e.getMessage());
        }
        if (type == CampaignFormElementType.RANGE) {
            dynamicField.setEnabled(true);
        } else if (type == CampaignFormElementType.DECIMAL) {
            dynamicField.setEnabled(true);
        } else if (isDisIgnore) {
            dynamicField.setEnabled(true);
        }else{
            dynamicField.setEnabled(false);
        }

    }


    public static void handleExpression(
            ExpressionParser expressionParser,
            List<CampaignFormDataEntry> formValues,
            CampaignFormElementType type,
            ControlPropertyField dynamicField,
            String expressionString,
            Boolean isDisIgnore) {
        try {
            if(!expressionString.isEmpty() && expressionString != null && !expressionString.equals("")) {
                final Object expressionValue = getExpressionValue(expressionParser, formValues, expressionString);
                String valuex = expressionValue + "";

                System.out.println("handleExpression___))))))))))))1111111111= " + valuex);

                if (!valuex.isEmpty() && !valuex.equals("") && expressionValue != null) {//&& !valuex.equals("0")

                    if (expressionValue != null) {
                        if (type == CampaignFormElementType.YES_NO) {
                            ControlSwitchField.setValue((ControlSwitchField) dynamicField, expressionValue, true, YesNo.class, null);
                        } else if (type == CampaignFormElementType.RANGE) {
                            // IMPROVED: Better handling of range values
                            String valudex = valuex;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    valudex = String.valueOf((int) num); // Whole number
                                } else {
                                    // For range fields, round to integer
                                    valudex = String.valueOf((int) Math.round(num));
                                }
                            } catch (NumberFormatException e) {
                                valudex = valuex;
                            }

                            if (valudex != null && !valudex.isEmpty() && !valudex.equals("")) {
                                // IMPROVED: Use field-specific update to prevent interference
                                if (dynamicField instanceof ControlTextEditFieldRange) {
                                    ControlTextEditFieldRange.setValue((ControlTextEditFieldRange) dynamicField, valudex);
                                }
                             }
                        } else if (type == CampaignFormElementType.NUMBER) {

                            String formatted;
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num == Math.floor(num)) {
                                    formatted = String.valueOf((int) num);
                                } else {
                                    formatted = String.format("%.2f", num);
                                }
                            } catch (NumberFormatException e) {
                                formatted = valuex;
                            }
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, formatted);
                            //                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue.toString().equals("0") ? "0" : (expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : expressionValue.toString()));

                        } else if (type == CampaignFormElementType.DECIMAL) {
                            String valudex = valuex;
                            System.out.println("handleExpressionhandleExpressionhandleExpressionhandleExpression");
//                            if (!valudex.isEmpty()) {
//                                    ControlTextEditField.setValue((ControlTextEditField) dynamicField, valudex.endsWith(".0") ? valudex.replace(".0","") : valudex);
//                                }
//                            }
                            try {
                                double num = Double.parseDouble(valuex);
                                if (num != 0) {
                                    if (num == Math.floor(num)) {
                                        valudex = String.valueOf((int) num); // Whole number
                                    } else {
                                        valudex = String.format("%.2f", num); // Decimal to 2 dp
                                    }
                                }else{
                                    valudex = String.valueOf((int) num); // Whole number
                                }
                            } catch (NumberFormatException e) {
                                valudex = valuex; // fallback for non-numeric input
                            }

                            if (valudex != null && !valudex.isEmpty() && !valudex.toString().equals("")) {
                                System.out.println(valudex + "valudexvaludexvaludexvaludexvaludexvaludexvaludexdecimallllllllllll");
                                ControlDecimalEditField.setValue((ControlDecimalEditField) dynamicField, valudex);
                            }
                        } else if (expressionValue.getClass().isAssignableFrom(Boolean.class)) {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, (Double) (!Double.isFinite((double) expressionValue) ? 0 : expressionValue.toString().endsWith(".0") ? expressionValue.toString().replace(".0", "") : df.format((double) expressionValue)));
                        } else {
                            ControlTextEditField.setValue((ControlTextEditField) dynamicField, expressionValue == null ? null : expressionValue.toString());
                        }
                    }
 
                }else{
                    System.out.println("Expression cant calculate becuae of  null ");
                }
            }
        } catch (SpelEvaluationException e) {

            Log.e("xxxError evaluating expression on field2 : " + dynamicField.getCaption(), e.getMessage());
         }
        if (type == CampaignFormElementType.RANGE) {
            dynamicField.setEnabled(true);
        } else if (type == CampaignFormElementType.DECIMAL) {
            dynamicField.setEnabled(true);
        } else if (isDisIgnore) {
            dynamicField.setEnabled(true);
        }else{
            dynamicField.setEnabled(false);
        }

    }

    public static Object getExpressionValue(ExpressionParser expressionParser, List<CampaignFormDataEntry> formValues, String expressionString)
            throws SpelEvaluationException, EvaluationException {
 
        System.out.println("111111111 getExpressionValue" + expressionString);

        final EvaluationContext context = refreshEvaluationContext(formValues);
        final Expression expression = expressionParser.parseExpression(expressionString);

        try {
            final Class<?> valueType = expression.getValueType(context);
            final Object valueFin = expression.getValue(context, valueType);

            System.out.println(valueType + "valueType value fin is null EmptyStackException: >>>>>>-" + valueFin);


            if(valueFin != null) {
                if (!valueFin.getClass().isAssignableFrom(valueType)) {
                    System.out.println("EmptyStackException: >>>>>>-");
                    throw new EmptyStackException();
                }
                return valueFin;
            } else {

                System.out.println(" value fin is null EmptyStackException: >>>>>>-");

                // Handle null values gracefully - return 0 for numeric expressions
                // Check if this is likely a numeric expression
                if (expressionString.contains("*") || expressionString.contains("+") ||
                        expressionString.contains("-") || expressionString.contains("/")) {
                    return 0; // Return 0 instead of null for mathematical expressions
                }
                return null;
            }
        } catch (SpelEvaluationException e) {
            // If there's a null operation error, return 0 for numeric fields
            if (e.getMessage().contains("The operator") && e.getMessage().contains("is not supported between objects of type 'null'")) {
                System.out.println("Null operation detected, returning 0");
                return 0;
            }
            throw e;
        }
    }

//    public static Object getExpressionValue(ExpressionParser expressionParser, List<CampaignFormDataEntry> formValues, String expressionString)
//            throws SpelEvaluationException, EvaluationException {
//        System.out.println("111111111 getExpressionValue" +expressionString);
//        final EvaluationContext context = refreshEvaluationContext(formValues);
//        final Expression expression = expressionParser.parseExpression(expressionString);
//        final Class<?> valueType = expression.getValueType(context);
//        final Object valueFin = expression.getValue(context, valueType);
//        if(valueFin != null) {
//            if (!valueFin.getClass().isAssignableFrom(valueType)) {
//                System.out.println("EmptyStackException: >>>>>>-");
//                throw new EmptyStackException();
//            }
//        } else{
//            throw new SpelEvaluationException(SpelMessage.NOT_A_REAL);
//        }
//        return expression.getValue(context, valueType);
//    }

     public static void handleDependingOnSectionAndLabel(
            Map<String, ControlPropertyField> fieldMap,
            CampaignFormElement campaignFormElement,
            LinearLayout dynamicField) {
        final String dependingOn = campaignFormElement.getDependingOn();
        final String[] dependingOnValues = campaignFormElement.getDependingOnValues();

        List<String> constraints;
        String depenValuexd = null;
        if (dependingOnValues != null) {
            constraints = (List) Arrays.stream(dependingOnValues).collect(Collectors.toList());
            ListIterator<String> lstItemsx = constraints.listIterator();
            if (lstItemsx.hasNext()) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@1111>>>>>>>> "+lstItemsx);

                depenValuexd = lstItemsx.next().toString();
            }
        }

        System.out.println(dependingOn + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222>>>>>>>> "+depenValuexd);

        final String depenValuex = depenValuexd;

        if (dependingOn != null && depenValuex != null) {

            System.out.println(dependingOn + "111111Not nulll @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222>>>>>>>> "+depenValuexd);

            ControlPropertyField controlPropertyField = fieldMap.get(dependingOn);

            System.out.println(controlPropertyField + "111111------- @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222>>>>>>>> "+controlPropertyField.getValue());

            setVisibilityDependencyForSectionAndLabel(dynamicField, depenValuex, controlPropertyField.getValue());
        };
    }

    public static void handleDependingOn(
            Map<String, ControlPropertyField> fieldMap,
            CampaignFormElement campaignFormElement,
            ControlPropertyField dynamicField,
            List<CampaignFormDataEntry> formValues) {
        final String dependingOn = campaignFormElement.getDependingOn();
        final String[] dependingOnValues = campaignFormElement.getDependingOnValues();

       List<String> constraints;
       String depenValuexd = null;
        if (dependingOnValues != null) {
            constraints = (List) Arrays.stream(dependingOnValues).collect(Collectors.toList());
            ListIterator<String> lstItemsx = constraints.listIterator();
            while (lstItemsx.hasNext()) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@1111>>>>>>>> "+lstItemsx);

                depenValuexd = lstItemsx.next().toString();
            }
        }

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222>>>>>>>> "+depenValuexd);

        final String depenValuex = depenValuexd;

        if (dependingOn != null && depenValuex != null) {
            ControlPropertyField controlPropertyField = fieldMap.get(dependingOn);
            setVisibilityDependency(dynamicField, depenValuex, controlPropertyField.getValue(), fieldMap, formValues, campaignFormElement);
            final ControlPropertyField finalDynamicField = dynamicField;
            controlPropertyField.addValueChangedListener(field -> {
                setVisibilityDependency(dynamicField, depenValuex, field.getValue(), fieldMap, formValues, campaignFormElement);
            });
            };
        }

    public static void setVisibilityDependency(ControlPropertyField field, String dependingOnValues, Object dependingOnFieldValue, Map<String, ControlPropertyField> fieldMap, List<CampaignFormDataEntry> formValues, CampaignFormElement campaignFormElement) {
 
        System.out.println(dependingOnValues+ " =1111 static   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22>>>>>>>> dynamic = "+dependingOnFieldValue + "BBBTT" +  field.getValue());
 
        String parsedDependingOnFieldValue = dependingOnFieldValue == null
                ? ""
                : dependingOnFieldValue instanceof Boolean
                ? YesNoUnknown.valueOf(((Boolean) dependingOnFieldValue).booleanValue()).name()
                : dependingOnFieldValue.toString().equalsIgnoreCase("Yes") ? "true" : dependingOnFieldValue.toString().equalsIgnoreCase("No") ? "false" : dependingOnFieldValue.toString();

        if (dependingOnValues.contains("!")) {
            dependingOnValues = dependingOnValues.replace("!", "");
            if (dependingOnValues.contains(parsedDependingOnFieldValue)) {

                if(field.getValue() != null){
                    clearFormValue(campaignFormElement.getId(), formValues);
 
                    field.setValue(null);
                }
                field.setVisibility(View.GONE);
            } else {
                if(field.getValue() != null){
                    clearFormValue(campaignFormElement.getId(), formValues);
                    field.setValue(null);
                }

                 field.setVisibility(View.VISIBLE);
            }
        } else {
            if (dependingOnValues.equalsIgnoreCase(parsedDependingOnFieldValue)) {
 
                if(field.getValue() != null){
                    clearFormValue(campaignFormElement.getId(), formValues);
                    field.setValue(null);
                }

                 field.setVisibility(View.VISIBLE);
            } else {
                if(field.getValue() != null){
                    clearFormValue(campaignFormElement.getId(), formValues);
 
                    field.setValue(null);
                 }
                field.setVisibility(View.GONE);
            }
        }
    }

    public static void setVisibilityDependency(ControlPropertyField field, String dependingOnValues, Object dependingOnFieldValue, Map<String, ControlPropertyField> fieldMap) {

 
        System.out.println(dependingOnValues+ " =22222 static   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22>>>>>>>> dynamic = "+dependingOnFieldValue + "BBBTT" +  field.getValue());

         String parsedDependingOnFieldValue = dependingOnFieldValue == null
                ? ""
                : dependingOnFieldValue instanceof Boolean
                ? YesNoUnknown.valueOf(((Boolean) dependingOnFieldValue).booleanValue()).name()
                : dependingOnFieldValue.toString().equalsIgnoreCase("Yes") ? "true" : dependingOnFieldValue.toString().equalsIgnoreCase("No") ? "false" : dependingOnFieldValue.toString();

        if (dependingOnValues.contains("!")) {
            dependingOnValues = dependingOnValues.replace("!", "");
            if (dependingOnValues.contains(parsedDependingOnFieldValue)) {
                field.setVisibility(View.GONE);
            } else {
                field.setVisibility(View.VISIBLE);
            }
        } else {
            if (dependingOnValues.equalsIgnoreCase(parsedDependingOnFieldValue)) {
                field.setVisibility(View.VISIBLE);
            } else {
 
                field.setVisibility(View.GONE);

            }
        }
    }

    public static void setVisibilityDependencyForSectionAndLabel(LinearLayout field, String dependingOnValues, Object dependingOnFieldValue) {
        System.out.println(dependingOnValues+ " = static --  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22>>>>>>>> dynamic = "+dependingOnFieldValue);

        String parsedDependingOnFieldValue = dependingOnFieldValue == null
                ? ""
                : dependingOnFieldValue instanceof Boolean
                ? YesNoUnknown.valueOf(((Boolean) dependingOnFieldValue).booleanValue()).name()
                : dependingOnFieldValue.toString().equalsIgnoreCase("Yes") ? "true" : dependingOnFieldValue.toString().equalsIgnoreCase("No") ? "false" : dependingOnFieldValue.toString();

        System.out.println(" = dddddddddddddddddddddddddddddddd   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22>>>>>>>> dynamic = " + parsedDependingOnFieldValue);
        if (dependingOnValues.contains("!")) {
            dependingOnValues = dependingOnValues.replace("!", "");
            if (dependingOnValues.contains(parsedDependingOnFieldValue)) {
                field.setVisibility(View.GONE);
              } else {
                field.setVisibility(View.VISIBLE);
            }
        } else {
            if (dependingOnValues.equalsIgnoreCase(parsedDependingOnFieldValue)) {
                field.setVisibility(View.VISIBLE);
            } else {
                field.setVisibility(View.GONE);
            }
        }
    }

    public static void clearFormValue(String fieldId, List<CampaignFormDataEntry> formValues) {
        if (formValues != null) {
            formValues.removeIf(entry -> fieldId.equals(entry.getId()));
        }
    }

    private static boolean containsIgnoreCase(List<String> list, String soughtFor) {
        for (String current : list) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return true;
            }
        }
        return false;
    }

    public static CampaignFormDataEntry getOrCreateCampaignFormDataEntry(
            List<CampaignFormDataEntry> formValues,
            CampaignFormElement campaignFormElement) {
        for (CampaignFormDataEntry campaignFormDataEntry : formValues) {
            if (campaignFormDataEntry.getId().equals(campaignFormElement.getId())) {
                return campaignFormDataEntry;
            }
        }
        final CampaignFormDataEntry newCampaignFomDataEntry = new CampaignFormDataEntry(campaignFormElement.getId(), null);
        formValues.add(newCampaignFomDataEntry);
        return newCampaignFomDataEntry;
    }

    public static Map<String, String> getUserTranslations(CampaignFormMeta campaignFormMeta) {
        final Map<String, String> userTranslations = new HashMap<>();

        final Locale locale = I18nProperties.getUserLanguage().getLocale();
        if (locale != null) {
            final List<CampaignFormTranslations> campaignFormTranslations = campaignFormMeta.getCampaignFormTranslations();
            campaignFormTranslations.forEach(cft -> {
                if (cft.getLanguageCode().equalsIgnoreCase(locale.toString())) {
                    cft.getTranslations()
                            .forEach(translationElement -> userTranslations.put(translationElement.getElementId(), translationElement.getCaption()));
                }
            });
        }
        return userTranslations;
    }

    public static String getUserLanguageCaption(Map<String, String> userTranslations, CampaignFormElement campaignFormElement) {
        if (userTranslations != null && userTranslations.containsKey(campaignFormElement.getId())) {
            return userTranslations.get(campaignFormElement.getId());
        } else {
            return campaignFormElement.getCaption();
        }
    }

    public static ControlTextEditField createControlTextEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired) {
        return new ControlTextEditField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }

            //	@Override
            //	public int getMinLength() {
            //		return DEFAULT_MIN_LENGTH;
            //	}
//
            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired, false, null, null, false, false);
            }
        };
    }

    public static ControlPhoneField createControlPhoneField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired) {
        return new ControlPhoneField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }

            //	@Override
            //	public int getMinLength() {
            //		return DEFAULT_MIN_LENGTH;
            //	}
//
            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired, false, null, null, false, false);
            }
        };
    }


    public static ControlTextEditField createControlTextEditFieldWithErrorMessage(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            String errorMsg) {
        return new ControlTextEditField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }

            //	@Override
            //	public int getMinLength() {
            //		return DEFAULT_MIN_LENGTH;
            //	}

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(false);
                initInput(isIntegerField, isRequired, false, null, null, false, false);
            }
        };
    }


    public static ControlTextEditField createControlTextEditFieldRangex(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            String errorMsg,
            Boolean warnOnError) {
        return new ControlTextEditField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
//                initLabelAndValidationListeners();
                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired, true, null, null, true, false);

            }
        };
    }


    public static ControlTextEditField createControlTextEditFieldRangex(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            String errorMsg) {
        return new ControlTextEditField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
//                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired, true, null, null, true, false);

            }
        };
    }


    public static ControlTextEditField createControlTextEditFieldRange(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            Integer minVal,
            Integer maxVal,
            Boolean isExpression,
            Boolean warnOnError) {

        System.out.println(context+" --------------------- running range stage 1 : "+isExpression);
        final boolean isExpressionx = isExpression;
        return new ControlTextEditField(context) {



            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return 8;
            }

            @Override
            public int getMinLength() {
                return 1;
            }

            //
            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired, true, minVal, maxVal, isExpressionx, warnOnError);
            }
        };
    }


    public static ControlTextEditFieldRange createControlTextEditFieldRangexOnlyExpression(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            String errorMsg) {
        return new ControlTextEditFieldRange(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
//                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(true);
//                initInput(isIntegerField, isRequired, true, null, null, true, false);

                initInput(true, isRequired, true, null, null, true, false);


            }
        };
    }

 
    public static ControlTextEditFieldRange createControlTextEditFieldRangexOnlyExpressionNew(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            String errorMsg,
            Boolean isNewForm) {
        return new ControlTextEditFieldRange(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
//                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(true);
//                initInput(isIntegerField, isRequired, true, null, null, true, false);

                initInput(true, isRequired, true, null, null, true, false);


            }
        };
    }

     public static ControlTextEditFieldRange createControlTextEditFieldRangeOnly(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            Boolean isRequired,
            Integer minVal,
            Integer maxVal,
            Boolean isExpression,
            Boolean warnOnError) {

        System.out.println(context+" --------------------- running range stage 1 : "+isExpression);
        final boolean isExpressionx = isExpression;
        return new ControlTextEditFieldRange(context) {



            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return 8;
            }

            @Override
            public int getMinLength() {
                return 1;
            }

            //
            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
//                initInput(isIntegerField, isRequired, true, minVal, maxVal, isExpressionx, warnOnError);
                initInput(true, isRequired, true, minVal, maxVal, isExpressionx, warnOnError);

            }
        };
    }


    public static ControlDecimalEditField createControlTextEditFieldDecimal(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isDecimalField,
            Boolean isRequired,
            Integer minVal,
            Integer maxVal,
            Boolean isExpression,
            Boolean warnOnError) {
        final boolean isExpressionx = isExpression;
        return new ControlDecimalEditField(context) {
            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return 8;
            }

            @Override
            public int getMinLength() {
                return 1;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initInput(isDecimalField, isRequired, true, minVal, maxVal, isExpressionx, warnOnError);
//                setVisibility(GONE);
            }
        };
    }

    public static ControlDecimalEditField createControlTextEditFieldDecimalExpression(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isDecimalField,
            Boolean isRequired,
            String errorMsg) {

        System.out.println(context+" --------------------- running decimal stage 1 : "+ isDecimalField);
        return new ControlDecimalEditField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            public int getMaxLength() {
                return CHARACTER_LIMIT_DEFAULT;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
//                initLabelAndValidationListeners();
                initLabelAndValidationListenersErrorMsg(errorMsg);
                setLiveValidationDisabled(true);
                initInput(isDecimalField, isRequired, true, null, null, true, false);
            }
        };
    }


    //to fix required for dropdown
    public static ControlSpinnerField createControlSpinnerFieldEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Map<String, String> isIntegerField,
            boolean isRequired) {
        return new ControlSpinnerField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initInput(isIntegerField, isRequired);
            }
        };
    }


    public static ControlSpinnerField createControlSpinnerFieldEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
//            Map<String, String> isIntegerField) {
            Map<String, String> optionsList) {


        return new ControlSpinnerField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
//                initInput(isIntegerField);
                initInput(optionsList);


            }
        };
    }


    public static ControlDateField createControlDateEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            FragmentManager fm, boolean isRequired) {
        return new ControlDateField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }


            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initializeDateField(fm);
                initInput(true, isRequired);
            }
        };
    }


    public static ControlCheckBoxGroupField createControlMultiSelectCheckBoxEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Map<String, String> optionValues,
            List<?> selectedKeys,
            boolean isRequired) {

        return new ControlCheckBoxGroupField(context) {
            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);

                // Initialize the parent field components
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(false);

                // Set the group label text from campaign form element
                String labelText = getUserLanguageCaption(userTranslations, campaignFormElement);
                if (labelText != null && !labelText.isEmpty()) {
                    setGroupLabel(labelText);
                }

                if (isRequired) {
                    setRequired(true);
                }

                // Set up the checkbox options
                if (optionValues != null && !optionValues.isEmpty()) {
                    System.out.println("DEBUG - Options: " + optionValues);

                    // Convert selectedKeys to proper format for the field
                    List<String> finalSelectedKeys = new ArrayList<>();
                    if (selectedKeys != null && !selectedKeys.isEmpty()) {
                        for (Object key : selectedKeys) {
                            if (key != null) {
                                String strKey = key.toString().trim();
                                if (!strKey.isEmpty()) {
                                    finalSelectedKeys.add(strKey);
                                }
                            }
                        }
                    }

                    System.out.println("DEBUG - Setting selected keys: " + finalSelectedKeys);
                    setOptionsAndValue(optionValues, finalSelectedKeys);
                }
            }


        };
    }
//
//    public static ControlCheckBoxGroupField createControlMultiSelectCheckBoxEditField (
//        CampaignFormElement campaignFormElement,
//        Context context,
//        Map<String, String> userTranslations,
//        Map<String, String> optionValues,
//        List<?> selectedKeys,
//        boolean isRequired) {
//
//    return new ControlCheckBoxGroupField(context) {
//        @Override
//        protected String getPrefixDescription() {
//            return getUserLanguageCaption(userTranslations, campaignFormElement);
//        }
//
//        @Override
//        protected String getPrefixCaption() {
//            return getUserLanguageCaption(userTranslations, campaignFormElement);
//        }
//
//        @Override
//        public int getTextAlignment() {
//            return View.TEXT_ALIGNMENT_VIEW_START;
//        }
//
//        @Override
//        public int getGravity() {
//            return Gravity.CENTER_VERTICAL;
//        }
//
//        @Override
//        protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
//            super.inflateView(context, attrs, defStyle);
//
//            // Initialize the parent field components
//            initLabel();
//            initLabelAndValidationListeners();
//            setLiveValidationDisabled(true);
//
//            // Set the group label text from campaign form element
//            String labelText = getUserLanguageCaption(userTranslations, campaignFormElement);
//            if (labelText != null && !labelText.isEmpty()) {
//                setGroupLabel(labelText);
//            }
//
//            if (isRequired) {
//                setRequired(true);
//            }
//
//            // IMPORTANT: Set up the checkbox options FIRST
//            if (optionValues != null && !optionValues.isEmpty()) {
//                System.out.println("Setting up options in createControlCheckBoxField: " + optionValues);
//                setOptionsAndValue(optionValues, selectedKeys);
//            }
//        }
//
//        public void handleCampaignFormErrors(String errorMessage, boolean warnOnError) {
//            if (errorMessage != null && !errorMessage.isEmpty()) {
//                if (warnOnError) {
//                    showError("Warning: " + errorMessage);
//                } else {
//                    showError(errorMessage);
//                }
//            }
//        }
//    };
//}

    public static ControlCheckBoxGroupField createControlMultiSelectCheckBoxField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Map<String, String> optionValues,
            boolean isRequired) {

        return new ControlCheckBoxGroupField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);

                // Initialize the parent field components
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);

                // Set the group label text from campaign form element
                String labelText = getUserLanguageCaption(userTranslations, campaignFormElement);
                if (labelText != null && !labelText.isEmpty()) {
//                    setGroupLabel(labelText);
                }

                // Set up the checkbox options
                if (optionValues != null && !optionValues.isEmpty()) {
                    setOptions(optionValues);
                }

//                 Configure required field if needed
                 if (isRequired) {
                     setRequired(true);
                 }
            }


            // Handle error messages from campaign form element
            public void handleCampaignFormErrors(String errorMessage, boolean warnOnError) {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    if (warnOnError) {
                        showError("Warning: " + errorMessage);
                    } else {
                        showError(errorMessage);
                    }
                }
            }
        };
    }



    public static ControlCheckBoxField createControlCheckBoxField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations) {
        return new ControlCheckBoxField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                //required = true;

                initInput();
            }
        };
    }



    public static ControlSwitchField createControlYesNoUnknownField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations) {
        return new ControlSwitchField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                required = true;

               // initialize();
                setLiveValidationDisabled(true);
                initInputFirst();
            }
        };
    }

    public static ControlTextReadField createControlTextReadField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations) {
        return new ControlTextReadField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            public int getMaxLines() {
                return 1;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initTextView();
            }
        };
    }

    public class ControlEmailEditField extends ControlTextEditField {
        private String errorMessage;
        private boolean isRequired;

        public ControlEmailEditField(Context context, String errorMessage, boolean isRequired) {
            super(context);
            this.errorMessage = errorMessage;
            this.isRequired = isRequired;
            initEmailField();
        }

        private void initEmailField() {
            // Access the EditText through the appropriate method from parent class
            // Assuming your parent class has a method to get the input view
            TextView editText = getInputView(); // Or whatever method provides the input field

            if (editText != null) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                addValueChangedListener(new ValueChangeListener() {
                    @Override
                    public void onChange(ControlPropertyField field) {
                        validateEmail(field);
                    }
                });
            }
        }

        private void validateEmail(ControlPropertyField field) {
            String input = field.getValue() != null ? field.getValue().toString() : "";

            if (input.isEmpty() && !isRequired) {
                disableErrorState();
                return;
            }

            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();

            if (!isValid) {
                enableErrorState(errorMessage);
            } else {
                disableErrorState();
            }
        }

        // Helper method to get the input view
        // This should match whatever method your parent class uses
        private EditText getInputView() {
            // Check if your parent class has a method like getEditText()
            // If not, you may need to find the view by ID
            try {
                return findViewById(R.id.date_input); // Use your actual EditText ID
            } catch (Exception e) {
                return null;
            }
        }
    }

    
    public static ControlTimeField createControlTimeEditField(
            CampaignFormElement campaignFormElement,
            Context context,
            Map<String, String> userTranslations,
            Boolean isIntegerField,
            FragmentManager fm,
            boolean isRequired) {

        return new ControlTimeField(context) {

            @Override
            protected String getPrefixDescription() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            protected String getPrefixCaption() {
                return getUserLanguageCaption(userTranslations, campaignFormElement);
            }

            @Override
            public int getTextAlignment() {
                return View.TEXT_ALIGNMENT_VIEW_START;
            }

            @Override
            public int getGravity() {
                return Gravity.CENTER_VERTICAL;
            }

            @Override
            protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
                super.inflateView(context, attrs, defStyle);
                initLabel();
                initLabelAndValidationListeners();
                setLiveValidationDisabled(true);
                initializeTimeField(fm);
                initInput(false, isRequired, false, 0, 1000, false, false);
            }
        };
    }

}
