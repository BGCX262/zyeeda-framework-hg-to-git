/*
 * Copyright 2010 Zyeeda Co. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.zyeeda.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.zyeeda.framework.intfs.SearchHandler;
import com.zyeeda.framework.intfs.TreeNode;

/**
 * Tree utils.
 * 
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class TreeUtils {

	@SuppressWarnings("unchecked")
	public static <NODE extends TreeNode, RESULT> RESULT breadthFirstSearch(
			Collection<NODE> nodes, SearchHandler<NODE, RESULT> handler) throws Exception {
		
		List<NODE> cache = new ArrayList<NODE>(nodes);
		while (cache.size() > 0) {
			NODE node = cache.remove(0);
			cache.addAll((Collection<? extends NODE>) node.getChildren());
			
			handler.process(node);
		}
		
		return handler.getResult();
	}
}
