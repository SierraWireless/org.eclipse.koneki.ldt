package org.eclipse.koneki.ldt.core.internal.ast.models.dltk;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceField;

@SuppressWarnings("restriction")
public class FakeField extends SourceField implements ISourceRange {
	private final int offset;
	private final int length;
	private final int flags;
	private final boolean hasFlags;

	public FakeField(ISourceModule parent, String name, int offset, int length) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = 0;
		this.hasFlags = false;
	}

	public FakeField(ISourceModule parent, String name, int offset, int length, int flags) {
		super((ModelElement) parent, name);
		this.offset = offset;
		this.length = length;
		this.flags = flags;
		this.hasFlags = true;
	}

	public ISourceRange getNameRange() throws ModelException {
		return this;
	}

	public ISourceRange getSourceRange() throws ModelException {
		return this;
	}

	public boolean exists() {
		return true;
	}

	public int getFlags() throws ModelException {
		return hasFlags ? flags : super.getFlags();
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String getType() throws ModelException {
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.dltk.internal.core.SourceField#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO we probably need to override it
		return super.equals(o);
	}

	/**
	 * @see org.eclipse.dltk.internal.core.ModelElement#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO we probably need to override it
		return super.hashCode();
	}
}