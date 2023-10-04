package prog07;

import prog02.GUI;
import prog02.UserInterface;
import prog05.LinkedQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NotWordle {

    UserInterface ui;

    protected class Node {

        private String word;

        protected Node next;

        protected Node(String word) {
            this.word = word;
            next = null;
        }
    }

    ArrayList<Node> wordEntries = new ArrayList<Node>();


    public NotWordle(UserInterface ui) {
        this.ui = ui;

    }

    public static void main(String[] args) {
        GUI ui = new GUI("NotWordle Game");
        NotWordle game = new NotWordle(ui);
        String fileName = ui.getInfo("Please enter a words file");
        game.loadWords(fileName);


        //ASK start
        String start = ui.getInfo("Enter the starting word");

        //ASK final
        String finalWord = ui.getInfo("Enter the final word");

        //ASK human Computer
        String[] commands = {"Human plays.", "Computer plays.", "Computer plays 2", "Computer plays 3"};
        int choice = ui.getCommand(commands);
        switch (choice) {
            case -1:
                return;
            case 0:
                //if Human
                game.play(start, finalWord);
            case 1:
                //if Computer
                game.solve(start, finalWord);
            case 2:
                game.solve2(start, finalWord);
            case 3:
                game.solve3(start, finalWord);

        }

        //if Human
        //game.play(...)

        //if Computer
        //game.solve(...)
    }


    static int lettersDifferent(String word, String target) {
        int count = 0;
        for (int i = 0; i < word.length(); ++i) {
            if (word.charAt(i) != target.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    int distanceToStart(Node node) {
        int count = 0;
        while (node != null) {
            node = node.next;
            count++;
        }
        return count;
    }

    protected class NodeComparator implements Comparator<Node> {

        String target;

        NodeComparator(String target) {
            this.target = target;
        }

        int priority(Node node) {
            return (distanceToStart(node) + lettersDifferent(node.word, target));
        }

        public int compare(Node node, Node node2) {
            return (priority(node) - priority(node2));
        }

    }

    void play(String start, String end) {
        while (true) {
            ui.sendMessage("The current word is " + start + ", and the target word is " + end);
            String userWord = ui.getInfo("Please enter the next word.");
            if (userWord == null) {
                break;
            }
            if (oneLetterDifferent(start, userWord) == false) {
                ui.sendMessage("Warning, " + userWord + " is not one letter away from " + start);
                continue;
            }
            if (find(userWord) == null) {
                ui.sendMessage("Warning, " + userWord + " is not in the dictionary");
                continue;
            }
            start = userWord;
            if (start.equalsIgnoreCase(end)) {
                ui.sendMessage("You win!");
                return;
            }
        }

        // In play, do the following forever (until the return occurs).  Tell
        // the user the current word (the start) and the target word.  Ask for
        // the next word.  Set the start word variable to that next word.  If
        //  it equals the target, tell the user ``You win!'' and return.
        //   Otherwise keep looping.  Test.
    }

    public static boolean oneLetterDifferent(String string1, String string2) {
        if (string2 == null) {
            return false;
        }
        if (string1.length() != string2.length()) {
            return false;
        } else {
            boolean differentCharacter = false;

            for (int i = 0; i < string1.length(); ++i) {
                if (string1.charAt(i) != string2.charAt(i)) {
                    if (differentCharacter == false) {
                        differentCharacter = true;
                    } else {
                        return (false);
                    }
                }
            }
            return (differentCharacter);
        }
    }

    public void loadWords(String fileName) {
        try {
            File dictionary = new File(fileName);
            Scanner readFile = new Scanner(dictionary);
            while (readFile.hasNext()) {
                Node nodeWord = new Node(readFile.next());
                wordEntries.add(nodeWord);
                nodeWord.next = null;
            }
        } catch (FileNotFoundException e) {
            ui.sendMessage(e.getMessage());
            fileName = ui.getInfo("Please enter a words file");
            loadWords(fileName);
        }
    }

    public Node find(String word) {
        for (Node nodeWord : wordEntries) {
            if (nodeWord.word.equalsIgnoreCase(word)) {
                return (nodeWord);
            }
        }
        return (null);
    }

    void solve(String start, String end) {
        LinkedQueue<Node> nodeQueue = new LinkedQueue<Node>();
        int count = 0;
        Node startNode = find(start);
        nodeQueue.add(startNode);
        while (nodeQueue.size() != 0) {
            Node theNode = nodeQueue.poll();
            count++;
            for (Node word : wordEntries) {
                if (!word.word.equalsIgnoreCase(start) && word.next == null && oneLetterDifferent(word.word, theNode.word)) {
                    Node nextNode = word;
                    nextNode.next = theNode;
                    nodeQueue.add(nextNode);
                    if (nextNode.word.equalsIgnoreCase(end)) {
                        String finalString = "";
                        while (nextNode.next != null) {
                            finalString = nextNode.word + " " + finalString;
                            nextNode = nextNode.next;
                        }
                        finalString = nextNode.word + " " + finalString;
                        finalString = finalString.trim();
                        ui.sendMessage("Success! The word " + end + " has been reached by following the path: " + finalString);
                        System.out.println(count);
                    }
                }
            }
        }
    }


    void solve2(String start, String end) {
        Queue<Node> nodeQueue = new PriorityQueue<Node>(new NodeComparator(end));
        int count = 0;

        Node startNode = find(start);
        nodeQueue.add(startNode);
        while (nodeQueue.size() != 0) {
            Node theNode = nodeQueue.poll();
            count++;
            for (Node word : wordEntries) {
                if (!word.word.equalsIgnoreCase(start) && word.next == null && oneLetterDifferent(word.word, theNode.word)) {
                    Node nextNode = word;
                    nextNode.next = theNode;
                    nodeQueue.add(nextNode);
                    if (nextNode.word.equalsIgnoreCase(end)) {
                        String finalString = "";
                        while (nextNode.next != null) {
                            finalString = nextNode.word + " " + finalString;
                            nextNode = nextNode.next;
                        }
                        finalString = nextNode.word + " " + finalString;
                        finalString = finalString.trim();
                        ui.sendMessage("Success! The word " + end + " has been reached by following the path: " + finalString);
                        System.out.println(count);
                    }
                }
            }
        }
    }

    void solve3(String start, String end) {
        Queue<Node> nodeQueue = new PriorityQueue<Node>(new NodeComparator(end));
        int count = 0;

        Node startNode = find(start);
        nodeQueue.add(startNode);
        while (nodeQueue.size() != 0) {
            Node theNode = nodeQueue.poll();
            count++;
            for (Node word : wordEntries) {
                if (!word.word.equalsIgnoreCase(start) && word.next == null && oneLetterDifferent(word.word, theNode.word)) {
                    Node nextNode = word;
                    nextNode.next = theNode;
                    nodeQueue.add(nextNode);
                    if (nextNode.word.equalsIgnoreCase(end)) {
                        String finalString = "";
                        while (nextNode.next != null) {
                            finalString = nextNode.word + " " + finalString;
                            nextNode = nextNode.next;
                        }
                        finalString = nextNode.word + " " + finalString;
                        finalString = finalString.trim();
                        ui.sendMessage("Success! The word " + end + " has been reached by following the path: " + finalString);
                        System.out.println(count);
                    }
                    } else if (word != startNode && oneLetterDifferent(word.word, theNode.word)  && distanceToStart(word) > distanceToStart(theNode) + 1) {
                        word.next = theNode;
                        nodeQueue.remove(word);
                        nodeQueue.offer(word);
                    }
                }

            }
        }
    }

