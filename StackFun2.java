import java.util.*;

public class StackFun2 {
	static State STATE;
	static Scanner sc = new Scanner(System.in);
	static ArrayDeque<Integer> stack = new ArrayDeque<>(),
								retpoints = new ArrayDeque<>();
	static HashMap<String, Character[]> functions = new HashMap<>();
	static int inp_p = 0, code_p = 0;
	static char[] input, code;
	static byte peek_next = 0;
	
	public static void main(String[] args){
		int lines_input = sc.nextInt(), lines_code = sc.nextInt();
		sc.nextLine();
		
		input = get_input(lines_input);
		code = get_input(lines_code);
		int a, b;
		
		while(code_p < code.length){
			//System.err.printf("%d, %c [%d]: %s%n", peek_next, code[code_p], code_p, Arrays.toString(stack.toArray(new Integer[0])));
			//try{Thread.sleep(500);}catch(Exception e){}
			switch(code[code_p++]){
			case ':': // function definition
				StringBuilder name = new StringBuilder();
				ArrayList<Character> func = new ArrayList<>();
				while(alpha_num_under(code[code_p]))
					name.append(code[code_p++]);
				while(code[code_p] != ';')
					func.add(code[code_p++]);
				functions.put(name.toString(), func.toArray(new Character[0]));
				//TODO: get next alphanumeric
				//TODO: get code to ;
				//TODO: store.
				break;
			case '`': // peek next instead of pop
				peek_next = 2;
				break;
			case '!': // pop
				if(!stack.isEmpty())
					stack.pop();
				break;
			case '.': // peek char
				if(!stack.isEmpty())
					System.out.print((char)(int)stack.peek());
				break;
			case ',': // read char
				if(inp_p >= input.length)
					stack.push(0);
				else
					stack.push((int)input[inp_p++]);
				break;
			case '#': // peek int
				if(!stack.isEmpty())
					System.out.print(stack.peek());
				break;
			case '$': // read int
				StringBuilder str = new StringBuilder();
				while(inp_p < input.length && input[inp_p] <= '9' && input[inp_p] >= '0')
					str.append(input[inp_p++]);
				if(str.length() > 0)
					stack.push(Integer.parseInt(str.toString()));
				while(inp_p < input.length && (input[inp_p] == ' ' || input[inp_p] == '\t' || input[inp_p ] == '\n'))
					if(input[inp_p++] == '\n')
						break;
				break;
			case '>': // pop str
				if(peek_next > 0){
					ArrayDeque<Integer> retstack = new ArrayDeque<>();
					while(!stack.isEmpty() && stack.peek() != 0){
						retstack.push(stack.pop());
						System.out.print((char)(int)retstack.peek());
					}
					while(!retstack.isEmpty())
						stack.push(retstack.pop());
				} else {
					while(!stack.isEmpty() && stack.peek() != 0)
						System.out.print((char)(int)stack.pop());
					if(stack.peek() == 0)
						stack.pop();
				}
				break;
			case '<': // read str
				if(inp_p >= input.length)
					stack.push(0);
				else {
					ArrayDeque<Character> char_arr = new ArrayDeque<>();
					while(inp_p < input.length && input[inp_p] != '\n')
						char_arr.push(input[inp_p++]);
					char_arr.push('\0');
					while(!char_arr.isEmpty())
						stack.push((int)char_arr.pop());
				}
				break;
			case '@': // dup
				if(!stack.isEmpty())
					stack.push(stack.peek());
				break;
			case '+': // add
				if(stack.size() > 1){
					if(peek_next > 0){
						a = stack.pop();
						b = a + stack.peek();
						stack.push(a);
						stack.push(b);
					} else stack.push(stack.pop() + stack.pop());
				}
				break;
			case '-': // sub
				if(stack.size() > 1){
					if(peek_next > 0){
						a = stack.pop();
						b = stack.peek() - a;
						stack.push(a);
						stack.push(b);
					} else {
						a = stack.pop();
						stack.push(stack.pop() - a);
					}
				}
				break;
			case '*': // mult
				if(stack.size() > 1)
					if(peek_next > 0){
						a = stack.pop();
						b = stack.peek() * a;
						stack.push(a);
						stack.push(b);
					} else stack.push(stack.pop() * stack.pop());
				break;
			case '/': // div
				if(stack.size() > 1){
					if(peek_next > 0){
						a = stack.pop();
						b = stack.peek() / a;
						stack.push(a);
						stack.push(b);
					} else {
						a = stack.pop();
						stack.push(stack.pop() / a);
					}
				}
				break;
			case '%': // mod
				if(stack.size() > 1){
					if(peek_next > 0){
						a = stack.pop();
						b = stack.peek() % a;
						stack.push(a);
						stack.push(b);
					} else {
						a = stack.pop();
						stack.push(stack.pop() % a);
					}
				}
				break;
			case '?': // compare
				if(stack.size() > 1){
					a = stack.pop();
					if(peek_next > 0){
						int o = Integer.compare(stack.peek(), a);
						stack.push(a);
						stack.push(o);
					} else
						stack.push(Integer.compare(stack.pop(), a));
				}
				break;
			case '&': // length
				ArrayDeque<Integer> ret = new ArrayDeque<>();
				while(!stack.isEmpty() && stack.peek() != 0){
					ret.addFirst(stack.pop());
				}
				ret.addLast(ret.size());
				while(!ret.isEmpty())
					stack.push(ret.removeFirst());
				break;
			case '~': // swap
				a = stack.pop();
				b = stack.pop();
				stack.push(a);
				stack.push(b);
				break;
			case '^': // not
				if(stack.isEmpty())
					stack.push(1);
				else if(peek_next > 0)
					stack.push(stack.peek() == 0?1:0);
				else stack.push(stack.pop() == 0?1:0);
				break;
			case '|': // or
				if(stack.isEmpty())
					stack.push(0);
				else if(peek_next > 0){
					a = stack.pop();
					b = stack.peek() | a;
					stack.push(a);
					stack.push(b);
				} else stack.push(stack.pop() | stack.pop());
				break;
			case '(': // push literal
				StringBuilder string = new StringBuilder();
				boolean is_string = code[code_p] == '~' || code[code_p] == '\\';
				boolean put_null = code[code_p] == '~';
				if(is_string)
					code_p++;
				while(!(code[code_p] == ')' && code[code_p - 1] != '\\')){ // until non-escaped end-paren
					if(code[code_p] == '\\' && code[code_p - 1] != '\\')
						continue;
					if(code[code_p - 1] == '\\'){
						char to_add = code[code_p];
						switch(code[code_p]){
						case ')':
							to_add = ')';
							break;
						case 'n':
							to_add = '\n';
							break;
						case 't':
							to_add = '\t';
							break;
						case 'r':
							to_add = '\r';
							break;
						case 'a':
							to_add = "\0007".charAt(0);
							break;
						case '\\':
							to_add = '\\';
							break;
						default:
							string.append('\\');
						}
						string.append(to_add);
					} else string.append(code[code_p]);
					code_p++;
				}
				if(is_string) {
					if(put_null)
						string.append('\0');
					string.reverse();
					char[] chars = string.toString().toCharArray();
					for(char ch: chars)
						stack.push((int)ch);
				} else stack.push(Integer.parseInt(string.toString()));
				break;
			case '[': // while
				if(stack.isEmpty() || stack.peek() != 0)
					retpoints.push(code_p - 1);
				else
					skip_to_matching('[',']');
				break;
			case '{': // if
				if(stack.isEmpty() || stack.peek() == 0)
					skip_to_matching('{','}');
				break;
			case ']':
				code_p = retpoints.pop();
			case '}':
			default:
			}
			if(peek_next > 0)
				peek_next--;
		}
	}
	
	static String get_input(int lines){
		String inp = "";
		while(lines-->0){
			inp += sc.nextLine();
			if(lines > 0)
				inp += "\n";
		}
		return inp.toCharArray();
	}
	
	static void skip_to_matching(final char start, final char end){
		int depth = 1;
		while(code_p < code.length && depth > 0){
			if(code[code_p] == start)
				depth++;
			else if(code[code_p] == end)
				depth--;
			code_p++;
		}
	}
	
	static boolean alpha_num_under(char ch){
		return ch == '_' || (ch <= 'Z' && ch >= 'A') || (ch <= 'z' && ch >= 'a') || (ch <= '9' && ch >= '0');
	}
}
