# Student Grade Management System
# This program helps manage student grades, calculate statistics, and generate reports.
# It allows users to add students, input grades, view information, generate class reports, and export data.

import datetime  # Import datetime module for working with dates

# Student class to store student information and grades
class Student:
    # Constructor - initialize a new student with name and ID
    def __init__(self, name, student_id):
        self.name = name  # Student's name
        self.student_id = student_id  # Student's unique ID
        self.grades = {}  # Dictionary to store subject:grade pairs
    
    # Calculate the average grade for a student across all subjects
    def calculate_average(self):
        # If no grades exist, return 0
        if not self.grades:
            return 0.0
        # Calculate average by summing all grades and dividing by number of subjects
        return sum(self.grades.values()) / len(self.grades)

# Function to add a new student to the system
def add_student(students):
    print("\n----- Add New Student -----")
    
    # Get student information from user input
    name = input("Enter student name: ")
    student_id = input("Enter student ID: ")
    
    # Check if student ID already exists to avoid duplicates
    for student in students:
        if student.student_id == student_id:
            print("Error: Student ID already exists.")
            return
    
    # Create a new Student object and add it to the list
    new_student = Student(name, student_id)
    students.append(new_student)
    
    print("Student added successfully!")

# Function to add grades for an existing student
def add_grades(students):
    print("\n----- Add Grades -----")
    
    # Find the student by ID
    student = find_student(students)
    if student is None:
        return  # Exit if student not found
    
    # List of subjects for which we'll collect grades
    subjects = ["Math", "Chinese", "English"]
    
    # Loop through each subject to get grades
    for subject in subjects:
        while True:
            try:
                # Get the grade as input and convert to float
                grade = float(input(f"Enter {subject} grade: "))
                
                # Validate that grade is within valid range (0-100)
                if grade < 0 or grade > 100:
                    print("Error: Grade must be between 0 and 100.")
                    continue
                    
                # Add the grade to the student's grades dictionary
                student.grades[subject] = grade
                break  # Break the loop once valid grade is entered
            except ValueError:
                # Handle case where input cannot be converted to float
                print("Error: Please enter a valid number.")
    
    print("Grades added successfully!")

# Function to view information about a specific student
def view_student(students):
    print("\n----- View Student -----")
    
    # Find the student by ID
    student = find_student(students)
    if student is None:
        return  # Exit if student not found
    
    # Display student's basic information
    print("\nStudent Information:")
    print(f"Name: {student.name}")
    print(f"ID: {student.student_id}")
    
    # Display all grades for the student
    print("\nGrades:")
    if not student.grades:
        print("No grades recorded yet.")
        return
    
    # Loop through each subject and display its grade
    for subject, grade in student.grades.items():
        print(f"{subject}: {grade}")
    
    # Calculate and display average and grade level
    average = student.calculate_average()
    grade_level = determine_grade_level(average)
    
    print(f"\nAverage: {average:.2f}")  # Format average to 2 decimal places
    print(f"Grade Level: {grade_level}")

# Function to generate a comprehensive class report with statistics
def generate_class_report(students):
    print("\n----- Class Report -----")
    
    # Check if there are any students in the system
    if not students:
        print("No students in the system yet.")
        return
    
    # Initialize dictionaries to track statistics
    subject_totals = {}  # Sum of grades for each subject
    subject_counts = {}  # Count of grades for each subject
    highest_scores = {}  # Highest score for each subject
    highest_students = {}  # Student with highest score for each subject
    lowest_scores = {}  # Lowest score for each subject
    lowest_students = {}  # Student with lowest score for each subject
    
    # Collect data from all students
    for student in students:
        for subject, grade in student.grades.items():
            # Update totals and counts for calculating averages
            subject_totals[subject] = subject_totals.get(subject, 0) + grade
            subject_counts[subject] = subject_counts.get(subject, 0) + 1
            
            # Update highest scores if this grade is higher than current highest
            if subject not in highest_scores or grade > highest_scores[subject]:
                highest_scores[subject] = grade
                highest_students[subject] = student.name
            
            # Update lowest scores if this grade is lower than current lowest
            if subject not in lowest_scores or grade < lowest_scores[subject]:
                lowest_scores[subject] = grade
                lowest_students[subject] = student.name
    
    # Display class averages for each subject
    print("\nClass Averages:")
    overall_total = 0  # Sum of all grades across all subjects
    overall_count = 0  # Count of all grades across all subjects
    
    for subject in subject_totals:
        # Calculate average for this subject
        average = subject_totals[subject] / subject_counts[subject]
        print(f"{subject}: {average:.2f}")
        
        # Update overall totals for calculating overall average
        overall_total += subject_totals[subject]
        overall_count += subject_counts[subject]
    
    # Display overall class average across all subjects
    if overall_count > 0:
        overall_average = overall_total / overall_count
        print(f"\nOverall Class Average: {overall_average:.2f}")
    
    # Display highest scores for each subject
    print("\nHighest Scores:")
    for subject in highest_scores:
        print(f"{subject}: {highest_scores[subject]:.2f} ({highest_students[subject]})")
    
    # Display lowest scores for each subject
    print("\nLowest Scores:")
    for subject in lowest_scores:
        print(f"{subject}: {lowest_scores[subject]:.2f} ({lowest_students[subject]})")
    
    # Identify students who need additional help (grades below 60)
    print("\nStudents Needing Additional Help:")
    any_need_help = False  # Flag to track if any students need help
    
    for student in students:
        low_subjects = []  # List to collect subjects with low grades
        
        # Check each subject for low grades
        for subject, grade in student.grades.items():
            if grade < 60:
                low_subjects.append(subject)
        
        # If student has any low grades, display them
        if low_subjects:
            any_need_help = True
            print(f"{student.name} ({student.student_id}) - Low performance in: {', '.join(low_subjects)}")
    
    # If no students need help, display a message
    if not any_need_help:
        print("No students identified as needing additional help.")

# Function to export all student data to a text file
def export_data(students):
    print("\n----- Export Data -----")
    
    # Check if there are any students in the system
    if not students:
        print("No students in the system to export.")
        return
    
    # Create filename with current date (e.g., StudentGrades_2025-03-06.txt)
    today = datetime.date.today()
    filename = f"StudentGrades_{today}.txt"
    
    try:
        # Open the file for writing
        with open(filename, 'w') as f:
            # Write header information
            f.write(f"Student Grade Management System - Export Date: {today}\n\n")
            
            # Write data for each student
            for student in students:
                f.write(f"Name: {student.name}\n")
                f.write(f"ID: {student.student_id}\n")
                
                # If student has grades, write them
                if student.grades:
                    f.write("Grades:\n")
                    
                    # Write each subject and grade
                    for subject, grade in student.grades.items():
                        f.write(f"  {subject}: {grade}\n")
                    
                    # Calculate and write average and grade level
                    average = student.calculate_average()
                    grade_level = determine_grade_level(average)
                    
                    f.write(f"Average: {average:.2f}\n")
                    f.write(f"Grade Level: {grade_level}\n")
                else:
                    # If no grades, write a message
                    f.write("No grades recorded.\n")
                
                f.write("\n")  # Add an empty line between students
        
        # Display success message with filename
        print(f"Data exported successfully to file: {filename}")
    except Exception as e:
        # Handle any errors that might occur during file operations
        print(f"Error exporting data: {e}")

# Helper function to find a student by ID
def find_student(students):
    # Check if there are any students in the system
    if not students:
        print("No students in the system yet.")
        return None
    
    # Get student ID from user
    student_id = input("Enter student ID: ")
    
    # Search for student with matching ID
    for student in students:
        if student.student_id == student_id:
            return student  # Return student if found
    
    # If no matching student found, display error and return None
    print(f"Student not found with ID: {student_id}")
    return None

# Function to determine grade level based on average score
def determine_grade_level(average):
    # Convert numerical average to letter grade
    if average >= 90: return "A"  # 90-100 = A
    if average >= 80: return "B"  # 80-89 = B
    if average >= 70: return "C"  # 70-79 = C
    if average >= 60: return "D"  # 60-69 = D
    return "F"  # Below 60 = F

# Function to display the main menu and get user choice
def display_menu():
    print("\n----- MENU -----")
    print("1. Add Student")  # Option to add a new student
    print("2. Add Grades")   # Option to add grades for a student
    print("3. View Student") # Option to view a student's information
    print("4. Generate Class Report") # Option to generate a class report
    print("5. Export Data")  # Option to export data to a file
    print("6. Exit")         # Option to exit the program
    return input("Enter your choice: ")  # Get and return user's choice

# Main function that drives the program
def main():
    # Initialize empty list to store students
    students = []
    
    # Display welcome message
    print("Welcome to Student Grade Management System")
    
    # Main program loop
    running = True
    while running:
        # Display menu and get user choice
        choice = display_menu()
        
        # Process user choice
        if choice == "1":
            add_student(students)  # Add a new student
        elif choice == "2":
            add_grades(students)   # Add grades for a student
        elif choice == "3":
            view_student(students) # View a student's information
        elif choice == "4":
            generate_class_report(students) # Generate a class report
        elif choice == "5":
            export_data(students)  # Export data to a file
        elif choice == "6":
            # Exit the program
            print("Thank you for using Student Grade Management System. Goodbye!")
            running = False
        else:
            # Handle invalid menu choices
            print("Invalid option. Please try again.")
        
        print()  # Add an empty line for better readability

# Entry point of the program
# This ensures the main() function is only called when this script is run directly
# (not when imported as a module in another script)
if __name__ == "__main__":
    main()
