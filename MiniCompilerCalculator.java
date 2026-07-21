import java.util.*;

public class MiniCompilerCalculator {

    // Check operator precedence
    static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    // Perform calculation
    static double applyOperator(double a, double b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero!");
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid Operator");
        }
    }

    // Evaluate expression
    static double evaluate(String expression) {

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {

            char ch = expression.charAt(i);

            if (Character.isWhitespace(ch))
                continue;

            // Read number
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i))
                        || expression.charAt(i) == '.')) {

                    sb.append(expression.charAt(i));
                    i++;
                }

                values.push(Double.parseDouble(sb.toString()));
                i--;
            }

            // Opening bracket
            else if (ch == '(') {
                operators.push(ch);
            }

            // Closing bracket
            else if (ch == ')') {

                while (!operators.isEmpty() && operators.peek() != '(') {

                    double b = values.pop();
                    double a = values.pop();
                    char op = operators.pop();

                    values.push(applyOperator(a, b, op));
                }

                operators.pop(); // remove '('
            }

            // Operator
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {

                while (!operators.isEmpty()
                        && precedence(operators.peek()) >= precedence(ch)) {

                    double b = values.pop();
                    double a = values.pop();
                    char op = operators.pop();

                    values.push(applyOperator(a, b, op));
                }

                operators.push(ch);
            }

            else {
                throw new IllegalArgumentException("Invalid Character: " + ch);
            }
        }

        while (!operators.isEmpty()) {

            double b = values.pop();
            double a = values.pop();
            char op = operators.pop();

            values.push(applyOperator(a, b, op));
        }

        return values.pop();
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("     MINI COMPILER CALCULATOR");
        System.out.println("=================================");
        System.out.println("Supports: +  -  *  /  ( )");
        System.out.println();

        while (true) {

            System.out.print("Enter Expression (or 'exit'): ");

            String input = sc.nextLine();

            if (input.equalsIgnoreCase("exit"))
                break;

            try {
                double result = evaluate(input);
                System.out.println("Result = " + result);
            }
            catch (Exception e) {
                System.out.println("Error : " + e.getMessage());
            }

            System.out.println();
        }

        sc.close();
    }
}
