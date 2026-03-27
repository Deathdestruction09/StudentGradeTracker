import java.io.*;
import java.util.*;

// ==================== STUDENT CLASS ====================
class Student {
    private String studentId;
    private String name;
    private String email;
    private List<Grade> grades;
    private static int idCounter = 1000;

    public Student(String name, String email) {
        this.studentId = "STU" + (++idCounter);
        this.name = name;
        this.email = email;
        this.grades = new ArrayList<>();
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public double calculateGPA() {
        if (grades.isEmpty()) return 0.0;
        
        double totalPoints = 0;
        int totalCredits = 0;
        
        for (Grade grade : grades) {
            totalPoints += grade.getGradePoints() * grade.getCredits();
            totalCredits += grade.getCredits();
        }
        
        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    public String getLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Grade> getGrades() { return new ArrayList<>(grades); }

    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, GPA=%.2f]", 
            studentId, name, calculateGPA());
    }
}

// ==================== GRADE CLASS ====================
class Grade {
    private String courseCode;
    private String courseName;
    private double score; // 0-100
    private int credits;
    private String semester;
    private Date dateRecorded;

    public Grade(String courseCode, String courseName, double score, int credits, String semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.score = Math.max(0, Math.min(100, score)); // Clamp between 0-100
        this.credits = credits;
        this.semester = semester;
        this.dateRecorded = new Date();
    }

    public double getGradePoints() {
        if (score >= 90) return 4.0;
        else if (score >= 80) return 3.0;
        else if (score >= 70) return 2.0;
        else if (score >= 60) return 1.0;
        else return 0.0;
    }

    public String getLetterGrade() {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public double getScore() { return score; }
    public int getCredits() { return credits; }
    public String getSemester() { return semester; }

    @Override
    public String toString() {
        return String.format("%s (%s): %.1f%% [%s, %.1f GPA pts, %d cr]", 
            courseCode, courseName, score, getLetterGrade(), getGradePoints(), credits);
    }
}

// ==================== GRADE MANAGER CLASS ====================
class GradeManager {
    private List<Student> students;
    private Scanner scanner;

    public GradeManager() {
        this.students = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void addStudent(String name, String email) {
        Student student = new Student(name, email);
        students.add(student);
        System.out.println("✓ Student added: " + student.getStudentId());
    }

    public Student findStudent(String studentId) {
        return students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst()
            .orElse(null);
    }

    public void addGradeToStudent(String studentId, String courseCode, String courseName, 
                                   double score, int credits, String semester) {
        Student student = findStudent(studentId);
        if (student != null) {
            Grade grade = new Grade(courseCode, courseName, score, credits, semester);
            student.addGrade(grade);
            System.out.println("✓ Grade added: " + grade);
        } else {
            System.out.println("✗ Student not found: " + studentId);
        }
    }

    public void displayStudentReport(String studentId) {
        Student student = findStudent(studentId);
        if (student == null) {
            System.out.println("✗ Student not found");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           STUDENT ACADEMIC REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("Student ID: %s%n", student.getStudentId());
        System.out.printf("Name: %s%n", student.getName());
        System.out.printf("Email: %s%n", student.getEmail());
        System.out.printf("Overall GPA: %.2f%n", student.calculateGPA());
        System.out.println("-".repeat(60));
        
        List<Grade> grades = student.getGrades();
        if (grades.isEmpty()) {
            System.out.println("No grades recorded yet.");
        } else {
            System.out.println("COURSE HISTORY:");
            System.out.printf("%-10s %-20s %-8s %-6s %-8s %s%n", 
                "Code", "Course Name", "Score", "Grade", "Credits", "Semester");
            System.out.println("-".repeat(60));
            
            for (Grade g : grades) {
                System.out.printf("%-10s %-20s %-8.1f %-6s %-8d %s%n",
                    g.getCourseCode(), 
                    g.getCourseName().length() > 20 ? g.getCourseName().substring(0, 17) + "..." : g.getCourseName(),
                    g.getScore(), 
                    g.getLetterGrade(), 
                    g.getCredits(),
                    g.getSemester());
            }
        }
        System.out.println("=".repeat(60));
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in system.");
            return;
        }
        
        System.out.println("\n--- All Students ---");
        System.out.printf("%-10s %-20s %-25s %s%n", "ID", "Name", "Email", "GPA");
        System.out.println("-".repeat(70));
        
        for (Student s : students) {
            System.out.printf("%-10s %-20s %-25s %.2f%n",
                s.getStudentId(), s.getName(), s.getEmail(), s.calculateGPA());
        }
    }

    public void displayClassStatistics(String courseCode) {
        List<Double> scores = new ArrayList<>();
        
        for (Student s : students) {
            for (Grade g : s.getGrades()) {
                if (g.getCourseCode().equalsIgnoreCase(courseCode)) {
                    scores.add(g.getScore());
                }
            }
        }

        if (scores.isEmpty()) {
            System.out.println("No data for course: " + courseCode);
            return;
        }

        double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        
        long aCount = scores.stream().filter(s -> s >= 90).count();
        long bCount = scores.stream().filter(s -> s >= 80 && s < 90).count();
        long cCount = scores.stream().filter(s -> s >= 70 && s < 80).count();
        long dCount = scores.stream().filter(s -> s >= 60 && s < 70).count();
        long fCount = scores.stream().filter(s -> s < 60).count();

        System.out.println("\n--- Statistics for " + courseCode + " ---");
        System.out.printf("Students enrolled: %d%n", scores.size());
        System.out.printf("Average: %.2f%%%n", avg);
        System.out.printf("Highest: %.2f%%%n", max);
        System.out.printf("Lowest: %.2f%%%n", min);
        System.out.println("\nGrade Distribution:");
        System.out.printf("A: %d | B: %d | C: %d | D: %d | F: %d%n", 
            aCount, bCount, cCount, dCount, fCount);
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Student s : students) {
                writer.printf("STUDENT|%s|%s|%s%n", 
                    s.getStudentId(), s.getName(), s.getEmail());
                for (Grade g : s.getGrades()) {
                    writer.printf("GRADE|%s|%s|%.1f|%d|%s%n",
                        g.getCourseCode(), g.getCourseName(), 
                        g.getScore(), g.getCredits(), g.getSemester());
                }
            }
            System.out.println("✓ Data saved to " + filename);
        } catch (IOException e) {
            System.out.println("✗ Error saving: " + e.getMessage());
        }
    }

    public void runInteractiveMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║     STUDENT GRADE TRACKER          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║  1. Add Student                    ║");
            System.out.println("║  2. Add Grade                      ║");
            System.out.println("║  3. View Student Report            ║");
            System.out.println("║  4. View All Students              ║");
            System.out.println("║  5. Course Statistics              ║");
            System.out.println("║  6. Save Data                      ║");
            System.out.println("║  0. Exit                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    addStudent(name, email);
                    break;

                case "2":
                    System.out.print("Student ID: ");
                    String sid = scanner.nextLine();
                    System.out.print("Course Code: ");
                    String code = scanner.nextLine();
                    System.out.print("Course Name: ");
                    String cname = scanner.nextLine();
                    System.out.print("Score (0-100): ");
                    double score = Double.parseDouble(scanner.nextLine());
                    System.out.print("Credits: ");
                    int credits = Integer.parseInt(scanner.nextLine());
                    System.out.print("Semester (e.g., Fall 2025): ");
                    String sem = scanner.nextLine();
                    addGradeToStudent(sid, code, cname, score, credits, sem);
                    break;

                case "3":
                    System.out.print("Student ID: ");
                    displayStudentReport(scanner.nextLine());
                    break;

                case "4":
                    displayAllStudents();
                    break;

                case "5":
                    System.out.print("Course Code: ");
                    displayClassStatistics(scanner.nextLine());
                    break;

                case "6":
                    System.out.print("Filename: ");
                    saveToFile(scanner.nextLine());
                    break;

                case "0":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}

// ==================== MAIN CLASS ====================
public class StudentGradeTracker {
    public static void main(String[] args) {
        GradeManager manager = new GradeManager();
        
        // Demo data
        System.out.println("Loading demo data...\n");
        manager.addStudent("Alice Johnson", "alice@university.edu");
        manager.addStudent("Bob Smith", "bob@university.edu");
        manager.addStudent("Carol White", "carol@university.edu");
        
        // Add some grades
        manager.addGradeToStudent("STU1001", "CS101", "Intro to Programming", 92.5, 3, "Fall 2025");
        manager.addGradeToStudent("STU1001", "MATH201", "Calculus II", 87.0, 4, "Fall 2025");
        manager.addGradeToStudent("STU1001", "ENG102", "English Composition", 95.0, 3, "Fall 2025");
        
        manager.addGradeToStudent("STU1002", "CS101", "Intro to Programming", 78.5, 3, "Fall 2025");
        manager.addGradeToStudent("STU1002", "PHYS101", "Physics I", 88.0, 4, "Fall 2025");
        
        manager.addGradeToStudent("STU1003", "CS101", "Intro to Programming", 91.0, 3, "Fall 2025");
        manager.addGradeToStudent("STU1003", "MATH201", "Calculus II", 89.5, 4, "Fall 2025");
        
        // Display demo reports
        manager.displayStudentReport("STU1001");
        manager.displayStudentReport("STU1002");
        
        System.out.println("\n--- Course Statistics ---");
        manager.displayClassStatistics("CS101");
        
        // Start interactive menu
        System.out.println("\n\nStarting interactive mode...");
        manager.runInteractiveMenu();
    }
}