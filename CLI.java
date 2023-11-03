//this file contains the two major classes for the CLI program
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

class CLI {

    // this is the parser Class ******************************************

    static public class Parser {
        String commandName;
        String[] args;

        // This method will divide the input into commandName and args
        // where "input" is the string command entered by the user
        public boolean parse(String input) {
            String[] inputSplit = input.split(" ");
            commandName = inputSplit[0];
            if (inputSplit.length > 1) {
                if (inputSplit[1].contains("-")) {
                    commandName += " ";
                    commandName += inputSplit[1];
                }
            }
            String[] arr = new String[inputSplit.length - 1];
            for (int i = 1; i < inputSplit.length; i++) {
                arr[i - 1] = inputSplit[i];
            }
            args = Arrays.copyOf(arr, arr.length);

            return true;
        }

        public String getCommandName() {
            return commandName;
        }

        public String[] getArgs() {
            return args;
        }
    }



    // this is the Terminal Class ******************************************

    static public class Terminal {
        Parser parser;
        File currentPath = new File(System.getProperty("user.dir"));

        // Commands implementaion
        /*****************************************/
        public String echo(String[] args) {
            return String.join(" ", args);
        }

        public String pwd() {
            return currentPath.getAbsolutePath();
        }

        public void cd(String[] args) {
            try {
                if (args.length == 0) {
                    currentPath = new File(System.getProperty("user.home"));
                } else if (args.length == 1 && args[0].equals("..")) {
                    File parent = currentPath.getParentFile();
                    if (parent != null && parent.exists() && parent.isDirectory()) {
                        currentPath = parent;
                    } else {
                        System.out.println("Cannot navigate to the parent directory.");
                    }
                } else {
                    File file = new File(args[0]);
                    if (file.exists() && file.isDirectory()) {
                        currentPath = file.getAbsoluteFile();
                    } else {
                        System.out.println("Invalid directory path.");
                    }
                }
            } catch (SecurityException e) {
                System.out.println("Access to the directory is denied.");
            }
        }

        public String ls() {
            String[] content = currentPath.list();
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                if (i == 0) {
                    text.append(content[i]);
                } else {
                    text.append("\n" + content[i]);
                }
            }
            return text.toString();
        }

        public String lsr() {
            String[] content = currentPath.list();
            StringBuilder text = new StringBuilder();
            for (int i = content.length - 1; i >= 0; i--) {
                if (i == content.length - 1) {
                    text.append(content[i]);
                } else {
                    text.append("\n" + content[i]);
                }
            }
            return text.toString();
        }

        public void mkdir(String[] args) {
            for (String arg : args) {
                File newDirectory;

                // check if it is a file name or absolute path.
                if (arg.contains(File.separator)) {
                    newDirectory = new File(arg);
                } else {
                    newDirectory = new File(currentPath, arg);
                }

                // check if directory is already exists or not.
                if (!newDirectory.exists()) {
                    if (newDirectory.mkdir()) {
                        System.out.println("Directory Created.");
                    } else {
                        System.err.println("Failed to create directory.");
                    }
                } else {
                    System.err.println("Directory already exists.");
                }
            }
        }

        public void rmdir(String[] args) {
            if (args.length == 1) {
                if (args[0].equals("*")) {
                    // remove all empty directories.
                    removeEmptyDirectories(currentPath);
                } else {
                    File dirToRemove = new File(args[0]);
                    // check that this directory exists.
                    if (dirToRemove.isDirectory() && dirToRemove.exists()) {
                        // make sure that this directory is empty.
                        if (isDirectoryEmpty(dirToRemove)) {
                            // try to remove this directory, if removed successfully:
                            if (dirToRemove.delete()) {
                                System.out.println("Removed directory: " + dirToRemove.getAbsolutePath());
                                // if not removed successfully.
                            } else {
                                System.err.println("Failed to remove directory: " + dirToRemove.getAbsolutePath());
                            }
                            // if directory is not empty we can not delete it.
                        } else {
                            System.err.println("Directory is not empty: " + dirToRemove.getAbsolutePath());
                        }
                        // if directory does not exist.
                    } else {
                        System.err.println("Directory does not exist: " + dirToRemove.getAbsolutePath());
                    }
                }
                // if no arguments is passed.
            } else {
                System.err.println("Invalid usage of rmdir command.");
            }
        }

        public void touch(String[] args) {
            for (String arg : args) {
                File newFile = new File(arg);

                if (!newFile.exists()) {
                    try {
                        if (newFile.createNewFile()) {
                            System.out.println("File Created: " + newFile.getAbsolutePath());
                        } else {
                            System.err.println("Failed to create file: " + newFile.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        System.err.println("Error creating file: " + e.getMessage());
                    }
                } else {
                    System.err.println("File already exists: " + newFile.getAbsolutePath());
                }
            }
        }

        public void cp(String[] args) {
            if (args.length != 2) {
                System.err.println("Usage: cp <source> <destination>");
                return;
            }

            String sourcePath = args[0];
            String destinationPath = args[1];

            File sourceFile = new File(sourcePath);
            File destinationFile = new File(destinationPath);

            if (!sourceFile.exists() || !sourceFile.isFile()) {
                System.err.println("Source file does not exist or is not a valid file: " + sourcePath);
                return;
            }
            if (destinationFile.exists() && destinationFile.isDirectory()) {
                // If the destination is a directory, create a new file inside the directory
                destinationFile = new File(destinationFile, sourceFile.getName());
            }

            try (InputStream inputStream = new FileInputStream(sourceFile);
                 OutputStream outputStream = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File copied from " + sourcePath + " to " + destinationPath);
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
            }
        }

        public void cpR(String[] args) {
            if (args.length != 2) {
                System.err.println("Usage: cp -r <source> <destination>");
                return;
            }

            String sourcePath = args[0];
            String destinationPath = args[1];

            File source = new File(sourcePath);
            File destination = new File(destinationPath);

            if (!source.exists()) {
                System.err.println("Source does not exist: " + sourcePath);
                return;
            }

            try {
                if (source.isDirectory()) {
                    // If the source is a directory, copy it and its contents recursively
                    copyDirectory(source.toPath(), destination.toPath());
                    System.out.println("Directory copied from " + sourcePath + " to " + destinationPath);
                } else if (source.isFile()) {
                    // If the source is a file, simply copy it to the destination
                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File copied from " + sourcePath + " to " + destinationPath);
                }
            } catch (IOException e) {
                System.err.println("Error copying: " + e.getMessage());
            }
        }

        // remove file from the current directory
         // CL to remove file from the current directory
    public void rm(String[] args){
            if (args.length == 1) {
                
                
                Path filePath = Paths.get(currentPath.toString(),args[0]);
                File toDelFile  = new File(filePath.toString());

                

                if (toDelFile.exists()) {
                     toDelFile.delete();
                }else{
                    System.err.format("%s: no such" + " file or directory%n", filePath);

                }
            }
            else{
                System.out.println("Error: there's more than file name");
            }
        }


        // Cl to prints the file contents or the two files content

        public void cat(String[] args){

            if (args.length == 1 || args.length == 2) {

                for(int i = 0 ; i < args.length ;i++){

                    String fileName = args[i];
                    Path filePath = Paths.get(currentPath.toString(), args[i]);
                    File file = new File(filePath.toString());

                    if (file.exists()) {
                        
                        try(Scanner fileSc = new Scanner(file)){
                            while (fileSc.hasNextLine()) {
                                System.out.println(fileSc.nextLine());
                            }

                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }

                    }else{
                        System.err.format("%s: no such" + " file or directory%n", filePath);
                    }


                }
            }else {
                System.err.println("Error: invalid arguments. One or two arguments expected.");
            }
        }

        
        // Cl to count # words , lines , characters
        public void wc(String[] args) {

            if (args.length == 1) {

                String fileName = args[0];
                Path filePath = Paths.get(currentPath.toString(), args[0]);
                File file = new File(filePath.toString());

                if (file.exists()) {
                    int words = 0, lines = 0, chars = 0;

                    try (Scanner fScan = new Scanner(file)) {

                        while (fScan.hasNextLine()) {
                            lines++;
                            Scanner lineSc = new Scanner(fScan.nextLine());

                            while (lineSc.hasNext()) {
                                words++;
                                String w = lineSc.next();
                                chars += w.length();
                            }
                            lineSc.close();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    }
                    System.out.println(lines + " " + words + ' ' + chars + ' ' + fileName);

                } else {
                    System.err.format("%s: no such" + " file or directory%n", filePath.toString());

                }

            } else {
                System.out.println(" invalid arguments%n");

            }
        }

        // secondry functions
        private void copyDirectory(Path source, Path destination) throws IOException {
            Files.walk(source)
                    .forEach(sourcePath -> {
                        Path targetPath = destination.resolve(source.relativize(sourcePath));
                        try {
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) { }
                        
                    });
        }

        private void removeEmptyDirectories(File dir) {
            LinkedList<File> directories = new LinkedList<>();
            // push the initial directory.
            directories.push(dir);

            while (!directories.isEmpty()) {
                File currentDir = directories.pop();

                // check if it is a directory.
                if (currentDir.isDirectory()) {
                    // take all the sub directories inside the current directory.
                    File[] subDirs = currentDir.listFiles();
                    // if thier exists subdirectories.
                    if (subDirs != null) {
                        // push them to the stack.
                        for (File subDir : subDirs) {
                            directories.push(subDir);
                        }
                    }

                    // check if the current directory is empty and deleted successfully.
                    if (isDirectoryEmpty(currentDir) && currentDir.delete()) {
                        System.out.println("Removed directory: " + currentDir.getAbsolutePath());
                    }
                }
            }
        }

        private boolean isDirectoryEmpty(File dir) {
            return dir.listFiles() == null || dir.listFiles().length == 0;
        }
        // This method will choose the suitable command method to be called
        public void chooseCommandAction(String command, String[] args)throws IOException {
            switch (command) {
                case "echo":
                    System.out.println(echo(args));
                    break;

                case "pwd":
                    System.out.println(pwd());
                    break;

                case "cd":
                    cd(args);
                    break;

                case "ls":
                    System.out.println(ls());
                    break;

                case "ls -r":
                    System.out.println(lsr());
                    break;

                case "mkdir":
                    mkdir(args);
                    break;

                case "rmdir":
                    rmdir(args);
                    break;

                case "touch":
                    touch(args);
                    break;

                case "cp":
                    cp(args);
                    break;

                case "cp-r":
                    cpR(args);
                    break;

                case "rm":
                    rm(args);
                    break;

                case "cat":
                    cat(args);
                    break;

                case "wc":
                    wc(args);
                    break;

                default:
                    System.out.println("s:Error this command doesn't exist");
                    break;
            }

        }

    }


     public static void main(String[] args) throws IOException  {
         Parser parser = new Parser();
         Terminal terminal = new Terminal();
         String command;
         String[] Args;
         String Input;
         boolean flag = true;
         Scanner input = new Scanner(System.in);
         while (flag) {
             System.out.print(">");
             Input = input.nextLine();
             if (Input.equals("exit")) {
                 flag = false;
                 break;
             }
             if (parser.parse(Input)) {
                 command = parser.getCommandName();
                 Args = parser.getArgs();
                 terminal.chooseCommandAction(command, Args);
             }
         }
     }

}
