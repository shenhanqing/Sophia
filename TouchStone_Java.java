import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Student Grade Management System
 * This program helps manage student grades, calculate statistics,
 * and generate reports.
 */
public class StudentGradeSystem {

    // Main method - entry point of the program
    public static void main(String[] args) {
        // Create a scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Initialize storage for students
        ArrayList<Student> students = new ArrayList<>();

        // Welcome message
        System.out.println("Welcome to Student Grade Management System");

        // Main program loop
        boolean running = true;
        while (running) {
            // Display menu options
            displayMenu();

            try {
                // Get user choice
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the newline character

                // Process user choice
                switch (choice) {
                    case 1:
                        addStudent(scanner, students);
                        break;
                    case 2:
                        addGrades(scanner, students);
                        break;
                    case 3:
                        viewStudent(scanner, students);
                        break;
                    case 4:
                        generateClassReport(students);
                        break;
                    case 5:
                        exportData(students);
                        break;
                    case 6:
                        running = false;
                        System.out.println("Thank you for using Student Grade Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }

            System.out.println(); // Empty line for better readability
        }

        // Close scanner
        scanner.close();
    }

    // Display the menu options
    private static void displayMenu() {
        System.out.println("\n----- MENU -----");
        System.out.println("1. Add Student");
        System.out.println("2. Add Grades");
        System.out.println("3. View Student");
        System.out.println("4. Generate Class Report");
        System.out.println("5. Export Data");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    // Add a new student to the system
    private static void addStudent(Scanner scanner, ArrayList<Student> students) {
        System.out.println("\n----- Add New Student -----");

        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();

        // Check if student ID already exists
        for (Student student : students) {
            if (student.getId().equals(id)) {
                System.out.println("Error: Student ID already exists.");
                return;
            }
        }

        // Create and add the new student
        Student newStudent = new Student(name, id);
        students.add(newStudent);

        System.out.println("Student added successfully!");
    }

    // Add grades for a student
    private static void addGrades(Scanner scanner, ArrayList<Student> students) {
        System.out.println("\n----- Add Grades -----");

        // Find the student
        Student student = findStudent(scanner, students);
        if (student == null) return;

        // Get grades for three subjects
        String[] subjects = {"Math", "Chinese", "English"};

        for (String subject : subjects) {
            System.out.print("Enter " + subject + " grade: ");
            try {
                double grade = scanner.nextDouble();

                // Validate grade
                if (grade < 0 || grade > 100) {
                    System.out.println("Error: Grade must be between 0 and 100.");
                    scanner.nextLine(); // Clear the input
                    return;
                }

                // Add the grade
                student.addGrade(subject, grade);
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
                return;
            }
        }

        scanner.nextLine(); // Clear the last newline character
        System.out.println("Grades added successfully!");
    }

    // View a student's information and grades
    private static void viewStudent(Scanner scanner, ArrayList<Student> students) {
        System.out.println("\n----- View Student -----");

        // Find the student
        Student student = findStudent(scanner, students);
        if (student == null) return;

        // Display student information
        System.out.println("\nStudent Information:");
        System.out.println("Name: " + student.getName());
        System.out.println("ID: " + student.getId());

        // Display grades
        System.out.println("\nGrades:");
        HashMap<String, Double> grades = student.getGrades();

        if (grades.isEmpty()) {
            System.out.println("No grades recorded yet.");
            return;
        }

        for (String subject : grades.keySet()) {
            System.out.println(subject + ": " + grades.get(subject));
        }

        // Display average and grade level
        double average = student.calculateAverage();
        String gradeLevel = determineGradeLevel(average);

        System.out.println("\nAverage: " + formatDouble(average));
        System.out.println("Grade Level: " + gradeLevel);
    }

    // Generate a report with class statistics
    private static void generateClassReport(ArrayList<Student> students) {
        System.out.println("\n----- Class Report -----");

        if (students.isEmpty()) {
            System.out.println("No students in the system yet.");
            return;
        }

        // Initialize variables for statistics
        HashMap<String, Double> subjectTotals = new HashMap<>();
        HashMap<String, Integer> subjectCounts = new HashMap<>();
        HashMap<String, Double> highestScores = new HashMap<>();
        HashMap<String, String> highestStudents = new HashMap<>();
        HashMap<String, Double> lowestScores = new HashMap<>();
        HashMap<String, String> lowestStudents = new HashMap<>();

        // Collect data from all students
        for (Student student : students) {
            HashMap<String, Double> grades = student.getGrades();

            for (String subject : grades.keySet()) {
                double grade = grades.get(subject);

                // Update totals and counts for averages
                subjectTotals.put(subject, subjectTotals.getOrDefault(subject, 0.0) + grade);
                subjectCounts.put(subject, subjectCounts.getOrDefault(subject, 0) + 1);

                // Update highest scores
                if (!highestScores.containsKey(subject) || grade > highestScores.get(subject)) {
                    highestScores.put(subject, grade);
                    highestStudents.put(subject, student.getName());
                }

                // Update lowest scores
                if (!lowestScores.containsKey(subject) || grade < lowestScores.get(subject)) {
                    lowestScores.put(subject, grade);
                    lowestStudents.put(subject, student.getName());
                }
            }
        }

        // Display class averages
        System.out.println("\nClass Averages:");
        double overallTotal = 0;
        int overallCount = 0;

        for (String subject : subjectTotals.keySet()) {
            double average = subjectTotals.get(subject) / subjectCounts.get(subject);
            System.out.println(subject + ": " + formatDouble(average));

            overallTotal += subjectTotals.get(subject);
            overallCount += subjectCounts.get(subject);
        }

        // Display overall average
        if (overallCount > 0) {
            double overallAverage = overallTotal / overallCount;
            System.out.println("\nOverall Class Average: " + formatDouble(overallAverage));
        }

        // Display highest and lowest scores
        System.out.println("\nHighest Scores:");
        for (String subject : highestScores.keySet()) {
            System.out.println(subject + ": " + formatDouble(highestScores.get(subject)) +
                              " (" + highestStudents.get(subject) + ")");
        }

        System.out.println("\nLowest Scores:");
        for (String subject : lowestScores.keySet()) {
            System.out.println(subject + ": " + formatDouble(lowestScores.get(subject)) +
                              " (" + lowestStudents.get(subject) + ")");
        }

        // Identify students needing help (below 60 in any subject)
        System.out.println("\nStudents Needing Additional Help:");
        boolean anyNeedHelp = false;

        for (Student student : students) {
            HashMap<String, Double> grades = student.getGrades();
            boolean needsHelp = false;
            ArrayList<String> lowSubjects = new ArrayList<>();

            for (String subject : grades.keySet()) {
                if (grades.get(subject) < 60) {
                    needsHelp = true;
                    lowSubjects.add(subject);
                }
            }

            if (needsHelp) {
                anyNeedHelp = true;
                System.out.println(student.getName() + " (" + student.getId() + ") - Low performance in: " +
                                  String.join(", ", lowSubjects));
            }
        }

        if (!anyNeedHelp) {
            System.out.println("No students identified as needing additional help.");
        }
    }

    // Export student data to a file
    private static void exportData(ArrayList<Student> students) {
        System.out.println("\n----- Export Data -----");

        if (students.isEmpty()) {
            System.out.println("No students in the system to export.");
            return;
        }

        // Create filename with current date
        String filename = "StudentGrades_" + LocalDate.now() + ".txt";

        try {
            // Create file writer
            FileWriter writer = new FileWriter(filename);

            // Write header
            writer.write("Student Grade Management System - Export Date: " + LocalDate.now() + "\n\n");

            // Write student data
            for (Student student : students) {
                writer.write("Name: " + student.getName() + "\n");
                writer.write("ID: " + student.getId() + "\n");

                HashMap<String, Double> grades = student.getGrades();

                if (!grades.isEmpty()) {
                    writer.write("Grades:\n");

                    for (String subject : grades.keySet()) {
                        writer.write("  " + subject + ": " + grades.get(subject) + "\n");
                    }

                    double average = student.calculateAverage();
                    String gradeLevel = determineGradeLevel(average);

                    writer.write("Average: " + formatDouble(average) + "\n");
                    writer.write("Grade Level: " + gradeLevel + "\n");
                } else {
                    writer.write("No grades recorded.\n");
                }

                writer.write("\n"); // Empty line between students
            }

            // Close the writer
            writer.close();

            System.out.println("Data exported successfully to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting data: " + e.getMessage());
        }
    }

    // Helper method to find a student by ID
    private static Student findStudent(Scanner scanner, ArrayList<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students in the system yet.");
            return null;
        }

        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();

        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }

        System.out.println("Student not found with ID: " + id);
        return null;
    }

    // Determine grade level based on average score
    private static String determineGradeLevel(double average) {
        if (average >= 90) return "A";
        if (average >= 80) return "B";
        if (average >= 70) return "C";
        if (average >= 60) return "D";
        return "F";
    }

    // Format a double to two decimal places
    private static String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}

/**
 * Student class to store student information and grades
 */
class Student {
    private String name;
    private String id;
    private HashMap<String, Double> grades;

    // Constructor
    public Student(String name, String id) {
        this.name = name;
        this.id = id;
        this.grades = new HashMap<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, Double> getGrades() {
        return grades;
    }

    // Add a grade for a subject
    public void addGrade(String subject, double grade) {
        grades.put(subject, grade);
    }

    // Calculate the average grade
    public double calculateAverage() {
        if (grades.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double grade : grades.values()) {
            sum += grade;
        }

        return sum / grades.size();
    }
}