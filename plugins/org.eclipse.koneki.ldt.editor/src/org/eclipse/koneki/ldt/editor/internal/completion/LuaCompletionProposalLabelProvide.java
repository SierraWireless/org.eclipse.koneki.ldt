package org.eclipse.koneki.ldt.editor.internal.completion;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.internal.navigation.Messages;
import org.eclipse.koneki.ldt.parser.LuaDLTKModelUtils;

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
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/module.gif"); //$NON-NLS-1$
						} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/global_table.gif"); //$NON-NLS-1$
						} else if (LuaDLTKModelUtils.isLocalTable(member)) {
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/local_table.gif"); //$NON-NLS-1$
						}
					} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
						return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/module_function.gif"); //$NON-NLS-1$
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
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/module.gif"); //$NON-NLS-1$
						} else if (LuaDLTKModelUtils.isGlobalTable(member)) {
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/global_table.gif"); //$NON-NLS-1$
						} else if (LuaDLTKModelUtils.isLocalTable(member)) {
							return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/local_table.gif"); //$NON-NLS-1$
						}
					} else if (LuaDLTKModelUtils.isModuleFunction(member)) {
						return Activator.getImageDescriptor(Activator.PLUGIN_ID, "/img/module_function.gif"); //$NON-NLS-1$
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
