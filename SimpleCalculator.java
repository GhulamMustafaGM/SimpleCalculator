
// SimpleCalculator works that can add, subtract and multiply values in a set of registers. It works with variables or registers and lazy evaluation or call by need. 

import java.io.*;
import java.util.*;

// Main class defined
public class SimpleCalculator {
    
    // Main class operations and its objects.
    static class Operation{
        public String register1;
        public String operation;
        public String register2;
            
        // Declared string type operations like Line String[] e.g. "a add b".
        public Operation(String[] line) throws IllegalOperationException{
            if (line.length != 3){
                throw new IllegalOperationException();
            }
            this.register1 = line[0];
            this.operation = line[1];
            this.register2 = line[2];
        }
    }
    
    // Declared exception for cycles
    static class CycleException extends Exception{
        // Declared cycle exception for Illegal operation. 
        public CycleException(){
            super("Oops! Illegal operation. It is a cycle in the evaluation");
        }
    }
    
    // Declared exception for illegal operations
    static class IllegalOperationException extends Exception{
        
        // Declared illegal operation exception.
        public IllegalOperationException(){
            super("Oops! Illegal operation");
        }
    }
    
    // It checks if the input string is an integer type and returns true when it is integer type and otherwise false. For example, input string type and returns boolean typle.
    private static boolean checkIfInteger(String input){
        try {
            Integer.parseInt(input);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
    
    // It checks if the input string only characters that are the alphabet and integers like alphanumeric. It returns true and otherwise false type. For example, input string type and returns boolean typle.
    private static boolean checkIfAlphanumeric(String input){
        return input.matches("[a-zA-Z0-9]+"); // Declared alphanumeric characters 
    }
    
    // It checks if the input string is a valid register and it returns true type or false type. For example, input string type and returns boolean typle.
    private static boolean checkIfValidRegister(String input){
        if (!checkIfInteger(input) && !checkIfAlphanumeric(input))
            return false;
            return true;
    }
    
    // It checks and makes valid operations. Register1 type is not allowed to become an integer. Both operations as register1 and register2 must be a valid register. It returns boolean type.    
    private static boolean checkValidOperation(String register1, String register2){
        if(!(checkIfValidRegister(register1) && checkIfInteger(register1)) && checkIfValidRegister(register2))
            return false;
            return true;
    }
    
    // It finds the defined register. It makes a Hashset and Hasmap that defined for depency and values. Here, register is string type and defined to register to find it.
    // Whereas, operations are ArrayList, all the reocred operations, and return int type evaluates the result. Here, throws CycleException is a cycle.  
    private static int evaluate(String register, ArrayList<Operation> operations) throws CycleException{
        HashSet<String> dependent_operations = new HashSet<>();
        HashMap<String, Integer> mapped_values = new HashMap<>();
        return evaluate(register, operations, mapped_values, dependent_operations);
    }
     // It checks the defined register, recursively by recording depencies, and the resulted values. Here, Register string is to defined register to evaluate.
     // Operations ArrayList is to defined all the recorded operations. Mapped_values HashMapp is to defined to add founded values for registers. 
     // Dependent_registers HashSet is to defined record dependency.  
    private static int evaluate(String register, ArrayList<Operation> operations, HashMap<String, Integer> mapped_values, HashSet<String> dependent_registers) throws CycleException {
        int result = 0;

        if(checkIfInteger(register)){ // It checks if register is an integer type and then return it. 
            return Integer.parseInt(register);
        }

        if(mapped_values.containsKey(register)) return mapped_values.get(register); // It checks if the register already checked. 

        if (dependent_registers.contains(register)){ // It checks if the register already evaluated a cycle and then quit the evaluation. 
            throw new CycleException();
        }
        dependent_registers.add(register);

        for(Operation operation : operations){ // Here, it checks entire operations. 
            
            if (!operation.register1.equals(register)){ // It checks if the register1 is not the match and otherwise it does not consider to evaluate it. 
                continue;
            }
            if (operation.operation.equals("add")){  // It checks the register2 in the operation. 
                result += evaluate(operation.register2, operations, mapped_values, dependent_registers);
            } else if (operation.operation.equals("subtract")){
                result -= evaluate(operation.register2, operations, mapped_values, dependent_registers);
            } else if (operation.operation.equals("multiply")){
                result *= evaluate(operation.register2, operations, mapped_values, dependent_registers);
            } else {
                System.err.println("Oops! Operation: " + operation.operation + " is not supported,  invalid command will be ignored.");
            }
        }
        mapped_values.replace(register, result); // It adds the register and its value and returns to evaluate it. 
        return result;
    }

    // Declared main function and string array. Here one argument is allowed and file name with extension. 
    public static void main(String[] args) {
        boolean running = true;
        boolean reading_file = false;
        Scanner scanner = new Scanner(System.in);
        ArrayList<Operation> operations = new ArrayList<>();
            
        if(args.length != 0) { // It checks if file type of use.
            if (args.length > 1) {
                running = false;
                System.err.println("Program error, to many arguments or processes.");
            }
            try {
                reading_file = true;
                File file = new File(args[0]);
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                System.err.println("Program error, Sorry file not found here: " + e.getMessage());
                running = false;
            }
        }
            
        while(running){ // It starts to run program
            String line = null;
            
            if(scanner.hasNext()){ // It evaluates the next line
                line = scanner.nextLine();
            }
                
                if(line != null) {
                line.replaceAll("\n", "");
                String lineComponents[] = line.split(" ");
                
                if (lineComponents.length == 1){
                    // It checks if the length is 1 then it quits command and if it does not quit then continue it. 
                    if (lineComponents[0].toLowerCase().equals("quit")){
                        running = false;
                            break;
                    } else {
                        System.err.println("Oops! Illegal command here:" + line);
                            continue;
                    }
                    
                } else if (lineComponents.length == 3){
                        // It checks if the length is three and then generate an operation, and if operation is legal then record it. 
                    if (checkValidOperation(lineComponents[0], lineComponents[2])){
                        System.err.println("Oops! Illegal command here:" + line);
                            continue;
                    }
                    try {
                        operations.add(new Operation(lineComponents));
                    }catch (IllegalOperationException exception){
                        System.err.println(exception.getMessage());
                    }
                            
                } else if (lineComponents.length == 2){
                    // It checks if the length is two and print operation, and evaluate the defined register.
                    if (lineComponents[0].toLowerCase().equals("print")){
                        try {
                            int result = evaluate(lineComponents[1], operations);
                            System.out.println(result);
                        } catch (CycleException exception){
                            System.err.println(exception.getMessage());
                                continue;
                        }
                    } else {
                        System.err.println("Oops! Illegal command here: " + line);
                            continue;
                    }
                    
                } else {
                    System.err.println("Oops! Illegal command here: " + line);
                        continue;
                }
            }
        }        
        if (reading_file){
            scanner.close(); // colse scanner
        }
    }
}

// =============================================== End SimpleCalculator Programm =============================================================================

