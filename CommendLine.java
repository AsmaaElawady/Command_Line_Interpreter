import java.util.Scanner;
import java.io.IOException;

public class CommendLine {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Terminal terminal = new Terminal();
        String command;
        String[] Args;
        String Input;
        boolean flag = true ;
        Scanner input = new Scanner(System.in);
        while (flag){
            System.out.print(">");
            Input = input.nextLine();
            if (Input.equals("exit")){
                flag = false ;
                break ;
            }
            if(parser.parse(Input) ) {
                command = parser.getCommandName();
                Args = parser.getArgs();
                terminal.chooseCommandAction(command ,Args);
            }
        }
    }
}
