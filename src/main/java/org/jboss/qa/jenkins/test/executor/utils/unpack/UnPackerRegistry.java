/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.jenkins.test.executor.utils.unpack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class UnPackerRegistry {

	private static List<UnPacker> unPackers;

	private UnPackerRegistry() {
	}

	public static UnPacker get(File file) {
		if (unPackers == null) {
			unPackers = new ArrayList<>();
			final ServiceLoader<UnPacker> unPackerServiceLoader = ServiceLoader.load(UnPacker.class);
			for (UnPacker unPacker : unPackerServiceLoader) {
				unPackers.add(unPacker);
			}
		}
		for (UnPacker unPacker : unPackers) {
			if (unPacker.handles(file)) {
				return unPacker;
			}
		}
		return null;
	}
}
