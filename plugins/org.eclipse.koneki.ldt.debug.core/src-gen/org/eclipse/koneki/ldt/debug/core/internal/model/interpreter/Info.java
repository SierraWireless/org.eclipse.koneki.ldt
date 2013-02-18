/**
 */
package org.eclipse.koneki.ldt.debug.core.internal.model.interpreter;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info#isExecuteOptionCapable <em>Execute Option Capable</em>}</li>
 *   <li>{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info#isFileAsArgumentsCapable <em>File As Arguments Capable</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterPackage#getInfo()
 * @model
 * @generated
 */
public interface Info extends EObject {
	/**
	 * Returns the value of the '<em><b>Execute Option Capable</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Execute Option Capable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Execute Option Capable</em>' attribute.
	 * @see #setExecuteOptionCapable(boolean)
	 * @see org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterPackage#getInfo_ExecuteOptionCapable()
	 * @model default="true" required="true"
	 * @generated
	 */
	boolean isExecuteOptionCapable();

	/**
	 * Sets the value of the '{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info#isExecuteOptionCapable <em>Execute Option Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Execute Option Capable</em>' attribute.
	 * @see #isExecuteOptionCapable()
	 * @generated
	 */
	void setExecuteOptionCapable(boolean value);

	/**
	 * Returns the value of the '<em><b>File As Arguments Capable</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File As Arguments Capable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File As Arguments Capable</em>' attribute.
	 * @see #setFileAsArgumentsCapable(boolean)
	 * @see org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterPackage#getInfo_FileAsArgumentsCapable()
	 * @model default="true"
	 * @generated
	 */
	boolean isFileAsArgumentsCapable();

	/**
	 * Sets the value of the '{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info#isFileAsArgumentsCapable <em>File As Arguments Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File As Arguments Capable</em>' attribute.
	 * @see #isFileAsArgumentsCapable()
	 * @generated
	 */
	void setFileAsArgumentsCapable(boolean value);

} // Info
