/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.entity.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.model.ModelField.EncryptMethod;
import org.w3c.dom.Element;
import java.util.LinkedList;

/**
 * This class extends ModelViewEntity and provides additional information appropriate to performing entity unions
 */
@SuppressWarnings("serial")
public class ModelUnionViewEntity extends ModelViewEntity {
    public static final String module = ModelViewEntity.class.getName();

    // alias used in union views
    public static final String UNION_VIEW_ALIAS = "uva";

    /** Indicates if all unioned records should be returned (even if they are identical) default is false */ 
    protected boolean unionAll;
    
    /** List of union aliases with information in addition to what is in the standard field list */
    protected List<ModelUnionField> unionFields = new LinkedList<ModelUnionField>();

    public ModelUnionViewEntity(ModelReader reader, Element entityElement, UtilTimer utilTimer, ModelInfo def) {
        super(reader, entityElement, utilTimer, def);
        this.unionAll = UtilXml.checkBoolean(entityElement.getAttribute("union-all"), false);
        
        for (Element unionAliasElement: UtilXml.childElementList(entityElement, "union-field")) {
            ModelUnionField alias = new ModelUnionField(unionAliasElement);
            this.unionFields.add(alias);
        }
    }

    public boolean getUnionAll() {
        return this.unionAll;
    }
    
    public int getUnionFieldsSize() {
        return this.unionFields.size();
    }

    public Iterator<ModelUnionField> getUnionFieldsIterator() {
        return this.unionFields.iterator();
    }

    public List<ModelUnionField> getUnionFieldsCopy() {
        List<ModelUnionField> newList = new LinkedList<ModelUnionField>();
        newList.addAll(this.unionFields);
        return newList;
    }

    public void populateFieldsBasic(ModelReader modelReader) {
        super.populateFieldsBasic(modelReader);

        for (ModelUnionField unionFld: unionFields) {
            String fieldType = null;
            EncryptMethod encrypt = EncryptMethod.FALSE;

            for (ModelAlias alias : unionFld.getMemberFields()) {
                ModelEntity aliasedEntity = getAliasedEntity(alias.entityAlias, modelReader);
                if ( aliasedEntity == null ) {
                    Debug.logError("[ModelViewEntity.populateFields (" + this.getEntityName() + ")] ERROR: ModelUnionField could not find Aliased entity for alias : " +
                        alias.entityAlias, module);
                    continue;
                }
                ModelField aliasedField = getAliasedField(aliasedEntity, alias.field, modelReader);
                if (aliasedField == null) {
                    Debug.logError("[ModelViewEntity.populateFields (" + this.getEntityName() + ")] ERROR: ModelUnionField could not find ModelField for field name \"" +
                        alias.field + "\" on entity with name: " + aliasedEntity.getEntityName(), module);
                } else {
                    String aliasedFieldType = aliasedField.getType();

                    if (fieldType == null) {
                        fieldType = aliasedFieldType;
                    } else {
                        // Ensure that all members have the same type -- we strip out any "-ne" business so we match id with id-ne (HACK)
                        if (!fieldType.replace("-ne", "").equals(aliasedFieldType.replace("-ne", ""))) {
                            Debug.logError("ModelUnionField [" + this.entityName + "] has inconsistent UnionMemberField types, first member had " +
                                    "type [" + fieldType + "] while EntityAlias [" + alias.getEntityAlias() + "] Name [" + aliasedField.getName() +
                                    "] had type [" + aliasedFieldType + "]", module);
                        }
                    }
                    
                    encrypt = aliasedField.getEncryptMethod();
                }
            }

            if (fieldType == null) {
                throw new RuntimeException("ModelUnionField [" + this.entityName + "] unable to getType - member fields resulted in null type");
            }

            String colName = UNION_VIEW_ALIAS + "." + ModelUtil.javaNameToDbName(unionFld.name);
            
            ModelField field = ModelField.create(this, unionFld.description, unionFld.name, fieldType, colName, null, null, false, false, encrypt, false, false, null);

            // if this is a groupBy field, add it to the groupBys list
            if (unionFld.groupBy || groupByFields.contains(unionFld.name)) {
                this.groupBys.add(field);
            }
            
            this.addField(field);
        }
    }

    @Override
    public String toString() {
        return "ModelUnionViewEntity[" + getEntityName() + "]";
    }

    public static class ModelUnionField implements Serializable {
        protected String name = "";
        protected boolean groupBy = false;
        // The description for documentation purposes
        protected String description = "";
        protected List<ModelAlias> memberFields = new LinkedList<ModelAlias>();

        protected ModelUnionField() {}

        public ModelUnionField(Element element) {
            this.name = UtilXml.checkEmpty(element.getAttribute("name")).intern();
            this.groupBy = "true".equals(UtilXml.checkEmpty(element.getAttribute("group-by")));
            List<? extends Element> memberElements = UtilXml.childElementList(element, "union-member-field");
            if (memberElements != null) {
                for ( Element aliasElement : memberElements ) {
                    String entityAlias = UtilXml.checkEmpty(aliasElement.getAttribute("entity-alias")).intern();
                    String field = UtilXml.checkEmpty(aliasElement.getAttribute("field"), this.name).intern();    // default to union field name if not specified
                    
                    ModelAlias alias = new ModelAlias(entityAlias, this.name, field, null, null, null, null, null);
                    memberFields.add(alias);
                }
            }
        }
        
        public ModelUnionField(String name, Boolean groupBy) {
            this.name = name;
            if (groupBy != null) {
                this.groupBy = groupBy.booleanValue();
            } else {
                this.groupBy = false;
            }
        }
        
        public String getName() {
            return this.name;
        }

        public List<ModelAlias> getMemberFields() {
            return memberFields;
        }
    }
}