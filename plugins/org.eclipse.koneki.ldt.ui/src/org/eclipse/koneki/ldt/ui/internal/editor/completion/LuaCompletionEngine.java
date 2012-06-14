/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.internal.editor.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.koneki.ldt.core.LuaConstants;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaASTModelUtils;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaASTUtils;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaASTUtils.Definition;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaASTUtils.TypeResolution;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.Item;
import org.eclipse.koneki.ldt.core.internal.ast.models.api.RecordTypeDef;
import org.eclipse.koneki.ldt.core.internal.ast.models.common.LuaSourceRoot;
import org.eclipse.koneki.ldt.ui.internal.Activator;

public class LuaCompletionEngine extends ScriptCompletionEngine {

	@Override
	public void complete(IModuleSource module, int position, int k) {
		// extract source module
		final IModelElement modelElement = module.getModelElement();
		if (!(modelElement instanceof ISourceModule)) {
			Activator.logWarning("Unable to perform completion proposal. Module [" + module.getFileName() + "] has not source module associated."); //$NON-NLS-1$//$NON-NLS-2$
			return;
		}
		ISourceModule sourceModule = (ISourceModule) modelElement;

		// Retrieve start position of word current user is typing
		String start = getWordStarting(module.getSourceContents(), position);

		this.requestor.beginReporting();
		if (start.contains(".") || start.contains(":")) { //$NON-NLS-1$//$NON-NLS-2$
			// Select between module fields if completion is asked after a module reference
			final List<String> ids = new ArrayList<String>();
			Character lastOperator = getExpressionIdentifiers(start, ids);
			addFields(sourceModule, ids, position, lastOperator, start);
		} else {
			// Search local declaration in AST
			addLocalDeclarations(sourceModule, start, position);

			// Search global declaration in DLTK model
			addGlobalDeclarations(sourceModule, start, position);

			// Add keywords
			addKeywords(start, position);
		}
	}

	private void addGlobalDeclarations(ISourceModule sourceModule, String start, int cursorPosition) {
		// get all global variable which start by the string "start"
		List<Definition> globalvars = LuaASTUtils.getAllGlobalVarsDefinition(sourceModule, start);

		// for each global var, get the corresponding model element and create the proposal
		for (Definition definition : globalvars) {
			IMember member = LuaASTModelUtils.getIMember(definition.getModule(), definition.getItem());
			if (member != null)
				createMemberProposal(member, cursorPosition - start.length(), cursorPosition);
		}
	}

	private void addKeywords(String start, int cursorPosition) {
		// TODO key word should be define in a static attribute
		String[] keywords = new String[] { "and", "break", "do", "else", "elseif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"end", "false", "for", "function", "if",//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"in", "local", "nil", "not", "or",//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				"repeat", "return", "then", "true", "until", "while" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		// create proposal for each keyword
		for (int j = 0; j < keywords.length; j++) {
			if (start.isEmpty() || keywords[j].startsWith(start)) {
				createKeyWordProposal(keywords[j], cursorPosition - start.length(), cursorPosition);
			}
		}
	}

	private void addLocalDeclarations(ISourceModule sourceModule, String start, int cursorPosition) {
		// get lua source root
		LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(sourceModule);
		if (luaSourceRoot == null)
			return;

		// find all local vars and create corresponding proposal
		Collection<Item> localVars = LuaASTUtils.getLocalVars(luaSourceRoot, cursorPosition - start.length(), start);
		for (Item var : localVars) {
			IMember member = LuaASTModelUtils.getIMember(sourceModule, var);
			if (member != null)
				createMemberProposal(member, cursorPosition - start.length(), cursorPosition);
		}

	}

	private void addFields(final ISourceModule initialSourceModule, final List<String> ids, int position, Character lastOperator, String start) {
		if (ids.size() < 2)
			return;

		// get the closest definition with the name of the first element of ids
		// we support only Identifier root for now.
		final String rootIdentifierName = ids.get(0);
		final LuaSourceRoot luaSourceRoot = LuaASTModelUtils.getLuaSourceRoot(initialSourceModule);
		Item rootItem = LuaASTUtils.getClosestLocalVar(luaSourceRoot, rootIdentifierName, position - start.length());
		ISourceModule itemSourceModule = initialSourceModule;
		if (rootItem == null) {
			// try to find a global
			Definition globalVarDefinition = LuaASTUtils.getGlobalVarDefinition(initialSourceModule, rootIdentifierName);
			if (globalVarDefinition == null)
				return;
			rootItem = globalVarDefinition.getItem();
			itemSourceModule = globalVarDefinition.getModule();
		}

		// resolve Item Type
		TypeResolution typeResolution = LuaASTUtils.resolveType(itemSourceModule, rootItem.getType());
		if (typeResolution == null || !(typeResolution.getTypeDef() instanceof RecordTypeDef))
			return;

		// found type of the last bigger complete index
		// (e.g. for identifier.field1.field2.f, get the type of identifier.field1.field2)
		RecordTypeDef currentRecordTypeDef = (RecordTypeDef) typeResolution.getTypeDef();
		ISourceModule currentSourceModule = typeResolution.getModule();
		for (int i = 1; i < ids.size() - 1; i++) {
			// check if the current type
			String fieldname = ids.get(i);
			Item item = currentRecordTypeDef.getFields().get(fieldname);

			// we could resolve the type of this field we stop the research
			if (item == null)
				return;

			// resolve the type
			typeResolution = LuaASTUtils.resolveType(currentSourceModule, item.getType());
			// we are interested only by record type
			if (typeResolution == null || !(typeResolution.getTypeDef() instanceof RecordTypeDef))
				return;

			currentRecordTypeDef = (RecordTypeDef) typeResolution.getTypeDef();
			currentSourceModule = typeResolution.getModule();
		}

		// get all the field of the complete index
		try {
			IType iType = LuaASTModelUtils.getIType(currentSourceModule, currentRecordTypeDef);
			IModelElement[] moduleFields = iType.getChildren();
			// get field name
			final String fieldName = ids.get(ids.size() - 1);
			// search field
			for (final IModelElement field : moduleFields) {
				if ((field instanceof IField && lastOperator == '.') || field instanceof IMethod) {
					final boolean goodStart = field.getElementName().toLowerCase().startsWith(fieldName.toLowerCase());
					final boolean nostart = fieldName.isEmpty();
					if (goodStart || nostart) {
						createMemberProposal((IMember) field, position - fieldName.length(), position, lastOperator);
					}
				}
			}
		} catch (ModelException e) {
			Activator.logWarning("Unable to get model element.", e); //$NON-NLS-1$
		}
	}

	// I'm unable to handle spaces in composed identifiers like 'someTable . someField'
	private String getWordStarting(final String content, final int position) {
		// manage inconsistent parameters
		if (position <= 0 || position > content.length())
			return Util.EMPTY_STRING;

		// search the begin on the string sequence to autocomplete
		int currentPosition = position;
		int lastValidPosition = position;
		boolean lastCharIsIndex = false;
		boolean finish = false;
		do {
			currentPosition--;
			final char currentChar = content.charAt(currentPosition);
			final boolean isInvokeChar = currentChar == ':';
			final boolean isIndexChar = currentChar == '.';
			final boolean isIdentifierPart = Character.isLetterOrDigit(currentChar) || currentChar == '_';

			// we stop if we found a character which is neiter a identifier part or an operator
			// or if we found the concatenation character (..)
			if (lastCharIsIndex && isIndexChar) { // we found a the .. char
				lastValidPosition = lastValidPosition + 1;
				finish = true;
			} else if (isIdentifierPart || isIndexChar || isInvokeChar) { // we found a valid char
				lastValidPosition = currentPosition;
				lastCharIsIndex = isIndexChar;
			} else {
				finish = true;
			}

			// if we are at the end of the file it's finish too
		} while (!finish && currentPosition > 0);

		if (lastValidPosition >= position)
			return Util.EMPTY_STRING;
		return content.substring(lastValidPosition, position);
	}

	private Character getExpressionIdentifiers(final String composedId, List<String> result) {
		StringBuffer stringToParse = new StringBuffer(composedId);
		StringBuffer nextId = new StringBuffer();
		Character lastOperator = '\0'; // we support only if invoke is the last operator

		for (int i = 0; i < stringToParse.length(); i++) {
			Character character = stringToParse.charAt(i);

			if (!(character == '.') && !(character == ':')) {
				// if it's not an operator then append the next char
				nextId.append(character);
			} else {
				// we have an operator

				// don't allow 2 sucesssive operator
				if (nextId.length() == 0)
					return null;

				// we support only if invoke is the last operator
				if (lastOperator == ':' && character == ':')
					return null;

				// store value to next validation
				lastOperator = character;

				// store previous value
				result.add(nextId.toString());
				nextId = new StringBuffer();
			}
		}

		result.add(nextId.toString());

		return lastOperator;
	}

	private void createKeyWordProposal(String keyword, int startIndex, int endIndex) {
		CompletionProposal proposal = CompletionProposal.create(CompletionProposal.KEYWORD, 0);
		proposal.setRelevance(1);
		proposal.setName(keyword);
		proposal.setCompletion(keyword);
		proposal.setReplaceRange(startIndex, endIndex);
		this.requestor.accept(proposal);
	}

	private void createMemberProposal(IMember member, int startIndex, int endIndex) {
		createMemberProposal(member, startIndex, endIndex, '\0');
	}

	private void createMemberProposal(IMember member, int startIndex, int endIndex, char operator) {
		try {
			CompletionProposal proposal = null;
			switch (member.getElementType()) {
			case IModelElement.METHOD:
				// create method proposal
				proposal = CompletionProposal.create(CompletionProposal.METHOD_REF, 0);
				IMethod method = (IMethod) member;

				if (operator == ':') {
					// manage the invoke case
					String[] parameterNames = method.getParameterNames();

					if (parameterNames.length == 0)
						return;

					if (!parameterNames[0].equals(LuaConstants.SELF_PARAMETER))
						return;

					String[] parameterNamesWithoutFirstOne = Arrays.copyOfRange(parameterNames, 1, parameterNames.length);
					proposal.setParameterNames(parameterNamesWithoutFirstOne);
				} else {
					proposal.setParameterNames(method.getParameterNames());
				}
				break;
			case IModelElement.FIELD:
				proposal = CompletionProposal.create(CompletionProposal.FIELD_REF, 0);
				break;
			case IModelElement.TYPE:
				proposal = CompletionProposal.create(CompletionProposal.TYPE_REF, 0);
				break;
			default:
				return;
			}

			proposal.setFlags(member.getFlags());
			proposal.setModelElement(member);
			proposal.setName(member.getElementName());
			proposal.setCompletion(member.getElementName());
			proposal.setReplaceRange(startIndex, endIndex);
			proposal.setRelevance(2);
			this.requestor.accept(proposal);

		} catch (ModelException e) {
			Activator.logWarning(Messages.LuaCompletionEngineProblemProcessingGlobals, e);
			return;
		}
	}
}
