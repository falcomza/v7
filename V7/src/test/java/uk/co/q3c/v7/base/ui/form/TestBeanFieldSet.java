/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.base.ui.form;

import uk.co.q3c.v7.base.data.TestEntity;
import uk.co.q3c.v7.i18n.AnnotationI18NTranslator;
import uk.co.q3c.v7.i18n.DescriptionKey;
import uk.co.q3c.v7.i18n.I18N;
import uk.co.q3c.v7.i18n.LabelKey;

import com.vaadin.ui.TextField;

public class TestBeanFieldSet extends BeanFieldSet<TestEntity> {

	@I18N(caption = LabelKey.First_Name)
	private TextField firstName;
	@I18N(caption = LabelKey.Last_Name, description = DescriptionKey.Last_Name)
	private TextField lastName;

	protected TestBeanFieldSet(AnnotationI18NTranslator translator) {
		super(translator);
	}

	public TextField getFirstName() {
		return firstName;
	}

	public TextField getLastName() {
		return lastName;
	}
}
