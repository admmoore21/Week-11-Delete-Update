package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	/* The following is the list of operations available to the user */
	// @formatter: off
	private List<String> operations = List.of(
			"1) Add a project", 
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			);
	// @formatter:on

	/* Calls the user selection prompt to main class of the app */
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

	/* class that receives the user selection and processes that input by calling
	 * upon other created classes to accomplish those objectives */
	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;

				case 2:
					listProjects();
					break;
					
				case 3:
					selectProject();
					break;
					
				case 4: 
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again");
				}

			} catch (Exception e) {
				System.out.println("\n Error: " + e + " Try again.");
			}

		}

	}

	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the ID of the Project you want to delete.");
		
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
		
	}

	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project");
			return;
		} 
		/* for each field in the Project Object print a message along with the 
		 * current setting in curProject  */
		String projectName = getStringInput("Enter the project name ["
				+ "[" + curProject.getProjectName() + "]");
		
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" 
				+ curProject.getEstimatedHours() + "]");
		
		BigDecimal actualHours = getDecimalInput("Enter the actual hours + [" 
				+ curProject.getActualHours() + "]");
		
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" 
				+ curProject.getDifficulty() + "]");
		
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		/* Create new Project Object. If not null: add the value to the Project object
		 * If null: add the value from curProject */
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) 
				? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) 
				? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) 
				? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) 
				? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) 
				? curProject.getNotes() : notes);

		/**/
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	/*This method displays the projects, then prompts the user to select one with the 
	 * getIntInput method.  */
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		// Clear the current project selection.
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		if(Objects.isNull(curProject)) {
			System.out.println("Invalid project ID selected.");
		}
	}

	private void listProjects() {
		// Creates a list of Projects named projects using the fetchAllProjects class from the
		// ProjectService class.
		List<Project> projects = projectService.fetchAllProjects();

		// Prints to the console
		System.out.println("\nProjects:");

		// For each project in the list, prints to console the project ID and Project Name
		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() 
				+ ": " + project.getProjectName()));
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)"); // Instructions do not include code to
																				// validate that the input is valid.
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);

	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu.");
		return true;
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Please try again.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		
		//Prints to console which operations are available to select.
		operations.forEach(line -> System.out.println("   " + line));
		
		/* Checks to see if curProjects has selected a project, if it hasn't then
		 * prints that to the console. If curProjects has selected a project it will
		 * print to the console which project is selected */
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
