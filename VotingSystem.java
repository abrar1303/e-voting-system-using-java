import java.io.*;
import java.util.*;

// Main Class for Running the Voting System
public class VotingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ElectionManager electionManager = new ElectionManager();

        electionManager.loadElectionData(); // Load data from file

        while (true) {
            System.out.println("Welcome to the Electronic Voting System!");
            System.out.println("1. Student Panel\n2. Admin Panel\n3. Exit");
            System.out.print("Enter your choice: ");
            int choice = getValidIntegerInput(scanner);

            switch (choice) {
                case 1:
                    electionManager.studentPanel(scanner);
                    break;
                case 2:
                    electionManager.adminPanel(scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using the Electronic Voting System. Goodbye!");
                    electionManager.saveElectionData(); // Save data to file
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static int getValidIntegerInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a valid number.");
            scanner.next(); // Consume invalid input
        }
        return scanner.nextInt();
    }
}

// General User Class
abstract class User {
    protected String id;
    protected String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

// Admin Class extending User
class Admin extends User {
    private String password;

    public Admin(String id, String password) {
        super(id, "Admin");
        this.password = password;
    }

    public boolean authenticate(String username, String password) {
        return this.id.equals(username) && this.password.equals(password);
    }
}

// Student Class extending User
class Student extends User {
    private boolean voted;

    public Student(String id) {
        super(id, "Student");
        this.voted = false;
    }

    public boolean hasVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}

// Candidate Class extending User
class Candidate extends User {
    private int votes;

    public Candidate(String id, String name) {
        super(id, name);
        this.votes = 0;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote() {
        this.votes++;
    }
}

// ElectionManager Class for Managing the System
class ElectionManager {
    private Admin admin;
    private Map<String, Student> students = new HashMap<>();
    private List<Candidate> candidates = new ArrayList<>();
    private boolean electionStarted = false;
    private final String DATA_FILE = "election_data.txt";

    public ElectionManager() {
        admin = new Admin("admin", "1234");
    }

    public void studentPanel(Scanner scanner) {
        if (!electionStarted) {
            System.out.println("Election has not started yet. Please try again later.");
            return;
        }

        System.out.print("Enter your Student ID: ");
if (scanner.hasNextLine()) scanner.nextLine(); // Clear buffer
String studentId = scanner.nextLine();

        if (students.containsKey(studentId)) {
            Student student = students.get(studentId);

            if (student.hasVoted()) {
                System.out.println("You have already voted. Access denied.");
                return;
            }

            System.out.println("Candidates:");
            for (int i = 0; i < candidates.size(); i++) {
                System.out.println((i + 1) + ". " + candidates.get(i).getName());
            }

            System.out.print("Enter your vote (candidate number): ");
            int vote = getValidIntegerInput(scanner);

            if (vote > 0 && vote <= candidates.size()) {
                candidates.get(vote - 1).addVote();
                student.setVoted(true);
                System.out.println("Your vote has been successfully cast!");
            } else {
                System.out.println("Invalid choice! Returning to main menu.");
            }
        } else {
            System.out.println("Invalid Student ID.");
        }
    }

    public void adminPanel(Scanner scanner) {
        System.out.print("Enter Admin Username: ");
if (scanner.hasNextLine()) scanner.nextLine(); // Clear buffer
String username = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        if (admin.authenticate(username, password)) {
            while (true) {
                System.out.println("Admin Panel:");
                System.out.println("1. Start New Election\n2. Add Candidate\n3. View Results\n4. Logout");
                System.out.print("Enter your choice: ");
                int choice = getValidIntegerInput(scanner);

                switch (choice) {
                    case 1:
                        startElection(scanner);
                        break;
                    case 2:
                        addCandidate(scanner);
                        break;
                    case 3:
                        viewResults();
                        break;
                    case 4:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } else {
            System.out.println("Invalid credentials! Access denied.");
        }
    }

    private void startElection(Scanner scanner) {
        if (electionStarted) {
            System.out.println("Election is already ongoing.");
            return;
        }

        System.out.print("Enter number of students: ");
        int numStudents = getValidIntegerInput(scanner);

        for (int i = 1; i <= numStudents; i++) {
            String studentId = String.format("S%03d", i);
            students.put(studentId, new Student(studentId));
        }

        electionStarted = true;
        System.out.println("Election has been successfully started!");
    }

    private void addCandidate(Scanner scanner) {
        System.out.print("Enter candidate name: ");
if (scanner.hasNextLine()) scanner.nextLine(); // Clear buffer
String name = scanner.nextLine();
        String candidateId = "C" + (candidates.size() + 1);
        candidates.add(new Candidate(candidateId, name));
        System.out.println("Candidate added successfully!");
    }

    private void viewResults() {
        if (!electionStarted) {
            System.out.println("No election results to display. Election has not started.");
            return;
        }

        System.out.println("Election Results:");
        for (Candidate candidate : candidates) {
            System.out.println(candidate.getName() + ": " + candidate.getVotes() + " votes");
        }
    }

    private int getValidIntegerInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input! Please enter a valid number.");
            scanner.next(); // Consume invalid input
        }
        return scanner.nextInt();
    }

    public void saveElectionData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println(electionStarted);
            writer.println(students.size());
            for (Student student : students.values()) {
                writer.println(student.getId() + "," + student.hasVoted());
            }
            writer.println(candidates.size());
            for (Candidate candidate : candidates) {
                writer.println(candidate.getId() + "," + candidate.getName() + "," + candidate.getVotes());
            }
            System.out.println("Election data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving election data: " + e.getMessage());
        }
    }

    public void loadElectionData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            electionStarted = Boolean.parseBoolean(reader.readLine());
            int numStudents = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numStudents; i++) {
                String[] studentData = reader.readLine().split(",");
                Student student = new Student(studentData[0]);
                student.setVoted(Boolean.parseBoolean(studentData[1]));
                students.put(student.getId(), student);
            }
            int numCandidates = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numCandidates; i++) {
                String[] candidateData = reader.readLine().split(",");
                Candidate candidate = new Candidate(candidateData[0], candidateData[1]);
                for (int j = 0; j < Integer.parseInt(candidateData[2]); j++) {
                    candidate.addVote();
                }
                candidates.add(candidate);
            }
            System.out.println("Election data loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading election data: " + e.getMessage());
        }
    }
}
