package org.eclipse.koneki.ldt.ui.internal.editor.completion;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
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
