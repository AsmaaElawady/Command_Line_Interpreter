import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;
class Terminal {
    Parser parser;
    File currentPath = new File(System.getProperty("user.dir"));
    //Implement each command in a method, for example:
    public String echo(String [] args) {
        return String.join(" ", args);
    }
    public String pwd(){
        return currentPath.getAbsolutePath();
    }
    public void cd(String[] args){
        if(args.length==0){
            String pathOfUser;
            currentPath = new File(System.getProperty("user.home"));
            pathOfUser = (System.getProperty("user.home"));
        }else if(args.length==1 && args[0].equals("..")){
            currentPath = currentPath.getParentFile();
        }else {
            File file = new File(args[0]);
            currentPath = file.getAbsoluteFile();
        }
    }

    public String ls(String [] args){
        String [] content = currentPath.list();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < content.length; i++)
        {
            if (i==0){
                text.append(content[i]);
            }
            else{
                text.append("\n"+content[i]);
            }
        }
        return text.toString();
    }

    // ...
//This method will choose the suitable command method to be called
    public void chooseCommandAction(String command,String [] args)throws IOException{
        switch (command) {
            case "echo" :
                System.out.println(echo(args));
                break;

            case "pwd":
                System.out.println(pwd());
                break;

            case "cd":
                cd(args);
                break;

            case "ls":
                System.out.println(ls(args));
                break;

            default:
                break;
        }
    }
}
