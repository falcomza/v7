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
package uk.co.q3c.v7.demo.config;

import javax.inject.Provider;

import org.apache.shiro.config.Ini.Section;

import uk.co.q3c.v7.base.config.V7Ini;

/**
 * /** Replaces the configured database setting with an in memory db, as this is generally what is required for testing.
 * *
 * 
 * @author David Sowerby 16 Feb 2013
 * 
 */
public class TestDemoIniProvider implements Provider<V7Ini> {

	@Override
	public DemoIni get() {
		DemoIni ini = new DemoIni();
		// exceptions are all handled in the load method
		ini.loadFromPath("classpath:V7.ini");
		Section section = ini.getSection("db");
		section.put("dbURL", "memory:scratchpad");
		return ini;
	}

}