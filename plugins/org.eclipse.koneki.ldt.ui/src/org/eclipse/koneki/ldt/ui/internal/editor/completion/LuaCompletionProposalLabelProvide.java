package org.eclipse.koneki.ldt.ui.internal.editor.completion;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.koneki.ldt.core.internal.ast.models.LuaDLTKModelUtils;
import org.eclipse.koneki.ldt.ui.internal.Activator;
import org.eclipse.koneki.ldt.ui.internal.ImageConstants;
import org.eclipse.koneki.ldt.ui.internal.editor.navigation.Messages;

//TODO factorize some code with LuaLabelProvider
public class LuaCompletionProposalLabelProvide extends CompletionProposalLabelProvider {

	public LuaCompletionProposalLabelProvide() {
	}

	// TODO BUG_ECLIPSE 403751
	protected String createMethodProposalLabel(CompletionProposal methodProposal) {
		StringBuffer buffer = new StringBuffer();

		// method name
		buffer.append(methodProposal.getName());

		// parameters
		buffer.append('(');
		appendParameterList(buffer, methodProposal);
		buffer.append(')');
		IModelElement element = methodProposal.getModelElement();
		if (element != null && element.getElementType() == IModelElement.METHOD && element.exists()) {
			final IMethod method = (IMethod) element;
			try {
				if (!method.isConstructor()) {
					String type = method.getType();
					if (type != null) {
						buffer.append(" : ").append(type); //$NON-NLS-1$
					}
					IType declaringType = method.getDeclaringType();
					if (declaringType != null) {
						buffer.append(" - ").append(declaringType.getElementName()); //$NON-NLS-1$
					}
				}
				// CHECKSTYLE:OFF
			} catch (ModelException e) {
				// ignore
				// CHECKSTYLE:ON
			}
		}

		return buffer.toString();
	}

	// TODO BUG_ECLIPSE 403751
	protected String createSimpleLabelWithType(CompletionProposal proposal) {
		IModelElement element = proposal.getModelElement();

		if (element != null && element.getElementType() == IModelElement.LOCAL_VARIABLE && element.exists()) {
			final ILocalVariable var = (ILocalVariable) element;
			String type = var.getType();
			if (type != null) {
				return proposal.getName() + " : " + type; //$NON-NLS-1$
			}
		}
		return proposal.getName();
	}

	// TODO BUG_ECLIPSE 403751
	protected String createFieldProposalLabel(CompletionProposal proposal) {
		IModelElement element = proposal.getModelElement();
		if (element != null && element.getElementType() == IModelElement.FIELD && element.exists()) {
			final IField field = (IField) element;
			try {
				String type = field.getType();
				if (type != null) {
					return proposal.getName() + " : " + type; //$NON-NLS-1$
				}
				// CHECKSTYLE:OFF
			} catch (ModelException e) {
				// ignore
				// CHECKSTYLE:ON
			}
		}
		return proposal.getName();
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	// TODO factorize some code with LuaLabelProvider..
	@Override
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		if (proposal.getModelElement() instanceof IMember) {
			IMember member = (IMember) proposal.getModelElement();
			try {
				// Special icon for private type
				if (member.exists()) {
					if (member instanceof IType) {
						if (LuaDLTKModelUtils.isModule(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.MODULE_OBJ16);
						} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.GLOBAL_TABLE_OBJ16);
						} else if (LuaDLTKModelUtils.isLocalTable(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.LOCAL_TABLE_OBJ16);
						}
					} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
						return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.MODULE_FUNCTION_OBJ16);
					}
				}
			} catch (ModelException e) {
				Activator.logError(Messages.LuaCompletionProvidersFlags, e);
				return super.createImageDescriptor(proposal);
			}
		}
		return super.createImageDescriptor(proposal);

	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createTypeImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	@Override
	public ImageDescriptor createTypeImageDescriptor(CompletionProposal proposal) {
		if (proposal.getModelElement() instanceof IMember) {
			IMember member = (IMember) proposal.getModelElement();
			try {
				// Special icon for private type
				if (member.exists()) {
					if (member instanceof IType) {
						if (LuaDLTKModelUtils.isModule(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.MODULE_OBJ16);
						} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.GLOBAL_TABLE_OBJ16);
						} else if (LuaDLTKModelUtils.isLocalTable(member)) {
							return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.LOCAL_TABLE_OBJ16);
						}
					} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
						return Activator.getDefault().getImageRegistry().getDescriptor(ImageConstants.MODULE_FUNCTION_OBJ16);
					}
				}
			} catch (ModelException e) {
				Activator.logError(Messages.LuaCompletionProvidersFlags, e);
				return super.createImageDescriptor(proposal);
			}
		}
		return super.createImageDescriptor(proposal);
	}
}
