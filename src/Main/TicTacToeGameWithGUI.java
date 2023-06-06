package Main;

public class TicTacToeGameWithGUI {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Check your input! the number of parameter is more than expect!");
            System.exit(1);
        }

        int agentIQ = 100;
        Tool person = null;

        if (args.length == 1) {
            try {
                agentIQ = Integer.parseInt(args[0]);
                System.out.println("The agentIQ is " + agentIQ + " and the tool will be randomly selected.\n");
            } catch (NumberFormatException e) {
                if ("X".equalsIgnoreCase(args[0]) || "O".equalsIgnoreCase(args[0])) {
                    person = Tool.valueOf(args[0].toUpperCase());
                    System.out.println("The person's tool is " + person + " and the agent IQ is the default value 100.\n");
                } else {
                    System.err.printf("Invalid input '%s': please choose either 'X' or 'O' (or lower case) as the tool.\n", args[0].toUpperCase());
                    System.exit(3);
                }
            }
        }

        if (args.length == 2) {
            int numCount = 0;
            for (int i = 0; i < 2; i++) {
                try {
                    agentIQ = Integer.parseInt(args[i]);
                    numCount++;
                } catch (NumberFormatException e) {
                    if ("X".equalsIgnoreCase(args[i]) || "O".equalsIgnoreCase(args[i])) {
                        if (person == null) {
                            person = Tool.valueOf(args[i].toUpperCase());
                        } else {
                            System.err.printf("Invalid input '%s %s': duplicate tool specified! " +
                                    "you can only type in one tool!", args[0].toUpperCase(), args[1].toUpperCase());
                            System.exit(2);
                        }
                    } else {
                        System.err.printf("Invalid input '%s': please choose either 'X' or 'O' (or lower case) as the tool\n", args[i].toUpperCase());
                        System.exit(3);
                    }
                }
            }
            if (numCount > 1) {
                System.err.printf("Invalid input: you can only type in one IQ value! The input was '%s %s'.", args[0], args[1]);
                System.exit(4);
            }
            System.out.println("The agentIQ is " + agentIQ + " and the person tool is " + person +".\n");
        }
    }
}

