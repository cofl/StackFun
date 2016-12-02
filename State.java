import java.io.PrintStream;
import java.util.*;

class State {
	ArrayDeque<Integer> stack = new ArrayDeque<>();
	ArrayDeque<Integer> loop_points = new ArrayDeque<>();
	HashMap<String, String> functions = new HashMap<>();
	Scanner input;
	PrintStream output;
	
	String code;
	int code_pointer = 0;
	
	private int peek_next = 0;
	
	State(Scanner input, PrintStream output, String code){
		this.input = input;
		this.output = output;
		this.code = code;
	}
	
	boolean peek_next(){
		return peek_next > 0;
	}
	
	void peek_next(boolean set){
		if(set)
			peek_next = 2;
	}
	
	void update(){
		if(peek_next > 0)
			peek_next--;
		code_pointer++;
		while(code_pointer < code.length() && Character.isWhitespace(code.charAt(code_pointer)))
			code_pointer++;
	}
	
	boolean running(){
		return code_pointer < code.length();
	}
	
	char current_instruction(){
		if(code_pointer >= code.length())
			return '\0';
		return code.charAt(code_pointer);
	}
	
	char next_instruction(){
		if(code_pointer + 1 >= code.length())
			return '\0';
		return code.charAt(code_pointer + 1);
	}
	
	char advance_instruction(){
		if(code_pointer + 1 >= code.length())
			return '\0';
		return code.charAt(++code_pointer);
	}
	
	void skip_to_matching(final char start, final char end){
		int depth = 1;
		advance_instruction();
		while(code_pointer < code.length() && depth > 0){
			if(current_instruction() == start)
				depth++;
			else if(current_instruction() == end)
				depth--;
			advance_instruction();
		}
	}
	
	void add_loop_point(){
		loop_points.add(code_pointer - 1);
	}
	
	void loop(){
		code_pointer = loop_points.pop();
	}
	
	void debug(long ms){
		System.err.printf("%3d [%c] [%1d]: %s%n", code_pointer, current_instruction(), peek_next, Arrays.toString(stack.toArray(new Integer[0])));
		try{Thread.sleep(ms);}catch(Exception e){}
	}

	public State forkFunction(String function_name) {
		State new_state = new State(input, output, functions.get(function_name));
		new_state.functions = functions;
		new_state.stack = stack;
		return new_state;
	}
}
