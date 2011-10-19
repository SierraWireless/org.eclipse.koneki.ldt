package org.eclipse.koneki.ldt.internal.parser;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.core.Flags;
import org.eclipse.koneki.ldt.parser.ast.LuaSourceRoot;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.LuaModuleDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.ModuleReference;
import org.eclipse.koneki.ldt.parser.ast.declarations.ScalarVariableDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.TableDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.VariableDeclaration;
import org.eclipse.koneki.ldt.parser.ast.expressions.BinaryExpression;
import org.eclipse.koneki.ldt.parser.ast.expressions.Boolean;
import org.eclipse.koneki.ldt.parser.ast.expressions.Call;
import org.eclipse.koneki.ldt.parser.ast.expressions.Dots;
import org.eclipse.koneki.ldt.parser.ast.expressions.Function;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.ast.expressions.Index;
import org.eclipse.koneki.ldt.parser.ast.expressions.Invoke;
import org.eclipse.koneki.ldt.parser.ast.expressions.Nil;
import org.eclipse.koneki.ldt.parser.ast.expressions.Number;
import org.eclipse.koneki.ldt.parser.ast.expressions.Pair;
import org.eclipse.koneki.ldt.parser.ast.expressions.Parenthesis;
import org.eclipse.koneki.ldt.parser.ast.expressions.Table;
import org.eclipse.koneki.ldt.parser.ast.expressions.UnaryExpression;
import org.eclipse.koneki.ldt.parser.ast.statements.Break;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;
import org.eclipse.koneki.ldt.parser.ast.statements.Do;
import org.eclipse.koneki.ldt.parser.ast.statements.ElseIf;
import org.eclipse.koneki.ldt.parser.ast.statements.ForInPair;
import org.eclipse.koneki.ldt.parser.ast.statements.ForNumeric;
import org.eclipse.koneki.ldt.parser.ast.statements.If;
import org.eclipse.koneki.ldt.parser.ast.statements.Local;
import org.eclipse.koneki.ldt.parser.ast.statements.LocalRec;
import org.eclipse.koneki.ldt.parser.ast.statements.Repeat;
import org.eclipse.koneki.ldt.parser.ast.statements.Return;
import org.eclipse.koneki.ldt.parser.ast.statements.Set;
import org.eclipse.koneki.ldt.parser.ast.statements.While;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

/**
 * Offers to create DLTK Java objects from a single access, avoid wasting time with reflection.
 */
public final class DLTKObjectFactory {
	private DLTKObjectFactory() {
	}

	/**
	 * Gives a {@link LuaState} access to a logic module named <code>DLTK</code> which enable DLTK Object creation
	 * 
	 * @param L
	 *            {@link LuaState} to load
	 * @return true in case of success, false else way
	 */
	public static boolean register(LuaState L) {
		// Nothing to do if no LuaState is given
		if (L == null) {
			return false;
		}

		/*
		 * Create module functions
		 */
		NamedJavaFunction[] functions = new NamedJavaFunction[] {

				// CallArgumentsList
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						if (l.getTop() == 2) {
							l.pushJavaObject(new CallArgumentsList(l.checkInteger(1), l.checkInteger(2)));
						} else {
							l.pushJavaObject(new CallArgumentsList());
						}
						return 1;
					}

					@Override
					public String getName() {
						return "CallArgumentsList"; //$NON-NLS-1$
					}
				},
				// LuaSourceRoot
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						if (l.getTop() == 2) {
							l.pushJavaObject(new LuaSourceRoot(l.checkInteger(1), l.checkBoolean(2)));
						} else {
							l.pushJavaObject(new LuaSourceRoot(l.checkInteger(1)));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "LuaSourceRoot"; //$NON-NLS-1$
					}
				},
				// BinaryExpression
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						final int start = l.checkInteger(1);
						final int end = l.checkInteger(2);
						Expression left = l.checkJavaObject(3, Expression.class);
						Expression right = l.checkJavaObject(5, Expression.class);
						if (l.isNumber(3)) {
							l.pushJavaObject(new BinaryExpression(start, end, left, l.toInteger(4), right));
						} else {
							l.pushJavaObject(new BinaryExpression(start, end, left, l.toString(4), right));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "BinaryExpression"; //$NON-NLS-1$
					}
				},// Boolean
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Boolean(l.checkInteger(1), l.checkInteger(2), l.checkBoolean(3)));
						return 1;
					}

					@Override
					public String getName() {
						return "Boolean"; //$NON-NLS-1$
					}
				},// Call
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						CallArgumentsList list = l.checkJavaObject(4, CallArgumentsList.class);
						if (list == null) {
							l.pushJavaObject(new Call(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, Expression.class)));
						} else {
							l.pushJavaObject(new Call(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, Expression.class), list));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Call"; //$NON-NLS-1$
					}
				}, // Dots
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Dots(l.checkInteger(1), l.checkInteger(2)));
						return 1;
					}

					@Override
					public String getName() {
						return "Dots"; //$NON-NLS-1$
					}
				}, // Identifier
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Identifier(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, java.lang.String.class)));
						return 1;
					}

					@Override
					public String getName() {
						return "Identifier"; //$NON-NLS-1$
					}
				}, // Function
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Function(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, Chunk.class), l.checkJavaObject(4,
								Chunk.class)));
						return 1;
					}

					@Override
					public String getName() {
						return "Function"; //$NON-NLS-1$
					}
				}, // Index
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Expression left = l.checkJavaObject(1, Expression.class);
						Statement statement = l.checkJavaObject(2, Statement.class);
						if (statement instanceof Declaration) {
							l.pushJavaObject(new Index(left, (Declaration) statement));
						} else {
							l.pushJavaObject(new Index(left, (Expression) statement));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Index"; //$NON-NLS-1$
					}
				}, // Invoke
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Expression expr = l.checkJavaObject(3, Expression.class);
						org.eclipse.koneki.ldt.parser.ast.expressions.String name;
						name = l.checkJavaObject(4, org.eclipse.koneki.ldt.parser.ast.expressions.String.class);
						CallArgumentsList list = l.checkJavaObject(5, CallArgumentsList.class);
						if (list != null) {
							l.pushJavaObject(new Invoke(l.checkInteger(1), l.checkInteger(2), expr, name, list));
						} else {
							l.pushJavaObject(new Invoke(l.checkInteger(1), l.checkInteger(2), expr, name));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Invoke"; //$NON-NLS-1$
					}
				}, // Nil
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Nil(l.checkInteger(1), l.checkInteger(2)));
						return 1;
					}

					@Override
					public String getName() {
						return "Nil"; //$NON-NLS-1$
					}
				}, // Number
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Number(l.checkInteger(1), l.checkInteger(2), l.checkNumber(3)));
						return 1;
					}

					@Override
					public String getName() {
						return "Number"; //$NON-NLS-1$
					}
				}, // Pair
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						final Expression left = l.checkJavaObject(1, Expression.class);
						final Statement right = l.checkJavaObject(2, Statement.class);
						if (left instanceof Literal) {
							l.pushJavaObject(new Pair((Literal) left, right));
						} else {
							l.pushJavaObject(new Pair((SimpleReference) left, right));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Pair"; //$NON-NLS-1$
					}
				}, // Parenthesis
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Parenthesis(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, Expression.class)));
						return 1;
					}

					@Override
					public String getName() {
						return "Parenthesis"; //$NON-NLS-1$
					}
				}, // String
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						org.eclipse.koneki.ldt.parser.ast.expressions.String string;
						String value = l.checkJavaObject(3, String.class);
						string = new org.eclipse.koneki.ldt.parser.ast.expressions.String(l.checkInteger(1), l.checkInteger(2), value);
						l.pushJavaObject(string);
						return 1;
					}

					@Override
					public String getName() {
						return "String"; //$NON-NLS-1$
					}
				}, // Table
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Table(l.checkInteger(1), l.checkInteger(2)));
						return 1;
					}

					@Override
					public String getName() {
						return "Table"; //$NON-NLS-1$
					}
				}, // Unary expression
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String operator = l.checkJavaObject(3, String.class);
						Statement statement = l.checkJavaObject(4, Statement.class);
						if (operator != null) {
							l.pushJavaObject(new UnaryExpression(l.checkInteger(1), l.checkInteger(2), operator, statement));
						} else {
							l.pushJavaObject(new UnaryExpression(l.checkInteger(1), l.checkInteger(2), l.checkInteger(3), statement));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "UnaryExpression"; //$NON-NLS-1$
					}
				}, // Break
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Break(l.checkInteger(1), l.checkInteger(2)));
						return 1;
					}

					@Override
					public String getName() {
						return "Break"; //$NON-NLS-1$
					}
				}, // Chunk
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Chunk(l.checkInteger(1), l.checkInteger(2)));
						return 1;
					}

					@Override
					public String getName() {
						return "Chunk"; //$NON-NLS-1$
					}
				}, // Do
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						l.pushJavaObject(new Do(l.checkInteger(1), l.checkInteger(2), l.checkJavaObject(3, Chunk.class)));
						return 1;
					}

					@Override
					public String getName() {
						return "Do"; //$NON-NLS-1$
					}
				}, // Elseif
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Expression condition = l.checkJavaObject(3, Expression.class);
						Chunk nominal = l.checkJavaObject(4, Chunk.class);
						Chunk alternative = l.checkJavaObject(5, Chunk.class);
						if (alternative != null) {
							l.pushJavaObject(new ElseIf(l.checkInteger(1), l.checkInteger(2), condition, nominal, alternative));
						} else {
							l.pushJavaObject(new ElseIf(l.checkInteger(1), l.checkInteger(2), condition, nominal));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "ElseIf"; //$NON-NLS-1$
					}
				}, // ForInPair
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk identifiers = l.checkJavaObject(3, Chunk.class);
						Chunk expression = l.checkJavaObject(4, Chunk.class);
						Chunk body = l.checkJavaObject(5, Chunk.class);
						l.pushJavaObject(new ForInPair(l.checkInteger(1), l.checkInteger(2), identifiers, expression, body));
						return 1;
					}

					@Override
					public String getName() {
						return "ForInPair"; //$NON-NLS-1$
					}
				}, // ForNumeric
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Identifier identifier = l.checkJavaObject(3, Identifier.class);
						Expression from = l.checkJavaObject(4, Expression.class);
						Expression to = l.checkJavaObject(5, Expression.class);
						Expression range = l.checkJavaObject(6, Expression.class);
						Chunk body = l.checkJavaObject(7, Chunk.class);
						if (body != null) {
							l.pushJavaObject(new ForNumeric(l.checkInteger(1), l.checkInteger(2), identifier, from, to, range, body));
						} else {
							l.pushJavaObject(new ForNumeric(l.checkInteger(1), l.checkInteger(2), identifier, from, to, (Chunk) range));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "ForNumeric"; //$NON-NLS-1$
					}
				}, // If
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {

						Expression condition = l.checkJavaObject(3, Expression.class);
						Chunk nominal = l.checkJavaObject(4, Chunk.class);
						Chunk alternative = l.checkJavaObject(5, Chunk.class);
						if (alternative != null) {
							l.pushJavaObject(new If(l.checkInteger(1), l.checkInteger(2), condition, nominal, alternative));
						} else {
							l.pushJavaObject(new If(l.checkInteger(1), l.checkInteger(2), condition, nominal));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "If"; //$NON-NLS-1$
					}
				}, // Local
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk vars = l.checkJavaObject(3, Chunk.class);
						Chunk inits = l.checkJavaObject(4, Chunk.class);
						if (inits != null) {
							l.pushJavaObject(new Local(l.checkInteger(1), l.checkInteger(2), vars, inits));
						} else {
							l.pushJavaObject(new Local(l.checkInteger(1), l.checkInteger(2), vars));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Local"; //$NON-NLS-1$
					}
				},// LocalRec
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk vars = l.checkJavaObject(3, Chunk.class);
						Chunk inits = l.checkJavaObject(4, Chunk.class);
						if (inits != null) {
							l.pushJavaObject(new LocalRec(l.checkInteger(1), l.checkInteger(2), vars, inits));
						} else {
							l.pushJavaObject(new LocalRec(l.checkInteger(1), l.checkInteger(2), vars));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "LocalRec"; //$NON-NLS-1$
					}
				},// Repeat
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk body = l.checkJavaObject(3, Chunk.class);
						Expression condition = l.checkJavaObject(4, Expression.class);
						l.pushJavaObject(new Repeat(l.checkInteger(1), l.checkInteger(2), body, condition));
						return 1;
					}

					@Override
					public String getName() {
						return "Repeat"; //$NON-NLS-1$
					}
				}, // Return
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk values = l.checkJavaObject(3, Chunk.class);
						if (values != null) {
							l.pushJavaObject(new Return(l.checkInteger(1), l.checkInteger(2), values));
						} else {
							l.pushJavaObject(new Return(l.checkInteger(1), l.checkInteger(2)));
						}
						return 1;
					}

					@Override
					public String getName() {
						return "Return"; //$NON-NLS-1$
					}
				}, // Set
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk left = l.checkJavaObject(3, Chunk.class);
						Chunk right = l.checkJavaObject(4, Chunk.class);
						l.pushJavaObject(new Set(l.checkInteger(1), l.checkInteger(2), left, right));
						return 1;
					}

					@Override
					public String getName() {
						return "Set"; //$NON-NLS-1$
					}
				}, // While
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Expression condition = l.checkJavaObject(3, Expression.class);
						Chunk body = l.checkJavaObject(4, Chunk.class);
						l.pushJavaObject(new While(l.checkInteger(1), l.checkInteger(2), condition, body));
						return 1;
					}

					@Override
					public String getName() {
						return "While"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Chunk chunk = l.checkJavaObject(1, Chunk.class);
						chunk.addStatement(l.checkJavaObject(2, Statement.class));
						return 1;
					}

					@Override
					public String getName() {
						return "appendStatementToChunk"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						CallArgumentsList list = l.checkJavaObject(1, CallArgumentsList.class);
						list.addNode(l.checkJavaObject(2, ASTNode.class));
						return 1;
					}

					@Override
					public String getName() {
						return "appendNodeToCallArgumentList"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						ElseIf elseif = l.checkJavaObject(1, ElseIf.class);
						Expression condition = l.checkJavaObject(2, Expression.class);
						Chunk chunk = l.checkJavaObject(3, Chunk.class);
						elseif.addExpressionAndRelatedChunk(condition, chunk);
						return 1;
					}

					@Override
					public String getName() {
						return "addExpressionAndRelatedChunk"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Return returnNode = l.checkJavaObject(1, Return.class);
						Statement state = l.checkJavaObject(2, Statement.class);
						returnNode.addReturnValue(state);
						return 1;
					}

					@Override
					public String getName() {
						return "addReturnValue"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Table table = l.checkJavaObject(1, Table.class);
						Statement state = l.checkJavaObject(2, Statement.class);
						table.addStatement(state);
						return 1;
					}

					@Override
					public String getName() {
						return "addStatement"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						ModuleDeclaration mod = l.checkJavaObject(1, ModuleDeclaration.class);
						Statement state = l.checkJavaObject(2, Statement.class);
						mod.addStatement(state);
						return 1;
					}

					@Override
					public String getName() {
						return "addStatementToModuleDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Table table = l.checkJavaObject(1, Table.class);
						Statement state = l.checkJavaObject(2, Statement.class);
						table.addStatement(state);
						return 1;
					}

					@Override
					public String getName() {
						return "addStatement"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						LuaSourceRoot mod = l.checkJavaObject(1, LuaSourceRoot.class);
						mod.setProblem(l.checkInteger(2), l.checkInteger(3), l.checkInteger(4), l.checkString(5));
						return 1;
					}

					@Override
					public String getName() {
						return "setProblem"; //$NON-NLS-1$
					}
				}, // FunctionDeclaration
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String name = l.checkJavaObject(1, String.class);
						int nameStart = l.checkInteger(2);
						int nameEnd = l.checkInteger(3);
						int start = l.checkInteger(4);
						int end = l.checkInteger(5);
						FunctionDeclaration declaration = new FunctionDeclaration(name, nameStart, nameEnd, start, end);
						final String scope = l.checkJavaObject(6, String.class);
						if ("local".equals(scope)) { //$NON-NLS-1$
							declaration.setModifier(Declaration.AccPrivate);
						} else if ("indexed".equals(scope)) { //$NON-NLS-1$
							declaration.setModifier(Flags.AccModule);
						} else {
							declaration.setModifier(Declaration.AccPublic);
						}
						l.pushJavaObject(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "FunctionDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						FunctionDeclaration declaration = l.checkJavaObject(1, FunctionDeclaration.class);
						Chunk body = l.checkJavaObject(2, Chunk.class);
						declaration.acceptBody(body, false);
						return 1;
					}

					@Override
					public String getName() {
						return "acceptBody"; //$NON-NLS-1$
					}
				}, // FunctionDeclaration
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						FunctionDeclaration declaration = l.checkJavaObject(1, FunctionDeclaration.class);
						Chunk params = l.checkJavaObject(2, Chunk.class);
						declaration.acceptArguments(params);
						return 1;
					}

					@Override
					public String getName() {
						return "acceptArguments"; //$NON-NLS-1$
					}
				}, // TableDeclaration
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String name = l.checkJavaObject(1, String.class);
						int nameStart = l.checkInteger(2);
						int nameEnd = l.checkInteger(3);
						int start = l.checkInteger(4);
						int end = l.checkInteger(5);
						boolean isModuleRepresentation = l.checkBoolean(7);
						TableDeclaration declaration = new TableDeclaration(name, nameStart, nameEnd, start, end, isModuleRepresentation);
						if ("local".equals(l.checkJavaObject(6, String.class))) { //$NON-NLS-1$
							declaration.setModifier(Declaration.AccPrivate);
						} else {
							declaration.setModifier(Declaration.AccPublic);
						}
						l.pushJavaObject(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "TableDeclaration"; //$NON-NLS-1$
					}
				}, // ScalarVariableDeclaration
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String name = l.checkJavaObject(1, String.class);
						int nameStart = l.checkInteger(2);
						int nameEnd = l.checkInteger(3);
						int start = l.checkInteger(4);
						int end = l.checkInteger(5);
						ScalarVariableDeclaration declaration = new ScalarVariableDeclaration(name, nameStart, nameEnd, start, end);
						if ("local".equals(l.checkJavaObject(6, String.class))) { //$NON-NLS-1$
							declaration.setModifier(Declaration.AccPrivate);
						} else {
							declaration.setModifier(Declaration.AccPublic);
						}
						l.pushJavaObject(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "ScalarVariableDeclaration"; //$NON-NLS-1$
					}
				}, // VariableDeclaration
				new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String name = l.checkJavaObject(1, String.class);
						int nameStart = l.checkInteger(2);
						int nameEnd = l.checkInteger(3);
						VariableDeclaration declaration = new VariableDeclaration(name, nameStart, nameEnd);
						if ("local".equals(l.checkJavaObject(4, String.class))) { //$NON-NLS-1$
							declaration.setModifier(Declaration.AccPrivate);
						} else {
							declaration.setModifier(Declaration.AccPublic);
						}
						l.pushJavaObject(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "VariableDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						ScalarVariableDeclaration declaration = l.checkJavaObject(1, ScalarVariableDeclaration.class);
						Expression init = l.checkJavaObject(2, Expression.class);
						declaration.setInitialization(init);
						return 1;
					}

					@Override
					public String getName() {
						return "setInitialization"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						IOccurrenceHolder declaration = l.checkJavaObject(1, IOccurrenceHolder.class);
						Statement node = l.checkJavaObject(2, Statement.class);
						declaration.addOccurrence(node);
						return 1;
					}

					@Override
					public String getName() {
						return "addOccurrence"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						Identifier id = l.checkJavaObject(1, Identifier.class);
						Declaration declaration = l.checkJavaObject(2, Declaration.class);
						id.setDeclaration(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "setDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						INavigableNode node = l.checkJavaObject(1, INavigableNode.class);
						ASTNode parent = l.checkJavaObject(2, ASTNode.class);
						node.setParent(parent);
						return 1;
					}

					@Override
					public String getName() {
						return "setParent"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						LuaModuleDeclaration module = l.checkJavaObject(1, LuaModuleDeclaration.class);
						FunctionDeclaration function = l.checkJavaObject(2, FunctionDeclaration.class);
						module.getStatements().add(function);
						return 0;
					}

					@Override
					public String getName() {
						return "addFunction"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						String name = l.checkJavaObject(1, String.class);
						int nameStart = l.checkInteger(2);
						int nameEnd = l.checkInteger(3);
						int start = l.checkInteger(4);
						int end = l.checkInteger(5);
						String modulereference = l.checkString(7);
						ModuleReference moduleReference = new ModuleReference(name, nameStart, nameEnd, start, end, modulereference);
						if ("local".equals(l.checkJavaObject(6, String.class))) { //$NON-NLS-1$
							moduleReference.setModifier(Declaration.AccPrivate);
						} else {
							moduleReference.setModifier(Declaration.AccPublic);
						}
						l.pushJavaObject(moduleReference);
						return 1;
					}

					@Override
					public String getName() {
						return "ModuleReference"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						int nameStart = l.checkInteger(1);
						int nameEnd = l.checkInteger(2);
						int start = l.checkInteger(3);
						int end = l.checkInteger(4);

						l.pushJavaObject(new LuaModuleDeclaration(nameStart, nameEnd, start, end));
						return 1;
					}

					@Override
					public String getName() {
						return "LuaModuleDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						LuaSourceRoot root = l.checkJavaObject(1, LuaSourceRoot.class);
						LuaModuleDeclaration module = l.checkJavaObject(2, LuaModuleDeclaration.class);
						root.getDeclarationsContainer().setLuaModuleDeclaration(module);
						return 1;
					}

					@Override
					public String getName() {
						return "setModuleDeclaration"; //$NON-NLS-1$
					}
				}, new NamedJavaFunction() {
					@Override
					public int invoke(LuaState l) {
						final LuaSourceRoot root = l.checkJavaObject(1, LuaSourceRoot.class);
						final Declaration declaration = l.checkJavaObject(2, Declaration.class);
						root.getDeclarationsContainer().getDeclarations().add(declaration);
						return 1;
					}

					@Override
					public String getName() {
						return "addFunctionToModuleContainer"; //$NON-NLS-1$
					}
				} };

		// Register module with objects creating functions
		L.register("DLTK", functions); //$NON-NLS-1$
		L.pop(1);
		return true;
	}
}
