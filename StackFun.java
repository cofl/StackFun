import java.io.IOException;
import java.util.*;

public class StackFun {
	static State STATE;
	
	public static void main(String[] args) throws IOException{
		Scanner sc = new Scanner(System.in);
		int lines = 1;//Integer.parseInt(sc.nextLine());
		STATE = new State(sc, System.out, get_input(sc, lines));
		
		while(STATE.running()){
			STATE.debug(100);
			act(STATE);
			STATE.update();
		}
	}
	
	static void act(State STATE){
		switch(STATE.current_instruction()){
		case '\'': // function definition
			Functions.Funcs.define_new_function(STATE);
			break;
		case ':': // function execution
			Functions.Funcs.execute_function(STATE);
			break;
		case '`': // peek next instead of pop
			STATE.peek_next(true);
			break;
		case '!':
			Functions.pop(STATE);
			break;
		case '.':
			Functions.peekChar(STATE);
			break;
		case ',':
			Functions.readChar(STATE);
			break;
		case '#':
			Functions.peekInt(STATE);
			break;
		case '$':
			Functions.readInt(STATE);
			break;
		case '>': // responds to peek-next
			Functions.popString(STATE);
			break;
		case '<':
			Functions.readString(STATE);
			break;
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '?':
			Functions.Math.apply(STATE, STATE.current_instruction());
			break;
		case '~':
			Functions.swapTopTwo(STATE);
			break;
		case '@':
			Functions.duplicateTop(STATE);
			break;
		case '^': // not
			Functions.invertTop(STATE);
			break;
		case '|':
			Functions.binaryOr(STATE);
			break;
		case '&':
			Functions.Strings.pushLength(STATE);
			break;
		case '(': // push literal
			if(STATE.next_instruction() == '-' || STATE.next_instruction() == '+')
				Functions.Math.pushLiteral(STATE);
			else Functions.Strings.pushLiteral(STATE);
			break;
		case '[': // while
			if(STATE.stack.isEmpty() || STATE.stack.peek() != 0)
				STATE.add_loop_point();
			else
				STATE.skip_to_matching('[',']');
			break;
		case '{': // if
			if(STATE.stack.isEmpty() || STATE.stack.peek() == 0)
				STATE.skip_to_matching('{','}');
			break;
		case ']':
			STATE.loop();
			break;
		default:
		}
	}
	
	static String get_input(Scanner sc, int lines){
		StringBuilder in = new StringBuilder();
		while(lines-->0){
			in.append(sc.nextLine());
			if(lines > 0)
				in.append('\n');
		}
		return in.toString();
	}
	
	static boolean alpha_num_under(char ch){
		return ch == '_' || (ch <= 'Z' && ch >= 'A') || (ch <= 'z' && ch >= 'a') || (ch <= '9' && ch >= '0');
	}
}
