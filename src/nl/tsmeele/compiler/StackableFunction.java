package nl.tsmeele.compiler;

/**
 * Stackable functions can be pushed upon the stack. Later they can be pulled
 * from the stack, yet the pull action effectively executes the apply method of
 * the function and returns the function result as the pulled value.
 * 
 * A term definition may include the reference to a stackable function type. If
 * so, this has the following impact: 1) At the START of term evaluation, the
 * function is created by a factory in the code generator. Hence there can be
 * multiple implementations of a function type. In addition, the setup method of
 * the function is called. 2) DURING evaluation of subterms, data may be pushed
 * upon the stack. Terms that evaluate a literal value or an identifier, will
 * automatically push this data as a value respectively variable onto the stack.
 * 3) At the END of term evaluation, the function is automatically pushed onto
 * the stack, and immediately thereafter the stack is pulled, which will cause
 * the function to be applied. The function can use the data pushed by subterms
 * as input parameters. Should the function return a stackable value other than
 * null, this value is evaluated by the generic code generator's popValue
 * method. If the function result is a variable or a function, then this result
 * will again be evaluated. For instance, if the function returns a function as
 * result, then that function will be applied immediately.
 * 
 * @author Ton Smeele
 *
 */
public abstract class StackableFunction extends Stackable {
	private CodeGenerator code = null;
	private int initialStackSize = 0;
	
	public StackableFunction() {
		super(StackItemType.FUNCTION);
	}
	
	public int getStackFrameSize() {
		return code.getStackSize() - initialStackSize;
	}
	
	public void assertStackFrameSize(int expectedFrameSize) {
		int frameSize = getStackFrameSize();
		if (frameSize!= expectedFrameSize) {
			throw new GeneratorException("Expected " + expectedFrameSize + " stacked elements, but got " + frameSize + " at function " + this.getClass().getSimpleName());
		}
	}

	/**
	 * Setup is called at the start of evaluation of the term in which this
	 * function(type) is specified.
	 * It registers the stack size.  
	 * 
	 * @param code code generator implmentation
	 */
	public void setup(CodeGenerator code) {
		this.code = code;
		initialStackSize = code.getStackSize();
	}
	

	/**
	 * Apply is called at the end of evaluation of the term in which this
	 * function(type) is specified. 
	 * All stackelements that have been added beyond initialStackSize 
	 * could be considered function input parameters.
	 * 
	 * @param code code generator implementation
	 * @return the value resulting from applying the function If the returned value
	 *         is not null, the caller is supposed to push the value on the stack
	 */
	public abstract Stackable apply(CodeGenerator code);
}
