import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

class Terminal {
    Parser parser;
    File currentPath = new File(System.getProperty("user.dir"));

    public String echo(String[] args) {
        return String.join(" ", args);
    }

    public String pwd() {
        return currentPath.getAbsolutePath();
    }

    public void cd(String[] args) {
        if (args.length == 0) {
            currentPath = new File(System.getProperty("user.home"));
        } else if (args.length == 1 && args[0].equals("..")) {
            currentPath = currentPath.getParentFile();
        } else {
            File file = new File(args[0]);
            currentPath = file.getAbsoluteFile();
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
    public void chooseCommandAction(String command, String[] args) throws IOException {
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

            default:
                break;
        }
    }
}
