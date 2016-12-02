import java.util.ArrayDeque;
import java.util.regex.Pattern;

class Functions {
	static void pop(State self){
		if(!self.stack.isEmpty())
			self.stack.pop();
	}
	
	static void peekChar(State self){
		if(!self.stack.isEmpty())
			self.output.print((char)(int)self.stack.peek());
	}
	
	static void readChar(State self) {
		Pattern pat = self.input.delimiter();
		self.input.useDelimiter("");
		String buf = self.input.next();
		if(buf.length() > 0)
			self.stack.push((int) buf.charAt(0));
		else
			self.stack.push(0);
		self.input.useDelimiter(pat);
	}
	
	static void peekInt(State self){
		if(!self.stack.isEmpty())
			self.output.print(self.stack.peek());
	}
	
	static void readInt(State self){
		self.stack.push(self.input.nextInt());
	}
	
	static void popString(State self){
		if(self.peek_next()){
			ArrayDeque<Integer> retstack = new ArrayDeque<>();
			while(!self.stack.isEmpty() && self.stack.peek() != 0){
				retstack.push(self.stack.pop());
				System.out.print((char)(int)retstack.peek());
			}
			while(!retstack.isEmpty())
				self.stack.push(retstack.pop());
		} else {
			while(!self.stack.isEmpty() && self.stack.peek() != 0)
				System.out.print((char)(int)self.stack.pop());
			if(self.stack.peek() == 0)
				self.stack.pop();
		}
	}
	
	static void readString(State self){
		char[] chars = self.input.nextLine().toCharArray();
		self.stack.push(0);
		for(int i = chars.length - 1; i >= 0; i--)
			self.stack.push((int) chars[i]);
	}
	
	static void swapTopTwo(State self){
		int a = self.stack.pop(),
			b = self.stack.pop();
		self.stack.push(a);
		self.stack.push(b);
	}
	
	static void duplicateTop(State self){
		if(!self.stack.isEmpty())
			self.stack.push(self.stack.peek());
	}
	
	static void invertTop(State self){
		if(self.stack.isEmpty())
			self.stack.push(1);
		else if(self.peek_next())
			self.stack.push(self.stack.peek() == 0?1:0);
		else self.stack.push(self.stack.pop() == 0?1:0);
	}
	
	static void binaryOr(State self){
		if(self.stack.isEmpty())
			self.stack.push(0);
		else {
			int a = self.stack.pop();
			if(self.peek_next()){
				int b = self.stack.peek() | a;
				self.stack.push(a);
				self.stack.push(b);
			} else self.stack.push(self.stack.pop() | a);
		}
	}
	
	static class Funcs {
		static void define_new_function(State self) {
			self.functions.put(get_next_token(self,':'), get_next_token(self, ';'));
		}
		
		static void execute_function(State self){
			State func_state = self.forkFunction(get_next_token(self,';'));
			while(func_state.running()){
				func_state.debug(100);
				StackFun.act(func_state);
				func_state.update();
			}
		}
		
		private static String get_next_token(State self, char delim){
			StringBuilder string = new StringBuilder();
			while(self.running() && self.next_instruction() != delim)
				// while available until next is delim
				string.append(self.advance_instruction()); // add next
			self.advance_instruction(); // consume delim
			return string.toString();
			// delim remains after
		}
	}
	
	static class Strings {
		static void pushLength(State self){
			ArrayDeque<Integer> ret = new ArrayDeque<>();
			while(!self.stack.isEmpty() && self.stack.peek() != 0){
				ret.addFirst(self.stack.pop());
			}
			ret.addLast(ret.size());
			while(!ret.isEmpty())
				self.stack.push(ret.removeFirst());
		}
		static void pushLiteral(State self){
			StringBuilder string = new StringBuilder();
			boolean escape = false, no_null = false;
			if(self.next_instruction() == '~'){
				no_null = true;
				self.advance_instruction();
			}
			while(self.next_instruction() != ')' || escape){
				self.advance_instruction();
				if(self.current_instruction() == '\\'){
					if(!escape)
						escape = true;
					else {
						//TODO: escape codes: \n \t \r \a (\0007) \\ \0 \+ \- \~
					}
				} else
					string.append(self.current_instruction());
			}
			if(!no_null)
				string.append('\0');
			string.reverse();
			char[] chars = string.toString().toCharArray();
			for(char ch: chars)
				self.stack.push((int) ch);
		}
	}

	static class Math {
		static void apply(State STATE, char ch){
			switch(ch){
			case '+':
				Functions.Math.add(STATE);
				break;
			case '-':
				Functions.Math.subtract(STATE);
				break;
			case '*':
				Functions.Math.multiply(STATE);
				break;
			case '/':
				Functions.Math.divide(STATE);
				break;
			case '%':
				Functions.Math.modulo(STATE);
				break;
			case '?':
				Functions.Math.compare(STATE);
				break;
			default:
			}
		}
		static void add(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = self.stack.peek() + a;
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(self.stack.pop() + a);
			}
		}
		static void subtract(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = self.stack.peek() - a;
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(self.stack.pop() - a);
			}
		}
		static void multiply(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = self.stack.peek() * a;
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(self.stack.pop() * a);
			}
		}
		static void divide(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = self.stack.peek() / a;
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(self.stack.pop() / a);
			}
		}
		static void modulo(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = self.stack.peek() % a;
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(self.stack.pop() % a);
			}
		}
		static void compare(State self){
			if(self.stack.size() > 1){
				int a = self.stack.pop();
				if(self.peek_next()){
					int b = Integer.compare(self.stack.peek(), a);
					self.stack.push(a);
					self.stack.push(b);
				} else self.stack.push(Integer.compare(self.stack.peek(), a));
			}
		}
		static void pushLiteral(State self){
			StringBuilder string = new StringBuilder();
			if(self.next_instruction() == '-')
				 string.append(self.advance_instruction());
			else self.advance_instruction(); // skip +
			while(Character.isDigit(self.next_instruction()))
				string.append(self.advance_instruction());
			self.stack.push(Integer.parseInt(string.toString()));
		}
	}
}
