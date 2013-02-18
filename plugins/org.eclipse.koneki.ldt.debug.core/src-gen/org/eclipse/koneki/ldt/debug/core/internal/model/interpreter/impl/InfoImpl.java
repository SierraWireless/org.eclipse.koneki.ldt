/**
 */
package org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.Info;
import org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.InterpreterPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.impl.InfoImpl#isExecuteOptionCapable <em>Execute Option Capable</em>}</li>
 *   <li>{@link org.eclipse.koneki.ldt.debug.core.internal.model.interpreter.impl.InfoImpl#isFileAsArgumentsCapable <em>File As Arguments Capable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InfoImpl extends EObjectImpl implements Info {
	/**
	 * The default value of the '{@link #isExecuteOptionCapable() <em>Execute Option Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExecuteOptionCapable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXECUTE_OPTION_CAPABLE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isExecuteOptionCapable() <em>Execute Option Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExecuteOptionCapable()
	 * @generated
	 * @ordered
	 */
	protected boolean executeOptionCapable = EXECUTE_OPTION_CAPABLE_EDEFAULT;

	/**
	 * The default value of the '{@link #isFileAsArgumentsCapable() <em>File As Arguments Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isFileAsArgumentsCapable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean FILE_AS_ARGUMENTS_CAPABLE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isFileAsArgumentsCapable() <em>File As Arguments Capable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isFileAsArgumentsCapable()
	 * @generated
	 * @ordered
	 */
	protected boolean fileAsArgumentsCapable = FILE_AS_ARGUMENTS_CAPABLE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return InterpreterPackage.Literals.INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isExecuteOptionCapable() {
		return executeOptionCapable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExecuteOptionCapable(boolean newExecuteOptionCapable) {
		boolean oldExecuteOptionCapable = executeOptionCapable;
		executeOptionCapable = newExecuteOptionCapable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, InterpreterPackage.INFO__EXECUTE_OPTION_CAPABLE, oldExecuteOptionCapable, executeOptionCapable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isFileAsArgumentsCapable() {
		return fileAsArgumentsCapable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFileAsArgumentsCapable(boolean newFileAsArgumentsCapable) {
		boolean oldFileAsArgumentsCapable = fileAsArgumentsCapable;
		fileAsArgumentsCapable = newFileAsArgumentsCapable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, InterpreterPackage.INFO__FILE_AS_ARGUMENTS_CAPABLE, oldFileAsArgumentsCapable, fileAsArgumentsCapable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case InterpreterPackage.INFO__EXECUTE_OPTION_CAPABLE:
				return isExecuteOptionCapable();
			case InterpreterPackage.INFO__FILE_AS_ARGUMENTS_CAPABLE:
				return isFileAsArgumentsCapable();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case InterpreterPackage.INFO__EXECUTE_OPTION_CAPABLE:
				setExecuteOptionCapable((Boolean)newValue);
				return;
			case InterpreterPackage.INFO__FILE_AS_ARGUMENTS_CAPABLE:
				setFileAsArgumentsCapable((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case InterpreterPackage.INFO__EXECUTE_OPTION_CAPABLE:
				setExecuteOptionCapable(EXECUTE_OPTION_CAPABLE_EDEFAULT);
				return;
			case InterpreterPackage.INFO__FILE_AS_ARGUMENTS_CAPABLE:
				setFileAsArgumentsCapable(FILE_AS_ARGUMENTS_CAPABLE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case InterpreterPackage.INFO__EXECUTE_OPTION_CAPABLE:
				return executeOptionCapable != EXECUTE_OPTION_CAPABLE_EDEFAULT;
			case InterpreterPackage.INFO__FILE_AS_ARGUMENTS_CAPABLE:
				return fileAsArgumentsCapable != FILE_AS_ARGUMENTS_CAPABLE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (executeOptionCapable: ");
		result.append(executeOptionCapable);
		result.append(", fileAsArgumentsCapable: ");
		result.append(fileAsArgumentsCapable);
		result.append(')');
		return result.toString();
	}

} //InfoImpl
