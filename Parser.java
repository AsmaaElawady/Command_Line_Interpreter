import java.util.Arrays;


public class Parser {
    String commandName;
    String[] args;
    //This method will divide the input into commandName and args
    //where "input" is the string command entered by the user
    public boolean parse(String input){
        String [] inputSplit = input.split(" ");
        commandName = inputSplit[0];
        String [] arr= new String[inputSplit.length-1];
        for (int i =1 ; i<inputSplit.length ; i++){
            arr[i-1]=inputSplit[i];
        }
        args = Arrays.copyOf(arr , arr.length);

        return true;

    }
    public String getCommandName(){
        return commandName;
    }
    public String[] getArgs(){
        return args;
    }
}
