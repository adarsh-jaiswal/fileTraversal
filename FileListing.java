package com.playment;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.io.FileUtils;

public class FileListing {

	private static final String ROOT = ".";
	private Stack<FileNode> filesStack;

	public FileListing() {
		this.filesStack = new Stack<>();
	}

	public static void main(String[] args) {

		File rootDir = new File(ROOT);

		Scanner sc = new Scanner(System.in);
		String line = null;
		String action = null;

		System.out.println("Application started...");
		FileListing fileList = new FileListing();
		fileList.getFilesStack().push(new FileNode("root", rootDir.getAbsolutePath()));
		while (sc.hasNextLine()) {

			line = sc.nextLine();
			if (line.length() == 0) {
				continue;
			}

			String[] input = ("session clear".equalsIgnoreCase(line)) ? line.split(",") : line.split("\\s", 2);
			if (input.length > 0 && input[0] != null) {
				action = input[0];
				if ("cd".equalsIgnoreCase(action)) {
					if (!isEmptyPathArgs(input)) {
						fileList.changeDirectory(fileList.getFilesStack().peek(), input[1]);
					}
				} else if ("mkdir".equalsIgnoreCase(action)) {
					if (!isEmptyPathArgs(input)) {
						fileList.makeDirectory(fileList.getFilesStack().peek(), input[1]);
					}
				} else if ("rm".equalsIgnoreCase(action)) {
					if (!isEmptyPathArgs(input)) {
						fileList.removeDirectory(fileList.getFilesStack().peek(), input[1]);
					}
				} else if ("pwd".equalsIgnoreCase(action)) {
					if ("root".equalsIgnoreCase(fileList.getFilesStack().peek().getName())) {
						System.out.println("PATH: /");
					} else {
						System.out.println("PATH: " + fileList.getFilesStack().peek().getName());
					}
				} else if ("ls".equalsIgnoreCase(action)) {
					fileList.getAllFiles(fileList.getFilesStack().peek().getPath());
				} else if ("session clear".equalsIgnoreCase(action)) {
					fileList.clearStack();
				} else {
					System.out.println("ERR: CANNOT RECOGNIZE INPUT.");
				}

			}
		}
	}

	private void clearStack() {
		if (this.getFilesStack().size() == 1) {
			System.out.println("ERR: ALREADY AT ROOT.");
		} else {
			File rootDir = new File(ROOT);
			Stack<FileNode> newStack = new Stack<>();
			newStack.push(new FileNode("root", rootDir.getAbsolutePath()));
			this.setFilesStack(newStack);
			System.out.println("SUCC: CLEARED: RESET TO ROOT");
		}
	}

	private void removeDirectory(FileNode currPath, String directories) {

		if (directories == null || directories.trim().isEmpty()) {
			System.out.println("ERR: Empty directory name.");
			return;
		}
		String[] dirNames = directories.split("\\s");
		File file = null;
		boolean dirExists = true;
		if (dirNames.length > 0) {
			for (String name : dirNames) {
				file = new File(currPath.getPath() + "\\" + name);
				if (!file.exists()) {
					dirExists = false;
					break;
				}
			}
			if (dirExists) {
				for (String name : dirNames) {
					file = new File(currPath.getPath() + "\\" + name);
					if (file.exists()) {
						try {
							FileUtils.deleteDirectory(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				System.out.println("SUCC: DELETED");
			} else {
				System.out.println("ERR: DIRECTORY DOES NOT EXISTS");
			}

		}

	}

	static boolean isEmptyPathArgs(String[] input) {
		if (input.length > 1 && input[1].trim().length() > 0) {
			return false;
		} else {
			System.out.println("ERR: EMPTY PATH ARGS.");
			return true;
		}

	}

	private void getAllFiles(String currDir) {
		File curDir = new File(currDir);
		File[] filesList = curDir.listFiles();
		if (filesList == null || filesList.length == 0) {
			System.out.println("SUCC: No Files.");

		} else {
			for (File f : filesList) {
				if (f.isDirectory())
					System.out.println(f.getName());
				if (f.isFile()) {
					System.out.println(f.getName());
				}
			}
			System.out.println("SUCC: REACHED");
		}
	}

	public void changeDirectory(FileNode currNode, String newPath) {
		if ("\\/".equalsIgnoreCase("\\" + newPath)) {
			if (this.getFilesStack().size() == 1) {
				System.out.println("ERR: AT ROOT");
				return;
			}
			this.getFilesStack().pop();
			System.out.println("SUCC: REACHED");
			return;
		}
		File file = new File(currNode.getPath() + "\\" + newPath);
		if (!file.exists()) {
			System.out.println("ERR: INVALID PATH");
			return;
		}
		if (this.getFilesStack().size() != 1) {
			this.getFilesStack()
					.push(new FileNode(currNode.getName() + "/" + newPath, currNode.getPath() + "\\" + newPath));
		} else {
			this.getFilesStack().push(new FileNode("/" + newPath, currNode.getPath() + "\\" + newPath));
		}

		System.out.println("SUCC: REACHED");
	}

	public void makeDirectory(FileNode currPath, String directories) {
		if (directories == null || directories.trim().isEmpty()) {
			System.out.println("ERR: Empty directory name.");
			return;
		}
		String[] dirNames = directories.split("\\s");
		File file = null;
		boolean dirExists = false;
		if (dirNames.length > 0) {
			for (String name : dirNames) {
				file = new File(currPath.getPath() + "\\" + name);
				if (file.exists()) {
					dirExists = true;
					break;
				}
			}
			if (!dirExists) {
				for (String name : dirNames) {
					file = new File(currPath.getPath() + "\\" + name);
					if (!file.exists()) {
						file.mkdir();
					}
				}
				System.out.println("SUCC: CREATED");
			} else {
				System.out.println("ERR: DIRECTORY ALREADY EXISTS");
			}

		}

	}

	public Stack<FileNode> getFilesStack() {
		return filesStack;
	}

	public void setFilesStack(Stack<FileNode> filesStack) {
		this.filesStack = filesStack;
	}

}

class FileNode {
	private String name;
	private String path;

	public FileNode(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
