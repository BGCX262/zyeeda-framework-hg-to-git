package com.zyeeda.framework.knowledge.internal;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.service.AbstractService;

public class DroolsKnowledgeServiceProvider extends AbstractService implements
		KnowledgeService<KnowledgeBase> {
	
	private KnowledgeBase kbase;
	
	public DroolsKnowledgeServiceProvider() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("drools-changeset.xml"), ResourceType.CHANGE_SET);
		if (kbuilder.hasErrors()) {
			throw new RuntimeException(kbuilder.getErrors().toString());
		}
		this.kbase = kbuilder.newKnowledgeBase();
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.kbase;
	}

}
